package com.act.core.exception;


public class ConflitException extends Exception {
    public ConflitException(Class c) {
        super(c.getSimpleName() + " existe déjà");
    }

    public ConflitException(String message) {
        super(message);
    }
}
