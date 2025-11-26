package com.exemplo.apifest.exception;

/**
 * Exceção lançada quando um recurso solicitado não é encontrado.
 * 
 * Esta exceção é usada nos serviços quando um recurso (produto, cliente, restaurante, etc.)
 * não é encontrado no banco de dados.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Correção Sistema - Compatibilidade
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Construtor padrão
     */
    public ResourceNotFoundException() {
        super();
    }

    /**
     * Construtor com mensagem
     * 
     * @param message Mensagem de erro
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa
     * 
     * @param message Mensagem de erro
     * @param cause Causa da exceção
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construtor com causa
     * 
     * @param cause Causa da exceção
     */
    public ResourceNotFoundException(Throwable cause) {
        super(cause);
    }
}