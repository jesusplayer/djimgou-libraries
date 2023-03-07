package com.djimgou.audit.model;

import com.djimgou.audit.service.AuditBdService;
import com.djimgou.core.util.model.IBaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
    public <T extends IBaseEntity> void prePersist(T entity) {

    }

    /**
     * Ici on a la certitude que l'entité a été modifiée
     *
     * @param entity entite
     * @param <T>    parametre
     */
    //@Async
    @PreUpdate// @PreUpdate
    public <T extends IBaseEntity> void preUpdate(T entity) {
        prePersist(entity);
    }
    //@Async
    @PostPersist
    public <T extends IBaseEntity> void creation(T entity) {
        nonNull(() -> EntityListener.auditBdService.addAsync(entity, AuditAction.CREATION));
    }
    //@Async
    @PostUpdate
    public <T extends IBaseEntity> void modification(T entity) {
        nonNull(() -> EntityListener.auditBdService.addAsync(entity, AuditAction.MODIFICATION));
    }
    //@Async
    @PostRemove
    public <T extends IBaseEntity> void suppression(T entity) {
        nonNull(() -> EntityListener.auditBdService.addAsync(entity, AuditAction.SUPPRESSION));
    }

    void nonNull(Supplier fn) {
        if (EntityListener.auditBdService != null) {
            fn.get();
        }
    }
}
