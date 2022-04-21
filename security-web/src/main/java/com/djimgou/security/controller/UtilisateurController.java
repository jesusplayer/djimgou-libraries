/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.djimgou.security.controller;

import com.djimgou.core.exception.ConflitException;
import com.djimgou.core.exception.NotFoundException;
import com.djimgou.core.util.AppUtils;
import com.djimgou.security.core.exceptions.BadConfirmPasswordException;
import com.djimgou.security.core.exceptions.UtilisateurNotFoundException;
import com.djimgou.security.core.model.Utilisateur;
import com.djimgou.security.core.model.dto.utilisateur.ModifierProfilDto;
import com.djimgou.security.core.model.dto.utilisateur.UtilisateurDto;
import com.djimgou.security.core.model.dto.utilisateur.UtilisateurFilterDto;
import com.djimgou.security.core.model.dto.utilisateur.UtilisateurFindDto;
import com.djimgou.tenantmanager.exceptions.TenantNotFoundException;
import com.djimgou.security.core.service.*;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.djimgou.core.util.AppUtils.has;

/**
 * Classe Manage Bean permettant l'Edition d'un secteur
 *
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */

@Log4j2
@Getter
@CrossOrigin(maxAge = 3600)
@RestController()
@RequestMapping("/utilisateur")
public class UtilisateurController {
    public static final String APP_UTILISATEUR_SERVICE = "appUtilisateurService";
    @Autowired
    ApplicationContext appContext;

    @Autowired
    RoleService authorityBdService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    SecuritySessionService sessionService;

    @Autowired
    SessionManager sessionManager;

    @Autowired
    AuthenticationService authenticationService;

    @Qualifier("appDefaultUtilisateurService")
    @Autowired
    UtilisateurBdServiceBase<Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto> utilisateurBdService;

    @Qualifier(APP_UTILISATEUR_SERVICE)
    @Autowired(required = false)
    UtilisateurBdServiceBase<? extends Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto> customBdService;

    //@FieldFilterSetting(className = Utilisateur.class, fields = {"password"})
    @PostMapping("/creer")
    public Utilisateur create(@RequestBody @Valid UtilisateurDto utilisateurDto) throws ConflitException, BadConfirmPasswordException, NotFoundException {
        //return  getService().createUtilisateur(utilisateurDto);
        utilisateurDto.setEncodedPasswd(bCryptPasswordEncoder.encode(utilisateurDto.getPasswordConfirm()));
        return getService().createCompteUtilisateur(utilisateurDto);
    }

    @PutMapping("/modifier/{utilisateurId}")
    public Utilisateur update(
            @PathVariable("utilisateurId") final UUID id, @RequestBody @Valid final UtilisateurDto utilisateurDto) throws ConflitException, BadConfirmPasswordException, NotFoundException {
        return getService().saveUtilisateur(id, utilisateurDto);
    }

    @PostMapping("/activer/{utilisateurId}")
    public void activateProfil(@PathVariable("utilisateurId") final UUID utilisateurId) {
        getService().activer(utilisateurId);
    }

    @PostMapping("/desactiver/{utilisateurId}")
    public void desactivateProfil(@PathVariable("utilisateurId") final UUID utilisateurId) {
        getService().desactiver(utilisateurId);
    }

    @PutMapping("/ajouterTenant/{utilisateurId}")
    public Utilisateur addTenant(
            @PathVariable("utilisateurId") final UUID id, @RequestParam("tenantId") UUID tenantId) throws UtilisateurNotFoundException, TenantNotFoundException {
        return getService().addTenant(id, tenantId);
    }

    @GetMapping("/detail/{utilisateurId}")
    public Utilisateur findById(@PathVariable("utilisateurId") UUID id) throws NotFoundException {
        return getService().findById(id)
                .orElseThrow(UtilisateurNotFoundException::new);
    }

    @GetMapping("/actifs")
    public Page<Utilisateur> displayActifs(Pageable pageable) {
        return getService().getRepo().findByEnabledTrue(pageable);
    }

    @GetMapping("/inactifs")
    public Page<Utilisateur> displayIActifs(Pageable pageable) {
        return getService().getRepo().findByEnabledFalse(pageable);
    }

    @GetMapping("/")
    public Collection<Utilisateur> findUtilisateurs() {
        return getService().findAll();
    }

    @GetMapping("/list")
    public Page<Utilisateur> listUtilisateurs(Pageable pageable) {
        return getService().findAll(pageable);
    }

    @GetMapping("/filter")
    public Page<Utilisateur> filterUtilisateurs(UtilisateurFilterDto utilisateurFilterDto) throws Exception {
        //utilisateurService.findByDto()
        return getService().findBy(utilisateurFilterDto);
    }

    @GetMapping("/search")
    public List<Utilisateur> searchUtilisateurs(UtilisateurFindDto utilisateurFindDto) throws Exception {
        return getService().search(utilisateurFindDto).hits();
    }

    @GetMapping("/find")
    public Page<Utilisateur> findUtilisateurs(UtilisateurFindDto utilisateurFindDto) throws Exception {
        return getService().searchPageable2(utilisateurFindDto);
    }

    public UtilisateurBdServiceBase<Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto>
    getService() {
        if(appContext.containsBean(APP_UTILISATEUR_SERVICE) && !has(customBdService)){
            customBdService = appContext.getBean(APP_UTILISATEUR_SERVICE,UtilisateurBdServiceBase.class);
        }
        return AppUtils.has(customBdService) ? (UtilisateurBdServiceBase<Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto>) customBdService : utilisateurBdService;
    }

}
