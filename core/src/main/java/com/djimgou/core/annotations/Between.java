package com.djimgou.core.annotations;

import java.lang.annotation.*;

/**
 * Annotation qui permet de faire la validation automatique
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Between {
    /**
     * Valeur de l'entité
     * @return
     */
    String entityField();

    /**
     * Valeur du Dto
     * @return
     */
    String value1();

    /**
     * Valeur du Dto
     * @return
     */
    String value2();
}
