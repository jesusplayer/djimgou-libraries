package com.act.core.testing.app.model.dto.categorie;

import com.act.core.dto.DtoClass;
import com.act.core.model.IEntityDto;
import com.act.core.testing.app.model.Categorie;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@DtoClass
@Data
public class CategorieDto implements IEntityDto {
    @NotBlank(message = "Le code de la categorie doit être renseigné")
    String code;

    @NotBlank(message = "Le code de la categorie doit être renseigné")
    String nom;

}
