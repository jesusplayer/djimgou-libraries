package com.djimgou.core.cooldto.testing.app.model.dto.produit;


import com.djimgou.core.cooldto.model.IDto;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
public class ModifierComissionDto implements IDto {
    @NotNull()
    Double commission;
}
