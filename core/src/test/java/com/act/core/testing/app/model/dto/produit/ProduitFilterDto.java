package com.act.core.testing.app.model.dto.produit;


import com.act.core.infra.BaseFilterDto;
import com.act.core.testing.app.model.enums.TypeDeBoite;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;


@Data
public class ProduitFilterDto extends BaseFilterDto {

    @NotNull()
    UUID marqueId;

    @NotNull
    UUID categorieId;

    @NotNull
    UUID quartierId;

    @NotNull
    UUID partenaireId;

    String commentaire;


    @NotBlank()
    String version;

    @NotBlank()
    String kilometrage;

    @NotNull()
    TypeDeBoite typeDeBoite =  TypeDeBoite.MANUELLE;

    String carburant;

   // String localisation;

    Double prixParJour = 0.0;

    Double prixparHeure = 0.0;

    String statut;

    @NotBlank()
    String contact;
    /**
     * Indique si le produit peut être loué ou pas. dans ce cas il sera vue par le client
     * Sinon seul l'administrateur pourra voir le produit
     */
    Boolean enLocation = Boolean.TRUE;
    /**
     * Permet à l'administrateur d'autauriser un produit ou pas
     */
    Boolean actif = Boolean.TRUE;
}
