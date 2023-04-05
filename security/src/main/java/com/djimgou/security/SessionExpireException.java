package com.djimgou.security;

import javax.servlet.ServletException;

public class SessionExpireException extends ServletException {
    public SessionExpireException() {
    }

    public SessionExpireException(String message) {
        super(message);
    }

    public SessionExpireException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public SessionExpireException(Throwable rootCause) {
        super(rootCause);
    }
}
