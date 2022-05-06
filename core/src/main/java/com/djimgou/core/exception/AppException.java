package com.djimgou.core.exception;


public class AppException extends Exception {
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
