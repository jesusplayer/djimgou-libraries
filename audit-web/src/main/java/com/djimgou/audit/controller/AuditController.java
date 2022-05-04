/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.djimgou.audit.controller;

import com.djimgou.audit.exceptions.AuditNotFoundException;
import com.djimgou.audit.model.Audit;
import com.djimgou.audit.model.dto.AuditFilterAdvDto;
import com.djimgou.audit.model.dto.AuditFilterDto;
import com.djimgou.audit.model.dto.AuditFindDto;
import com.djimgou.audit.service.AuditBdService;
import com.djimgou.core.annotations.Endpoint;
import com.djimgou.core.exception.NotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    AuditBdService auditService;


    @GetMapping("/detail/{auditId}")
    @Endpoint("Afficher le détail d'un audit")
    public Audit findById(@PathVariable("auditId") UUID id) throws NotFoundException {
        return auditService.findById(id)
                .orElseThrow(AuditNotFoundException::new);
    }

    @DeleteMapping("supprimer/{auditId}")
    @Endpoint("Supprimer un audit")
    public void delete(@PathVariable("auditId") UUID auditId) throws Exception {
        auditService.deleteById(auditId);
    }

    @GetMapping("/")
    @Endpoint("Lister tous les audits")
    public Collection<Audit> findAudits() {
        return auditService.findAll();
    }

    @GetMapping("/list")
    @Endpoint("Lister les audits avec pagination")
    public Page<Audit> listAudits(Pageable pageable) {
        return auditService.findAll(pageable);
    }

    @GetMapping("/filter")
    @Endpoint("Filtrer les audits avec pagination")
    public Page<Audit> filterAudits(AuditFilterDto auditFilterDto) throws Exception {
        //auditService.findByDto()
        return auditService.findBy(auditFilterDto);
    }

    @PostMapping("/advancedFilter")
    @Endpoint(value = "Filtre avancé des audits avec pagination", readOnlyMethod = true)
    public Page<Audit> filterAdvancedAudits(AuditFilterAdvDto auditFilterDto) throws Exception {
        return auditService.advancedFindBy(auditFilterDto);
    }

    @GetMapping("/search")
    @Endpoint("Recherche sur les audits")
    public List<Audit> searchAudits(AuditFindDto auditFindDto) throws Exception {
        //auditService.findByDto()
        return auditService.search(auditFindDto).hits();
    }

    @GetMapping("/find")
    @Endpoint("Recherche sur les audits avec pagination")
    public Page<Audit> findAudits(AuditFindDto auditFindDto) throws Exception {
        //auditService.findByDto()
        return auditService.searchPageable(auditFindDto);
    }


}
