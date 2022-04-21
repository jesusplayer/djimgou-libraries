package com.djimgou.tenantmanager.exceptions;


import com.djimgou.core.exception.NotFoundException;

public class PaysNotFoundException extends NotFoundException {
    public PaysNotFoundException() {
        super("Ce pays n'existe pas");
    }

    public PaysNotFoundException(String message) {
        super(message);
    }
}
