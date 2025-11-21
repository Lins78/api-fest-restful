package com.exemplo.apifest.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger/OpenAPI para documentação da API - ROTEIRO 8
 * 
 * Esta configuração cria uma documentação interativa completa
 * da API REST com suporte à autenticação JWT, facilitando integração 
 * por parte de desenvolvedores e parceiros externos.
 * 
 * FUNCIONALIDADES IMPLEMENTADAS:
 * - Documentação completa de todos os endpoints
 * - Interface interativa para testes
 * - Suporte à autenticação JWT Bearer Token
 * - Esquemas de dados documentados
 * - Exemplos de requests e responses
 * 
 * URLs de Acesso:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - API Docs JSON: http://localhost:8080/v3/api-docs
 * 
 * @author DeliveryTech Team
 * @version 2.0 - Roteiro 8
 * @since Roteiro 5 - Documentação e Padronização
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, createAPIKeyScheme()));
    }

    private Info apiInfo() {
        return new Info()
                .title("API FEST RESTful - Sistema de Delivery")
                .description("API REST completa para plataforma de delivery de restaurantes com autenticação JWT e sistema completo de CRUD para clientes, restaurantes, produtos e pedidos.")
                .version("2.0.0")
                .contact(new Contact()
                        .name("DeliveryTech Development Team")
                        .email("dev@deliverytech.com")
                        .url("https://github.com/Lins78/api-fest-restful"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .description("JWT Bearer Token para autenticação. " +
                           "Obtenha o token fazendo login em /api/auth/login " +
                           "e use o formato: Bearer {token}");
    }
}