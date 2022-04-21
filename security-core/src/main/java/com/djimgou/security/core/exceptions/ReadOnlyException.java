package com.djimgou.security.core.exceptions;


public class ReadOnlyException extends Exception {
    public ReadOnlyException() {
        super("Lecture seule: Impossible de modifier");
    }

    public ReadOnlyException(String message) {
        super(message);
    }
}
