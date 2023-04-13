package com.djimgou.core.coolvalidation.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Unique {
    boolean ignoreCase() default false;

    /**
     * Message à afficher en cas d'erreur d'unicité lors de la création
     * @return
     */
    String[] createMsg() default {};

    String message() default "";

    /**
     * message à afficher en cas d'erreur d'unicité lors de la modification
     * @return
     */
    String[] updateMsg() default {};
}
