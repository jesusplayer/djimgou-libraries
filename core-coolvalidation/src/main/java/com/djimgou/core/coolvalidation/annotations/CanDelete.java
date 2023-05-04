package com.djimgou.core.coolvalidation.annotations;

import java.lang.annotation.*;

/**
 * Annotation qui permet de faire la validation automatique
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface CanDelete {
    String childColName();
    String childTableName();
    String message();
}
