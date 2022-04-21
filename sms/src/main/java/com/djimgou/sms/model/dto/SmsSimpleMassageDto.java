package com.djimgou.sms.model.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * @author DJIMGOU NKENNE DANY MARC
 * 04/2022
 */
@Getter@Builder
public class SmsSimpleMassageDto {
    String to;
    String text;
}
