package com.act.core.testing.app.model.dto.produit;


import com.act.core.dto.DtoClass;
import com.act.core.dto.DtoFieldDb;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

import static com.act.core.util.AppUtils.has;


@DtoClass
@Data
public class ProduitDto extends IProduitDto {

    @NotNull(message = "Le numero du quartier ne doit pas être null")
    @DtoFieldDb(targetKey = "quartier")
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
