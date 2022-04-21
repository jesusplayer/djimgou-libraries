package com.djimgou.tenantmanager.model.dto.tenant;

import com.djimgou.core.infra.BaseFilterDto;
import lombok.Data;

import java.util.UUID;

@Data
public class TenantFilterDto extends BaseFilterDto {
    String code;

    String nom;

    String ville;

    UUID paysId;
}
