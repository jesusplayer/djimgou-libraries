package com.djimgou.tenantmanagerweb.controller;

import com.djimgou.core.exception.NotFoundException;
import com.djimgou.tenantmanager.exceptions.PaysNotFoundException;
import com.djimgou.tenantmanager.model.Pays;
import com.djimgou.tenantmanager.model.dto.pays.PaysDto;
import com.djimgou.tenantmanager.model.dto.pays.PaysFilterDto;
import com.djimgou.tenantmanager.model.dto.pays.PaysFindDto;

import com.djimgou.tenantmanager.service.PaysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/*https://www.baeldung.com/spring-rest-openapi-documentation
https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
*/
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/pays")
public class PaysController {

    private PaysService paysService;

    @Autowired
    public PaysController(PaysService paysService) {
        this.paysService = paysService;
    }

    @PostMapping("/creer")
    public Pays create(@RequestBody @Valid PaysDto paysDto) throws PaysNotFoundException {
        return paysService.createPays(paysDto);
    }

    @PutMapping("/modifier/{paysId}")
    public Pays update(
            @PathVariable("paysId") final UUID paysId, @RequestBody @Valid final PaysDto agentDto) throws PaysNotFoundException {
        return paysService.savePays(paysId, agentDto);
    }

    @GetMapping("/detail/{paysId}")
    public Pays findById(@PathVariable("paysId") UUID paysId) throws NotFoundException {
        return paysService.findById(paysId)
                .orElseThrow(PaysNotFoundException::new);
    }

    @DeleteMapping("supprimer/{paysId}")
    public void delete(@PathVariable("paysId") UUID paysId) throws Exception {
        paysService.deleteById(paysId);
    }

    @GetMapping("/")
    public Collection<Pays> findPayss() {
        return paysService.findAll();
    }

    @GetMapping("/list")
    public Page<Pays> listPayss(@Valid Pageable pageable) {
        return paysService.findAll(pageable);
    }

    @GetMapping("/filter")
    public Page<Pays> filterPayss(@Valid PaysFilterDto paysFilterDto) throws Exception {
        return paysService.findBy(paysFilterDto);
    }

    @GetMapping("/search")
    public List<Pays> searchPayss(@Valid PaysFindDto agentFindDto) {
        return paysService.search(agentFindDto).hits();
    }

    @GetMapping("/find")
    public Page<Pays> findPayss(@Valid PaysFindDto agentFindDto) throws Exception {
        return paysService.searchPageable2(agentFindDto);
    }


}