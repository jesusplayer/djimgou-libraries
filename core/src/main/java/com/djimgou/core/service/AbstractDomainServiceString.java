package com.djimgou.core.service;

import com.djimgou.core.cooldto.model.IEntityDetailDto;
import com.djimgou.core.cooldto.model.IEntityDto;
import com.djimgou.core.infra.BaseFilterDto;
import com.djimgou.core.infra.BaseFindDto;
import com.djimgou.core.repository.BaseJpaRepository;
import com.djimgou.core.util.model.IBaseEntity;
import com.querydsl.core.types.dsl.EntityPathBase;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class AbstractDomainServiceString<T extends IBaseEntity<String>, FIND_DTO extends BaseFindDto, FILTER_DTO extends BaseFilterDto, DTO extends IEntityDto, DETAIL_DTO extends IEntityDetailDto>

        extends AbstractDomainServiceBaseV2<T, FIND_DTO, FILTER_DTO, DTO, DETAIL_DTO, String> {

    public AbstractDomainServiceString(BaseJpaRepository<T, String> repo, EntityPathBase<T> qEntity) {
        super(repo, qEntity);
    }

}