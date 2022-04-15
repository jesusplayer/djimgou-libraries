package com.act.core.testing.app.model.dto.categorie;

import com.act.core.infra.BaseFilterDto;
import lombok.Data;

@Data
public class CategorieFilterDto extends BaseFilterDto {
    //@NotBlank()
    String code;

    //@NotBlank()
    String nom;
}
