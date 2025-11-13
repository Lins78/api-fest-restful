package com.exemplo.apifest.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * Implementação do validador de categoria
 */
public class CategoriaValidator implements ConstraintValidator<ValidCategoria, String> {

    private List<String> allowedValues;

    @Override
    public void initialize(ValidCategoria constraintAnnotation) {
        allowedValues = Arrays.asList(constraintAnnotation.values());
    }

    @Override
    public boolean isValid(String categoria, ConstraintValidatorContext context) {
        if (categoria == null) {
            return true; // Use @NotNull for null validation
        }
        
        if (categoria.trim().isEmpty()) {
            return false; // String vazia é inválida
        }
        
        // Se não foram especificadas categorias na anotação, usar categorias padrão
        if (allowedValues == null || allowedValues.isEmpty()) {
            List<String> categoriasValidas = Arrays.asList(
                "LANCHE", "BEBIDA", "SOBREMESA", "ACOMPANHAMENTO", "PRATO_PRINCIPAL", "ENTRADA"
            );
            
            return categoriasValidas.contains(categoria); // Case sensitive
        }
        
        return allowedValues.contains(categoria);
    }
}