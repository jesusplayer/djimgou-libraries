package com.act.core.service;

import com.act.core.model.AbstractBaseEntity;

import java.util.UUID;

/**
 * Interface permettant de lancer le cycle de vie après enregistrement
 * N'est valable qu'à partir de AbstractDomainServiceV2
 *
 */
public interface IAfterSave<T extends AbstractBaseEntity> {
    /**
     *
     * @param id permet d'indiquer s'il s'agit d'une creation(id=null) ou d'une modification(id!=null)
     * @param newEntity l'entité enregistrée
     * @return Par défaut, retournez newEntity
     */
    T afterSave(UUID id, T newEntity);
}
