package com.djimgou.tenantmanager.model.dto.pays;

import com.djimgou.core.infra.BaseFilterDto;
import lombok.Data;

@Data
public class PaysFilterDto extends BaseFilterDto {
    String code;

    String nom;
}
