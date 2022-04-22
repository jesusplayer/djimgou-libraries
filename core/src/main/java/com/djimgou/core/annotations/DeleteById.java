package com.djimgou.core.annotations;


import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeleteById {
    String[] value() default {};
}
