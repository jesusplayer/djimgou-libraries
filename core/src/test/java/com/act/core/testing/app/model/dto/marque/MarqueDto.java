package com.act.core.testing.app.model.dto.marque;

import com.act.core.dto.DtoClass;
import com.act.core.model.IEntityDto;
import com.act.core.testing.app.model.Marque;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@DtoClass
@Data
public class MarqueDto implements IEntityDto {
    @NotBlank(message = "La marque du produit doit être renseigné")
    String code;

    @NotBlank(message = "Le nom du produit doit être renseigné")
    String nom;
}
