/*
 * Copyright (c) 2022. Créé par DJIMGOU NKENNE Dany
 */

package com.djimgou.core.testing.app.model;

import com.djimgou.core.cooldto.annotations.DtoField;
import com.djimgou.core.infra.BaseFilterAdvancedDto;
import com.djimgou.core.infra.QueryFieldFilter;
import lombok.Data;

import java.util.UUID;

@Data
public class CategorieFilterAdvDto extends BaseFilterAdvancedDto {

    String code;

    String nom;

    Integer annee;


    @DtoField("parent.id")
    UUID parentId;

}
