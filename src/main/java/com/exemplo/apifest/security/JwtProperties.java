package com.exemplo.apifest.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configurações JWT centralizadas
 * 
 * Esta classe gerencia todas as configurações relacionadas ao JWT,
 * fornecendo valores padrão seguros e permitindo customização via
 * variáveis de ambiente.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 7 - Sistema de Autenticação JWT
 */
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /**
     * Chave secreta para assinatura dos tokens JWT
     * Mínimo de 256 bits (32 caracteres) para HS256
     */
    private String secret = "delivery-tech-secret-key-2025-api-fest-restful-security-jwt-token-signature";

    /**
     * Tempo de expiração do token em millisegundos
     * Padrão: 24 horas (86400000 ms)
     */
    private long expiration = 86400000L; // 24 horas

    /**
     * Issuer do token JWT
     */
    private String issuer = "api-fest-restful";

    /**
     * Audience do token JWT  
     */
    private String audience = "api-fest-users";

    // Getters e Setters

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    /**
     * Retorna o tempo de expiração em segundos
     * Útil para configurações que requerem tempo em segundos
     */
    public long getExpirationInSeconds() {
        return expiration / 1000;
    }
}