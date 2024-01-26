package com.djimgou.security.core.exceptions;


public class BadPasswordException extends Exception {
    public BadPasswordException() {
        super("bad.oldPassword");
    }

    public BadPasswordException(String message) {
        super(message);
    }
}
