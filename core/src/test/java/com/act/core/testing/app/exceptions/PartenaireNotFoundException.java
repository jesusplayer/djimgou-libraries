package com.act.core.testing.app.exceptions;


import com.act.core.exception.NotFoundException;

public class PartenaireNotFoundException extends NotFoundException {
    public PartenaireNotFoundException() {
        super("Ce partenaire n'existe pas");
    }

    public PartenaireNotFoundException(String message) {
        super(message);
    }
}
