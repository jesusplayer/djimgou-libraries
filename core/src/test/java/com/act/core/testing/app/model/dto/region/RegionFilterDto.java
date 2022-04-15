package com.act.core.testing.app.model.dto.region;

import com.act.core.infra.BaseFilterDto;
import lombok.Data;

import java.util.UUID;

@Data
public class RegionFilterDto  extends BaseFilterDto {
    //@NotBlank()
    String code;

    //@NotBlank()
    String nom;

    UUID paysId;
}
