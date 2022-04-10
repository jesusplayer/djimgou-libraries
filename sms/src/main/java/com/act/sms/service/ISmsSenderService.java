package com.act.sms.service;

import com.act.sms.model.SmSResponse;
import com.act.sms.model.Sms;
import com.act.sms.model.dto.SmsSimpleMassageDto;
import com.act.sms.model.dto.sms.SmsDto;
import org.springframework.scheduling.annotation.Async;

import static com.act.core.util.AppUtils.has;

/**
 * @author DJIMGOU NKENNE DANY MARC
 * 04/2022
 */
public interface ISmsSenderService {
    /**
     * Configuration par défaut de SMS
     * @return
     */
    ISmsConfig defaultConfig();

    /**
     * Fonction à customiser par lors de l'envoix par les différents provider d'SMS
     *
     * @param from
     * @param to
     * @param text
     * @return
     */
    SmSResponse sendCustom(String from, String to, String text);

    default boolean isNotBlank(String text) {
        return text != null && text.trim().length() > 0;
    }

    /**
     * Fonction asynchrone appelée à chaque fois qu'on veux envoyer un SMS
     *
     * @param dto
     * @return
     */
    @Async
    default SmSResponse sendAsync(SmsSimpleMassageDto dto) {
        return send(SmsSimpleMassageDto.builder().to(dto.getTo()).text(dto.getText()).build());
    }

    /**
     * Fonction appelée à chaque fois qu'on veux envoyer un SMS synchrone
     *
     * @param dto
     * @return
     */
    default SmSResponse send(SmsSimpleMassageDto dto) {
        SmSResponse ms = sendCustom(defaultConfig().getFrom(), dto.getTo(), dto.getText());
        if (has(ms) && defaultConfig().isPersistable()) {
            save(dto);
        }
        return ms;
    }

    /**
     * Fonction d'enregistrement/journalisation d'un SMS en BD après envoi.
     * @param dto
     * @return
     */
    default Sms save(SmsSimpleMassageDto dto) {
        SmsDto smsDto = SmsDto.builder()
                .to(dto.getTo()).from(defaultConfig().getFrom()).text(dto.getText()).build();
        return getSmsBdService().createSms(smsDto);
    }

    SmsBdService getSmsBdService();

}
