package com.djimgou.tenantmanager.model;

import com.djimgou.audit.model.EntityListener;
import com.djimgou.core.util.model.BaseBdEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.validation.constraints.NotBlank;

/*@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "tenantId", type = "uuid")})
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")*/
@Data
@Entity
@Indexed
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(EntityListener.class)
public class Pays extends BaseBdEntity {

    @Column(unique = true)
    @NotBlank()
    @KeywordField
    String code;

    @Column(unique = true, nullable = false)
    @NotBlank()
    @FullTextField
    String nom;
}
