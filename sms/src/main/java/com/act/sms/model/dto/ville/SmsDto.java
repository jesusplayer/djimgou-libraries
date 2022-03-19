package com.act.sms.model.dto.ville;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

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
