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
    /**
     * Indique si l'entité est une dirty ou une copie
     */
    Boolean dirty = Boolean.FALSE;

    UUID dirtyValueId;

    @Column(name = "commentaire_admin_crea", length = 500)
    String commentaireAdmin1;

    /**
     * première partie du mot de passe à voir par l'admin createur
     */

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_creation")
    StatutSecurityWorkflow statutCreation = StatutSecurityWorkflow.EN_ATTENTE_DE_VALIDATION;

    //    @ManyToOne
    UUID adminValidateurId;

    UUID adminSuppresseurId;

    /**
     * Utilisateur ayant créé ou étant le premier initiateur
     * d'une modification. Son action doit être validée par un second utilisateur
     * avant d'entrer en production
     */
    UUID adminCreateurId;

    @Column(name = "commentaire_admin_valid", length = 500)
    String commentaireAdminValidateur;

//    UUID cleanEntityId;
//    Object dirtyValue;

    public void setStatutWorkflow(StatutSecurityWorkflow statutSecurityWorkflow) {
        setStatutCreation(statutSecurityWorkflow);
    }

    public void updateValidateur(UUID validateurId) {
        setAdminValidateurId(validateurId);
    }

    public void updateCreateur(UUID createurId) {
        if (has(createurId) ){
            setAdminCreateurId(createurId);
        }
    }

    public void updateSuppresseur(UUID suppresseurId) {
        setAdminSuppresseurId(suppresseurId);
    }

}
