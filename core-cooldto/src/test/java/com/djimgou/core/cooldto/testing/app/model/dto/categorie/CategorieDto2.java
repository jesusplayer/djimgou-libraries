package com.djimgou.core.cooldto.testing.app.model.dto.categorie;

import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.model.IEntityDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Dto
@Data
public class CategorieDto2 implements IEntityDto {
    @NotBlank(message = "Le code de la categorie doit être renseigné")
    String code;

    @NotBlank(message = "Le code de la categorie doit être renseigné")
    String nom;

}
