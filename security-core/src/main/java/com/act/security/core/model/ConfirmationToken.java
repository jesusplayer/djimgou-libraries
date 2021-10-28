package com.act.security.core.model;

import com.act.core.model.BaseBdEntity;
import lombok.Data;

import javax.persistence.*;
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





