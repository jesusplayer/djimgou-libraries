package com.djimgou.core.service;

import com.djimgou.core.cooldto.model.IEntityDetailDto;
import com.djimgou.core.cooldto.model.IEntityDto;
import com.djimgou.core.infra.BaseFilterDto;
import com.djimgou.core.infra.BaseFindDto;
import com.djimgou.core.repository.BaseJpaRepository;
import com.djimgou.core.util.model.IUuidBaseEntity;
import com.querydsl.core.types.dsl.EntityPathBase;
import lombok.extern.log4j.Log4j2;

import java.util.UUID;


/**
 * Service abstrait de gestion des opérations métiers de la base de données
 *
 * @param <T> parametre
 * @author djimgou
 */

@Log4j2
public abstract class AbstractDomainServiceV2<T extends IUuidBaseEntity, FIND_DTO extends BaseFindDto, FILTER_DTO extends BaseFilterDto, DTO extends IEntityDto, DETAIL_DTO extends IEntityDetailDto>

        extends AbstractDomainServiceBaseV2<T, FIND_DTO, FILTER_DTO, DTO, DETAIL_DTO, UUID> {

    public AbstractDomainServiceV2(BaseJpaRepository<T, UUID> repo, EntityPathBase<T> qEntity) {
        super(repo, qEntity);
    }

}
