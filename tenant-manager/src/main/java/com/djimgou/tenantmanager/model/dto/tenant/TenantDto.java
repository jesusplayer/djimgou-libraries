package com.djimgou.tenantmanager.model.dto.tenant;

import com.djimgou.core.cooldto.model.IEntityDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class TenantDto implements IEntityDto {
    @NotBlank()
    String code;

    @NotBlank()
    String nom;

    @NotBlank()
    String ville;

    @NotNull()
    UUID paysId;
}
