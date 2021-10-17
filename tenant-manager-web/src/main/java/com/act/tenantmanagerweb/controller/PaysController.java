package com.act.tenantmanagerweb.controller;

import com.act.core.exception.NotFoundException;
import com.act.tenantmanager.exceptions.PaysNotFoundException;
import com.act.tenantmanager.model.Pays;
import com.act.tenantmanager.model.dto.pays.PaysDto;
import com.act.tenantmanager.model.dto.pays.PaysFilterDto;
import com.act.tenantmanager.model.dto.pays.PaysFindDto;

import com.act.tenantmanager.service.PaysService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

    @Autowired
    PaysService paysService;


    @PostMapping("/creer")
    @ResponseStatus(HttpStatus.CREATED)
    public Pays create(@RequestBody @Valid PaysDto paysDto) {
        return paysService.createPays(paysDto);
    }

    @SneakyThrows
    @PutMapping("/modifier/{paysId}")
    @ResponseStatus(HttpStatus.OK)
    public Pays update(
            @PathVariable("paysId") final UUID paysId, @RequestBody @Valid final PaysDto agentDto) {
        return paysService.savePays(paysId, agentDto);
    }

    @GetMapping("/detail/{paysId}")
    public Pays findById(@PathVariable("paysId") UUID paysId) throws NotFoundException {
        return paysService.findById(paysId)
                .orElseThrow(PaysNotFoundException::new);
    }

    @DeleteMapping("supprimer/{paysId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("paysId") UUID paysId) throws Exception {
        paysService.deleteById(paysId);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Pays> findPayss() {
        return paysService.findAll();
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public Page<Pays> listPayss(@Valid Pageable pageable) {
        return paysService.findAll(pageable);
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public Page<Pays> filterPayss(@Valid PaysFilterDto paysFilterDto) throws Exception {
        return paysService.findBy(paysFilterDto);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Pays> searchPayss(@Valid PaysFindDto agentFindDto) throws Exception {
        return paysService.search(agentFindDto).hits();
    }

    @GetMapping("/find")
    @ResponseStatus(HttpStatus.OK)
    public Page<Pays> findPayss(@Valid PaysFindDto agentFindDto) throws Exception {
        return paysService.searchPageable2(agentFindDto);
    }


}
