package com.djimgou.core.cooldto.testing.app.exceptions;


import com.djimgou.core.cooldto.exception.DtoFieldNotFoundException;

public class VilleNotFoundException extends DtoFieldNotFoundException {
    public VilleNotFoundException() {
        super("Cette ville n'existe pas");
    }

    public VilleNotFoundException(String message) {
        super(message);
    }
}
