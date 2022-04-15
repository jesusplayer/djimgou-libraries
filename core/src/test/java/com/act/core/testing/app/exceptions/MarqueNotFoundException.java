package com.act.core.testing.app.exceptions;


import com.act.core.exception.NotFoundException;

public class MarqueNotFoundException extends NotFoundException {
    public MarqueNotFoundException() {
        super("Cette marque n'existe pas");
    }

    public MarqueNotFoundException(String message) {
        super(message);
    }
}
