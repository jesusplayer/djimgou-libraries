/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.djimgou.security.controller;

import com.djimgou.core.annotations.Endpoint;
import com.djimgou.core.exception.ConflitException;
import com.djimgou.core.exception.NotFoundException;
import com.djimgou.security.core.exceptions.*;
import com.djimgou.security.core.model.Utilisateur;
import com.djimgou.security.core.model.dto.utilisateur.*;
import com.djimgou.security.core.service.AuthenticationService;
import com.djimgou.security.core.service.SecuritySessionService;
import com.djimgou.security.core.service.UtilisateurBdServiceBase;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

import static com.djimgou.core.util.AppUtils.has;

/**
 * Classe Manage Bean permettant l'Edition d'un secteur
 *
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Getter
@Log4j2
@CrossOrigin(maxAge = 3600)
@RestController()
@RequestMapping("/compteUtilisateur")
public class CompteUtilisateurController {
    private UtilisateurBdServiceBase<Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto> utilisateurBdService;

    private SecuritySessionService sessionService;

    private AuthenticationService authenticationService;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public CompteUtilisateurController(@Qualifier("appDefaultUtilisateurService") UtilisateurBdServiceBase<Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto> utilisateurBdService, SecuritySessionService sessionService, AuthenticationService authenticationService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.utilisateurBdService = utilisateurBdService;
        this.sessionService = sessionService;
        this.authenticationService = authenticationService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @PutMapping("/profil/modifier")
    @Endpoint("Modifier un profil utilisateur")
    public Utilisateur update(@RequestBody @Valid final ModifierProfilDto utilisateurDto) throws ConflitException, UnautorizedException, NotFoundException {
        return utilisateurBdService.modifierProfil(utilisateurDto);
    }


    @PostMapping("/profil/creer")
    @Endpoint("Créer un profil utilisateur")
    public Utilisateur updateProfil(@RequestBody @Valid final UtilisateurDto utilisateurDto) throws UtilisateurConfiltException, ConflitException, BadConfirmPasswordException, NotFoundException {
        utilisateurDto.setEncodedPasswd(bCryptPasswordEncoder.encode(utilisateurDto.getPasswordConfirm()));
        Utilisateur user = utilisateurBdService.createCompteUtilisateur(utilisateurDto);
        authenticationService.inviteUser(user);
        return user;
    }

    @PutMapping("/changerNomUtilisateur")
    @Endpoint("Changer le nom d'utilisateur")
    public void changeusername(@RequestBody @Valid UserNameChangeDto logininfo) throws NotFoundException, UnautorizedException, ConflitException {
        authenticationService.changeUsername(logininfo);
    }

    @PutMapping("/changerMotDePasse")
    @Endpoint("Changer le mot de passe")
    public void changePassword(@RequestBody @Valid PasswordChangeDto logininfo) throws NotFoundException, BadConfirmPasswordException, UnautorizedException {
        authenticationService.changePassword(logininfo);
    }

/*    @PutMapping("/applyUserSession/{utilisateurId}")
    // @ResponseStatus(HttpStatus.OK)
    public void applyUserSession(@PathVariable("utilisateurId") final UUID id, HttpSession httpSession) throws NotFoundException, BadConfirmPasswordException {
        utilisateurBdService.findById(id).orElseThrow(UtilisateurNotFoundException::new);
        httpSession.setAttribute(SessionKeys.CONNECTED_USER_ID, id.toString());
    }*/


    @PostMapping("/inviter/{utilisateurId}")
    @Endpoint("Inviter un utilisateur par son Id")
    public void invite(@PathVariable("utilisateurId") final UUID id) throws UtilisateurNotFoundException, UtilisateurConfiltException {
        Utilisateur user = utilisateurBdService.getRepo().
                findById(id).orElseThrow(UtilisateurNotFoundException::new);
        authenticationService.inviteUser(user);
    }


    @PostMapping("/inviterParEmail/{utilisateurEmail}")
    @Endpoint("Inviter un utilisateur par son email")
    public void inviteByEmail(@PathVariable("utilisateurEmail") final String utilisateurEmail) throws UtilisateurConfiltException, UtilisateurNotFoundException {
        Utilisateur user = utilisateurBdService.getRepo().
                findOneByEmail(utilisateurEmail).orElseThrow(UtilisateurNotFoundException::new);
        authenticationService.inviteUser(user);
    }


    @GetMapping("/confirmerInvitation/{token}")
    @Endpoint("Confirmer l'invitation d'un utilisateur par son token")
    public Utilisateur confirmInvitation(@PathVariable("token") final String token, @RequestParam(value = "password", required = false) String password) throws BadInvitationLinkException, NotFoundException {
        String encPassword = null;
        if(has(password)){
            encPassword = bCryptPasswordEncoder.encode(password);
        }
        return authenticationService.confirmUtilisateurAccount(token, password, encPassword);
    }

}
