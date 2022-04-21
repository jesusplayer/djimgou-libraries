package com.djimgou.core.cooldto.annotations;

public enum DtoCollectionStrategyType {
    /**
     * Indique si on ajoute les éléments de la collection DTO lorsqu'ils ne sont pas présent
     * dans la collection de destination.
     * On se servira de la condition DTO.keyId = null
     */
    ADD_NEW,
    /**
     * Indique qu'on supprime les ophelins, c'est à dire les entité de la collection de destination
     * qui ne sont pas présente dans la collection DTO.
     * On se servira de la propriété keyId pour faire la recherche
     */
    DELETE_ORPHANS
}
