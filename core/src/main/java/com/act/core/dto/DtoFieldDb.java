package com.act.core.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * NB: Ne pas le mettre sur une propiété de type tableau
 * Cette annotation se met sur une propriété de type Objet qui herite de IEntityDto
 *
 * Annotation qui permet de faire le mapping automatique
 * d'un champ et son entité originale
 * Elle prend en parametre:
 * <p><ul>
 * <li>targetKey : Le nom de la propriété dans targetEntity, dans lequel on va injecter la
 * valeur de la propriété possédant l'annotation @DtoFieldDb. Par défaut elle prendra le
 * meme nom que celui de la propriété possédant @DtoFieldDb. L'entité est chargée en BD dynamiquement à partir de l'ID provenant de la propiété
 * annoté @DtoFieldDb</li>
 *
 * <li>nullable: Indique comment transformer les données. On a deux options possible:
 * <p><ul>
 * <li> nullable=true:  la propriété annoté @DtoFieldDb peut être nulle</li>
 * <p>
 * <li> nullable=false: comportement par défaut, la propriété annoté @DtoFieldDb ne peut pas être nulle</li>
 * </ul>
 * </li>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DtoFieldDb {

    /**
     * Indique la cle de targetClass dans laquelle on va mettre la donnée chargée
     * Notez que targetClass.targetKey doit être du même type que la propiété
     * sur laquelle l'annotation @DtoFieldDb est posée
     * <p>
     * Si targetKey est nul, elle prend automatiquement le meme nom que la propriété
     * sur laquelle est placée @DtoFieldDb
     *
     * @return
     */
    String[] targetKey() default {};

    boolean nullable() default false;
}
