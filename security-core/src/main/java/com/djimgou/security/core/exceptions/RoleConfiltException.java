package com.djimgou.security.core.exceptions;


public class RoleConfiltException extends Exception {
    public RoleConfiltException() {
        super("conflict.role");
    }

    public RoleConfiltException(String message) {
        super(message);
    }
}
