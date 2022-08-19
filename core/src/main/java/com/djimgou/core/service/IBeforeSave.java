package com.djimgou.core.service;

import com.djimgou.core.cooldto.exception.DtoMappingException;
import com.djimgou.core.cooldto.model.IEntityDto;
import com.djimgou.core.exception.AppException;
import com.djimgou.core.exception.NotFoundException;
import com.djimgou.core.util.model.IBaseEntity;

/**
 * Interface permettant de lancer le cycle de vie avant un enregistrement
 * N'est valable qu'à partir de AbstractDomainServiceV2
 */
public interface IBeforeSave<T extends IBaseEntity, ID, DTO extends IEntityDto> {
    /**
     * Pour éffectuer des traitements customisée avant l'enregistrement
     *
     * @param id     permet d'indiquer s'il s'agit d'une creation(id=null) ou d'une modification(id!=null)
     * @param entity entité dynamiquement renseignée qui sera enregistrer
     * @param dto
     * @return Le nouvelle entité à enregistrer. Par défaut retournez la même entité
     */
    T beforeSave(ID id, T entity, DTO dto) throws DtoMappingException, AppException;
}
