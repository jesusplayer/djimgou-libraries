package com.act.securityweb.controller.exceptions;

import com.act.core.exception.*;
import com.act.security.exceptions.*;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalSecurityExceptionHandler extends AppExceptionHandler {

    @ExceptionHandler({
            ConversionFailedException.class,
            BadConfirmPasswordException.class,
            BadInvitationLinkException.class,
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
            PrivilegeNotFoundException.class,
            RoleNotFoundException.class,
            UtilisateurNotFoundException.class,
            BadPasswordException.class,
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
            UnautorizedException.class,
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
