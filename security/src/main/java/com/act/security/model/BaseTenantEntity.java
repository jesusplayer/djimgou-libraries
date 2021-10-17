package com.act.security.model;

import com.act.core.model.BaseBdEntity;
import com.act.tenantmanager.aop.TenantContext;
import com.act.tenantmanager.model.Tenant;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter@Getter
@MappedSuperclass
public abstract class BaseTenantEntity extends BaseBdEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name="tenant_id", referencedColumnName="external_id"),
    })
    private Tenant tenant;

    @PrePersist
    public void prePersist() {
        setTenant(TenantContext.getCurrentTenant());
    }

}
