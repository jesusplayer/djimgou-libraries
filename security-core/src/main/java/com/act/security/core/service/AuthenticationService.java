package com.act.security.core.service;

import com.act.core.exception.ConflitException;
import com.act.core.exception.NotFoundException;
import com.act.mail.EmailSenderService;
import com.act.security.core.exceptions.*;
import com.act.security.core.model.ConfirmationToken;
import com.act.security.core.model.Utilisateur;
import com.act.security.core.model.dto.utilisateur.PasswordChangeDto;
import com.act.security.core.model.dto.utilisateur.UserNameChangeDto;
import com.act.security.core.repo.ConfirmationTokenRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.act.core.util.AppUtils.has;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Service
public class AuthenticationService {
    private UtilisateurBdService utilisateurBdService;

    private ConfirmationTokenRepo confirmationTokenRepo;

    /**
     * Dans le cas où le module parent est un microservice,
     * le SecurityConfig n'est pas créé et par conséquent
     * authenticationManager sera à null
     * Donc impossible de vérifier si c'est le bon user qui a le
     * bon mot de passe il faut donc un filtre pour la requete de
     * changement de mot de passe qui sera effectué par Zuul ou Spring cloud gateway
     */
    private AuthenticationManager authenticationManager;

    private SecuritySessionService sessionService;
    private Integer expiryTokenDay;

    private String host;

    private String contextPath;

    private String port;

    private String ssl;
    private String mailFrom;
    private String mailSubject;
    private String mailSignupMsg;
    private String signupConfirmUrl;
    private String mailClickLinkText;
    private String mailFromName;

    private EmailSenderService emailSender;

    public AuthenticationService(
            UtilisateurBdService utilisateurBdService,
            ConfirmationTokenRepo confirmationTokenRepo,
            Optional<AuthenticationManager> authenticationManager,
            SecuritySessionService sessionService,
            EmailSenderService emailSenderService,
            @Value("${auth.expiryTokenDay:}") Integer expiryTokenDay,
            @Value("${server.address:}") String host,
            @Value("${server.servlet.context-path:}") String contextPath,
            @Value("${server.port:}") String port,
            @Value("${server.ssl.enabled:}") String ssl,
            @Value("${auth.mail.signup.text:" +
                    "Bienvenue<p> Merci pour votre inscription. Pour confirmez votre compte, cliquez ici : " +
                    "}") String mailSignupMsg,
            @Value("${auth.mail.signup.clickLinkText}") String mailClickLinkText,
            @Value("${auth.mail.signup.from}") String mailFrom,
            @Value("${auth.mail.signup.fromName}") String mailFromName,
            @Value("${auth.mail.signup.confirmationUrl:}") String signupConfirmUrl,
            @Value("${auth.mail.signup.subject:Completez votre enregistrement!" +
                    "}") String mailSubject
    ) {
        this.utilisateurBdService = utilisateurBdService;
        this.confirmationTokenRepo = confirmationTokenRepo;
        this.sessionService = sessionService;
        this.emailSender = emailSenderService;
        if (authenticationManager.isPresent()) {
            this.authenticationManager = authenticationManager.get();
        }
        this.expiryTokenDay = expiryTokenDay;
        this.host = host;
        this.contextPath = contextPath;
        this.port = port;
        this.ssl = ssl;
        this.mailSignupMsg = mailSignupMsg;
        this.signupConfirmUrl = signupConfirmUrl;
        this.mailFrom = mailFrom;
        this.mailSubject = mailSubject;
        this.mailClickLinkText = mailClickLinkText;
        this.mailFromName = mailFromName;
    }

/*
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;*/


    /**
     * Envoi de l'email de confirmation
     *
     * @param user utilisateur
     * @throws Exception exception
     */
    // @Async
    @Transactional
    public void inviteUser(Utilisateur user) throws UtilisateurConfiltException {

        Optional<Utilisateur> userOpt = utilisateurBdService.getRepo().findOneByEmail(user.getEmail());
        if (userOpt.isPresent() && userOpt.get().getEnabled()) {
            throw new UtilisateurConfiltException("Cet utilisateur est déjà actif");
        } else {
            user = userOpt.orElse(user);
            user.setIsInvitationPending(Boolean.TRUE);
            utilisateurBdService.save(user);

            ConfirmationToken confirmationToken = new ConfirmationToken(user);

            String prefixConfirmUrl = signupConfirmUrl;
            if (!has(signupConfirmUrl)) {
                prefixConfirmUrl = (Boolean.parseBoolean(ssl) ? "https://" : "http://")
                        + (has(host) ? host : "localhost") + ":" + port + contextPath;
                prefixConfirmUrl = prefixConfirmUrl + "/compteUtilisateur/confirmerInvitation/";
            }
            final String url = prefixConfirmUrl +
                    confirmationToken.getConfirmationToken();

            MimeMessagePreparator msg = emailSender.buildMessage(
                    mailFrom,
                    mailFromName,
                    mailSubject, user.getEmail(), null, null,
                    mailSignupMsg + " <a href=\"" + url + "\">" + mailClickLinkText + "</a>",
                    null);
            emailSender.sendEmail(msg);
            // gmailMessageService.send(mailMessage);
            confirmationTokenRepo.save(confirmationToken);
        }

    }

    public void sendPaswordToUser(Utilisateur user, String password) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Invitation à vous connecter à CARRENT");
        mailMessage.setFrom("carrentapp@gmail.com");
        String body = String.format("Bienvenue sur CARRENT. utilisez ces informations pour vous connecter: " +
                "- Login: %s " +
                "- Mot de passe: %s " +
                " Notez que vous pouvez changer ce mot de passe ultérieurement. Pour vous connecter, cliquez sur ce lien : "
                + "http://%s:%s", user.getUsername(), password, host, port);

        mailMessage.setText(body);

        //gmailMessageService.send(mailMessage);
    }

    /**
     * Confirmation de l'email
     *
     * @param token    token
     * @param password mot de passse
     * @throws Exception exception
     */
    @Transactional
    public void confirmUtilisateurAccount(String token, String password, String passwordEnc) throws BadInvitationLinkException, NotFoundException {
        ConfirmationToken confirmationToken = confirmationTokenRepo.findByConfirmationToken(token);
        if (has(confirmationToken)) {
            Utilisateur user = utilisateurBdService.
                    findById(confirmationToken.getUtilisateurId())
                    .orElseThrow(UtilisateurNotFoundException::new);
            validateVerificationToken(token, user.getEmail());
            user.setEnabled(true);
            user.setIsInvitationPending(Boolean.FALSE);
            password = has(password) ? password : user.getPassword();
            if (has(password)) {
                if (has(passwordEnc)) {
                    user.setPassword(passwordEnc);
                }
                // user.setPassword(utilisateurBdService.getBCryptPasswordEncoder().encode(password));
            }
            utilisateurBdService.save(user);
            confirmationTokenRepo.delete(confirmationToken);
        } else {
            throw new BadInvitationLinkException("Ce lien d'invitation est invalide");
        }
    }

 /*   @Transactional
    public void confirmUtilisateurAccount(String token) throws BadInvitationLinkException, NotFoundException {
        confirmUtilisateurAccount(token, null);
    }*/

    public void changePassword(String encryptedPassword, Utilisateur utilisateur) {
        utilisateurBdService.changePassword(encryptedPassword, utilisateur);
    }

    public void changeUsername(UserNameChangeDto dto) throws NotFoundException, UnautorizedException, ConflitException {
        Optional<UUID> optUId = sessionService.currentUserId();
        UUID userId = optUId.orElseThrow(UnautorizedException::new);
        Utilisateur user = utilisateurBdService
                .findById(userId).orElseThrow(UtilisateurNotFoundException::new);

        // Tentative de changement du nom d'utilisateur
        if (has(dto.getNewUsername()) && Objects.equals(user.getUsername(), dto.getUsername())) {
            // L'utilisateur a bien saisi l'ancien nom d'utilisateur
            utilisateurBdService.checkDuplicateUserName(dto.getNewUsername(), userId);
            utilisateurBdService.changeUsername(user.getId(), dto.getNewUsername());
        }
    }

    public void changePassword(PasswordChangeDto dto) throws NotFoundException, BadConfirmPasswordException, UnautorizedException {
        Optional<UUID> optUId = sessionService.currentUserId();
        UUID userId = optUId.orElseThrow(UnautorizedException::new);
        Utilisateur user = utilisateurBdService
                .findById(userId).orElseThrow(UtilisateurNotFoundException::new);
        if (has(dto.getNewPassword())) {
            //String oldP = bCryptPasswordEncoder.encode(dto.getOldPassword());
            try {
                if (has(dto.getPasswordConfirm()) && Objects.equals(dto.getNewPassword(), dto.getPasswordConfirm())) {
                    // On vérifie si cest anciennes
                    UsernamePasswordAuthenticationToken authReq
                            = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
                    /**
                     * Dans le cas ou le module parent
                     * est un microservice, le SecurityConfig n'est
                     * pas créé et par conséquent
                     * nest pas controlé par spring security
                     * par co
                     */
                    if (has(authenticationManager)) {
                        authenticationManager.authenticate(authReq);
                    }
                    // L'utilisateur a bien saisi l'ancien nom d'utilisateur
                    utilisateurBdService.changePassword(user.getId(), dto.getPasswordEnc());
                    //utilisateurBdService.changePassword(user.getId(), utilisateurBdService.getBCryptPasswordEncoder().encode(dto.getNewPassword()));
                } else {
                    throw new BadConfirmPasswordException();
                }
            } catch (AuthenticationException e) {
                throw new BadCredentialsException("Vos anciennes information Les information de connexion sont éronnées");
            }
        }


    }

    public void validateVerificationToken(String token, String email) throws BadInvitationLinkException, NotFoundException {
        final ConfirmationToken verificationToken = confirmationTokenRepo.findByConfirmationToken(token);
        if (verificationToken == null) {
            throw new BadInvitationLinkException("Token invalide");
        }

        final Utilisateur user = utilisateurBdService.findById(verificationToken.getUtilisateurId())
                .orElseThrow(UtilisateurNotFoundException::new);

        if (!(has(user) && has(user.getEmail()) && user.getEmail().equals(email))) {
            throw new BadInvitationLinkException("Cet email est invalide vérifiez qu'il s'agit de la bonne adresse d'invitation");
        }
        final Date dateDujour = Calendar.getInstance().getTime();
        long diff = dateDujour.getTime() - verificationToken.getCreatedDate().getTime();
        diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        if ((int) diff > expiryTokenDay) {
            confirmationTokenRepo.delete(verificationToken);
            throw new BadInvitationLinkException("Token expiré");
        }

    }
}
