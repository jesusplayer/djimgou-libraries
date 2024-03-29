package com.djimgou.tenantmanagerweb.controller;

import com.djimgou.core.annotations.Endpoint;
import com.djimgou.core.exception.AppException;
import com.djimgou.core.exception.ConflitException;
import com.djimgou.session.enums.SessionKeys;
import com.djimgou.tenantmanager.exceptions.PaysNotFoundException;
import com.djimgou.tenantmanager.exceptions.TenantNotFoundException;
import com.djimgou.tenantmanager.exceptions.TenantSessionNotFoundException;
import com.djimgou.tenantmanager.model.Tenant;
import com.djimgou.tenantmanager.model.dto.tenant.TenantDto;
import com.djimgou.tenantmanager.model.dto.tenant.TenantFilterDto;
import com.djimgou.tenantmanager.model.dto.tenant.TenantFindDto;
import com.djimgou.tenantmanager.model.dto.tenant.TenantSessionDto;
import com.djimgou.tenantmanager.service.TenantService;
import com.djimgou.tenantmanager.service.TenantSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private TenantService tenantService;

    private TenantSessionService sessionService;

    @Autowired
    public TenantController(TenantService tenantService, TenantSessionService sessionService) {
        this.tenantService = tenantService;
        this.sessionService = sessionService;
    }

    @PostMapping("/creer")
    @Endpoint("Créer un tenant")
    public Tenant create(@RequestBody @Valid TenantDto tenantDto) throws TenantNotFoundException, PaysNotFoundException, ConflitException {
        return tenantService.createTenant(tenantDto);
    }

    @PutMapping("/modifier/{tenantId}")
    @Endpoint("Modifier un tenant")
    public Tenant update(
            @PathVariable("tenantId") final UUID tenantId, @RequestBody @Valid final TenantDto tenantDto) throws TenantNotFoundException, PaysNotFoundException, ConflitException {
        return tenantService.saveTenant(tenantId, tenantDto);
    }

    @GetMapping("/detail/{tenantId}")
    @Endpoint("Afficher le détail d'un tenant")
    public Tenant findById(@PathVariable("tenantId") UUID tenantId) throws TenantNotFoundException {
        return tenantService.findById(tenantId)
                .orElseThrow(TenantNotFoundException::new);
    }

    @DeleteMapping("supprimer/{tenantId}")
    @Endpoint("supprimer d'un tenant")
    public void delete(@PathVariable("tenantId") UUID tenantId) throws AppException {
        tenantService.deleteById(tenantId);
    }

    @GetMapping("/")
    @Endpoint("Lister tous les tenants")
    public Collection<Tenant> findTenants(HttpSession session) {
        return tenantService.findAll();
    }

    @PostMapping("/selectTenant/{tenantId}")
    @Endpoint("Affecter un tenant courrant à un utilisateur")
    public TenantSessionDto selectTenant(@PathVariable("tenantId") UUID tenantId, HttpSession session) throws TenantSessionNotFoundException {
        Tenant tenant = sessionService.putTenant(tenantId.toString()).get();
        TenantSessionDto dto = new TenantSessionDto();
        dto = (TenantSessionDto) tenant.toDto(dto);
        session.setAttribute(SessionKeys.TENANT_ID, tenantId.toString());
        return dto;
    }

    @GetMapping("/list")
    @Endpoint("Lister les tenants avec pagination")
    public Page<Tenant> listTenants(@Valid Pageable pageable) {
        return tenantService.findAll(pageable);
    }

    @GetMapping("/filter")
    @Endpoint("Filtrer les tenants avec pagination")
    public Page<Tenant> filterTenants(@Valid TenantFilterDto tenantFilterDto) throws Exception {
        return tenantService.findBy(tenantFilterDto);
    }

  /*  @GetMapping("/search")
    @Endpoint("Recherche sur les tenants")
    public List<Tenant> searchTenants(@Valid TenantFindDto tenantFindDto) {
        return tenantService.search(tenantFindDto).hits();
    }
*/
    @GetMapping("/find")
    @Endpoint("Recherche sur les tenants avec pagination")
    public Page<Tenant> findTenants(@Valid TenantFindDto tenantFindDto) throws Exception {
        return tenantService.searchPageable2(tenantFindDto);
    }

}
