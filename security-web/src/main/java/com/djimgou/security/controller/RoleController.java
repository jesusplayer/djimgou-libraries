/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.djimgou.security.controller;

import com.djimgou.core.annotations.Endpoint;
import com.djimgou.core.exception.ConflitException;
import com.djimgou.core.exception.NotFoundException;
import com.djimgou.security.core.exceptions.RoleNotFoundException;
import com.djimgou.security.core.model.Role;
import com.djimgou.security.core.model.dto.role.RoleDto;
import com.djimgou.security.core.model.dto.role.RoleFilterDto;
import com.djimgou.security.core.model.dto.role.RoleFindDto;
import com.djimgou.security.core.service.PrivilegeService;
import com.djimgou.security.core.service.RoleService;
import com.djimgou.security.core.service.SecuritySessionService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Classe Manage Bean permettant l'Edition d'un Rôle(Authority)
 *
 * @author djimgou
 */
@Getter
@Setter
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/role")
public class RoleController {

    private SecuritySessionService sessionService;
    private PrivilegeService privilegeService;
    private RoleService roleService;

    private ApplicationContext applicationContext;


    private ApplicationEventPublisher applicationEventPublisher;


    public RoleController(SecuritySessionService sessionService, PrivilegeService privilegeService, RoleService roleService, ApplicationContext applicationContext, ApplicationEventPublisher applicationEventPublisher) {
        this.sessionService = sessionService;
        this.privilegeService = privilegeService;
        this.roleService = roleService;
        this.applicationContext = applicationContext;
        this.applicationEventPublisher = applicationEventPublisher;
    }


    @PostMapping("/creer")
    @Endpoint("Créer un rôle")
    public Role create(@RequestBody @Valid RoleDto roleDto) throws NotFoundException, ConflitException {
        return roleService.createRole(roleDto);
    }

    @PutMapping("/modifier/{roleId}")
    @Endpoint("Modifier un rôle")
    public Role update(
            @PathVariable("roleId") final UUID id, @RequestBody @Valid final RoleDto roleDto) throws NotFoundException, ConflitException {
        return roleService.saveRole(id, roleDto);
    }

    @PutMapping("/ajouterPrivilege/{roleId}")
    @Endpoint("Ajouter un privilège pour sur rôle")
    public Role update(
            @PathVariable("roleId") final UUID roleId, @RequestBody() final UUID privilegeId) throws NotFoundException {
        return roleService.addPrivilege(roleId, privilegeId);
    }

    @PutMapping("/ajouterPrivileges/{roleId}")
    @Endpoint("Ajouter plusieurs privilèges sur un rôle")
    public Role update(
            @PathVariable("roleId") final UUID roleId, @RequestBody() final List<UUID> privilegeIds) throws NotFoundException {
        return roleService.addPrivileges(roleId, privilegeIds);
    }

    @GetMapping("/detail/{roleId}")
    @Endpoint("Afficher le détail d'un rôle")
    public Role findById(@PathVariable("roleId") UUID id) throws NotFoundException {
        return roleService.findById(id)
                .orElseThrow(RoleNotFoundException::new);
    }

    @DeleteMapping("supprimer/{roleId}")
    @Endpoint("supprimer d'un rôle")
    public void delete(@PathVariable("roleId") UUID roleId) throws Exception {
        roleService.deleteById(roleId);
    }

    @GetMapping("/")
    @Endpoint("Lister tous les rôles")
    public Collection<Role> findRoles() {
        return roleService.findAll();
    }

    @GetMapping("/list")
    @Endpoint("Lister les rôles avec pagination")
    public Page<Role> listRoles(Pageable pageable) {
        return roleService.findAll(pageable);
    }

    @GetMapping("/filter")
    @Endpoint("Filtrer les rôles avec pagination")
    public Page<Role> filterRoles(RoleFilterDto roleFilterDto) throws Exception {
        return roleService.findBy(roleFilterDto);
    }

    @GetMapping("/search")
    @Endpoint("Recherche sur les rôles")
    public List<Role> searchRoles(RoleFindDto roleFindDto) throws Exception {
        return roleService.search(roleFindDto).hits();
    }

    @GetMapping("/find")
    @Endpoint("Recherche sur les rôles avec pagination")
    public Page<Role> findRoles(RoleFindDto roleFindDto) throws Exception {
        return roleService.searchPageable2(roleFindDto);
    }


}
