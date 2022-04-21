package com.djimgou.tenantmanager.exceptions;


import com.djimgou.core.exception.NotFoundException;

public class TenantNotFoundException extends NotFoundException {
    public TenantNotFoundException() {
        super("Cet agence n'existe pas");
    }

    public TenantNotFoundException(String message) {
        super(message);
    }
}
