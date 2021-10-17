package com.act.tenantmanager.exceptions;


import com.act.core.exception.NotFoundException;

public class TenantNotFoundException extends NotFoundException {
    public TenantNotFoundException() {
        super("Cet agence n'existe pas");
    }

    public TenantNotFoundException(String message) {
        super(message);
    }
}
