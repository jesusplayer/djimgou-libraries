/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.act.securityweb.controller;


import com.act.security.exceptions.PrivilegeNotFoundException;
import com.act.security.model.Privilege;
import com.act.security.model.dto.privilege.PrivilegeDto;
import com.act.security.model.dto.privilege.PrivilegeFilterDto;
import com.act.security.model.dto.privilege.PrivilegeFindDto;
import com.act.security.service.PrivilegeService;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;



/**
 * Classe Manage Bean permettant l'Edition d'un Rôle(Privilege)
 *
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Getter
@CrossOrigin(maxAge = 3600)
@RestController()
@RequestMapping("/privilege")
public class PrivilegeController {

    @Autowired
    PrivilegeService privilegeService;


    @SneakyThrows
    @PostMapping("/creer")
    @ResponseStatus(HttpStatus.CREATED)
    public Privilege create(@RequestBody @Valid PrivilegeDto privilegeDto) {
        return privilegeService.createPrivilege(privilegeDto);
    }

    @SneakyThrows
    @PutMapping("/modifier/{privilegeId}")
    @ResponseStatus(HttpStatus.OK)
    public Privilege update(
            @PathVariable("privilegeId") final UUID id, @RequestBody @Valid final PrivilegeDto privilegeDto) {
        return privilegeService.savePrivilege(id, privilegeDto);
    }

    @GetMapping("/detail/{privilegeId}")
    @SneakyThrows
    public Privilege findById(@PathVariable("privilegeId") UUID privilegeId) {
        return privilegeService.findById(privilegeId)
                .orElseThrow(PrivilegeNotFoundException::new);
    }

    @DeleteMapping("supprimer/{privilegeId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("privilegeId") UUID privilegeId) throws Exception {
        privilegeService.deleteById(privilegeId);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Privilege> findPrivileges() {
        return privilegeService.findAll();
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public Page<Privilege> listPrivileges(Pageable pageable) {
        return privilegeService.findAll(pageable);
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public Page<Privilege> filterPrivileges(PrivilegeFilterDto privilegeFilterDto) throws Exception {
        //privilegeService.findByDto()
        return privilegeService.findBy(privilegeFilterDto);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Privilege> searchPrivileges(PrivilegeFindDto privilegeFindDto) throws Exception {
        //privilegeService.findByDto()
        return privilegeService.search(privilegeFindDto).hits();
    }

    @GetMapping("/find")
    @ResponseStatus(HttpStatus.OK)
    public Page<Privilege> findPrivileges(PrivilegeFindDto privilegeFindDto) throws Exception {
        //privilegeService.findByDto()
        return privilegeService.searchPageable2(privilegeFindDto);
    }


}
