package com.exemplo.apifest.config;

import com.exemplo.apifest.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health Indicator customizado para verificar a saúde do sistema JWT.
 * 
 * Este componente verifica se o sistema de autenticação JWT está
 * funcionando corretamente, incluindo a geração e validação de tokens.
 * 
 * Acessível via: GET /actuator/health
 * 
 * @author DeliveryTech Team
 * @version 1.0 - Roteiro 8
 */
@Component
public class JwtHealthIndicator implements HealthIndicator {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Health health() {
        try {
            // Testa a geração e validação de um token JWT
            // Cria um UserDetails mock simples para teste
            org.springframework.security.core.userdetails.User testUser = 
                new org.springframework.security.core.userdetails.User(
                    "healthcheck@test.com", 
                    "password", 
                    java.util.Collections.emptyList()
                );
            
            String testToken = jwtUtil.generateToken(testUser);
            
            if (testToken != null && !testToken.isEmpty()) {
                // Testa se consegue extrair o username do token
                String extractedUsername = jwtUtil.extractUsername(testToken);
                
                if ("healthcheck@test.com".equals(extractedUsername)) {
                    return Health.up()
                        .withDetail("jwt", "JWT system operational")
                        .withDetail("token-generation", "OK")
                        .withDetail("token-validation", "OK")
                        .build();
                } else {
                    return Health.down()
                        .withDetail("jwt", "Token validation failed")
                        .build();
                }
            } else {
                return Health.down()
                    .withDetail("jwt", "Token generation failed")
                    .build();
            }
        } catch (Exception e) {
            return Health.down()
                .withDetail("jwt", "JWT system error")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}