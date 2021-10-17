package com.act.security.exceptions;


public class PrivilegeConfiltException extends Exception {
    public PrivilegeConfiltException() {
        super("Ce Privilege existe déjà dans le système");
    }

    public PrivilegeConfiltException(String message) {
        super(message);
    }
}
