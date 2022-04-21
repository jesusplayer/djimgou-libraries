package com.djimgou.core.cooldto.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indique une propriété à mapper de type Array. Dans ce cas, on ne fera
 * pas de traitement particulier si ce n'est de se rassurer que le tableau cible value
 * n'est pas null. Dans ce cas il faudra le créé
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DtoCollection {
    /**
     * Indique la cle de l'entité cible dans laquelle on va mettre la donnée.
     * Notez que targetClass.value doit être du même type que la propiété
     * annotée @DtoCollection
     * <p>
     * Si value est nul, elle prend automatiquement le meme nom que la propriété
     * sur laquelle est placée @DtoField
     *
     * @return
     */
    String[] value() default {};

    /**
     * Nom de la clé identifiant des éléments de la collection
     * @return
     */
    String keyId() default "id";

    /**
     * Pour mettre à jour automatiquement les éléments de la collection dans la base de donnée
     * Ainsi lorsque la strategy sera ADD_NEW ou DELETE_ORPHANS on pourra faire le traitement en
     * BD
     * @return
     */
    boolean persist() default false;
}
