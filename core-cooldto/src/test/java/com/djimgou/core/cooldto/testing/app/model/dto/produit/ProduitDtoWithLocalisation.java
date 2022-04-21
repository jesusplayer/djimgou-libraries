package com.djimgou.core.cooldto.testing.app.model.dto.produit;


import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.annotations.DtoEntityField;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

import static com.djimgou.core.util.AppUtils.has;


/***
 * Crée un produit avec la localisation
 */
@Dto
@Data
public class ProduitDtoWithLocalisation extends IProduitDto {
    @NotNull
    @DtoEntityField(value = "quartier")
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
