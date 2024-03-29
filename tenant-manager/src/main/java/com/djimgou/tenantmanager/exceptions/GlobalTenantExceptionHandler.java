package com.djimgou.tenantmanager.exceptions;

import com.djimgou.core.exception.*;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalTenantExceptionHandler extends AppExceptionHandler {

    @ExceptionHandler({
            org.springframework.web.bind.MethodArgumentNotValidException.class

    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String ValidationExceptions(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getAllErrors().stream().collect(Collectors.toMap(
                objectError -> ((FieldError) objectError).getField(),
                objectError -> objectError.getDefaultMessage()
        )).entrySet().stream().map(stringStringEntry -> stringStringEntry.getKey()+" "+stringStringEntry.getValue()).collect(Collectors.joining("; "));
    }

    @ExceptionHandler({
            ConversionFailedException.class,
            BadRequestException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConnversion(Exception ex) {
        return handleException(ex, HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity<String> handleNotFound(Exception ex) {
        return handleException(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            PaysNotFoundException.class,
            TenantNotFoundException.class,
            NotFoundException.class,
            TenantSessionNotFoundException.class

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
            TenantUnautorizedException.class,
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> handleUnauthorized(Exception ex) {
        return handleException(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
            AppException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> appExceptions(Exception ex) {
        return handleException(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
