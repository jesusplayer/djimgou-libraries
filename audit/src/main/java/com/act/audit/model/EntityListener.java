package com.act.audit.model;

import com.act.audit.service.AuditBdService;
import com.act.core.model.AbstractBaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.function.Supplier;

/**
 * @author djimgou
 * Listener d'écoute des enregistrement et mise à jour à la base de données
 */
@Component
public class EntityListener {
    private static AuditBdService auditBdService;

    @Autowired
    public void init(AuditBdService auditBdService) {
        EntityListener.auditBdService = auditBdService;
    }

    /**
     * On evalue l'entité JPA avant de l'enregistrer dans la base de données
     * Dans le cas d'un SuiviEntité, on passe la variable globale notationManager
     *
     * @param entity entite
     * @param <T>    parametre
     */
    @PrePersist
    public <T extends AbstractBaseEntity> void prePersist(T entity) {

    }

    /**
     * Ici on a la certitude que l'entité a été modifiée
     *
     * @param entity entite
     * @param <T>    parametre
     */
    @PreUpdate// @PreUpdate
    public <T extends AbstractBaseEntity> void preUpdate(T entity) {
        prePersist(entity);
    }

    @PostPersist
    public <T extends AbstractBaseEntity> void creation(T entity) {
        nonNull(() -> EntityListener.auditBdService.add(entity, AuditAction.CREATION));
    }

    @PostUpdate
    public <T extends AbstractBaseEntity> void modification(T entity) {
        nonNull(() -> EntityListener.auditBdService.add(entity, AuditAction.MODIFICATION));
    }

    @PostRemove
    public <T extends AbstractBaseEntity> void suppression(T entity) {
        nonNull(() -> EntityListener.auditBdService.add(entity, AuditAction.SUPPRESSION));
    }

    void nonNull(Supplier fn) {
        if (EntityListener.auditBdService != null) {
            fn.get();
        }
    }
}
