package com.djimgou.core.coolvalidation.app.model;

import com.djimgou.core.coolvalidation.annotations.Validations;
import com.djimgou.core.coolvalidation.annotations.Unique;
import com.djimgou.core.util.model.BaseBdEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.querydsl.core.annotations.QueryEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;

@Validations
@Data
@AllArgsConstructor
@QueryEntity
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({"createdDate", "lastModifiedDate", "utilisateur1Id", "utilisateur2Id", "historique"})
public class Categorie2 extends BaseBdEntity {
    public static final String UNIQ_CODE_MSG = "Le code de la catégorie doit être unique. Il existe déjà dans la bas de données";
    public static final String UNIQ_NOM_MSG = "Le nom de la catégorie doit être unique. Il existe déjà dans la bas de données";
    @Unique(createMsg = UNIQ_CODE_MSG, ignoreCase = true)
    @Column(unique = true)
    String code;

    @Unique(createMsg = UNIQ_NOM_MSG, ignoreCase = true)
    @Column(unique = true, nullable = false)
    String nom;


}
