package com.djimgou.sms.model.dto.sms;

import com.djimgou.core.cooldto.model.IEntityDto;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author DJIMGOU NKENNE DANY MARC
 * 04/2022
 */
@Data
@Builder
public class SmsDto implements IEntityDto {
    @NotBlank()
    String from;

    @NotBlank()
    String to;

    @NotBlank()
    String text;
}
