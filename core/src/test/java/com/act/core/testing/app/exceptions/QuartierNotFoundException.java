package com.act.core.testing.app.exceptions;


import com.act.core.exception.NotFoundException;

public class QuartierNotFoundException extends NotFoundException {
    public QuartierNotFoundException() {
        super("Ce quartier n'existe pas");
    }

    public QuartierNotFoundException(String message) {
        super(message);
    }
}
