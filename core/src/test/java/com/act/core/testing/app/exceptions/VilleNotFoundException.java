package com.act.core.testing.app.exceptions;


import com.act.core.exception.NotFoundException;

public class VilleNotFoundException extends NotFoundException {
    public VilleNotFoundException() {
        super("Cette ville n'existe pas");
    }

    public VilleNotFoundException(String message) {
        super(message);
    }
}
