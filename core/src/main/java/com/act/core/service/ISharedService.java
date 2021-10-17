package com.act.core.service;

import com.act.core.model.AbstractBaseEntity;

/**
 * @author djimgou
 * Interface de partage des fonctionnalités entre différents services
 * @param <T> parametre
 */
public interface ISharedService<T extends AbstractBaseEntity> {
    /**
     * Retourne les données recus par bloomberg dans l'objet passé en paramètre
     * Les informations sont injectées dans l'objet en question
     * @param item item à metre à jour
     * @return le nouvel item avec les données recuypérées
     */
    public  T get(T item) ;
    /**
     * Permet de savoir si bloomberg est disponible
     * @return true si blp est dispo
     * */
    public boolean isOnline();
}
