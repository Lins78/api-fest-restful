package com.exemplo.apifest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validação customizada para categorias permitidas
 */
@Documented
@Constraint(validatedBy = CategoriaValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCategoria {
    String message() default "Categoria deve ser uma das opções válidas";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Lista de categorias permitidas
     */
    String[] values() default {};
}