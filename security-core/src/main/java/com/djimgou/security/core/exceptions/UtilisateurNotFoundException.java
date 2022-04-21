package com.djimgou.security.core.exceptions;


import com.djimgou.core.exception.NotFoundException;

public class UtilisateurNotFoundException extends NotFoundException {
    public UtilisateurNotFoundException() {
        super("utilisateur inexistant");
    }

    public UtilisateurNotFoundException(String message) {
        super(message);
    }
}
