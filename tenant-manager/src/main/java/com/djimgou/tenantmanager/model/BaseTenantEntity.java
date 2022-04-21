package com.djimgou.tenantmanager.model;

import com.djimgou.core.util.model.BaseBdEntity;
import com.djimgou.tenantmanager.aop.TenantContext;
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
