package com.djimgou.core.testing.app.controller;

import com.djimgou.core.cooldto.exception.DtoChildFieldNotFound;
import com.djimgou.core.exception.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.UnexpectedTypeException;
import java.io.FileNotFoundException;
import java.lang.reflect.UndeclaredThrowableException;

@Log4j2
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    //    @ExceptionHandler(ConversionFailedException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<String> handleConnversion(RuntimeException ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
//    }
    public ResponseEntity<String> handleException(Exception ex, HttpStatus status) {
        String message = ex.getMessage();
        if (ex instanceof UndeclaredThrowableException) {
            message = ((UndeclaredThrowableException) ex).getUndeclaredThrowable().getMessage();
        }
        return new ResponseEntity<>(message, status);
    }

    public ResponseEntity<String> handleNotFound(Exception ex) {

        return handleException(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            NotFoundException.class,
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNotFoundExceptions(Exception ex) {
        return handleNotFound(ex);
    }

    @ExceptionHandler({
            UnexpectedTypeException.class,
            org.springframework.http.converter.HttpMessageNotReadableException.class,
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
            org.springframework.web.bind.MissingServletRequestParameterException.class,
            com.fasterxml.jackson.databind.exc.InvalidDefinitionException.class,
            org.springframework.web.bind.MethodArgumentNotValidException.class,
            org.springframework.web.HttpRequestMethodNotSupportedException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleForbiddenExceptions(Exception ex) {
        log.error(ex.getMessage());
        return handleException(ex, HttpStatus.BAD_REQUEST);
    }


}
