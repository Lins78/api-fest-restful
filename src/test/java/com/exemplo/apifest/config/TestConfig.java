package com.exemplo.apifest.config;

import com.exemplo.apifest.security.JwtUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração específica para testes da API FEST RESTful.
 * 
 * Esta configuração fornece beans específicos para o ambiente de teste,
 * garantindo que os testes sejam executados de forma isolada e rápida.
 * 
 * FUNCIONALIDADES:
 * - Configuração de JWT para testes
 * - Encoder de senha para testes
 * - Configurações de banco H2 em memória
 * 
 * @author DeliveryTech Team
 * @version 1.0 - Roteiro 8
 */
@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public JwtUtil testJwtUtil() {
        return new JwtUtil();
    }

    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}