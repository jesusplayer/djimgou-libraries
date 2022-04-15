package com.act.core.testing.app.model.dto.marque;

import com.act.core.infra.BaseFilterDto;
import lombok.Data;

@Data
public class MarqueFilterDto extends BaseFilterDto {
    //@NotBlank()
    String code;

    //@NotBlank()
    String nom;
}
