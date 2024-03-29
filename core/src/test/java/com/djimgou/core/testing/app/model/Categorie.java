package com.djimgou.core.testing.app.model;

import com.djimgou.core.util.model.BaseBdEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.querydsl.core.annotations.QueryEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Data
@QueryEntity
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({"createdDate", "lastModifiedDate", "utilisateur1Id", "utilisateur2Id", "historique"})
public class Categorie extends BaseBdEntity {

    @Column(unique = true)
    String code;

    @Column(unique = true, nullable = false)
    String nom;

    @Column(unique = true)
    Integer annee;

    @ManyToOne()
    Parent parent;
}
