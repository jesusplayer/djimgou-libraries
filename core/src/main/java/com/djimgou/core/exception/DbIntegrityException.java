package com.djimgou.core.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DbIntegrityException extends DataIntegrityViolationException {
    public DbIntegrityException(String msg) {
        super(msg);
    }

    public DbIntegrityException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
