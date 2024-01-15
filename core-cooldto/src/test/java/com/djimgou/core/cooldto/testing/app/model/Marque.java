package com.djimgou.core.cooldto.testing.app.model;

import com.djimgou.core.util.model.BaseBdEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.querydsl.core.annotations.QueryEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@QueryEntity
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({"createdDate", "lastModifiedDate", "utilisateur1Id", "utilisateur2Id", "historique"})
public class Marque extends BaseBdEntity {

    @Column(unique = true)
    String code;

    @Column(unique = true, nullable = false)
    String nom;

    @OneToMany
    List<Categorie> categories;

    public Marque() {
        this.categories = new ArrayList<>();
    }


}
