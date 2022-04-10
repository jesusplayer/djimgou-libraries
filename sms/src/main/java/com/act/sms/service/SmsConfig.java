package com.act.sms.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author DJIMGOU NKENNE DANY MARC
 * 04/2022
 */
@Getter
@Component
public class SmsConfig implements ISmsConfig {
    private String from;
    private String defaultTo;
    private boolean persistable;

    public SmsConfig(@Value("${auth.sms.from:}") String from,
                     @Value("${auth.sms.defaultTo:}") String defaultTo,
                     @Value("${auth.sms.persistable:false}") String persistable) {
        this.from = from;
        this.defaultTo = defaultTo;
        this.persistable = Boolean.parseBoolean(persistable);
    }
}
