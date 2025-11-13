package com.exemplo.apifest.exception;

import com.exemplo.apifest.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ===============================================================================
 * ROTEIRO 6 - SISTEMA ROBUSTO DE VALIDAÇÕES E TRATAMENTO DE ERROS
 * ===============================================================================
 * 
 * Handler global responsável por capturar e tratar todas as exceções da aplicação,
 * convertendo-as em respostas HTTP padronizadas seguindo RFC 7807.
 * 
 * EXCEÇÕES TRATADAS:
 * - EntityNotFoundException → 404 Not Found
 * - BusinessException → 400 Bad Request
 * - ValidationException → 422 Unprocessable Entity
 * - ConflictException → 409 Conflict
 * - MethodArgumentNotValidException → 400 Bad Request (Bean Validation)
 * - ConstraintViolationException → 400 Bad Request
 * - Exception (genérica) → 500 Internal Server Error
 * 
 * FORMATO DE RESPOSTA RFC 7807:
 * {
 *   "timestamp": "2024-06-04T14:30:00",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "Dados inválidos nos dados enviados",
 *   "path": "/api/restaurantes",
 *   "details": {
 *     "nome": "Nome é obrigatório",
 *     "preco": "Preço deve ser maior que zero"
 *   }
 * }
 * 
 * @author DeliveryTech Development Team
 * @version 2.0 - Roteiro 6
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
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        String path = extractPath(request);
        ErrorResponse errorResponse = ErrorResponse.notFound(ex.getMessage(), path);
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
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, WebRequest request) {
        String path = extractPath(request);
        ErrorResponse errorResponse = ErrorResponse.businessError(ex.getMessage(), path);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Trata exceções de validação customizada.
     * 
     * CENÁRIOS:
     * - Regras de negócio específicas violadas
     * - Validações complexas que falharam
     * - Dados que não atendem critérios específicos
     * 
     * @return 422 Unprocessable Entity
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex, WebRequest request) {
        String path = extractPath(request);
        ErrorResponse errorResponse = ErrorResponse.of(422, "Unprocessable Entity", ex.getMessage(), path);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    /**
     * Trata exceções de conflito de dados.
     * 
     * CENÁRIOS:
     * - Violação de constraints de unicidade
     * - Tentativa de criar dados duplicados
     * - Conflitos de estado de recursos
     * 
     * @return 409 Conflict
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex, WebRequest request) {
        String path = extractPath(request);
        ErrorResponse errorResponse = ErrorResponse.conflict(ex.getMessage(), path);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
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
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        String path = extractPath(request);
        
        // Criando mapa de detalhes com os erros de validação
        Map<String, String> details = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            details.put(error.getField(), error.getDefaultMessage())
        );
        
        ErrorResponse errorResponse = ErrorResponse.of(
            400, 
            "Bad Request", 
            "Erro de validação nos dados enviados", 
            path, 
            details
        );
        
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
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        String path = extractPath(request);
        
        Map<String, String> details = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> 
            details.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        
        ErrorResponse errorResponse = ErrorResponse.of(
            400,
            "Bad Request",
            "Dados violam restrições do sistema",
            path,
            details
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
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        String path = extractPath(request);
        
        ErrorResponse errorResponse = ErrorResponse.internalServerError(
            "Erro interno do servidor. Tente novamente mais tarde.",
            path
        );
        
        // Em ambiente de desenvolvimento, incluir detalhes do erro
        // Map<String, String> details = new HashMap<>();
        // details.put("debug", ex.getMessage());
        // errorResponse.setDetails(details);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Extrai o caminho da requisição do WebRequest
     */
    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}