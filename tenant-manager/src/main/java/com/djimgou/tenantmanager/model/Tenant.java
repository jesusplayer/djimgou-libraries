package com.djimgou.tenantmanager.model;

import com.djimgou.audit.model.EntityListener;
import com.djimgou.core.coolvalidation.annotations.Unique;
import com.djimgou.core.coolvalidation.annotations.Validations;
import com.djimgou.core.util.model.BaseBdEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Validations
@Data
@Entity
@Indexed
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(EntityListener.class)
public class Tenant extends BaseBdEntity {

    @Column(name = "external_id")
    private String externalId;

    @Unique(ignoreCase = true, message = "Impossible d'enregistrer ce centre car un centre de même code existe déjà")
    @Column(unique = true)
    @NotBlank()
    @KeywordField
    String code;

    /**
     * Nom agenge
     */
    @Unique(ignoreCase = true, message = "Impossible d'enregistrer ce centre car un centre de même nom existe déjà")
    @Column(unique = true, nullable = false)
    @NotBlank()
    @FullTextField
    String nom;

    @Column(nullable = false)
    @NotBlank()
    @FullTextField
    String ville;

    @ManyToOne(optional = false)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @IndexedEmbedded
    Pays pays;

    Boolean actif = Boolean.TRUE;

    Boolean readonlyValue = Boolean.FALSE;

    @PrePersist
    void beforeSave() {
        setExternalId(getId().toString());;
    }

}
