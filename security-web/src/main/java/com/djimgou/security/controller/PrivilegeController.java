/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.djimgou.security.controller;


import com.djimgou.core.annotations.Endpoint;
import com.djimgou.core.exception.NotFoundException;
import com.djimgou.security.core.exceptions.PrivilegeNotFoundException;
import com.djimgou.security.core.exceptions.ReadOnlyException;
import com.djimgou.security.core.model.Privilege;
import com.djimgou.security.core.model.dto.privilege.PrivilegeDto;
import com.djimgou.security.core.model.dto.privilege.PrivilegeFilterDto;
import com.djimgou.security.core.model.dto.privilege.PrivilegeFindDto;
import com.djimgou.security.core.service.PrivilegeService;
import com.djimgou.security.enpoints.SecuredEndPoint;
import com.djimgou.security.enpoints.EndPointsRegistry;
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
    private EndPointsRegistry endPointsRegistry;

    public PrivilegeController(PrivilegeService privilegeService, EndPointsRegistry endPointsRegistry) {
        this.privilegeService = privilegeService;
        this.endPointsRegistry = endPointsRegistry;
    }

    @GetMapping("/endpoints")
    @Endpoint("Lister tous les privilèges disponibles dans l'application")
    public Collection<SecuredEndPoint> listEndPoint() {
        return endPointsRegistry.getEndpointsMap().values();
    }

    @PostMapping("/creer")
    @Endpoint("Créer un privilège")
    public Privilege create(@RequestBody @Valid PrivilegeDto privilegeDto) throws NotFoundException, ReadOnlyException {
        return privilegeService.createPrivilege(privilegeDto);
    }

    @PutMapping("/modifier/{privilegeId}")
    @Endpoint("Modifier un privilège")
    public Privilege update(
            @PathVariable("privilegeId") final UUID id, @RequestBody @Valid final PrivilegeDto privilegeDto) throws NotFoundException, ReadOnlyException {
        return privilegeService.savePrivilege(id, privilegeDto);
    }

    @GetMapping("/detail/{privilegeId}")
    @Endpoint("Afficher le détail d'un privilège")
    public Privilege findById(@PathVariable("privilegeId") UUID privilegeId) throws NotFoundException {
        return privilegeService.findById(privilegeId)
                .orElseThrow(PrivilegeNotFoundException::new);
    }

    @DeleteMapping("supprimer/{privilegeId}")
    @Endpoint("Supprimer un privilège")
    public void delete(@PathVariable("privilegeId") UUID privilegeId) throws NotFoundException, ReadOnlyException {
        Privilege p = privilegeService.findById(privilegeId).orElseThrow(PrivilegeNotFoundException::new);
        privilegeService.chackreadOnly(p);
        privilegeService.deleteById(privilegeId);
    }

    @GetMapping("/")
    @Endpoint("Lister tous les privilèges")
    public Collection<Privilege> findPrivileges() {
        return privilegeService.findAll();
    }

    @GetMapping("/list")
    @Endpoint("Lister les privilèges avec pagination")
    public Page<Privilege> listPrivileges(Pageable pageable) {
        return privilegeService.findAll(pageable);
    }

    @GetMapping("/filter")
    @Endpoint("Filtrer les privilèges avec pagination")
    public Page<Privilege> filterPrivileges(PrivilegeFilterDto privilegeFilterDto) throws Exception {
        //privilegeService.findByDto()
        return privilegeService.findBy(privilegeFilterDto);
    }

    @GetMapping("/search")
    @Endpoint("Recherche sur les privilèges")
    public List<Privilege> searchPrivileges(PrivilegeFindDto privilegeFindDto) throws Exception {
        //privilegeService.findByDto()
        return privilegeService.search(privilegeFindDto).hits();
    }

    @GetMapping("/find")
    @Endpoint("Recherche sur les privilèges avec pagination")
    public Page<Privilege> findPrivileges(PrivilegeFindDto privilegeFindDto) throws Exception {
        //privilegeService.findByDto()
        return privilegeService.searchPageable2(privilegeFindDto);
    }


}
