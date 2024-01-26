package com.djimgou.security.core.exceptions;


import com.djimgou.core.exception.NotFoundException;

public class PrivilegeNotFoundException extends NotFoundException {
    public PrivilegeNotFoundException() {
        super("notFound.privilege");
    }

    public PrivilegeNotFoundException(String message) {
        super(message);
    }
}
