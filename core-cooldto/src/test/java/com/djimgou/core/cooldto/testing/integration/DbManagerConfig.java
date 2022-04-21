/*
 * Copyright (c) 2022. Créé par DJIMGOU NKENNE Dany
 */

package com.djimgou.core.cooldto.testing.integration;

import com.djimgou.core.cooldto.testing.app.model.Produit;
import com.djimgou.core.cooldto.testing.app.model.Reduction;
import com.djimgou.core.test.initilizer.GenericDbManager;

import java.util.Arrays;

public class DbManagerConfig {
    public static void initDb(GenericDbManager dbManager) {
        dbManager.potConstructDto(Produit.class, (Produit produitDto) -> {
                    Reduction r = new Reduction();
                    r.setInfOuEgal(Integer.MAX_VALUE);
                    r.setSupOuEgal(Integer.MAX_VALUE);
                    r.setValeur(456789.0);
                    produitDto.setReductions(Arrays.asList(r));
                }).ignore(Produit.class)
                .initDb();
    }
}
