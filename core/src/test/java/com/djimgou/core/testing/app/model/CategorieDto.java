package com.djimgou.core.testing.app.model;

import com.djimgou.core.cooldto.annotations.Dto;
import lombok.Data;

@Data
@Dto(Categorie.class)
public class CategorieDto {
    String code;
    String nom;
}
