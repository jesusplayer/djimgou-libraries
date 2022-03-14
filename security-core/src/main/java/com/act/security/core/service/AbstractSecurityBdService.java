package com.act.security.core.service;

import com.act.core.infra.BaseFilterDto;
import com.act.core.infra.BaseFindDto;
import com.act.core.exception.NotFoundException;
import com.act.core.service.AbstractDomainService;
import com.act.security.core.model.SecurityBaseEntity;
import com.act.security.core.model.StatutSecurityWorkflow;
import com.act.session.service.SessionService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.act.core.util.AppUtils.has;


@Log4j2
@Getter
@Setter
public abstract class AbstractSecurityBdService<T extends SecurityBaseEntity, S extends BaseFindDto, F extends BaseFilterDto>
        extends AbstractDomainService<T, S, F> {

    @Autowired(/*required = false*/)
    SessionService httpSession;

    public UUID currentUserId() {

        Object uid = null;
        if (has(httpSession)) {
            uid = httpSession.currentUserId();
        }
        if (has(uid)) {
            return UUID.fromString((String) uid);
        }
        return null;
    }

    public T save(T entity) {
        T item = null;
        try {
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
            getRepo().deleteById(entity.getId());
        } catch (Exception e) {
            throw e;
        }
    }

}
