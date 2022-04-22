package com.djimgou.core.cooldto.annotations;

import java.lang.annotation.*;

/**
 * Annotation qui permet de faire le mapping automatique
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Dto {
    /**
     * L'entité dans laquelle depent le Dto.
     * C'est la classe de l'entité dans laquele sera injecté les valeurs du DTO
     *
     * @return
     */
    Class[] value() default {};

    String[] autoCreate() default {};

}
