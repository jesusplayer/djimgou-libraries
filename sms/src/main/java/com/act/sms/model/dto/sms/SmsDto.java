package com.act.sms.model.dto.sms;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author DJIMGOU NKENNE DANY MARC
 * 04/2022
 */
@Data
@Builder
public class SmsDto {
    @NotBlank()
    String from;

    @NotBlank()
    String to;

    @NotBlank()
    String text;

}
