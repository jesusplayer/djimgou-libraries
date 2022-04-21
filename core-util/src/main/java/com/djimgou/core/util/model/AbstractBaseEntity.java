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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


/**
 * Entité de base partagée par toutes les entités d
 */
@Data
@Check(constraints = "(utilisateur1_Id=utilisateur2_Id AND utilisateur1_Id IS NULL) OR (utilisateur1_Id<>utilisateur2_Id AND utilisateur1_Id IS NOT NULL)")
@MappedSuperclass
public abstract class AbstractBaseEntity implements Persistable<UUID>, Serializable, IUuidBaseEntity {

    @Id
    //@GeneratedValue
    @Column(name = "id", length = 16, unique = true, nullable = false)
    private UUID id = UUID.randomUUID();

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
    @PostLoad
    void markNotNew() {
        this.isNew = false;
        if (!AppUtils.has(createdDate)) {
            createdDate = Calendar.getInstance().getTime();
        }
    }

    @PostUpdate
    void onPreUpdate() {
        lastModifiedDate = Calendar.getInstance().getTime();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractBaseEntity)) return false;
        AbstractBaseEntity that = (AbstractBaseEntity) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        // return Objects.hash(getId());
        return id.hashCode();
    }

    @Override
    public void format() {
    }

    @Override
    public String toString() {
        return getId().toString();
    }
}
