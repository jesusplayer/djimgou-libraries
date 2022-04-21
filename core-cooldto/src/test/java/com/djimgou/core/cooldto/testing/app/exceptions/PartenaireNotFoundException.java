package com.djimgou.core.cooldto.testing.app.exceptions;


import com.djimgou.core.cooldto.exception.DtoFieldNotFoundException;

public class PartenaireNotFoundException extends DtoFieldNotFoundException {
    public PartenaireNotFoundException() {
        super("Ce partenaire n'existe pas");
    }

    public PartenaireNotFoundException(String message) {
        super(message);
    }
}
