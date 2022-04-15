package com.act.core.testing.assertions;

import com.act.core.testing.app.model.Produit;
import com.act.core.testing.app.model.Quartier;
import com.act.core.testing.app.model.Ville;
import com.act.core.testing.app.model.dto.produit.IProduitDto;
import com.act.core.testing.app.model.dto.produit.Localisation;
import com.act.core.testing.app.model.dto.produit.ProduitDtoWithLocalisation;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.act.core.util.AppUtils.has;
import static org.junit.jupiter.api.Assertions.*;

public class ProduitAssertion {
    public static void assertProduitEquals(IProduitDto dto, Produit produit) {
        boolean isId;
        boolean hasDto;
        List<Boolean> isIdEquals = new ArrayList<>();
        assertProduitKeysWithouthRegionEquals(dto, produit, isIdEquals);
        hasDto = has(dto.getQuartierId());
        if (hasDto) {
            isId = has(produit.getQuartier()) && Objects.equals(dto.getQuartierId(), produit.getQuartier().getId());
        } else {
            isId = !has(produit.getQuartier()) || Objects.equals(dto.getQuartierId(), produit.getQuartier().getId());
        }
        isIdEquals.add(isId);

        Assertions.assertTrue(isIdEquals.stream().allMatch(a -> a));

        assertNonKeyFieldsEquals(dto, produit);
    }

    public static void assertProduitNewVilleAndQuartierEquals(ProduitDtoWithLocalisation dto, Produit produit) {
        boolean isId;
        List<Boolean> isIdEquals = new ArrayList<>();
        assertProduitKeysWithouthRegionEquals(dto, produit, isIdEquals);
        Assertions.assertTrue(isIdEquals.stream().allMatch(a -> a));

        final Localisation loc = dto.getLocalisation();
        Assertions.assertNotNull(loc);

        final Quartier quartier = produit.getQuartier();
        Assertions.assertNotNull(quartier);

        final Ville ville = produit.getQuartier().getVille();
        Assertions.assertNotNull(ville);

        if (dto.hasQuartierId()) {
            isId = has(quartier) && Objects.equals(dto.getQuartierId(), quartier.getId());
        } else {
            assertEquals(loc.getQuartierCode(), quartier.getCode());
            assertEquals(loc.getQuartierNom(), quartier.getNom());
            assertEquals(loc.getVilleCode(), ville.getCode());
            assertEquals(loc.getVilleNom(), ville.getNom());
            isId = true;
        }
        isIdEquals.add(isId);

        assertNonKeyFieldsEquals(dto, produit);
    }

    private static void assertProduitKeysWithouthRegionEquals(IProduitDto dto, Produit produit, List<Boolean> isIdEquals) {
        boolean hasDto;
        boolean isId;

        hasDto = has(dto.getCategorieId());
        if (hasDto) {
            isId = has(produit.getCategorie()) && Objects.equals(dto.getCategorieId(), produit.getCategorie().getId());
        } else {
            isId = !has(produit.getCategorie()) || Objects.equals(dto.getCategorieId(), produit.getCategorie().getId());
        }
        isIdEquals.add(isId);

        hasDto = has(dto.getMarqueId());
        if (hasDto) {
            isId = has(produit.getMarque()) && Objects.equals(dto.getMarqueId(), produit.getMarque().getId());
        } else {
            isId = !has(produit.getMarque()) || Objects.equals(dto.getMarqueId(), produit.getMarque().getId());
        }
        isIdEquals.add(isId);
    }

    private static void assertNonKeyFieldsEquals(IProduitDto dto, Produit produit) {
        assertEquals(dto.getCarburant(), produit.getCarburant());
        assertEquals(dto.getCommentaire(), produit.getCommentaire());
        assertEquals(dto.getContact(), produit.getContact());
        // assertEquals(dto.getEmail(), produit.getEmail());
        // assertEquals(dto.getEnLocation(), produit.getEnLocation());
        //assertEquals(dto.getPourcentReduction(), produit.getPourcentReduction());
        assertEquals(dto.getStatut(), produit.getStatut());
        //assertEquals(dto.getTelephone(), produit.getTelephone());
        assertEquals(dto.getTypeDeBoite(), produit.getTypeDeBoite());
        assertEquals(dto.getVersion(), produit.getVersion());
        //assertEquals(dto.getCommission(), produit.getCommission());
        assertEquals(dto.getMatricule(), produit.getMatricule());
        assertEquals(dto.getKilometrage(), produit.getKilometrage());

        assertEquals(dto.getCapacite(), produit.getCapacite());
        assertEquals(dto.getNbPortes(), produit.getNbPortes());
        assertEquals(dto.getPrixParHeure(), produit.getPrixParHeure());
        assertEquals(dto.getPrixParJour(), produit.getPrixParJour());

        assertEquals(dto.getPourcentRedParHeure(), produit.getPourcentRedParHeure());
        assertEquals(dto.getPourcentRedParJour(), produit.getPourcentRedParJour());
        assertEquals(dto.getClimatisation(), produit.getClimatisation());
        assertEquals(dto.getTypeEnergie(), produit.getTypeEnergie());

    }
    /*public static void updateChildrenDto(Produit produit, ProduitDto produitDto){
        
        produit.setPartenaireIdOb(partenaireIdOb);
        
        produit.setCategorieIdOb(categorieIdOb);
        
        produit.setMarqueIdOb(marqueIdOb);
        
        produit.setQuartierIdOb(quartierIdOb);
        
    }*/
}
