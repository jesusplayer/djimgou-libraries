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
public abstract class AbstractGenericBaseEntity<ID> implements Persistable<ID>, Serializable, IBaseEntity<ID> {

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Date lastModifiedDate;

    @CreatedDate
    @Column(name = "created_date")
    private Date createdDate;

    @Transient
    @JsonIgnore
    private boolean isNew = true;

    @Override
    @JsonIgnore
    public boolean isNew() {
        return isNew;
    }

    @Override
    public void changeIsNew(Boolean value) {
        isNew = value;
    }

    @PrePersist
    void postPersist() {
        if (isNew() && getId() == null) {
            setId(randomId());
        }
        markNotNew();
    }

    @PostLoad
    void markNotNew() {
        this.isNew = false;
        if (!AppUtils.has(createdDate)) {
            createdDate = Calendar.getInstance().getTime();
        }
    }

    public abstract ID randomId();

    @PostUpdate
    void onPreUpdate() {
        lastModifiedDate = Calendar.getInstance().getTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractGenericBaseEntity)) return false;
        AbstractGenericBaseEntity that = (AbstractGenericBaseEntity) o;
        return getId().equals(that.getId());
    }


    @Override
    public void format() {
    }

    @Override
    public String toString() {
        return getId().toString();
    }
}
