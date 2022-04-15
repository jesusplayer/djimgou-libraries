package com.act.core.testing.app.exceptions;


import com.act.core.exception.NotFoundException;

public class RegionNotFoundException extends NotFoundException {
    public RegionNotFoundException() {
        super("Cette region n'existe pas");
    }

    public RegionNotFoundException(String message) {
        super(message);
    }
}
