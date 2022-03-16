package com.act.mail;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.File;
import java.util.List;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Log4j2
@Service
public class EmailSenderService {

    //@Qualifier("JavaMailSenderImpl")
    private JavaMailSender javaMailSender;

    public EmailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(SimpleMailMessage email) {
        try {
            javaMailSender.send(email);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }

    public void sendEmail(MimeMessagePreparator email) {
        try {
            javaMailSender.send(email);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }

    boolean isNotBlank(String str) {
        return str != null && str.trim().length() > 0;
    }

    void addAttachements(MimeMessageHelper message, List<Resource> attachments) {
        if (attachments != null) {
            attachments.forEach(file -> {
                try {
                    String fileName = file != null ? file.getFilename() : "file";
                    fileName = isNotBlank(fileName) ? fileName : "file";
                    if (file != null) {
                        message.addAttachment(fileName, file);
                    } else {
                        log.warn("Fichier attaché null pour l'envoi de mail");
                    }
                } catch (MessagingException e) {
                    throw new RuntimeException("Problem attaching file to email", e);
                }
            });
        }
    }

    void addFileAttachements(MimeMessageHelper message, List<File> attachments) {
        if (attachments != null) {
            attachments.forEach(file -> {
                try {
                    FileSystemResource resource = new FileSystemResource(file);
                    message.addAttachment(file.getName(), resource);
                } catch (MessagingException e) {
                    throw new RuntimeException("Problem attaching file to email", e);
                }
            });
        }
    }


    public MimeMessagePreparator buildMessage(String from, String fromName, String subject, String toAddresses, String ccAddresses, String bccAddresses, String body, List<Resource> attachments) {
        return mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setTo(toAddresses.split("[,;]"));
            message.setFrom(from, fromName);
            message.setSubject(subject);
            if (isNotBlank(ccAddresses))
                message.setCc(ccAddresses.split("[;,]"));
            if (isNotBlank(bccAddresses))
                message.setBcc(bccAddresses.split("[;,]"));
            message.setText(body, true);
            addAttachements(message, attachments);
        };
    }

    public void sendMail(String from, String fromName, String subject, String toAddresses, String ccAddresses, String bccAddresses, String body) {
        javaMailSender.send(buildMessage(from, fromName, subject, toAddresses, ccAddresses, bccAddresses, body, null));
        log.info("Email sent successfully To {},{} with Subject {}", toAddresses, ccAddresses, subject);
    }

    @Async
    public void sendEmailAsync(SimpleMailMessage email) {
        javaMailSender.send(email);
    }
}
