/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.act.securityweb.controller;

import com.act.core.exception.ConflitException;
import com.act.security.core.model.Utilisateur;
import com.act.security.core.model.dto.utilisateur.*;
import com.act.security.core.service.AuthenticationService;
import com.act.security.core.exceptions.BadConfirmPasswordException;
import com.act.security.core.exceptions.UnautorizedException;
import com.act.security.core.exceptions.UtilisateurNotFoundException;
import com.act.core.model.enums.SessionKeys;
import com.act.core.exception.NotFoundException;
import com.act.security.core.service.SecuritySessionService;
import com.act.security.core.service.UtilisateurBdServiceBase;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
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
    @Qualifier("appDefaultUtilisateurService")
    @Autowired
    UtilisateurBdServiceBase<Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto> utilisateurBdService;

    @Autowired
    SecuritySessionService sessionService;

    @Autowired
    AuthenticationService authenticationService;

    @SneakyThrows
    @PutMapping("/profil/modifier")
    @ResponseStatus(HttpStatus.OK)
    public Utilisateur update(@RequestBody @Valid final ModifierProfilDto utilisateurDto) {
            return utilisateurBdService.modifierProfil(utilisateurDto);
    }

    @SneakyThrows
    @PostMapping("/profil/creer")
    @ResponseStatus(HttpStatus.OK)
    public Utilisateur updateProfil(@RequestBody @Valid final UtilisateurDto utilisateurDto) {
        Utilisateur user = utilisateurBdService.createCompteUtilisateur(utilisateurDto);
        authenticationService.inviteUser(user);
        return user;
    }

    @PutMapping("/changerNomUtilisateur")
    @ResponseStatus(HttpStatus.OK)
    public void changeusername(@RequestBody @Valid UserNameChangeDto logininfo) throws NotFoundException, UnautorizedException, ConflitException {
        authenticationService.changeUsername(logininfo);
    }

    @PutMapping("/changerMotDePasse")
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@RequestBody @Valid PasswordChangeDto logininfo) throws NotFoundException, BadConfirmPasswordException, UnautorizedException {
        authenticationService.changePassword(logininfo);
    }

    @PutMapping("/applyUserSession/{utilisateurId}")
    @ResponseStatus(HttpStatus.OK)
    public void applyUserSession(@PathVariable("utilisateurId") final UUID id, HttpSession httpSession) throws NotFoundException, BadConfirmPasswordException {
        utilisateurBdService.findById(id).orElseThrow(UtilisateurNotFoundException::new);
        httpSession.setAttribute(SessionKeys.CONNECTED_USER_ID, id.toString());
    }

    @SneakyThrows
    @PostMapping("/inviter/{utilisateurId}")
    @ResponseStatus(HttpStatus.OK)
    public void invite(@PathVariable("utilisateurId") final UUID id) {
        Utilisateur user = utilisateurBdService.getRepo().
                findById(id).orElseThrow(UtilisateurNotFoundException::new);
        authenticationService.inviteUser(user);
    }

    @SneakyThrows
    @PostMapping("/inviterParEmail/{utilisateurEmail}")
    @ResponseStatus(HttpStatus.OK)
    public void inviteByEmail(@PathVariable("utilisateurEmail") final String utilisateurEmail) {
        Utilisateur user = utilisateurBdService.getRepo().
                findOneByEmail(utilisateurEmail).orElseThrow(UtilisateurNotFoundException::new);
        authenticationService.inviteUser(user);
    }

    @SneakyThrows
    @GetMapping("/confirmerInvitation/{token}")
    @ResponseStatus(HttpStatus.OK)
    public void confirmInvitaion(@PathVariable("token") final String token, @RequestParam(value = "password", required = false) String password) {
        authenticationService.confirmUtilisateurAccount(token, password);
    }

    @SneakyThrows
    @PostMapping("/activer/{utilisateurId}")
    @ResponseStatus(HttpStatus.OK)
    public void activateProfil(@PathVariable("utilisateurId") final UUID utilisateurId) {
        utilisateurBdService.activer(utilisateurId);
    }

    @SneakyThrows
    @PostMapping("/desactiver/{utilisateurId}")
    @ResponseStatus(HttpStatus.OK)
    public void desactivateProfil(@PathVariable("utilisateurId") final UUID utilisateurId) {
        utilisateurBdService.desactiver(utilisateurId);
    }

}
