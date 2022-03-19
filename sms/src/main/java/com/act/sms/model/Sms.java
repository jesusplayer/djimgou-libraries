package com.act.sms.model;


import com.act.audit.model.EntityListener;
import com.act.core.model.BaseBdEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.querydsl.core.annotations.QueryEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Lob;


/**
 * @author DJIMGOU NKENNE DANY MARC 03/2022
 */
@Entity
@QueryEntity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(value = {"enfants"})
@EntityListeners({EntityListener.class})
public class Sms extends BaseBdEntity {

    @Column(name = "from", nullable = false)
    private String from;

    @Column(name = "to", nullable = false)
    private String to;

    @Lob
    @Column(name = "text")
    private String text;


    @Override
    public String toString() {
        return getId().toString();
    }

}
