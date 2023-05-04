package com.djimgou.core.annotations;

import java.lang.annotation.*;

/**
 * Annotation pour indiquer l'égalité
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface StartWith {
    String value();
}
