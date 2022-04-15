package com.act.core.testing.app.model.dto.produit;


import com.act.core.dto.DtoClass;
import com.act.core.dto.DtoFieldEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

import static com.act.core.util.AppUtils.has;


/***
 * Crée un produit avec la localisation
 */
@DtoClass
@Data
public class ProduitDtoWithLocalisation extends IProduitDto {
    @NotNull
    @DtoFieldEntity(targetKey = "quartier")
    Localisation localisation;

    @Override
    public UUID getQuartierId() {
        return this.getLocalisation().getQuartierId();
    }

    @Override
    public void setQuartierId(UUID quartierId) {
        this.getLocalisation().setQuartierId(quartierId);
    }

    @Override
    public boolean hasVilleId() {
        return this.getLocalisation().hasVilleId();
    }

    @Override
    public boolean hasQuartierId() {
        return has(getQuartierId());
    }

}
