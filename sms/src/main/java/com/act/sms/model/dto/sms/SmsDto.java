package com.act.sms.model.dto.sms;

import com.act.core.model.IEntityDto;
import com.act.sms.model.Sms;
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

    @Override
    public Class<Sms> originalClass() {
        return Sms.class;
    }
}
