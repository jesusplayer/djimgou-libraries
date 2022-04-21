package com.djimgou.tenantmanager.model.dto.pays;

import com.djimgou.core.cooldto.model.IEntityDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PaysDto implements IEntityDto {
    @NotBlank()
    String code;
    @NotBlank()
    String nom;
}
