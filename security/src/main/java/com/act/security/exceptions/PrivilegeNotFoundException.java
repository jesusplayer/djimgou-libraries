package com.act.security.exceptions;


import com.act.core.exception.NotFoundException;

public class PrivilegeNotFoundException extends NotFoundException {
    public PrivilegeNotFoundException() {
        super("Privilège inexistant");
    }

    public PrivilegeNotFoundException(String message) {
        super(message);
    }
}
