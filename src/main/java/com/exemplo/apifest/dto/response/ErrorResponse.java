package com.exemplo.apifest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Estrutura padronizada para respostas de erro seguindo RFC 7807
 * 
 * Fornece informações detalhadas sobre erros com formato consistente
 * para facilitar integração com front-ends e outras aplicações.
 * 
 * @author DeliveryTech Team
 * @version 2.0
 * @since Roteiro 6 - Sistema Robusto de Validações
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Resposta de erro padronizada seguindo RFC 7807")
public class ErrorResponse {

    @Schema(description = "Timestamp do erro", 
            example = "2024-06-04T14:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código de status HTTP", 
            example = "400")
    private Integer status;

    @Schema(description = "Tipo/categoria do erro", 
            example = "Bad Request")
    private String error;

    @Schema(description = "Mensagem principal do erro", 
            example = "Dados inválidos fornecidos")
    private String message;

    @Schema(description = "Caminho/endpoint onde ocorreu o erro", 
            example = "/api/restaurantes")
    private String path;

    @Schema(description = "Detalhes específicos do erro (para validações)")
    private Map<String, String> details;

    @Schema(description = "Lista de erros de validação (compatibilidade)")
    private List<ValidationError> validationErrors;

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

    // Métodos de conveniência para criação de respostas de erro

    /**
     * Cria uma resposta de erro simples
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }

    /**
     * Cria uma resposta de erro com detalhes
     */
    public static ErrorResponse of(int status, String error, String message, String path, Map<String, String> details) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .details(details)
                .build();
    }

    /**
     * Cria uma resposta de erro de validação (compatibilidade)
     */
    public static ErrorResponse validationError(String message, List<ValidationError> validationErrors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(400)
                .error("Bad Request")
                .message(message)
                .validationErrors(validationErrors)
                .build();
    }

    /**
     * Cria uma resposta de erro de negócio
     */
    public static ErrorResponse businessError(String message, String path) {
        return of(400, "Bad Request", message, path);
    }

    /**
     * Cria uma resposta de erro de recurso não encontrado
     */
    public static ErrorResponse notFound(String message, String path) {
        return of(404, "Not Found", message, path);
    }

    /**
     * Cria uma resposta de erro de conflito
     */
    public static ErrorResponse conflict(String message, String path) {
        return of(409, "Conflict", message, path);
    }

    /**
     * Cria uma resposta de erro interno do servidor
     */
    public static ErrorResponse internalServerError(String message, String path) {
        return of(500, "Internal Server Error", message, path);
    }
}