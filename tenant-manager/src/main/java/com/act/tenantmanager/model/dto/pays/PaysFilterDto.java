package com.act.tenantmanager.model.dto.pays;

import com.act.core.infra.BaseFilterDto;
import lombok.Data;

@Data
public class PaysFilterDto extends BaseFilterDto {
    String code;

    String nom;
}
