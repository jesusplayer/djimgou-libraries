package com.djimgou.core.cooldto.testing.app.exceptions;


import com.djimgou.core.cooldto.exception.DtoFieldNotFoundException;

public class MarqueNotFoundException extends DtoFieldNotFoundException {
    public MarqueNotFoundException() {
        super("Cette marque n'existe pas");
    }

    public MarqueNotFoundException(String message) {
        super(message);
    }
}
