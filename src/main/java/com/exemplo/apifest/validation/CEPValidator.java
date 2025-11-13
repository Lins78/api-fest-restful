package com.exemplo.apifest.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Implementação do validador de CEP brasileiro
 */
public class CEPValidator implements ConstraintValidator<ValidCEP, String> {

    private static final Pattern CEP_PATTERN = Pattern.compile("^\\d{5}-?\\d{3}$");

    @Override
    public void initialize(ValidCEP constraintAnnotation) {
        // Método de inicialização (pode ficar vazio)
    }

    @Override
    public boolean isValid(String cep, ConstraintValidatorContext context) {
        if (cep == null || cep.trim().isEmpty()) {
            return true; // Use @NotNull or @NotBlank for null/empty validation
        }
        
        return CEP_PATTERN.matcher(cep.trim()).matches();
    }
}