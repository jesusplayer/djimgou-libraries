package com.act.core.testing.app.model.dto.ville;

import com.act.core.infra.BaseFilterDto;
import lombok.Data;

import java.util.UUID;

@Data
public class VilleFilterDto extends BaseFilterDto {
    //@NotBlank()
    String code;

    //@NotBlank()
    String nom;

    //@NotNull
    UUID regionId;
}
