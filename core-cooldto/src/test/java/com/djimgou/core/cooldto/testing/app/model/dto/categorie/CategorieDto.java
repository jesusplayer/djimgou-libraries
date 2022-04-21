package com.djimgou.core.cooldto.testing.app.model.dto.categorie;

import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.model.IEntityDto;
import com.djimgou.core.cooldto.testing.app.model.Categorie;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Dto(Categorie.class)
@Data
public class CategorieDto implements IEntityDto {
    @NotBlank(message = "Le code de la categorie doit être renseigné")
    String code;

    @NotBlank(message = "Le code de la categorie doit être renseigné")
    String nom;

}
