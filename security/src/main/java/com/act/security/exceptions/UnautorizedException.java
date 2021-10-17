package com.act.security.exceptions;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UnautorizedException extends Exception {

    public UnautorizedException(String message) {
        super(message);
    }
}