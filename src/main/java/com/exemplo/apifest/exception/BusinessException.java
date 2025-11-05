package com.exemplo.apifest.exception;

/**
 * Exceção lançada quando regras de negócio são violadas
 * 
 * Esta exceção é usada quando:
 * - Email já cadastrado no sistema
 * - Produto não pertence ao restaurante selecionado
 * - Transição de status de pedido inválida
 * - Cliente inativo tentando fazer pedido
 * - Produto indisponível sendo pedido
 * 
 * Resulta em resposta HTTP 400 (Bad Request) quando capturada pelo
 * GlobalExceptionHandler.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 4 - Camada de Serviços e Controllers REST
 */
public class BusinessException extends RuntimeException {

    /**
     * Construtor com mensagem personalizada
     * 
     * @param message Mensagem descritiva da regra de negócio violada
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa raiz
     * 
     * @param message Mensagem descritiva da regra de negócio violada
     * @param cause Exceção que causou este erro
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}