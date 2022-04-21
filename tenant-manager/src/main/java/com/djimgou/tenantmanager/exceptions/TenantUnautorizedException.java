package com.djimgou.tenantmanager.exceptions;


import com.djimgou.core.exception.NotFoundException;

public class TenantUnautorizedException extends NotFoundException {
    public TenantUnautorizedException() {
        super("Vous ne pouvez effectuer des opérations sur cette agence elle est inactive");
    }

    public TenantUnautorizedException(String message) {
        super(message);
    }
}