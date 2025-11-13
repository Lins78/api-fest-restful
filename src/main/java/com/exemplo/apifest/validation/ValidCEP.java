package com.exemplo.apifest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validação customizada para formato de CEP brasileiro
 * Aceita formatos: 12345-678 ou 12345678
 */
@Documented
@Constraint(validatedBy = CEPValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCEP {
    String message() default "CEP deve estar no formato 12345-678 ou 12345678";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}