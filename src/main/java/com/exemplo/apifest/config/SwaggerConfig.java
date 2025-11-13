package com.exemplo.apifest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger/OpenAPI para documentação da API
 * 
 * Esta configuração cria uma documentação interativa completa
 * da API REST, facilitando integração por parte de desenvolvedores
 * e parceiros externos.
 * 
 * URLs de Acesso:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - API Docs JSON: http://localhost:8080/v3/api-docs
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 5 - Documentação e Padronização
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("DeliveryTech API")
                .description("API REST completa para plataforma de delivery de restaurantes")
                .version("1.0.0")
                .contact(new Contact()
                        .name("DeliveryTech Team")
                        .email("dev@deliverytech.com")
                        .url("https://github.com/Lins78/api-fest-restful"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }
}