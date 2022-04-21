package com.djimgou.core.service;

import com.djimgou.core.util.model.IUuidBaseEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Service abstrait de gestion des opérations métiers de la base de données
 *
 * @param <T> parametre
 * @author djimgou
 */

@Log4j2
public abstract class AbstractBdService<T extends IUuidBaseEntity> extends AbstractBdServiceBase<T, UUID> {

    public AbstractBdService() {
        super();
    }

    public AbstractBdService(JpaRepository<T, UUID> repo) {
        super(repo);
    }


}
