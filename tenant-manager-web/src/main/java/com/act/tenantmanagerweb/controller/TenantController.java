package com.act.tenantmanagerweb.controller;

import com.act.tenantmanager.exceptions.TenantNotFoundException;
import com.act.tenantmanager.exceptions.TenantSessionNotFoundException;
import com.act.tenantmanager.model.Tenant;
import com.act.tenantmanager.model.dto.tenant.TenantDto;
import com.act.tenantmanager.model.dto.tenant.TenantFilterDto;
import com.act.tenantmanager.model.dto.tenant.TenantFindDto;
import com.act.tenantmanager.model.dto.tenant.TenantSessionDto;
import com.act.session.enums.SessionKeys;
import com.act.tenantmanager.service.TenantService;
import com.act.tenantmanager.service.TenantSessionService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/*https://www.baeldung.com/spring-rest-openapi-documentation
https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api
*/
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/tenant")
public class TenantController {

    @Autowired
    TenantService tenantService;

    @Autowired
    TenantSessionService sessionService;

    @PostMapping("/creer")
    @ResponseStatus(HttpStatus.CREATED)
    public Tenant create(@RequestBody @Valid TenantDto tenantDto) {
        return tenantService.createTenant(tenantDto);
    }

    @SneakyThrows
    @PutMapping("/modifier/{tenantId}")
    @ResponseStatus(HttpStatus.OK)
    public Tenant update(
            @PathVariable("tenantId") final UUID tenantId, @RequestBody @Valid final TenantDto tenantDto) {
        return tenantService.saveTenant(tenantId, tenantDto);
    }

    @GetMapping("/detail/{tenantId}")
    public Tenant findById(@PathVariable("tenantId") UUID tenantId) throws TenantNotFoundException {
        return tenantService.findById(tenantId)
                .orElseThrow(TenantNotFoundException::new);
    }

    @DeleteMapping("supprimer/{tenantId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("tenantId") UUID tenantId) throws Exception {
        tenantService.deleteById(tenantId);
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Tenant> findTenants(HttpSession session) {
        return tenantService.findAll();
    }

    @PostMapping("/selectTenant/{tenantId}")
    @ResponseStatus(HttpStatus.OK)
    public TenantSessionDto selectTenant(@PathVariable("tenantId") UUID tenantId, HttpSession session) throws TenantSessionNotFoundException {
        Tenant tenant = sessionService.putTenant(tenantId.toString()).get();
        TenantSessionDto dto = new TenantSessionDto();
        dto = (TenantSessionDto) tenant.toDto(dto);
        session.setAttribute(SessionKeys.TENANT_ID, tenantId.toString());
        return dto;
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public Page<Tenant> listTenants(@Valid Pageable pageable) {
        return tenantService.findAll(pageable);
    }

    @GetMapping("/filter")
    @ResponseStatus(HttpStatus.OK)
    public Page<Tenant> filterTenants(@Valid TenantFilterDto tenantFilterDto) throws Exception {
        return tenantService.findBy(tenantFilterDto);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Tenant> searchTenants(@Valid TenantFindDto tenantFindDto) throws Exception {
        return tenantService.search(tenantFindDto).hits();
    }

    @GetMapping("/find")
    @ResponseStatus(HttpStatus.OK)
    public Page<Tenant> findTenants(@Valid TenantFindDto tenantFindDto) throws Exception {
        return tenantService.searchPageable2(tenantFindDto);
    }


}
