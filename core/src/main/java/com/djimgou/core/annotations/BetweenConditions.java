package com.djimgou.core.annotations;

import java.lang.annotation.*;

/**
 * Annotation qui permet de faire la validation automatique
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface BetweenConditions {
    Between[] value();
}
