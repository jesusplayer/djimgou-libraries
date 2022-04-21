package com.djimgou.core.cooldto.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Indique une propriété à mapper de type objet. Dans ce cas, on ne fera
 * pas de traitement particulier si ce n'est de se rassurer que l'objet cible value
 * n'est pas null. Dans ce cas il faudra le créer.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DtoEntityField {
    /**
     * Indique la cle de targetClass dans laquelle on va mettre la donnée chargée
     * Notez que targetClass.value doit être du même type que la propiété
     * sur laquelle l'annotation @DtoEntityField est posée
     * <p>
     * Si value est nul, elle prend automatiquement le meme nom que la propriété
     * sur laquelle est placée @DtoEntityField
     *
     * @return
     */
    String[] value() default {};

}
