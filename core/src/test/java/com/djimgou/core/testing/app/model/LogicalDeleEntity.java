package com.djimgou.core.testing.app.model;

import com.djimgou.core.annotations.LogicalDelete;
import com.djimgou.core.util.model.BaseBdEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.querydsl.core.annotations.QueryEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.Column;
import javax.persistence.Entity;


@LogicalDelete("deleted")
@FilterDef(name = "logicalDeleteFilter",
        /*parameters = {
                @ParamDef(name = "discriminator", type = "boolean")
        },*/
        defaultCondition = "deleted_discrim = false OR deleted_discrim IS NULL"
)

@Filter(name = "logicalDeleteFilter")
@Data
@AllArgsConstructor
@NoArgsConstructor
@QueryEntity
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties({"createdDate", "lastModifiedDate", "utilisateur1Id", "utilisateur2Id", "historique"})
public class LogicalDeleEntity extends BaseBdEntity {

    @Column()
    String code;

    @Column()
    String nom;

    @Column(name = "deleted_discrim")
    Boolean deleted;
}
