package com.act.core.testing.proxy;

import com.act.core.testing.app.FakeBuilder;
import com.act.core.testing.app.model.*;
import com.act.core.testing.app.service.ProduitService;
import com.act.core.testing.initilizer.DbManager;
import com.act.core.testing.app.model.dto.produit.ProduitDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;

@Service
public class ProduitServiceProxy implements IServiceProxy<Produit, ProduitDto> {
    @Autowired
    ProduitService produitService;

    @SneakyThrows
    @Override
    public Produit create(ProduitDto produitDto) {
        return produitService.create(produitDto);
    }

    @Override
    public ProduitDto fakeDto() {
        ProduitDto produitDto = new ProduitDto();
        FakeBuilder.fake(produitDto);
        Reduction r = new Reduction();
        r.setInfOuEgal(Integer.MAX_VALUE);
        r.setSupOuEgal(Integer.MAX_VALUE);
        r.setValeur(456789.0);
        produitDto.setReductions(Arrays.asList(r));



        Categorie categorie = (Categorie) DbManager.map.get(Categorie.class.getName()).getValue();
        UUID categorieId = categorie.getId();
        produitDto.setCategorieId(categorieId);

        Marque marque = (Marque) DbManager.map.get(Marque.class.getName()).getValue();
        UUID marqueId = marque.getId();
        produitDto.setMarqueId(marqueId);

        Quartier quartier = (Quartier) DbManager.map.get(Quartier.class.getName()).getValue();
        UUID quartierId = quartier.getId();
        produitDto.setQuartierId(quartierId);

        return produitDto;
    }


}
