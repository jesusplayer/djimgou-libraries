package com.djimgou.security.core.exceptions;


public class UtilisateurConfiltException extends Exception {
    public UtilisateurConfiltException() {
        super("Cet Utilisateur existe déjà dans le système");
    }

    public UtilisateurConfiltException(String message) {
        super(message);
    }
}
