package com.djimgou.core.testing.app.model;

import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.annotations.DtoField;
import com.djimgou.core.cooldto.annotations.DtoFkId;
import com.djimgou.core.cooldto.model.IEntityDto;
import lombok.Data;

import java.util.UUID;

@Data
@Dto(Categorie.class)
public class CategorieDto implements IEntityDto {
    String code;
    String nom;
    Integer annee;

    @DtoFkId(value = "parent", nullable = true)
    UUID parentId;
}
