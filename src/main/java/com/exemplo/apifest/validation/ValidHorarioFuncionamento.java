package com.exemplo.apifest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validação customizada para horário de funcionamento
 * Formato esperado: HH:MM-HH:MM (exemplo: 08:00-22:00)
 */
@Documented
@Constraint(validatedBy = HorarioFuncionamentoValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidHorarioFuncionamento {
    String message() default "Horário deve estar no formato HH:MM-HH:MM (exemplo: 08:00-22:00)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}