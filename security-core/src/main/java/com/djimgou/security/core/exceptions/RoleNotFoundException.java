package com.djimgou.security.core.exceptions;


import com.djimgou.core.exception.NotFoundException;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException() {
        super("Rôle inexistant");
    }

    public RoleNotFoundException(String message) {
        super(message);
    }
}
