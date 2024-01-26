package com.djimgou.security.core.exceptions;


public class PrivilegeConfiltException extends Exception {
    public PrivilegeConfiltException() {
        super("conflict.privilege");
    }

    public PrivilegeConfiltException(String message) {
        super(message);
    }
}
