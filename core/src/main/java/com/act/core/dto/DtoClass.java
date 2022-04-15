package com.act.core.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation qui permet de faire le mapping automatique
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DtoClass {
    /**
     * L'entité dans laquelle depent le Dto.
     * C'est la classe de l'entité dans laquele sera injecté les valeurs du DTO
     * @return
     */
    Class[] value() default {};
}
