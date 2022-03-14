package com.act.security.core.model;

import com.act.core.model.BaseBdEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

import static com.act.core.util.AppUtils.has;

/**
 * Entité de base partagée par toutes les entités de sécurity
 */
@Getter
@Setter
@MappedSuperclass
public abstract class SecurityBaseEntity extends BaseBdEntity {
    Boolean readonlyValue = Boolean.FALSE;
}
