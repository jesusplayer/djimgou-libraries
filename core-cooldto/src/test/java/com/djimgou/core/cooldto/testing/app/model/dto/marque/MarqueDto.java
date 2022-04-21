package com.djimgou.core.cooldto.testing.app.model.dto.marque;

import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.model.IEntityDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Dto
@Data
public class MarqueDto implements IEntityDto {
    @NotBlank(message = "La marque du produit doit être renseigné")
    String code;

    @NotBlank(message = "Le nom du produit doit être renseigné")
    String nom;
}
