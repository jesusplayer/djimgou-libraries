package com.act.security.core.exceptions;


import lombok.NoArgsConstructor;

import javax.servlet.ServletException;

@NoArgsConstructor
public class UnautorizedException extends ServletException {

    public UnautorizedException(String message) {
        super(message);
    }
}