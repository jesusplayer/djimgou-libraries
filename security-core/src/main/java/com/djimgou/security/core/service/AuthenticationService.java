package com.djimgou.security.core.service;

import com.djimgou.core.exception.BadRequestException;
import com.djimgou.core.exception.ConflitException;
import com.djimgou.core.exception.NotFoundException;
import com.djimgou.mail.EmailSenderService;
import com.djimgou.security.core.exceptions.*;
import com.djimgou.security.core.model.ConfirmationToken;
import com.djimgou.security.core.model.Utilisateur;
import com.djimgou.security.core.model.dto.utilisateur.PasswordChangeByEmailDto;
import com.djimgou.security.core.model.dto.utilisateur.PasswordChangeDto;
import com.djimgou.security.core.model.dto.utilisateur.UserNameChangeDto;
import com.djimgou.security.core.repo.ConfirmationTokenRepo;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.InternetAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.djimgou.core.util.AppUtils.has;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Log4j2
@Getter
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
    private String mailChangePasswordSubject;
    private String mailSignupMsg;
    private String mailChangePasswdMsg;
    private String signupConfirmUrl;
    private String changePasswordClientUrl;
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
            @Value("${auth.mail.changePasswordText:" +
                    "Pour le changement de votre mot de passe, cliquez ici : " +
                    "}") String mailChangePasswdMsg,
            @Value("${auth.mail.signup.clickLinkText}") String mailClickLinkText,
            @Value("${auth.mail.signup.from}") String mailFrom,
            @Value("${auth.mail.signup.fromName}") String mailFromName,
            @Value("${auth.mail.signup.confirmationUrl:}") String signupConfirmUrl,
            @Value("${auth.mail.changePasswordClientUrl:}") String changePasswordClientUrl,
            @Value("${auth.mail.signup.subject:Completez votre enregistrement!" +
                    "}") String mailSubject,
            @Value("${auth.mail.changePasswordSubject:Changement de mot de passe!" +
                    "}") String mailChangePasswordSubject
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
        this.changePasswordClientUrl = changePasswordClientUrl;
        this.mailFrom = mailFrom;
        this.mailSubject = mailSubject;
        this.mailClickLinkText = mailClickLinkText;
        this.mailFromName = mailFromName;
        this.mailChangePasswdMsg = mailChangePasswdMsg;
        this.mailChangePasswordSubject = mailChangePasswordSubject;
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
    public String inviteUser(Utilisateur user) throws UtilisateurConfiltException {

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
//            intAdr= new InternetAddress(mailFrom)
            MimeMessagePreparator msg = emailSender.buildMessage(
                    mailFrom,
                    mailFromName,
                    mailSubject
                    , user.getEmail(), null, null,
                    "<h1>Salut " + user.fullname() +".</h1>"+
                            mailSignupMsg + " avec pour login: <b>" + user.getUsername() + "</b>, <a href=\"" + url + "\">" + mailClickLinkText + "</a>",
                    null);
            emailSender.sendEmail(msg);
            // gmailMessageService.send(mailMessage);
            confirmationTokenRepo.save(confirmationToken);
            log.info("Le lien d'invitation de l'utilisateur {} roles: {} a été envoyé avec succès: urls: {}", user.getUsername(), user.getRoles(), url);
            return url;
        }

    }

    public void newPartenaireEmailNotif(Utilisateur user, List<String> destinationEmails) {
        MimeMessagePreparator msg = emailSender.buildMessage(
                mailFrom,
                mailFromName,
                mailSubject
                , String.join(",", destinationEmails), null, null,
                String.format("RalaleAuto<p>Un Nouveau partenaire <b>%s</b> s'est inscrit: <br><br> Nom utilisateur: %s <br> Email: %s <br> Téléphone: %s, Veuillez le contacter pour activer son compte", user.fullname(), user.getUsername(), user.getEmail(), user.getTelephone()),
                null);
        emailSender.sendEmail(msg);
    }

    @Transactional
    public void inviteChangePasswd(String email) throws BadRequestException, UtilisateurNotFoundException {

        Utilisateur user = utilisateurBdService.getRepo().findOneByEmail(email).orElseThrow(UtilisateurNotFoundException::new);
        if (!user.getEnabled()) {
            throw new BadRequestException("Cet utilisateur est inactif. impossible d'éffectuer cette opération");
        }

        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        String prefixConfirmUrl = changePasswordClientUrl;
        if (!has(changePasswordClientUrl)) {
            prefixConfirmUrl = (Boolean.parseBoolean(ssl) ? "https://" : "http://")
                    + (has(host) ? host : "localhost") + ":" + port + contextPath;
            prefixConfirmUrl = prefixConfirmUrl + "/compteUtilisateur/changerMotDePasse/";
        }
        final String url = prefixConfirmUrl +
                confirmationToken.getConfirmationToken() + "/" + user.getEmail();

        MimeMessagePreparator msg = emailSender.buildMessage(
                mailFrom,
                mailFromName,
                mailChangePasswordSubject, user.getEmail(), null, null,
                mailChangePasswdMsg + " <a href=\"" + url + "\">" + mailClickLinkText + "</a>",
                null);
        emailSender.sendEmail(msg);
        // gmailMessageService.send(mailMessage);
        confirmationTokenRepo.save(confirmationToken);
        log.info("Le lien d'invitation de changement de mot de passe de l'utilisateur {} roles: {} a été envoyé avec succès: urls: {}", user.getUsername(), user.getRoles(), url);
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
     * @return
     * @throws Exception exception
     */
    @Transactional
    public Utilisateur confirmUtilisateurAccount(String token, String password, String passwordEnc) throws BadInvitationLinkException, NotFoundException {
        ConfirmationToken confirmationToken = confirmationTokenRepo.findByConfirmationToken(token);
        if (has(confirmationToken)) {
            Utilisateur user = utilisateurBdService.findById(confirmationToken.getUtilisateurId())
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
            Utilisateur newUser = utilisateurBdService.findById(user.getId()).get();
            log.info("Le compte utilisateur {} roles: {} a été confirmé avec succès", user.getUsername(), user.getRoles());
            return newUser;
        } else {
            throw new BadInvitationLinkException("bad.invitationLink");
        }
    }

    @Transactional
    public Utilisateur confirmResetPassword(String token, PasswordChangeByEmailDto dto) throws BadInvitationLinkException, NotFoundException {
        ConfirmationToken confirmationToken = confirmationTokenRepo.findByConfirmationToken(token);
        if (has(confirmationToken)) {
            Utilisateur user = utilisateurBdService.
                    findById(confirmationToken.getUtilisateurId())
                    .orElseThrow(UtilisateurNotFoundException::new);

            validateVerificationToken(token, user.getEmail());
            if (has(dto.getNewPassword())) {
                //String oldP = bCryptPasswordEncoder.encode(dto.getOldPassword());
                try {
                    if (has(dto.getPasswordConfirm()) && Objects.equals(dto.getNewPassword(), dto.getPasswordConfirm())) {
                        // L'utilisateur a bien saisi l'ancien nom d'utilisateur
                        utilisateurBdService.changePassword(user.getId(), dto.getPasswordEnc());
                        //utilisateurBdService.changePassword(user.getId(), utilisateurBdService.getBCryptPasswordEncoder().encode(dto.getNewPassword()));
                    } else {
                        throw new BadConfirmPasswordException();
                    }
                } catch (AuthenticationException | BadConfirmPasswordException e) {
                    throw new BadCredentialsException("Vos anciennes information Les information de connexion sont éronnées");
                }
            }
            confirmationTokenRepo.delete(confirmationToken);
            Utilisateur newUser = utilisateurBdService.findById(user.getId()).get();
            log.info("Le mot de passe de l'utilisateur {} roles: {} a été modifié avec succès", user.getUsername(), user.getRoles());
            return newUser;
        } else {
            throw new BadInvitationLinkException("bad.invitationLink");
        }
    }

 /*   @Transactional
    public void confirmUtilisateurAccount(String token) throws BadInvitationLinkException, NotFoundException {
        confirmUtilisateurAccount(token, null);
    }*/

    public void changePassword(String encryptedPassword, Utilisateur utilisateur) {
        utilisateurBdService.changePassword(encryptedPassword, utilisateur);
    }

    @Transactional
    public void changeUsername(UserNameChangeDto dto) throws NotFoundException, UnautorizedException, ConflitException {
        /*Optional<UUID> optUId = sessionService.currentUserId();
        UUID userId = optUId.orElseThrow(UnautorizedException::new);*/
        Utilisateur user = utilisateurBdService
                .findByUsername(dto.getUsername()).orElseThrow(UtilisateurNotFoundException::new);

        // Tentative de changement du nom d'utilisateur
        if (has(dto.getNewUsername()) && Objects.equals(user.getUsername(), dto.getUsername())) {
            // L'utilisateur a bien saisi l'ancien nom d'utilisateur
            utilisateurBdService.checkDuplicateUserName(dto.getNewUsername(), user.getId());
            utilisateurBdService.changeUsername(user.getId(), dto.getNewUsername());
        }
    }

    @Transactional
    public void changePassword(PasswordChangeDto dto) throws NotFoundException, BadConfirmPasswordException, UnautorizedException {
        /*Optional<UUID> optUId = sessionService.currentUserId();
        UUID userId = optUId.orElseThrow(UnautorizedException::new);*/
        Utilisateur user = utilisateurBdService
                .findByUsername(dto.getUsername()).orElseThrow(UtilisateurNotFoundException::new);
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

    //    @Transactional
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
