package com.act.core.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Indique une propriété à mapper de type objet. Dans ce cas, on ne fera
 * pas de traitement particulier si ce n'est de se rassurer que l'objet cible targetKey
 * n'est pas null. Dans ce cas il faudra le créer.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DtoFieldEntity {
    /**
     * Indique la cle de targetClass dans laquelle on va mettre la donnée chargée
     * Notez que targetClass.targetKey doit être du même type que la propiété
     * sur laquelle l'annotation @DtoFieldEntity est posée
     * <p>
     * Si targetKey est nul, elle prend automatiquement le meme nom que la propriété
     * sur laquelle est placée @DtoFieldEntity
     *
     * @return
     */
    String[] targetKey() default {};

}
