package com.djimgou.smsweb.controller;

import com.djimgou.sms.exceptions.NoSmsServiceProvider;
import com.djimgou.sms.model.dto.SmsSimpleMassageDto;
import com.djimgou.sms.service.ISmsSenderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Log4j2
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/sms")
public class SmsController {
    private Optional<ISmsSenderService> smsSender;

    public SmsController(Optional<ISmsSenderService> smsSender) {
        this.smsSender = smsSender;
    }

    @PostMapping("/envoyerMsgTest")
    //@Endpoint("Envoyer un SMS")
    public void sendSms(@RequestBody @Valid SmsSimpleMassageDto smsMassageDto) throws NoSmsServiceProvider {
        getSender().send(smsMassageDto);
    }

    ISmsSenderService getSender() throws NoSmsServiceProvider {
        return smsSender.orElseThrow(NoSmsServiceProvider::new);
    }
}
