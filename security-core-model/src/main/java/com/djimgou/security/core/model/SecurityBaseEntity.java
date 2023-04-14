package com.djimgou.security.core.model;

import com.djimgou.core.util.model.BaseBdEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;


/**
 * Entité de base partagée par toutes les entités de sécurity
 */
@Getter
@Setter
@MappedSuperclass
public abstract class SecurityBaseEntity extends BaseBdEntity {
    Boolean readonlyValue = Boolean.FALSE;
    Boolean deleted = Boolean.FALSE;
}
