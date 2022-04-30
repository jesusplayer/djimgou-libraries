/*
 * Copyright (c) 2022. Créé par DJIMGOU NKENNE Dany
 */

package com.djimgou.core.cooldto.testing.app.model.dto.produit;

import com.djimgou.core.cooldto.annotations.DtoFkId;
import com.djimgou.core.cooldto.model.IEntityDto;
import com.djimgou.core.cooldto.testing.app.model.Reduction;
import com.djimgou.core.cooldto.testing.app.model.enums.TypeDeBoite;
import com.djimgou.core.cooldto.testing.app.model.enums.TypeEnergie;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public abstract class IProduitDto implements IEntityDto {
    @NotNull(message = "Le numero de la marque ne doit pas être null")
    @DtoFkId(value = "marque")
    UUID marqueId;

    @NotNull(message = "Le numero de la categorie ne doit pas être null")
    @DtoFkId(value = "categorie")
    UUID categorieId;


    String commentaire;

    @NotBlank(message = "Le matricule ne doit pas être vide")
    String matricule;

    @NotBlank(message = "La version ne doit pas être vide")
    String version;

    @Positive(message = "Le kilometrage doit être strictement positif")
    Long kilometrage;

    @NotNull(message = "Le type de boîte ne doit être null")
    TypeDeBoite typeDeBoite = TypeDeBoite.MANUELLE;

    String carburant;

    @NotNull(message = "Le prix par jour doit être renseigné")
    Double prixParJour;

    @NotNull(message = "Le prix par heure doit être renseigné")
    Double prixParHeure;

    Double pourcentRedParJour = 0.0;
    /**
     * pourcentage de reduction Horaire
     */
    Double pourcentRedParHeure = 0.0;

    /**
     * Nombre de places
     */
    @Positive
    Integer capacite;
    /**
     * Nombre de portes
     */
    Integer nbPortes;
    /**
     * Climatisation
     */
    Boolean climatisation = Boolean.FALSE;
    /**
     * Le type de moteur Essence ou Diesel
     */
    TypeEnergie typeEnergie = TypeEnergie.ESSENCE;


    String statut;

    String contact;
//    /**
//     * Indique si le produit peut être loué ou pas. dans ce cas il sera vue par le client
//     * Sinon seul l'administrateur pourra voir le produit
//     */
//    Boolean enLocation = false;

    private List<Reduction> reductions;


    public abstract UUID getQuartierId();

    public abstract void setQuartierId(UUID quartierId);

    public abstract boolean hasVilleId();

    public abstract boolean hasQuartierId();

}
