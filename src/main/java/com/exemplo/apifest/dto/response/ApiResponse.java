package com.exemplo.apifest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Wrapper padrão para todas as respostas da API
 * 
 * Fornece uma estrutura consistente para todas as respostas,
 * incluindo dados de sucesso, mensagens e timestamp.
 * 
 * @param <T> Tipo dos dados retornados
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 5 - Documentação e Padronização
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Resposta padrão da API")
public class ApiResponse<T> {

    @Schema(description = "Indica se a operação foi executada com sucesso", 
            example = "true")
    private Boolean success;

    @Schema(description = "Dados retornados pela operação")
    private T data;

    @Schema(description = "Mensagem descritiva sobre o resultado da operação", 
            example = "Operação realizada com sucesso")
    private String message;

    @Schema(description = "Timestamp da resposta", 
            example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Dados de erro, se houver")
    private Object errorData;

    // Construtor para sucesso
    public ApiResponse(Boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Cria uma resposta de sucesso com dados
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>(true, data, "Operação realizada com sucesso");
        return response;
    }

    /**
     * Cria uma resposta de sucesso com dados e mensagem customizada
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>(true, data, message);
        return response;
    }

    /**
     * Cria uma resposta de sucesso sem dados
     */
    public static <T> ApiResponse<T> success(String message) {
        ApiResponse<T> response = new ApiResponse<>(true, null, message);
        return response;
    }

    /**
     * Cria uma resposta de erro
     */
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>(false, null, message);
        return response;
    }

    /**
     * Cria uma resposta de erro com dados de erro
     */
    public static <T> ApiResponse<T> error(String message, Object errorData) {
        ApiResponse<T> response = new ApiResponse<>(false, null, message);
        response.setErrorData(errorData);
        return response;
    }
}