package com.act.sms.model.dto.sms;

import com.act.core.infra.BaseFilterDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author DJIMGOU NKENNE DANY MARC
 * 04/2022
 */
@Data
public class SmsFilterDto extends BaseFilterDto {
    @NotBlank()
    String from;

    @NotBlank()
    String to;

    @NotBlank()
    String text;

}
