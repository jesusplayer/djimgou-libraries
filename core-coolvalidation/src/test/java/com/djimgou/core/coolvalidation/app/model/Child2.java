package com.djimgou.core.coolvalidation.app.model;

import com.djimgou.core.coolvalidation.annotations.Validations;
import com.djimgou.core.util.model.BaseBdEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.querydsl.core.annotations.QueryEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Validations
@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({"createdDate", "lastModifiedDate", "utilisateur1Id", "utilisateur2Id", "historique"})
public class Child2 extends BaseBdEntity {
    String code;
    
    @ManyToOne
    Parent parent;

    @ManyToOne
    Parent2 parent2;
}
