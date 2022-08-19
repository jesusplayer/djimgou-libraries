package com.djimgou.core.service;

import com.djimgou.core.cooldto.exception.DtoMappingException;
import com.djimgou.core.cooldto.model.IEntityDto;
import com.djimgou.core.exception.AppException;
import com.djimgou.core.exception.NotFoundException;
import com.djimgou.core.util.model.IBaseEntity;

/**
 * Interface permettant de lancer le cycle de vie après enregistrement
 * N'est valable qu'à partir de AbstractDomainServiceV2
 */
public interface IAfterSave<T extends IBaseEntity, ID, DTO extends IEntityDto> {
    /**
     * @param id        permet d'indiquer s'il s'agit d'une creation(id=null) ou d'une modification(id!=null)
     * @param newEntity l'entité enregistrée
     * @param dto
     * @return Par défaut, retournez newEntity
     */
    T afterSave(ID id, T newEntity, DTO dto) throws DtoMappingException, AppException;
}
