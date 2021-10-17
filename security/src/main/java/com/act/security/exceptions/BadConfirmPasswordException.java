package com.act.security.exceptions;


public class BadConfirmPasswordException extends Exception {
    public BadConfirmPasswordException() {
        super("Le mot de passe de confirmation est invalide");
    }

    public BadConfirmPasswordException(String message) {
        super(message);
    }
}
