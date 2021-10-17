/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.act.securityweb.controller;

import com.act.security.exceptions.RoleNotFoundException;
import com.act.security.model.Role;
import com.act.security.model.dto.role.RoleDto;
import com.act.security.model.dto.role.RoleFilterDto;
import com.act.security.model.dto.role.RoleFindDto;
import com.act.security.service.PrivilegeService;
import com.act.security.service.RoleService;
import com.act.security.service.SessionServiceImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

import static com.act.core.util.AppUtils.has;

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

    @Autowired
    SessionServiceImpl sessionService;
    @Autowired
    PrivilegeService privilegeService;
    @Autowired
    RoleService roleService;

    @Autowired
    ApplicationContext applicationContext;


    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @SneakyThrows
        @PostMapping("/creer")
    @ResponseStatus(HttpStatus.CREATED)
    public Role create(@RequestBody @Valid RoleDto roleDto) {
        return roleService.createRole(roleDto);
    }

    @SneakyThrows
    @PutMapping("/modifier/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public Role update(
            @PathVariable("roleId") final UUID id, @RequestBody @Valid final RoleDto roleDto) {
        return roleService.saveRole(id, roleDto);
    }

    @SneakyThrows
    @PutMapping("/ajouterPrivilege/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public Role update(
            @PathVariable("roleId") final UUID roleId, @RequestBody() final UUID privilegeId) {

        return roleService.addPrivilege(roleId, privilegeId);
    }

    @SneakyThrows
    @PutMapping("/ajouterPrivileges/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public Role update(
            @PathVariable("roleId") final UUID roleId, @RequestBody() final List<UUID>  privilegeIds) {
        return roleService.addPrivileges(roleId, privilegeIds);
    }

    @GetMapping("/detail/{roleId}")
    @SneakyThrows
    public Role findById(@PathVariable("roleId") UUID id) {
        return roleService.findById(id)
                .orElseThrow(RoleNotFoundException::new);
    }

    @DeleteMapping("supprimer/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("roleId") UUID roleId) throws Exception {
        roleService.deleteById(roleId);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Role> findRoles() {
        return roleService.findAll();
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public Page<Role> listRoles(Pageable pageable) {
        return roleService.findAll(pageable);
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public Page<Role> filterRoles(RoleFilterDto roleFilterDto) throws Exception {
        //roleService.findByDto()
        return roleService.findBy(roleFilterDto);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Role> searchRoles(RoleFindDto roleFindDto) throws Exception {
        //roleService.findByDto()
        return roleService.search(roleFindDto).hits();
    }

    @GetMapping("/find")
    @ResponseStatus(HttpStatus.OK)
    public Page<Role> findRoles(RoleFindDto roleFindDto) throws Exception {
        //roleService.findByDto()
        return roleService.searchPageable2(roleFindDto);
    }


}
