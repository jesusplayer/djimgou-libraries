package com.djimgou.security.core.model;

import com.djimgou.core.util.model.BaseBdEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.UUID;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Data
@Entity
public class ConfirmationToken  extends BaseBdEntity {


    @Column(name="confirmation_token")
    private String confirmationToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    UUID utilisateurId;
/*
    @JsonBackReference("utilisateur-token")
    @OneToOne(targetEntity = Utilisateur.class)
    private Utilisateur utilisateur;*/

    public ConfirmationToken() {
    }

    public ConfirmationToken(Utilisateur utilisateur) {
        this.utilisateurId = utilisateur.getId();
        createdDate = new Date();
        confirmationToken = UUID.randomUUID().toString();
    }

    // getters and setters
}





