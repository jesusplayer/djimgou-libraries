package com.act.tenantmanager.exceptions;


import com.act.core.exception.NotFoundException;

public class TenantUnautorizedException extends NotFoundException {
    public TenantUnautorizedException() {
        super("Vous ne pouvez effectuer des opérations sur cette agence elle est inactive");
    }

    public TenantUnautorizedException(String message) {
        super(message);
    }
}