package com.act.tenantmanager.aop;


import com.act.tenantmanager.model.Tenant;

public class TenantContext {

    private static ThreadLocal<Tenant> currentTenant = new InheritableThreadLocal<>();
    private static ThreadLocal<String> currentTenantId = new InheritableThreadLocal<>();

    public static Tenant getCurrentTenant() {
        return currentTenant.get();
    }

    public static String getCurrentTenantId() {
        return currentTenantId.get();
    }

    public static void setCurrentTenant(Tenant tenant) {
        currentTenant.set(tenant);
        currentTenantId.set(tenant.getId().toString());
    }

    public static void clear() {
        currentTenant.set(null);
    }

}
