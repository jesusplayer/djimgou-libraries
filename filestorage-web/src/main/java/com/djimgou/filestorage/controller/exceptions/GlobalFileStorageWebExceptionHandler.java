package com.djimgou.filestorage.controller.exceptions;

import com.djimgou.core.exception.*;
import com.djimgou.filestorage.exception.FichierInvalidNameException;
import com.djimgou.filestorage.exception.FichierNotFoundException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalFileStorageWebExceptionHandler extends AppExceptionHandler {

    @ExceptionHandler({
            ConversionFailedException.class,
            BadRequestException.class,
            FichierInvalidNameException.class,
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConnversion(Exception ex) {
        return handleException(ex, HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity<String> handleNotFound(Exception ex) {
        return handleException(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            FichierNotFoundException.class,
            NotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNotFoundExceptions(Exception ex) {
        return handleNotFound(ex);
    }


    @ExceptionHandler({
            ConflitException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleConflitExceptions(Exception ex) {
        return handleException(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            AppException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> appExceptions(Exception ex) {
        return handleException(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
