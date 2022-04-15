/*
 * Copyright (c) 2022. Créé par DJIMGOU NKENNE Dany
 */

package com.act.core.testing.app.model.dto.produit;

import com.act.core.dto.DtoClass;
import com.act.core.dto.DtoFieldDb;
import com.act.core.testing.app.model.Quartier;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static com.act.core.util.AppUtils.has;

@DtoClass(Quartier.class)
@Data
public class Localisation {
    @NotBlank
    @NotNull

    UUID regionId;

    @DtoFieldDb(targetKey = "ville")
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
