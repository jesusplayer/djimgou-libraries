package com.act.core.testing.app.model;

import com.act.core.model.BaseBdEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.querydsl.core.annotations.QueryEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

@Data
@QueryEntity
@Entity
@Indexed
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({"createdDate","lastModifiedDate","utilisateur1Id","utilisateur2Id","historique"})
public class Quartier extends BaseBdEntity {

    @Column(unique = true)
    @NotBlank()
    @KeywordField
    String code;

    @Column(unique = true, nullable = false)
    @NotBlank()
    @FullTextField
    String nom;

    @ManyToOne(optional = false)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @IndexedEmbedded
    Ville ville;

    /*@OneToMany(mappedBy = "region")
    Collection<Produit> produits;*/
}
