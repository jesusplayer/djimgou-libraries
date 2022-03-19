package com.act.smsweb.controller;

import com.act.sms.model.dto.SmsSimpleMassageDto;
import com.act.sms.service.SmsSenderService;
import com.twilio.rest.api.v2010.account.Message;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Log4j2
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/sms")
public class SmsController {
    private SmsSenderService smsSender;

    public SmsController(SmsSenderService smsSender) {
        this.smsSender = smsSender;
    }

    @PostMapping("/envoyerMsgTest")
    public void sendSms(@RequestBody @Valid SmsSimpleMassageDto smsMassageDto) {
         smsSender.send(smsMassageDto);
    }


}
