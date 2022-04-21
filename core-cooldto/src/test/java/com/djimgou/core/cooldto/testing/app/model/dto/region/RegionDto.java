package com.djimgou.core.cooldto.testing.app.model.dto.region;

import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.model.IEntityDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Dto
@Data
public class RegionDto implements IEntityDto {
    @NotBlank(message = "Le code de la region doit être renseigné")
    String code;

    @NotBlank(message = "Le nom de la region doit être renseigné")
    String nom;
}
