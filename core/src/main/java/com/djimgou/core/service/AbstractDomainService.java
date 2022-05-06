package com.djimgou.core.service;

import com.djimgou.core.infra.BaseFilterDto;
import com.djimgou.core.infra.BaseFindDto;
import com.djimgou.core.util.model.IUuidBaseEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


/**
 * Service abstrait de gestion des opérations métiers de la base de données
 *
 * @param <T> parametre
 * @author djimgou
 */

@Log4j2
public abstract class AbstractDomainService<T extends IUuidBaseEntity, FIND_DTO extends BaseFindDto, F extends BaseFilterDto>
        extends AbstractDomainServiceBase<T, FIND_DTO, F, UUID> {

    public AbstractDomainService(JpaRepository<T, UUID> repo) {
        super(repo);
    }

    @Transactional
    @Override
    public Page<T> advancedFindBy(BaseFilterDto baseFilter) throws Exception {
        return findBy((F) baseFilter);
    }
}
