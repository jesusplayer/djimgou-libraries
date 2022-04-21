package com.djimgou.core.cooldto.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * NB: Ne pas le mettre sur une propiété de type tableau
 * Annotation qui permet de faire le mapping automatique entre deux propriétés de nom 
 * différent
 * Elle prend en parametre:
 * <p><ul>
 * <li>value : Le nom de la propriété dans targetEntity, dans lequel on va injecter la
 * valeur de la propriété possédant l'annotation @DtoField. Par défaut elle prendra le
 * meme nom que celui de la propriété possédant @DtoField</li>

 * NB: Dans le cas où une proprété ne possède pas cette annotation @DtoField, un mapping simple
 * est effectué à partir des noms des propriétés du DTO.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DtoField {
    /**
     * Indique la cle de l'entité cible dans laquelle on va mettre la donnée.
     * Notez que targetClass.value doit être du même type que la propiété
     * sur laquelle l'annotation @DtoField est posée
     * <p>
     * Si value est nul, elle prend automatiquement le meme nom que la propriété
     * sur laquelle est placée @DtoField
     *
     * @return
     */
    String[] value() default {};
}
