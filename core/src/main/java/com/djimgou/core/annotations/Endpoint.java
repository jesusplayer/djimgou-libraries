package com.djimgou.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Permet de décrire un pribilege de sorte que l'utilisateur puisse
 * reconnaître
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Endpoint {
    /**
     * La description du edpoint qui sera automatiquement injectée dans le ôle en BD
     *
     * @return
     */
    String value();

    /**
     * Pour indiquer qusi une methode est une lecture bien qu'elle ne soit pas avec GET
     * Ainsi un utilisateur de profil READONLY pourra y avoir accès
     *
     * @return
     */
    boolean readOnlyMethod() default false;
}
