package com.act.security.core.exceptions;


import com.act.core.exception.NotFoundException;

public class PrivilegeNotFoundException extends NotFoundException {
    public PrivilegeNotFoundException() {
        super("Privilège inexistant");
    }

    public PrivilegeNotFoundException(String message) {
        super(message);
    }
}
