package com.act.security.core.exceptions;


import com.act.core.exception.NotFoundException;

public class UtilisateurNotFoundException extends NotFoundException {
    public UtilisateurNotFoundException() {
        super("utilisateur inexistant");
    }

    public UtilisateurNotFoundException(String message) {
        super(message);
    }
}
