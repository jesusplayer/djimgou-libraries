package com.act.core.testing.app.model.dto.produit;


import com.act.core.model.IDto;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class ModifierComissionDto implements IDto {
    @NotNull()
    Double commission;
}
