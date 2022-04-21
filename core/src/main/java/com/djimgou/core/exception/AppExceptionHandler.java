package com.djimgou.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.UndeclaredThrowableException;

public abstract class AppExceptionHandler {
    public ResponseEntity<String> handleException(Exception ex, HttpStatus status) {
        String message = ex.getMessage();
        if(ex instanceof UndeclaredThrowableException){
            message = ((UndeclaredThrowableException)ex).getUndeclaredThrowable().getMessage();
        }
        return new ResponseEntity<>(message, status);
    }

}
