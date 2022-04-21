package com.djimgou.core.cooldto.annotations;

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
 * <li>value : Le nom de la propriété dans targetEntity, dans lequel on va injecter la
 * valeur de la propriété possédant l'annotation @DtoFieldFkId. Par défaut elle prendra le
 * meme nom que celui de la propriété possédant @DtoFieldFkId. L'entité est chargée en BD dynamiquement à partir de l'ID provenant de la propiété
 * annoté @DtoFieldFkId</li>
 *
 * <li>nullable: Indique comment transformer les données. On a deux options possible:
 * <p><ul>
 * <li> nullable=true:  la propriété annoté @DtoFieldFkId peut être nulle</li>
 * <p>
 * <li> nullable=false: comportement par défaut, la propriété annoté @DtoFieldFkId ne peut pas être nulle</li>
 * </ul>
 * </li>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DtoFkId {

    /**
     * Indique la cle de targetClass dans laquelle on va mettre la donnée chargée
     * Notez que targetClass.value doit être du même type que la propiété
     * sur laquelle l'annotation @DtoFieldFkId est posée
     * <p>
     * Si value est nul, elle prend automatiquement le meme nom que la propriété
     * sur laquelle est placée @DtoFieldFkId
     *
     * @return
     */
    String[] value() default {};

    boolean nullable() default false;
}
