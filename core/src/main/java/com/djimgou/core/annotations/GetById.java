package com.djimgou.core.annotations;


import java.lang.annotation.*;

//@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
//@Constraint(validatedBy = UrlValidatorImplementation.class)
@Documented
public @interface GetById {
    String[] value() default {};
}
