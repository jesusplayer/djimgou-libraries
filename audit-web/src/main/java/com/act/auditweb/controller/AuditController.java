/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.act.auditweb.controller;

import com.act.audit.exceptions.AuditNotFoundException;
import com.act.audit.model.Audit;
import com.act.audit.model.dto.AuditFilterDto;
import com.act.audit.model.dto.AuditFindDto;
import com.act.audit.service.AuditBdService;
import com.act.core.exception.NotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    public Audit findById(@PathVariable("auditId") UUID id) throws NotFoundException {
        return auditService.findById(id)
                .orElseThrow(AuditNotFoundException::new);
    }

    @DeleteMapping("supprimer/{auditId}")
    public void delete(@PathVariable("auditId") UUID auditId) throws Exception {
        auditService.deleteById(auditId);
    }

    @GetMapping("/")
    public Collection<Audit> findAudits() {
        return auditService.findAll();
    }

    @GetMapping("/list")
    public Page<Audit> listAudits(Pageable pageable) {
        return auditService.findAll(pageable);
    }

    @GetMapping("/filter")
    public Page<Audit> filterAudits(AuditFilterDto auditFilterDto) throws Exception {
        //auditService.findByDto()
        return auditService.findBy(auditFilterDto);
    }

    @GetMapping("/search")
    public List<Audit> searchAudits(AuditFindDto auditFindDto) throws Exception {
        //auditService.findByDto()
        return auditService.search(auditFindDto).hits();
    }

    @GetMapping("/find")
    public Page<Audit> findAudits(AuditFindDto auditFindDto) throws Exception {
        //auditService.findByDto()
        return auditService.searchPageable(auditFindDto);
    }


}
