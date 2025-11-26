package com.exemplo.apifest.exception;

/**
 * EXCEPTION UNAUTHORIZED - Sistema de Autenticação
 * 
 * Exceção lançada quando um usuário tenta acessar um recurso
 * sem as credenciais adequadas ou com credenciais inválidas.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Correção Sistema - Autenticação
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * Construtor padrão
     */
    public UnauthorizedException() {
        super("Acesso não autorizado");
    }

    /**
     * Construtor com mensagem personalizada
     * 
     * @param message Mensagem de erro
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa
     * 
     * @param message Mensagem de erro
     * @param cause Causa da exceção
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}