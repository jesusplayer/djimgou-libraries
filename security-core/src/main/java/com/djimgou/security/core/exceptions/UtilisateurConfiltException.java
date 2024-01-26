package com.djimgou.security.core.exceptions;


public class UtilisateurConfiltException extends Exception {
    public UtilisateurConfiltException() {
        super("conflict.user");
    }

    public UtilisateurConfiltException(String message) {
        super(message);
    }
}
