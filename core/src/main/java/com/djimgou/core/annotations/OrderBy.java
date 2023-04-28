package com.djimgou.core.annotations;

import java.lang.annotation.*;

/**
 * Annotation qui permet de faire la validation automatique
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface OrderBy {
    /**
     * Nom de la propriété de l'entité à ordoner
     *
     * @return
     */
    String[] value();

}