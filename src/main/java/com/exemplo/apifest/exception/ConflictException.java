package com.exemplo.apifest.exception;

/**
 * Exceção para conflitos de dados
 * Utilizada quando há tentativa de criar/atualizar dados que conflitam com regras de unicidade
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}