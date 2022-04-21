package com.djimgou.sms.exceptions;


import com.djimgou.core.exception.NotFoundException;

public class SmsNotFoundException extends NotFoundException {
    public SmsNotFoundException() {
        super("SmS inexistant");
    }

    public SmsNotFoundException(String message) {
        super(message);
    }
}
