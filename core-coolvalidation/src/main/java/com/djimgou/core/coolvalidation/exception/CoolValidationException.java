package com.djimgou.core.coolvalidation.exception;

public class CoolValidationException extends Throwable{
    public CoolValidationException() {
    }

    public CoolValidationException(String message) {
        super(message);
    }

    public CoolValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoolValidationException(Throwable cause) {
        super(cause);
    }

    public CoolValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
