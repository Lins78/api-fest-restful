package com.exemplo.apifest.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Implementação do validador de horário de funcionamento
 */
public class HorarioFuncionamentoValidator implements ConstraintValidator<ValidHorarioFuncionamento, String> {

    private static final Pattern HORARIO_PATTERN = Pattern.compile("^\\d{2}:\\d{2}-\\d{2}:\\d{2}$");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void initialize(ValidHorarioFuncionamento constraintAnnotation) {
        // Método de inicialização
    }

    @Override
    public boolean isValid(String horario, ConstraintValidatorContext context) {
        if (horario == null || horario.trim().isEmpty()) {
            return true; // Use @NotNull or @NotBlank for null/empty validation
        }
        
        // Verifica o padrão básico
        if (!HORARIO_PATTERN.matcher(horario.trim()).matches()) {
            return false;
        }
        
        try {
            String[] partes = horario.trim().split("-");
            
            // Validação manual de horas e minutos antes de tentar parse
            for (String parte : partes) {
                String[] horaMinuto = parte.split(":");
                int hora = Integer.parseInt(horaMinuto[0]);
                int minuto = Integer.parseInt(horaMinuto[1]);
                
                // Horas devem estar entre 0-23
                if (hora < 0 || hora > 23) {
                    return false;
                }
                
                // Minutos devem estar entre 0-59
                if (minuto < 0 || minuto > 59) {
                    return false;
                }
            }
            
            LocalTime abertura = LocalTime.parse(partes[0], TIME_FORMATTER);
            LocalTime fechamento = LocalTime.parse(partes[1], TIME_FORMATTER);
            
            // Verifica se o horário de abertura é antes do fechamento
            // Permite funcionamento 24h (00:00-23:59) mas não horários inválidos
            if (abertura.equals(fechamento)) {
                return false; // Não pode abrir e fechar no mesmo horário
            }
            
            // Se fechamento for antes da abertura, assume que funciona até o dia seguinte
            // Isso é válido (ex: 22:00-02:00)
            return true;
            
        } catch (DateTimeParseException | NumberFormatException e) {
            return false;
        }
    }
}