package com.djimgou.core.cooldto.annotations;

public enum DtoFieldIdStrategyType {
    /**
     * Je me sert du Id pour charger les élément de la BD et ensuite j'e m'arrête
     * sans rien modifier
     */
    READONLY,
    /**
     * Je charge en BD, mais je remplace les éléments de l'entité par ceux qui viennet du DTO
     */
    UPDATE
}
