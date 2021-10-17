package com.act.core.exception;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotFoundException extends Exception {
    public NotFoundException(Class c) {
        super(c.getSimpleName() + " inexistant");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
