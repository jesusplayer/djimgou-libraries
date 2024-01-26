package com.djimgou.security.core.exceptions;


public class ReadOnlyException extends Exception {
    public ReadOnlyException() {
        super("bad.readonly");
    }

    public ReadOnlyException(String message) {
        super(message);
    }
}
