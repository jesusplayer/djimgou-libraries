/*
 * Copyright (c) 2022. Créé par DJIMGOU NKENNE Dany
 */

package com.djimgou.core.testing.app.model;

import com.djimgou.core.cooldto.annotations.DtoField;
import com.djimgou.core.infra.BaseFilterDto;
import com.djimgou.core.infra.QueryFieldFilter;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class CategorieFilterDto extends BaseFilterDto {

    QueryFieldFilter<String> code;

    QueryFieldFilter<String> nom;

    QueryFieldFilter<Integer> annee;


    @DtoField("parent.id")
    UUID parentId;

}
