package com.act.tenantmanager.service;

import com.act.tenantmanager.exceptions.TenantSessionNotFoundException;
import com.act.tenantmanager.model.Tenant;
import com.act.tenantmanager.model.dto.tenant.TenantSessionDto;
import com.act.tenantmanager.repository.TenantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

import static com.act.core.util.AppUtils.has;

@Service
public class TenantSessionService {
    private static HashMap<String, Tenant> tenantMap = new HashMap<>();


    private TenantRepo tenantRepo;

    @Autowired(required = false)
    public TenantSessionService(TenantRepo tenantRepo) {
        this.tenantRepo = tenantRepo;
    }

    public synchronized Optional<Tenant> putTenant(String tenantId) throws TenantSessionNotFoundException {
        if (!has(tenantId)) {
            throw new TenantSessionNotFoundException();
        }
        if (!tenantMap.containsKey(tenantId)) {
            Tenant tenant = tenantRepo.findByExternalId(tenantId).orElseThrow(TenantSessionNotFoundException::new);
            tenantMap.put(tenantId, tenant);
        }
        return Optional.of(tenantMap.get(tenantId));
    }

    public synchronized Optional<Tenant> putTenant(TenantSessionDto tenantSessionDto) throws TenantSessionNotFoundException {
        if (!has(tenantSessionDto)) {
            throw new TenantSessionNotFoundException();
        }
        String tenantId = tenantSessionDto.getExternalId();
        return putTenant(tenantId);
    }

    public synchronized Tenant getTenant(String tenantId) {
        return tenantMap.get(tenantId);
    }

}
