package com.djimgou.core.service;

import com.djimgou.core.util.model.IBaseEntity;

/**
 * Interface permettant de lancer le cycle de vie après enregistrement
 * N'est valable qu'à partir de AbstractDomainServiceV2
 *
 */
public interface IAfterSave<T extends IBaseEntity, ID> {
    /**
     *
     * @param id permet d'indiquer s'il s'agit d'une creation(id=null) ou d'une modification(id!=null)
     * @param newEntity l'entité enregistrée
     * @return Par défaut, retournez newEntity
     */
    T afterSave(ID id, T newEntity);
}
