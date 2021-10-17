package com.act.security.service;

import com.act.core.infra.BaseFilterDto;
import com.act.core.infra.BaseFindDto;
import com.act.core.exception.NotFoundException;
import com.act.core.service.AbstractDomainService;
import com.act.security.model.SecurityBaseEntity;
import com.act.security.model.StatutSecurityWorkflow;
import com.act.core.model.enums.SessionKeys;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.UUID;

import static com.act.core.util.AppUtils.has;


@Log4j2
@Getter
@Setter
public abstract class AbstractSecurityBdService<T extends SecurityBaseEntity, S extends BaseFindDto, F extends BaseFilterDto>
        extends AbstractDomainService<T, S, F> {

    @Autowired
    HttpSession httpSession;

    public UUID currentUserId(){
        Object uid = httpSession.getAttribute(SessionKeys.CONNECTED_USER_ID);
        if(has(uid)){
            return UUID.fromString((String) uid);
        }
        return null;
    }
    /**
     * * Validation entité:
     * * - Je recherche sa copie je la met au statut validé
     * * - Je merge les propriétés de la copie(sauf l'ID) dans l'entité
     * * - Je supprime la dirty
     *
     * @param entity                     entité à traiter
     * @param validateurId               id du validateur
     * @param commentaireAdminValidateur commentaire du validateur
     * @param id                         id de l'entite
     * @param statutSecurityWorkflow     statut workflow
     */
    public abstract void validateEntity(T entity, StatutSecurityWorkflow statutSecurityWorkflow, UUID validateurId, String commentaireAdminValidateur, UUID id);

    public T save(T entity) {
        T item = null;
        try {
            if (entity.isNew()) entity.updateCreateur(currentUserId());
            item = getRepo().save(entity);
        } catch (Exception e) {
            log.error("Erreur d'enregistrement", e);
        }
        return item;
    }

    //    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public T save(T entity, T oldEntity) {
        return this.save(entity);
    }

    /**
     * @param entity entite
     * @return entite
     */
    @Transactional
    public T markEntityDeleted(T entity) {
        T ent = searchById(entity.getId());
        ent.setStatutCreation(StatutSecurityWorkflow.SUPPRIMER);
        ent.updateSuppresseur(currentUserId());
        ent.updateCreateur(currentUserId());
        return this.save(ent);
    }

    /**
     * Annuler la suppression d'une entité
     *
     * @param entity entité
     * @return l'entité
     * @throws Exception exception
     */
    public T revokeEntityDeleted(T entity) throws Exception {
        T ent = searchById(entity.getId());
        if (has(ent.getDirtyValueId())) {
            T dirty = searchById(ent.getDirtyValueId());
            ent.setStatutCreation(StatutSecurityWorkflow.VALIDE);
            ent.setDirtyValueId(null);
            this.delete(dirty);
        } else {
            ent.setStatutCreation(StatutSecurityWorkflow.EN_ATTENTE_DE_VALIDATION);
        }
        ent.updateValidateur(currentUserId());
        return this.save(ent);
    }

    public T revokeModifiedEntity(T entity) throws Exception {
        T ent = searchById(entity.getId());
        if (has(ent.getDirtyValueId())) {
            T dirty = searchById(ent.getDirtyValueId());
            ent.setStatutCreation(StatutSecurityWorkflow.VALIDE);
            this.delete(dirty);
        } else {
            ent.setStatutCreation(StatutSecurityWorkflow.EN_ATTENTE_DE_VALIDATION);
        }
        ent.setDirty(null);
        ent.setDirtyValueId(null);
        ent.updateValidateur(currentUserId());
        return this.save(ent);
    }

    /**
     * Supprime l'entité. Implémenter cette methode au besoin dans le cas
     * où l'entité possède des fils
     *
     * @param entity entité
     * @throws Exception exception
     */
    @Transactional
    @Override
    public void delete(T entity) throws NotFoundException {
        try {
            if (has(entity.getDirtyValueId()) && existsById(entity.getDirtyValueId())) {
                getRepo().deleteById(entity.getDirtyValueId());
            }
            getRepo().deleteById(entity.getId());
        } catch (Exception e) {
            throw e;
        }
    }

}
