package com.djimgou.core.exception;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotFoundException extends Exception {
    public NotFoundException(Class c) {
        super(c.getSimpleName() + " inexistant");
    }

    public NotFoundException(Class c, Object id) {
        super(c.getSimpleName() + "#" + id + " inexistant");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
