package com.djimgou.core.cooldto.testing.app.exceptions;


import com.djimgou.core.cooldto.exception.DtoFieldNotFoundException;

public class RegionNotFoundException extends DtoFieldNotFoundException {
    public RegionNotFoundException() {
        super("Cette region n'existe pas");
    }

    public RegionNotFoundException(String message) {
        super(message);
    }
}
