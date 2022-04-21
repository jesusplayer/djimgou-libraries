package com.djimgou.core.cooldto.testing.app.exceptions;


import com.djimgou.core.cooldto.exception.DtoFieldNotFoundException;

public class QuartierNotFoundException extends DtoFieldNotFoundException {
    public QuartierNotFoundException() {
        super("Ce quartier n'existe pas");
    }

    public QuartierNotFoundException(String message) {
        super(message);
    }
}
