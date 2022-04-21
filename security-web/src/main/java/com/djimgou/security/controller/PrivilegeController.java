/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.djimgou.security.controller;


import com.djimgou.core.exception.NotFoundException;
import com.djimgou.security.core.exceptions.PrivilegeNotFoundException;
import com.djimgou.security.core.exceptions.ReadOnlyException;
import com.djimgou.security.core.model.Privilege;
import com.djimgou.security.core.model.dto.privilege.PrivilegeDto;
import com.djimgou.security.core.model.dto.privilege.PrivilegeFilterDto;
import com.djimgou.security.core.model.dto.privilege.PrivilegeFindDto;
import com.djimgou.security.core.service.PrivilegeService;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


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

    private PrivilegeService privilegeService;

    public PrivilegeController(PrivilegeService privilegeService) {
        this.privilegeService = privilegeService;
    }

    @PostMapping("/creer")
    //@ResponseStatus(HttpStatus.CREATED)
    public Privilege create(@RequestBody @Valid PrivilegeDto privilegeDto) throws NotFoundException, ReadOnlyException {
        return privilegeService.createPrivilege(privilegeDto);
    }

    @PutMapping("/modifier/{privilegeId}")
    // @ResponseStatus(HttpStatus.OK)
    public Privilege update(
            @PathVariable("privilegeId") final UUID id, @RequestBody @Valid final PrivilegeDto privilegeDto) throws NotFoundException, ReadOnlyException {
        return privilegeService.savePrivilege(id, privilegeDto);
    }

    @GetMapping("/detail/{privilegeId}")
    public Privilege findById(@PathVariable("privilegeId") UUID privilegeId) throws NotFoundException {
        return privilegeService.findById(privilegeId)
                .orElseThrow(PrivilegeNotFoundException::new);
    }

    @DeleteMapping("supprimer/{privilegeId}")
    public void delete(@PathVariable("privilegeId") UUID privilegeId) throws NotFoundException, ReadOnlyException {
        Privilege p = privilegeService.findById(privilegeId).orElseThrow(PrivilegeNotFoundException::new);
        privilegeService.chackreadOnly(p);
        privilegeService.deleteById(privilegeId);
    }

    @GetMapping("/")
    // @ResponseStatus(HttpStatus.OK)
    public Collection<Privilege> findPrivileges() {
        return privilegeService.findAll();
    }

    @GetMapping("/list")
    // @ResponseStatus(HttpStatus.OK)
    public Page<Privilege> listPrivileges(Pageable pageable) {
        return privilegeService.findAll(pageable);
    }

    @GetMapping("/filter")
    // @ResponseStatus(HttpStatus.OK)
    public Page<Privilege> filterPrivileges(PrivilegeFilterDto privilegeFilterDto) throws Exception {
        //privilegeService.findByDto()
        return privilegeService.findBy(privilegeFilterDto);
    }

    @GetMapping("/search")
    // @ResponseStatus(HttpStatus.OK)
    public List<Privilege> searchPrivileges(PrivilegeFindDto privilegeFindDto) throws Exception {
        //privilegeService.findByDto()
        return privilegeService.search(privilegeFindDto).hits();
    }

    @GetMapping("/find")
    // @ResponseStatus(HttpStatus.OK)
    public Page<Privilege> findPrivileges(PrivilegeFindDto privilegeFindDto) throws Exception {
        //privilegeService.findByDto()
        return privilegeService.searchPageable2(privilegeFindDto);
    }


}
