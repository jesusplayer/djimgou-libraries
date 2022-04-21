package com.djimgou.security.core.exceptions;


public class BadPasswordException extends Exception {
    public BadPasswordException() {
        super("L'ancien mot de passe est invalide est invalide");
    }

    public BadPasswordException(String message) {
        super(message);
    }
}
