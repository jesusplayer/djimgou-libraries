package com.djimgou.audit.model;

import com.djimgou.core.util.model.BaseBdEntity;
import com.querydsl.core.annotations.QueryEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
@Data
@QueryEntity
@Table(name = "audit_table")
@Entity
public class Audit extends BaseBdEntity {
    /**
     * Eviter d'utiliser le nom date comme nom de colonne sinon oracle ne pourra pas accepter
     * Généralement aucune exception n'est générée, mais la table ne sera pas créer et l'ORM
     * va planter
     *
     * @author djimgou
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_valeur", nullable = false)
    Date date = Calendar.getInstance().getTime();

    /**
     * Utile pour filter les dates
     */
    @Transient
    Date dateDebut;

    /**
     * Utile pour filter les dates
     */
    @Transient
    Date dateFin;

    /**
     * JSON de la donnéé enregistréé ou suppriméé
     */
    @Lob
    String data;


    /**
     * Nom de l'entité de base de donnéé à laquelle
     * l'operation a été effectuée
     * Ex: Devise, SuiviDevise,
     */
    @Column(name = "nom_entite")
    String nomEntite;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    AuditAction action;

    @Column(name = "utilisateur_id")
    UUID utilisateurId;

    @Column(name = "username")
    private String username;

    public Audit(Date date, String data, String nomEntite, AuditAction action, UUID userId, String username) {
        this.date = date;
        this.data = data;
        this.nomEntite = nomEntite;
        this.action = action;

        this.utilisateurId = userId;
        this.username = username;

    }

    public Audit() {
    }
}
