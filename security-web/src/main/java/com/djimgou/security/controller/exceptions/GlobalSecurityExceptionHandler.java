package com.djimgou.security.controller.exceptions;

import com.djimgou.core.exception.*;
import com.djimgou.security.core.exceptions.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class GlobalSecurityExceptionHandler extends AppExceptionHandler {

    @ExceptionHandler({
            ConversionFailedException.class,
            BadConfirmPasswordException.class,
            BadInvitationLinkException.class,
            BadRequestException.class,
            ReadOnlyException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConnversion(Exception ex) {
        log.error(ex.getMessage());
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
        log.error(ex.getMessage());
        return handleNotFound(ex);
    }


    @ExceptionHandler({
            ConflitException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleConflitExceptions(Exception ex) {
        log.error(ex.getMessage());
        return handleException(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            UnautorizedException.class,
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> handleUnauthorized(Exception ex) {
        log.error(ex.getMessage());
        return handleException(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
            AppException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> appExceptions(Exception ex) {
        log.error(ex.getMessage());
        return handleException(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
