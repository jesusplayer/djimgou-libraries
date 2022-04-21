package com.djimgou.core.cooldto.testing.app.model.dto.produit;


import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.annotations.DtoFkId;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

import static com.djimgou.core.util.AppUtils.has;


@Dto
@Data
public class ProduitDto extends IProduitDto {

    @NotNull(message = "Le numero du quartier ne doit pas être null")
    @DtoFkId(value = "quartier")
    UUID quartierId;

    @Override
    public boolean hasVilleId() {
        return hasQuartierId();
    }

    @Override
    public boolean hasQuartierId() {
        return has(getQuartierId());
    }

}
