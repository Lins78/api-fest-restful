package com.exemplo.apifest.exception;

/**
 * Exceção lançada quando uma entidade não é encontrada no banco de dados
 * 
 * Esta exceção é usada quando um service tenta buscar uma entidade por ID
 * ou outro critério e não encontra resultado.
 * 
 * Resulta em resposta HTTP 404 (Not Found) quando capturada pelo
 * GlobalExceptionHandler.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 4 - Camada de Serviços e Controllers REST
 */
public class EntityNotFoundException extends RuntimeException {

    /**
     * Construtor com mensagem personalizada
     * 
     * @param message Mensagem descritiva do erro
     */
    public EntityNotFoundException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa raiz
     * 
     * @param message Mensagem descritiva do erro
     * @param cause Exceção que causou este erro
     */
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}