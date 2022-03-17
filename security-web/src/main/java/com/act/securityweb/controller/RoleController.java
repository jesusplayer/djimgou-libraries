/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.act.securityweb.controller;

import com.act.core.exception.NotFoundException;
import com.act.security.core.exceptions.RoleNotFoundException;
import com.act.security.core.model.Role;
import com.act.security.core.model.dto.role.RoleDto;
import com.act.security.core.model.dto.role.RoleFilterDto;
import com.act.security.core.model.dto.role.RoleFindDto;
import com.act.security.core.service.PrivilegeService;
import com.act.security.core.service.RoleService;
import com.act.security.core.service.SecuritySessionService;
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
    public Role create(@RequestBody @Valid RoleDto roleDto) throws NotFoundException {
        return roleService.createRole(roleDto);
    }

    @PutMapping("/modifier/{roleId}")
    public Role update(
            @PathVariable("roleId") final UUID id, @RequestBody @Valid final RoleDto roleDto) throws NotFoundException {
        return roleService.saveRole(id, roleDto);
    }

    @PutMapping("/ajouterPrivilege/{roleId}")
    public Role update(
            @PathVariable("roleId") final UUID roleId, @RequestBody() final UUID privilegeId) throws NotFoundException {
        return roleService.addPrivilege(roleId, privilegeId);
    }

    @PutMapping("/ajouterPrivileges/{roleId}")
    public Role update(
            @PathVariable("roleId") final UUID roleId, @RequestBody() final List<UUID> privilegeIds) throws NotFoundException {
        return roleService.addPrivileges(roleId, privilegeIds);
    }

    @GetMapping("/detail/{roleId}")
    public Role findById(@PathVariable("roleId") UUID id) throws NotFoundException {
        return roleService.findById(id)
                .orElseThrow(RoleNotFoundException::new);
    }

    @DeleteMapping("supprimer/{roleId}")
    public void delete(@PathVariable("roleId") UUID roleId) throws Exception {
        roleService.deleteById(roleId);
    }

    @GetMapping("/")
    public Collection<Role> findRoles() {
        return roleService.findAll();
    }

    @GetMapping("/list")
    public Page<Role> listRoles(Pageable pageable) {
        return roleService.findAll(pageable);
    }

    @GetMapping("/filter")
    public Page<Role> filterRoles(RoleFilterDto roleFilterDto) throws Exception {
        return roleService.findBy(roleFilterDto);
    }

    @GetMapping("/search")
    public List<Role> searchRoles(RoleFindDto roleFindDto) throws Exception {
        return roleService.search(roleFindDto).hits();
    }

    @GetMapping("/find")
    public Page<Role> findRoles(RoleFindDto roleFindDto) throws Exception {
        return roleService.searchPageable2(roleFindDto);
    }


}
