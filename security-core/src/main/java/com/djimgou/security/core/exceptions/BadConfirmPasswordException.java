package com.djimgou.security.core.exceptions;


public class BadConfirmPasswordException extends Exception {
    public BadConfirmPasswordException() {
        super("bad.passwordConfirm");
    }

    public BadConfirmPasswordException(String message) {
        super(message);
    }
}
