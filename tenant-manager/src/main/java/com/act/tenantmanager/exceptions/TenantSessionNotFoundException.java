package com.act.tenantmanager.exceptions;



public class TenantSessionNotFoundException extends Exception {
    public TenantSessionNotFoundException() {
        super("Cet agence n'est pas accéssible, la session a expirée");
    }

    public TenantSessionNotFoundException(String message) {
        super(message);
    }
}
