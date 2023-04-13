package com.djimgou.tenantmanager.service;

import com.djimgou.tenantmanager.exceptions.TenantSessionNotFoundException;
import com.djimgou.tenantmanager.model.Tenant;
import com.djimgou.tenantmanager.model.dto.tenant.TenantSessionDto;
import com.djimgou.tenantmanager.repository.TenantRepo;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

import static com.djimgou.core.util.AppUtils2.has;

@NoArgsConstructor
@Service
public class TenantSessionService {
    private static HashMap<String, Tenant> tenantMap = new HashMap<>();


    private TenantRepo tenantRepo;

    @Autowired(required = false)
    public TenantSessionService(TenantRepo tenantRepo) {
        this.tenantRepo = tenantRepo;
    }

    public synchronized void refreshTenant(Tenant tenant) throws TenantSessionNotFoundException {
        String tenantId = has(tenant.getExternalId()) ? tenant.getExternalId() : has(tenant.getId()) ? tenant.getId().toString() : null;
        if (!has(tenantId) || !tenantMap.containsKey(tenant.getExternalId())) {
            tenant = tenantRepo.findByExternalId(tenantId).orElseThrow(TenantSessionNotFoundException::new);

        }
        tenantMap.put(tenantId, tenant);
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
