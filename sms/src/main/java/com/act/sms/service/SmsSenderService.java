package com.act.sms.service;

import com.act.sms.model.dto.SmsSimpleMassageDto;
import com.act.sms.model.dto.ville.SmsDto;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.act.core.util.AppUtils.has;

@Service
public class SmsSenderService {
    private String sId;
    private String token;
    private String from;
    private String defaultTo;
    private String twilioRegion;
    private boolean persistable;
    private SmsBdService smsBdService;

    public SmsSenderService(
            SmsBdService smsBdService,
            @Value("${auth.sms.sid:}") String sId,
            @Value("${auth.sms.token:}") String token,
            @Value("${auth.sms.from:}") String from,
            @Value("${auth.sms.defaultTo:}") String defaultTo,
            @Value("${auth.sms.twilio.region:}") String twilioRegion,
            @Value("${auth.sms.persistable:false}") String persistable
    ) {
        this.smsBdService = smsBdService;
        this.sId = sId;
        this.token = token;
        this.from = from;
        this.defaultTo = defaultTo;
        this.twilioRegion = twilioRegion;
        this.persistable = Boolean.parseBoolean(persistable);
    }

    @Async
    @PostConstruct
    void init() {
        Twilio.init(sId, token);
        Twilio.setRegion(twilioRegion);
    }

    public boolean isNotBlank(String text) {
        return text != null && text.trim().length() > 0;
    }

    public Message send(String from, String to, String text) {
        to = isNotBlank(to) ? to : defaultTo;
        from = isNotBlank(from) ? from : this.from;
        Message msg = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(from),
                text
        ).create();
        if (has(msg) && persistable) {
            SmsDto dto = SmsDto.builder()
                    .to(to).from(from).text(text).build();
            smsBdService.createSms(dto);
        }
        return msg;
    }

    @Async
    public Message sendAsync(String from, String to, String text) {
        return send(from, to, text);
    }

    public Message send(SmsSimpleMassageDto dto) {
        return send(from, dto.getTo(), dto.getText());
    }
}
