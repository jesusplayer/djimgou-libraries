/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.act.securityweb.controller;

import com.act.core.exception.ConflitException;
import com.act.security.core.exceptions.*;
import com.act.security.core.model.Utilisateur;
import com.act.security.core.model.dto.utilisateur.*;
import com.act.security.core.service.AuthenticationService;
import com.act.core.exception.NotFoundException;
import com.act.security.core.service.SecuritySessionService;
import com.act.security.core.service.UtilisateurBdServiceBase;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

import static com.act.core.util.AppUtils.has;

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
    // @ResponseStatus(HttpStatus.OK)
    public Utilisateur update(@RequestBody @Valid final ModifierProfilDto utilisateurDto) throws ConflitException, UnautorizedException, NotFoundException {
        return utilisateurBdService.modifierProfil(utilisateurDto);
    }


    @PostMapping("/profil/creer")
    // @ResponseStatus(HttpStatus.OK)
    public Utilisateur updateProfil(@RequestBody @Valid final UtilisateurDto utilisateurDto) throws UtilisateurConfiltException, ConflitException, BadConfirmPasswordException, NotFoundException {
        utilisateurDto.setEncodedPasswd(bCryptPasswordEncoder.encode(utilisateurDto.getPasswordConfirm()));
        Utilisateur user = utilisateurBdService.createCompteUtilisateur(utilisateurDto);
        authenticationService.inviteUser(user);
        return user;
    }

    @PutMapping("/changerNomUtilisateur")
    // @ResponseStatus(HttpStatus.OK)
    public void changeusername(@RequestBody @Valid UserNameChangeDto logininfo) throws NotFoundException, UnautorizedException, ConflitException {
        authenticationService.changeUsername(logininfo);
    }

    @PutMapping("/changerMotDePasse")
    // @ResponseStatus(HttpStatus.OK)
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
    // @ResponseStatus(HttpStatus.OK)
    public void invite(@PathVariable("utilisateurId") final UUID id) throws UtilisateurNotFoundException, UtilisateurConfiltException {
        Utilisateur user = utilisateurBdService.getRepo().
                findById(id).orElseThrow(UtilisateurNotFoundException::new);
        authenticationService.inviteUser(user);
    }


    @PostMapping("/inviterParEmail/{utilisateurEmail}")
    // @ResponseStatus(HttpStatus.OK)
    public void inviteByEmail(@PathVariable("utilisateurEmail") final String utilisateurEmail) throws UtilisateurConfiltException, UtilisateurNotFoundException {
        Utilisateur user = utilisateurBdService.getRepo().
                findOneByEmail(utilisateurEmail).orElseThrow(UtilisateurNotFoundException::new);
        authenticationService.inviteUser(user);
    }


    @GetMapping("/confirmerInvitation/{token}")
    // @ResponseStatus(HttpStatus.OK)
    public void confirmInvitaion(@PathVariable("token") final String token, @RequestParam(value = "password", required = false) String password) throws BadInvitationLinkException, NotFoundException {
        authenticationService.confirmUtilisateurAccount(token, password, bCryptPasswordEncoder.encode(password));
    }

}
