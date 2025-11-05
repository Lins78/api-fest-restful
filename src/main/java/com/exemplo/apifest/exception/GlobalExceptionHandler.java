package com.exemplo.apifest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ===============================================================================
 * ROTEIRO 4 - HANDLER GLOBAL DE EXCEÇÕES
 * ===============================================================================
 * 
 * Classe responsável por capturar e tratar todas as exceções da aplicação,
 * convertendo-as em respostas HTTP padronizadas com status codes apropriados.
 * 
 * EXCEÇÕES TRATADAS:
 * - EntityNotFoundException → 404 Not Found
 * - BusinessException → 400 Bad Request  
 * - MethodArgumentNotValidException → 400 Bad Request (Bean Validation)
 * - ConstraintViolationException → 400 Bad Request
 * - Exception (genérica) → 500 Internal Server Error
 * 
 * FORMATO DE RESPOSTA PADRONIZADO:
 * {
 *   "timestamp": "2025-11-05T14:30:00",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "Email já cadastrado no sistema",
 *   "path": "/api/clientes"
 * }
 * 
 * @author DeliveryTech Development Team
 * @version 1.0 - Roteiro 4
 * @since Java 21 LTS + Spring Boot 3.4.0
 * ===============================================================================
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata exceções de entidade não encontrada.
     * 
     * CENÁRIOS:
     * - Cliente não existe com ID informado
     * - Restaurante não encontrado
     * - Produto inexistente
     * - Pedido não localizado
     * 
     * @return 404 Not Found
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.NOT_FOUND,
            ex.getMessage(),
            "Recurso não encontrado"
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Trata exceções de regras de negócio.
     * 
     * CENÁRIOS:
     * - Email já cadastrado
     * - Cliente inativo fazendo pedido
     * - Produto indisponível no pedido
     * - Transição de status inválida
     * - Restaurante não entrega no CEP
     * - Produto não pertence ao restaurante
     * 
     * @return 400 Bad Request
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex) {
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getMessage(),
            "Regra de negócio violada"
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Trata exceções de validação Bean Validation (@Valid).
     * 
     * CENÁRIOS:
     * - Campos obrigatórios não preenchidos (@NotNull, @NotBlank)
     * - Email com formato inválido (@Email)
     * - Valores fora do range permitido (@Size, @DecimalMin)
     * - Falha em validações customizadas
     * 
     * @return 400 Bad Request com detalhes dos campos inválidos
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Dados inválidos");
        
        // Coletando todos os erros de validação
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                    error -> error.getField(),
                    error -> error.getDefaultMessage(),
                    (existing, replacement) -> existing // Manter primeiro erro se houver duplicatas
                ));
        
        errorResponse.put("message", "Dados de entrada inválidos");
        errorResponse.put("fieldErrors", fieldErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Trata exceções de constraint validation (validações de banco de dados).
     * 
     * CENÁRIOS:
     * - Violação de unique constraints
     * - Violação de foreign key constraints
     * - Validações de tamanho de campos
     * 
     * @return 400 Bad Request
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Dados violam restrições do sistema: " + ex.getMessage(),
            "Violação de restrição"
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Trata exceções genéricas não capturadas pelos handlers específicos.
     * 
     * CENÁRIOS:
     * - Erros inesperados do sistema
     * - Falhas de conexão com banco de dados
     * - Problemas de infraestrutura
     * - Bugs não previstos
     * 
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> errorResponse = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Erro interno do servidor. Tente novamente mais tarde.",
            "Erro interno"
        );
        
        // Em ambiente de desenvolvimento, incluir stack trace
        // errorResponse.put("debug", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Cria estrutura padronizada de resposta de erro.
     * 
     * @param status Status HTTP da resposta
     * @param message Mensagem detalhada do erro
     * @param error Tipo do erro (resumo)
     * @return Map com estrutura padronizada
     */
    private Map<String, Object> createErrorResponse(HttpStatus status, String message, String error) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        
        return errorResponse;
    }
}