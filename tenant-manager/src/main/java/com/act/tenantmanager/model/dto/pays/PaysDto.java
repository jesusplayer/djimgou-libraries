package com.act.tenantmanager.model.dto.pays;

import com.act.core.model.IEntityDto;
import com.act.tenantmanager.model.Pays;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PaysDto implements IEntityDto {
    @NotBlank()
    String code;
    @NotBlank()
    String nom;
}
