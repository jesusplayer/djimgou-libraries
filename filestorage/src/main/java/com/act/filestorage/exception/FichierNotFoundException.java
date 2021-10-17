package com.act.filestorage.exception;


import com.act.core.exception.NotFoundException;

public class FichierNotFoundException extends NotFoundException {
    public FichierNotFoundException() {
        super("Ce fichier n'existe pas");
    }

    public FichierNotFoundException(String message) {
        super(message);
    }
}
