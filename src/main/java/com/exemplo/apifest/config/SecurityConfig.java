package com.exemplo.apifest.config;

import com.exemplo.apifest.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * ROTEIRO 5 - CONFIGURAÇÃO DE SEGURANÇA DA API
 * 
 * Configuração básica de Spring Security para a API RESTful do DeliveryTech.
 * Esta configuração implementa uma abordagem permissiva para desenvolvimento,
 * focando em CORS e preparação para futuras implementações de autenticação.
 * 
 * CARACTERÍSTICAS IMPLEMENTADAS:
 * - Configuração CORS para desenvolvimento frontend
 * - Desabilitação temporária de CSRF para APIs REST
 * - Permissão total para desenvolvimento (todos endpoints públicos)
 * - Encoder BCrypt preparado para futuras implementações de auth
 * - Headers de segurança configurados
 * 
 * ENDPOINTS PROTEGIDOS (FUTURO):
 * - POST /api/clientes, /api/restaurantes, /api/produtos
 * - PUT/DELETE para modificações de dados
 * - GET para dados sensíveis de usuário
 * 
 * ENDPOINTS PÚBLICOS (ATUAL):
 * - GET /api/home - Health check
 * - Swagger UI - Documentação da API
 * - Actuator endpoints - Monitoramento
 * - H2 Console - Debug (desenvolvimento)
 * 
 * @author DeliveryTech Development Team
 * @version 1.0 - Roteiro 5
 * @since Spring Security 6 + Spring Boot 3.4.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configuração principal da cadeia de filtros de segurança.
     * 
     * MODO ATUAL: Desenvolvimento permissivo
     * MODO FUTURO: Autenticação JWT + Role-based authorization
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ========== CORS Configuration ==========
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // ========== CSRF Configuration ==========
            // Desabilita CSRF para APIs REST (recomendado para APIs stateless)
            .csrf(AbstractHttpConfigurer::disable)
            
            // ========== Session Management ==========
            // Configura para stateless (JWT)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // ========== Authorization Configuration ==========
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos sempre acessíveis
                .requestMatchers(
                    "/",                      // Root endpoint
                    "/api/home",              // Home endpoint
                    "/api/v1/home",           // Health check
                    "/h2-console/**",         // H2 Database console (desenvolvimento)
                    "/actuator/**",           // Spring Boot Actuator endpoints
                    "/swagger-ui/**",         // Swagger UI
                    "/swagger-ui.html",       // Swagger UI página principal
                    "/v3/api-docs/**",        // OpenAPI documentation
                    "/api-docs/**",           // API documentation
                    "/api/auth/login",        // Endpoint de login
                    "/api/auth/register",     // Endpoint de registro
                    "/api/restaurantes",      // Listar restaurantes (público)
                    "/api/produtos"           // Listar produtos (público)
                ).permitAll()
                
                // Qualquer outra requisição requer autenticação
                .anyRequest().authenticated()
            )
            
            // ========== JWT Filter ==========
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // ========== Headers de Segurança ==========
            .headers(headers -> headers
                // Permite que o H2 Console seja exibido em frames (desenvolvimento)
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
                // Configurações básicas de segurança HTTP
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                )
            );

        return http.build();
    }

    /**
     * Configuração CORS para permitir requisições do frontend.
     * 
     * Configuração permissiva para desenvolvimento local.
     * Em produção, especificar origins exatos do frontend.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ========== Origins Permitidas ==========
        // Desenvolvimento: Permite localhost em várias portas
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",    // React padrão
            "http://localhost:4200",    // Angular padrão
            "http://localhost:5173",    // Vite padrão
            "http://localhost:8081",    // Outro backend
            "http://127.0.0.1:3000",   // IP localhost alternativo
            "http://127.0.0.1:5500"    // Live Server VS Code
        ));
        
        // ========== Métodos HTTP Permitidos ==========
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // ========== Headers Permitidos ==========
        configuration.setAllowedHeaders(List.of("*"));
        
        // ========== Credentials ==========
        // Permite envio de cookies/auth headers (necessário para JWT futuro)
        configuration.setAllowCredentials(true);
        
        // ========== Headers Expostos ==========
        // Headers que o frontend pode acessar
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Total-Count"
        ));

        // Aplicar configuração a todas as rotas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    /**
     * Bean do encoder de senhas BCrypt.
     * 
     * Preparado para futuras implementações de autenticação.
     * BCrypt é o padrão recomendado para hash de senhas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean do AuthenticationManager para autenticação JWT.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}