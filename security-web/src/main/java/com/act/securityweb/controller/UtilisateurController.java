/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.act.securityweb.controller;

import com.act.security.exceptions.UtilisateurNotFoundException;
import com.act.security.model.Role;
import com.act.security.model.StatutSecurityWorkflow;
import com.act.security.model.Utilisateur;
import com.act.security.model.dto.utilisateur.ModifierProfilDto;
import com.act.security.model.dto.utilisateur.UtilisateurDto;
import com.act.security.model.dto.utilisateur.UtilisateurFilterDto;
import com.act.security.model.dto.utilisateur.UtilisateurFindDto;
import com.act.security.service.AuthenticationService;
import com.act.security.service.RoleService;
import com.act.security.service.SessionServiceImpl;
import com.act.security.service.UtilisateurBdServiceBase;
import com.act.security.listeners.SessionManager;
import com.act.core.util.AppUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

import static com.act.core.util.AppUtils.has;

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
    Boolean refreshList = Boolean.TRUE;
    List<Item<Role>> authoritiesItems = new ArrayList<>();
    //List<Authority> selectedItems = new ArrayList<>();
    List<Role> toRemove = new ArrayList<>();

    @Autowired
    AppUtils appUtils;

    List<StatutSecurityWorkflow> statutCreations = Arrays.asList(StatutSecurityWorkflow.values());

    @Autowired
    RoleService authorityBdService;

/*    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;*/


    @Autowired
    SessionServiceImpl sessionService;

    @Autowired
    SessionManager sessionManager;

    @Autowired
    AuthenticationService authenticationService;

    @Qualifier("appDefaultUtilisateurService")
    @Autowired
    UtilisateurBdServiceBase<Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto> utilisateurBdService;

    @Qualifier("appUtilisateurService")
    @Autowired(required = false)
    UtilisateurBdServiceBase<? extends Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto> customBdService;

    //@FieldFilterSetting(className = Utilisateur.class, fields = {"password"})
    @SneakyThrows
    @PostMapping("/creer")
    @ResponseStatus(HttpStatus.CREATED)
    public Utilisateur create(@RequestBody @Valid UtilisateurDto utilisateurDto) {
        return  getService().createUtilisateur(utilisateurDto);
    }

    @SneakyThrows
    @PutMapping("/modifier/{utilisateurId}")
    @ResponseStatus(HttpStatus.OK)
    public Utilisateur update(
            @PathVariable("utilisateurId") final UUID id, @RequestBody @Valid final UtilisateurDto utilisateurDto) {
        return  getService().saveUtilisateur(id, utilisateurDto);
    }

    @SneakyThrows
    @PutMapping("/ajouterTenant/{utilisateurId}")
    @ResponseStatus(HttpStatus.OK)
    public Utilisateur addTenant(
            @PathVariable("utilisateurId") final UUID id, @RequestParam("tenantId") UUID tenantId) {
        return  getService().addTenant(id, tenantId);
    }

    @GetMapping("/detail/{utilisateurId}")
    @SneakyThrows
    public Utilisateur findById(@PathVariable("utilisateurId") UUID id) {
        return  getService().findById(id)
                .orElseThrow(UtilisateurNotFoundException::new);
    }

    @GetMapping("/actifs")
    @SneakyThrows
    public Page<Utilisateur> displayActifs(Pageable pageable) {
        return  getService().getRepo().findByEnabledTrue(pageable);
    }

    @GetMapping("/inactifs")
    @SneakyThrows
    public Page<Utilisateur> displayIActifs(Pageable pageable) {
        return  getService().getRepo().findByEnabledFalse(pageable);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Utilisateur> findUtilisateurs() {
        return  getService().findAll();
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public Page<Utilisateur> listUtilisateurs(Pageable pageable) {
        return  getService().findAll(pageable);
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public Page<Utilisateur> filterUtilisateurs(UtilisateurFilterDto utilisateurFilterDto) throws Exception {
        //utilisateurService.findByDto()
        return  getService().findBy(utilisateurFilterDto);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Utilisateur> searchUtilisateurs(UtilisateurFindDto utilisateurFindDto) throws Exception {
        //utilisateurService.findByDto()
        return  getService().search(utilisateurFindDto).hits();
    }

    @GetMapping("/find")
    @ResponseStatus(HttpStatus.OK)
    public Page<Utilisateur> findUtilisateurs(UtilisateurFindDto utilisateurFindDto) throws Exception {
        //utilisateurService.findByDto()
        return  getService().searchPageable2(utilisateurFindDto);
    }

    public UtilisateurBdServiceBase<Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto>
    getService() {
        return AppUtils.has(customBdService) ? (UtilisateurBdServiceBase<Utilisateur, UtilisateurFindDto, UtilisateurFilterDto, UtilisateurDto, ModifierProfilDto>) customBdService : utilisateurBdService;
    }

}
