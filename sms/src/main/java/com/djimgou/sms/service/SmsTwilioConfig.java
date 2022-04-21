package com.djimgou.sms.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author DJIMGOU NKENNE DANY MARC
 * 04/2022
 */
@Getter
@Component
public class SmsTwilioConfig implements ISmsConfig {
    private String sId;
    private String token;
    private String twilioRegion;
    private SmsConfig smsConfig;

    @Autowired
    public SmsTwilioConfig(
            SmsConfig smsConfig,
            @Value("${auth.sms.sid:}") String sId,
            @Value("${auth.sms.token:}") String token,
            @Value("${auth.sms.twilio.region:}") String twilioRegion) {
        this.sId = sId;
        this.token = token;
        this.twilioRegion = twilioRegion;
        this.smsConfig = smsConfig;
    }

    @Override
    public String getFrom() {
        return smsConfig.getFrom();
    }

    @Override
    public String getDefaultTo() {
        return smsConfig.getDefaultTo();
    }

    @Override
    public boolean isPersistable() {
        return smsConfig.isPersistable();
    }
}
