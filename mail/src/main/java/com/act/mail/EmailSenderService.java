package com.act.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Service
public class EmailSenderService {

    @Autowired()
    //@Qualifier("JavaMailSenderImpl")
    private JavaMailSender javaMailSender;

    public EmailSenderService() {
    }

    public void sendEmail(SimpleMailMessage email) {
        try {
            javaMailSender.send(email);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendEmailAsync(SimpleMailMessage email) {
        javaMailSender.send(email);
    }
}
