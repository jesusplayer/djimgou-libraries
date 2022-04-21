package com.djimgou.core.cooldto.testing.app.model;

import com.djimgou.core.util.model.BaseBdEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.querydsl.core.annotations.QueryEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@QueryEntity

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({"createdDate","lastModifiedDate","utilisateur1Id","utilisateur2Id","historique"})
public class Region extends BaseBdEntity {

    @Column(unique = true)
    String code;

    @Column(unique = true, nullable = false)
    String nom;


    /*@OneToMany(mappedBy = "region")
    Collection<Produit> produits;*/
}
