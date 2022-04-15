/*
 * Copyright (c) 2022. Créé par DJIMGOU NKENNE Dany
 */

package com.act.core.testing.integration;

import com.act.core.testing.app.model.Reduction;
import com.act.core.testing.app.model.dto.produit.ProduitDto;
import com.act.core.testing.initilizer.DbManager;

import java.util.Arrays;

public class DbManagerConfig {
    public static void initDb(DbManager dbManager) {
        dbManager.potConstructDto(ProduitDto.class, (ProduitDto produitDto) -> {
            Reduction r = new Reduction();
            r.setInfOuEgal(Integer.MAX_VALUE);
            r.setSupOuEgal(Integer.MAX_VALUE);
            r.setValeur(456789.0);
            produitDto.setReductions(Arrays.asList(r));
        }).initDb();
    }
}
