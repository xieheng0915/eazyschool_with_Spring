package com.eazybytes.eazyschool.annotation;

import com.eazybytes.eazyschool.validations.FieldsValueMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = FieldsValueMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldValueMatch {
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String message() default "Fields value don't match!";
    String field();
    String fieldMatch();

    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        FieldValueMatch[] value();
    }
}
