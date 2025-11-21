package com.exemplo.apifest.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Implementação do validador de telefone brasileiro
 */
public class TelefoneValidator implements ConstraintValidator<ValidTelefone, String> {

    // Padrões para telefone brasileiro (alternativo - não usado na implementação atual)
    // private static final Pattern TELEFONE_PATTERN = Pattern.compile(
    //     "^(?:\\(\\d{2}\\)\\s?)?(?:9?\\d{4}-?\\d{4})$|^\\d{10,11}$"
    // );

    @Override
    public void initialize(ValidTelefone constraintAnnotation) {
        // Método de inicialização
    }

    @Override
    public boolean isValid(String telefone, ConstraintValidatorContext context) {
        if (telefone == null || telefone.trim().isEmpty()) {
            return true; // Use @NotNull or @NotBlank for null/empty validation
        }
        
        // Verifica se contém apenas números, espaços e parênteses (não aceita hífen no meio)
        if (!telefone.matches("[\\d\\s\\(\\)]+")) {
            return false;
        }
        
        // Remove espaços e parênteses para validação
        String telefoneClean = telefone.replaceAll("[\\s\\(\\)]", "");
        
        // Verifica se tem entre 10 e 11 dígitos
        if (!telefoneClean.matches("\\d{10,11}")) {
            return false;
        }
        
        // Validação de DDD (11 a 99)
        if (telefoneClean.length() >= 2) {
            int ddd = Integer.parseInt(telefoneClean.substring(0, 2));
            if (ddd < 11 || ddd > 99) {
                return false;
            }
        }
        
        // Validações adicionais para padrões brasileiros
        if (telefoneClean.length() == 11) {
            // Celular: deve começar com 9 após o DDD
            return telefoneClean.charAt(2) == '9';
        } else if (telefoneClean.length() == 10) {
            // Fixo: não deve começar com 9 após o DDD
            return telefoneClean.charAt(2) != '9';
        }
        
        return false;
    }
}