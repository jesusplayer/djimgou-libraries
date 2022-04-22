package com.djimgou.core.cooldto.annotations;

import java.lang.annotation.*;

/**
 * Annotation qui permet de faire le mapping automatique
 * d'un champ Id et son entité originale
 * Elle prend en parametre:
 * <p><ul>
 * <li>value : Le nom de la propriété dans targetEntity, dans lequel on va injecter la
 * valeur de la propriété annotatiée @DtoId. Par défaut elle prendra le
 * meme nom que celui de la propriété possédant @DtoId. L'entité est chargée en BD dynamiquement à partir de l'ID provenant de la propiété
 * annoté @DtoId. cependant on peut la modifier en ajoutant </li>
 *
 * <li>nullable: Indique comment transformer les données. On a deux options possible:
 * <p><ul>
 * <li> nullable=true:  la propriété annoté @DtoId peut être nulle</li>
 * <p>
 * <li> nullable=false: comportement par défaut, la propriété annoté @DtoId ne peut pas être nulle</li>
 * </ul>
 * </li>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface DtoId {

    boolean nullable() default false;

    /**
     * Si strategie = READONLY
     * Je me sert du Id pour charger les élément de la BD et ensuite j'e m'arrête
     * sans rien modifier
     * <p>
     * strategy=UPDATE
     * Je charge en BD, mais je remplace les éléments de l'entité par ceux qui viennet du DTO
     *
     * @return
     */
    DtoFieldIdStrategyType[] strategy() default {DtoFieldIdStrategyType.READONLY};
}
