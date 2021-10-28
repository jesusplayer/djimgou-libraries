package com.act.security.core.exceptions;


public class RoleConfiltException extends Exception {
    public RoleConfiltException() {
        super("Ce rôle existe déjà dans le système");
    }

    public RoleConfiltException(String message) {
        super(message);
    }
}
