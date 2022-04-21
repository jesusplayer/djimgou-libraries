/*
 * Copyright (c) 2022. Créé par DJIMGOU NKENNE Dany
 */

package com.djimgou.core.cooldto.testing.app.model.dto.produit;

import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.annotations.DtoFkId;
import com.djimgou.core.cooldto.testing.app.model.Quartier;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static com.djimgou.core.util.AppUtils.has;

@Dto(Quartier.class)
@Data
public class Localisation {
    @NotBlank
    @NotNull

    UUID regionId;

    @DtoFkId(value = "ville")
    UUID villeId;

    @NotBlank
    String villeCode;

    @NotBlank
    String villeNom;

    UUID quartierId;
    @NotBlank
    String quartierCode;
    @NotBlank
    String quartierNom;

    public boolean hasVilleId() {
        return has(villeId);
    }

    public boolean hasQuartierId() {
        return has(villeId);
    }
}
