package com.djimgou.core.cooldto.annotations;

import java.lang.annotation.*;

/**
 * Pour Ignorer l'extraction
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface DtoIgnore { }
