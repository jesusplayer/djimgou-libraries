package com.djimgou.core.cooldto.annotations;

import java.lang.annotation.*;

/**
 * Indique une propriété à mapper de type Array. Dans ce cas, on ne fera
 * pas de traitement particulier si ce n'est de se rassurer que le tableau cible value
 * n'est pas null. Dans ce cas il faudra le créé
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface DtoCollectionId {
    /**
     * Indique la cle de l'entité cible dans laquelle on va mettre la donnée.
     * Notez que targetClass.value doit être du même type que la propiété
     * annotée @DtoCollectionId
     * <p>
     * Si value est nul, elle prend automatiquement le meme nom que la propriété
     * sur laquelle est placée @DtoField
     *
     * @return
     */
    String[] value() default {};

    /**
     * Clé unique de discrimination des éléments de la collection
     * @return
     */
    String keyId() default "id";
}
