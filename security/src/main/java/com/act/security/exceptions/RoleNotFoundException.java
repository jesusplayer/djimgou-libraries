package com.act.security.exceptions;


import com.act.core.exception.NotFoundException;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException() {
        super("Rôle inexistant");
    }

    public RoleNotFoundException(String message) {
        super(message);
    }
}
