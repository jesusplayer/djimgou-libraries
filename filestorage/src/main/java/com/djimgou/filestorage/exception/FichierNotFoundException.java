package com.djimgou.filestorage.exception;


import com.djimgou.core.exception.NotFoundException;

public class FichierNotFoundException extends NotFoundException {
    public FichierNotFoundException() {
        super("Ce fichier n'existe pas");
    }

    public FichierNotFoundException(String message) {
        super(message);
    }
}
