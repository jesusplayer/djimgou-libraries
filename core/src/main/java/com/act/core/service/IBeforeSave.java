package com.act.core.service;

import com.act.core.model.AbstractBaseEntity;

import java.util.UUID;

/**
 * Interface permettant de lancer le cycle de vie avant un enregistrement
 * N'est valable qu'à partir de AbstractDomainServiceV2
 */
public interface IBeforeSave<T extends AbstractBaseEntity> {
    /**
     * Pour éffectuer des traitements customisée avant l'enregistrement
     *
     * @param id     permet d'indiquer s'il s'agit d'une creation(id=null) ou d'une modification(id!=null)
     * @param entity entité dynamiquement renseignée qui sera enregistrer
     * @return Le nouvelle entité à enregistrer. Par défaut retournez la même entité
     */
    T beforeSave(UUID id, T entity);
}
