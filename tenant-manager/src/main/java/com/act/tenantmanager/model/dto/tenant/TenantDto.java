package com.act.tenantmanager.model.dto.tenant;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
public class TenantDto {
    @NotBlank()
    String code;

    @NotBlank()
    String nom;

    @NotBlank()
    String ville;

    @NotBlank()
    UUID paysId;
}
