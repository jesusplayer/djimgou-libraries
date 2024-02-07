package com.djimgou.core.exception;

import lombok.Getter;

@Getter
public class AppException extends Exception {
    Object[] transArgs;

    public AppException(String message, Object[] transArgs) {
        super(message);
        this.transArgs = transArgs;
    }

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public AppException(String message, Exception ex) {
        super(message, ex);
    }
}
