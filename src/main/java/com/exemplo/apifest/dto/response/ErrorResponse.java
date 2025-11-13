package com.exemplo.apifest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Classe padronizada para respostas de erro da API
 * 
 * Fornece informações detalhadas sobre erros, incluindo
 * código de erro, mensagem, detalhes técnicos e timestamp.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 5 - Documentação e Padronização
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Resposta de erro padronizada")
public class ErrorResponse {

    @Schema(description = "Código do erro", 
            example = "VALIDATION_ERROR")
    private String errorCode;

    @Schema(description = "Mensagem principal do erro", 
            example = "Dados inválidos fornecidos")
    private String message;

    @Schema(description = "Detalhes específicos do erro")
    private ErrorDetails details;

    @Schema(description = "Lista de erros de validação")
    private List<ValidationError> validationErrors;

    @Schema(description = "Timestamp do erro", 
            example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    /**
     * Classe para detalhes específicos do erro
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Detalhes específicos do erro")
    public static class ErrorDetails {

        @Schema(description = "Campo que causou o erro", 
                example = "email")
        private String field;

        @Schema(description = "Valor rejeitado", 
                example = "email_invalido")
        private Object rejectedValue;

        @Schema(description = "Causa raiz do erro", 
                example = "Formato de email inválido")
        private String rootCause;
    }

    /**
     * Classe para erros de validação
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Erro de validação específico")
    public static class ValidationError {

        @Schema(description = "Campo que falhou na validação", 
                example = "nome")
        private String field;

        @Schema(description = "Mensagem de erro", 
                example = "Nome não pode ser vazio")
        private String message;

        @Schema(description = "Valor rejeitado", 
                example = "")
        private Object rejectedValue;
    }

    // Construtores de conveniência
    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String errorCode, String message, ErrorDetails details) {
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Cria uma resposta de erro de validação
     */
    public static ErrorResponse validationError(String message, List<ValidationError> validationErrors) {
        return new ErrorResponse("VALIDATION_ERROR", message, null, validationErrors, LocalDateTime.now());
    }

    /**
     * Cria uma resposta de erro de negócio
     */
    public static ErrorResponse businessError(String message) {
        return new ErrorResponse("BUSINESS_ERROR", message, null, null, LocalDateTime.now());
    }

    /**
     * Cria uma resposta de erro de recurso não encontrado
     */
    public static ErrorResponse notFound(String message) {
        return new ErrorResponse("NOT_FOUND", message, null, null, LocalDateTime.now());
    }
}