package com.djimgou.tenantmanager.model;

import com.djimgou.audit.model.EntityListener;
import com.djimgou.core.coolvalidation.annotations.Unique;
import com.djimgou.core.coolvalidation.annotations.Validations;
import com.djimgou.core.util.model.BaseBdEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.validation.constraints.NotBlank;

/*@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "tenantId", type = "uuid")})
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")*/
@Data
@Validations
@Entity
@Indexed
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(EntityListener.class)
public class Pays extends BaseBdEntity {
    public static String SC = "SC";

    @Unique(ignoreCase = true, message = "Impossible d'enregistrer ce pays car un pays de même code existe déjà")
    @Column(unique = true)
    @NotBlank()
    @KeywordField
    String code;

    @Unique(ignoreCase = true, message = "Impossible d'enregistrer ce pays car un pays de même nom existe déjà")
    @Column(unique = true, nullable = false)
    @NotBlank()
    @FullTextField
    String nom;

    Boolean readonlyValue = Boolean.FALSE;
}
