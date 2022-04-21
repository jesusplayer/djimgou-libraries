package com.djimgou.sms.twilio.service;

import com.djimgou.sms.model.SmSResponse;
import com.djimgou.sms.service.ISmsConfig;
import com.djimgou.sms.service.ISmsSenderService;
import com.djimgou.sms.service.SmsBdService;
import com.djimgou.sms.service.SmsTwilioConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.Getter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author DJIMGOU NKENNE DANY MARC
 * 04/2022
 */
@Getter
@Service
public class SmsTwilioSenderService implements ISmsSenderService {
    private SmsBdService smsBdService;
    private SmsTwilioConfig twilioConfig;

    public SmsTwilioSenderService(
            SmsBdService smsBdService,
            SmsTwilioConfig smsTwilioConfig
    ) {
        this.smsBdService = smsBdService;
        this.twilioConfig = smsTwilioConfig;
    }

    @Async
    @PostConstruct
    void init() {
        Twilio.init(this.twilioConfig.getSId(), twilioConfig.getToken());
        Twilio.setRegion(twilioConfig.getTwilioRegion());
    }

    @Override
    public SmSResponse sendCustom(String from, String to, String text) {
        to = isNotBlank(to) ? to : twilioConfig.getDefaultTo();
        from = isNotBlank(from) ? from : twilioConfig.getFrom();
        Message msg = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(from),
                text
        ).create();
        return new SmSResponse(msg);
    }


    @Override
    public ISmsConfig defaultConfig() {
        return twilioConfig;
    }

}
