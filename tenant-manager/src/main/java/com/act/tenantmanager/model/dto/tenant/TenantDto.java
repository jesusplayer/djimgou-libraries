package com.act.tenantmanager.model.dto.tenant;

import com.act.core.model.IEntityDto;
import com.act.tenantmanager.model.Tenant;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
public class TenantDto implements IEntityDto {
    @NotBlank()
    String code;

    @NotBlank()
    String nom;

    @NotBlank()
    String ville;

    @NotBlank()
    UUID paysId;
}
