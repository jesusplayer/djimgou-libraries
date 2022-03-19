package com.act.sms.exceptions;


import com.act.core.exception.NotFoundException;

public class SmsNotFoundException extends NotFoundException {
    public SmsNotFoundException() {
        super("SmS inexistant");
    }

    public SmsNotFoundException(String message) {
        super(message);
    }
}
