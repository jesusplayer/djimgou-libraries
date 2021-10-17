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
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
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
    @SneakyThrows
    public Audit findById(@PathVariable("auditId") UUID id) {
        return auditService.findById(id)
                .orElseThrow(AuditNotFoundException::new);
    }

    @DeleteMapping("supprimer/{auditId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("auditId") UUID auditId) throws Exception {
        auditService.deleteById(auditId);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Audit> findAudits() {
        return auditService.findAll();
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public Page<Audit> listAudits(Pageable pageable) {
        return auditService.findAll(pageable);
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public Page<Audit> filterAudits(AuditFilterDto auditFilterDto) throws Exception {
        //auditService.findByDto()
        return auditService.findBy(auditFilterDto);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Audit> searchAudits(AuditFindDto auditFindDto) throws Exception {
        //auditService.findByDto()
        return auditService.search(auditFindDto).hits();
    }

    @GetMapping("/find")
    @ResponseStatus(HttpStatus.OK)
    public Page<Audit> findAudits(AuditFindDto auditFindDto) throws Exception {
        //auditService.findByDto()
        return auditService.searchPageable(auditFindDto);
    }


}
