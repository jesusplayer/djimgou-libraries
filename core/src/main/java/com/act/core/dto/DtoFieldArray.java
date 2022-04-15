package com.act.core.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indique une propriété à mapper de type Array. Dans ce cas, on ne fera
 * pas de traitement particulier si ce n'est de se rassurer que le tableau cible targetKey
 * n'est pas null. Dans ce cas il faudra le créé
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DtoFieldArray {
    /**
     * Indique la cle de l'entité cible dans laquelle on va mettre la donnée.
     * Notez que targetClass.targetKey doit être du même type que la propiété
     * sur laquelle l'annotation @DtoField est posée
     * <p>
     * Si targetKey est nul, elle prend automatiquement le meme nom que la propriété
     * sur laquelle est placée @DtoField
     *
     * @return
     */
    String targetKey();
}
