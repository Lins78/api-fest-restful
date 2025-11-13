package com.exemplo.apifest.exception;

/**
 * Exceção para erros de validação de dados
 * Utilizada quando dados não passam nas validações de negócio
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}