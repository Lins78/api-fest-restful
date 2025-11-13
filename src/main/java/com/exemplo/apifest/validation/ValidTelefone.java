package com.exemplo.apifest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validação customizada para telefones brasileiros
 * Aceita formatos: (11) 99999-9999, 11999999999, etc.
 */
@Documented
@Constraint(validatedBy = TelefoneValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTelefone {
    String message() default "Telefone deve estar no formato (11) 99999-9999 ou conter 10-11 dígitos";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}