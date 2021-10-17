package com.act.tenantmanager.exceptions;


import com.act.core.exception.NotFoundException;

public class PaysNotFoundException extends NotFoundException {
    public PaysNotFoundException() {
        super("Ce pays n'existe pas");
    }

    public PaysNotFoundException(String message) {
        super(message);
    }
}
