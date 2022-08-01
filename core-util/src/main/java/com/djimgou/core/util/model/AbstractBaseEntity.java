package com.djimgou.core.util.model;

import com.djimgou.core.util.AppUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Check;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


/**
 * Entité de base partagée par toutes les entités d
 */
@Data
@Check(constraints = "(utilisateur1_Id=utilisateur2_Id AND utilisateur1_Id IS NULL) OR (utilisateur1_Id<>utilisateur2_Id AND utilisateur1_Id IS NOT NULL)")
@MappedSuperclass
public abstract class AbstractBaseEntity extends AbstractGenericBaseEntity<UUID> {

    @Id
    //@GeneratedValue
    @Column(name = "id", length = 16, unique = true, nullable = false)
    private UUID id;

    @Override
    public UUID randomId() {
        return UUID.randomUUID();
    }
}
