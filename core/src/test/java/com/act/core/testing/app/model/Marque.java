package com.act.core.testing.app.model;

import com.act.core.model.BaseBdEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.querydsl.core.annotations.QueryEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@Indexed
@QueryEntity
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({"createdDate","lastModifiedDate","utilisateur1Id","utilisateur2Id","historique"})
public class Marque extends BaseBdEntity {

    @Column(unique = true)
    @NotBlank()
    @KeywordField
    String code;

    @Column(unique = true, nullable = false)
    @NotBlank()
    @FullTextField
    String nom;
}
