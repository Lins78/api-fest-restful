package com.exemplo.apifest.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Testes de integração para endpoints do Spring Boot Actuator.
 * 
 * Este teste verifica se todos os endpoints de monitoramento
 * estão funcionando corretamente e retornando as informações
 * esperadas sobre a saúde e métricas da aplicação.
 * 
 * CENÁRIOS TESTADOS:
 * - Health check básico
 * - Health checks customizados (Database, JWT)
 * - Informações da aplicação
 * - Métricas básicas
 * - Configurações do ambiente
 * 
 * @author DeliveryTech Team
 * @version 1.0 - Roteiro 8
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("📊 Testes de Integração - Spring Boot Actuator")
public class ActuatorIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("✅ Health check deve retornar status UP")
    public void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.components").exists());
    }

    @Test
    @DisplayName("✅ Health check do banco deve estar funcionando")
    public void testDatabaseHealthCheck() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.db.status", is("UP")))
                .andExpect(jsonPath("$.components.db.details.database").exists());
    }

    @Test
    @DisplayName("✅ Health check customizado do JWT deve funcionar")
    public void testJwtHealthCheck() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.jwtHealthIndicator").exists());
    }

    @Test
    @DisplayName("✅ Health check customizado do Database deve funcionar")
    public void testCustomDatabaseHealthCheck() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.databaseHealthIndicator").exists());
    }

    @Test
    @DisplayName("✅ Endpoint info deve retornar informações da aplicação")
    public void testInfoEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app").exists())
                .andExpect(jsonPath("$.app.name").exists())
                .andExpect(jsonPath("$.app.version").exists())
                .andExpect(jsonPath("$.app.description").exists());
    }

    @Test
    @DisplayName("✅ Endpoint de métricas deve estar disponível")
    public void testMetricsEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names").isArray())
                .andExpect(jsonPath("$.names", hasItem("jvm.memory.used")))
                .andExpect(jsonPath("$.names", hasItem("http.server.requests")));
    }

    @Test
    @DisplayName("✅ Métrica específica de memória JVM deve retornar dados")
    public void testSpecificJvmMemoryMetric() throws Exception {
        mockMvc.perform(get("/actuator/metrics/jvm.memory.used"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("jvm.memory.used")))
                .andExpect(jsonPath("$.measurements").isArray())
                .andExpect(jsonPath("$.availableTags").exists());
    }

    @Test
    @DisplayName("✅ Métrica de requests HTTP deve retornar dados")
    public void testHttpRequestsMetric() throws Exception {
        // Primeiro faz uma requisição para gerar métrica
        mockMvc.perform(get("/actuator/health"));

        // Então verifica a métrica
        mockMvc.perform(get("/actuator/metrics/http.server.requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("http.server.requests")))
                .andExpect(jsonPath("$.measurements").isArray())
                .andExpect(jsonPath("$.availableTags").exists());
    }

    @Test
    @DisplayName("✅ Endpoint de configurações deve estar disponível")
    public void testConfigPropsEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/configprops"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contexts").exists());
    }

    @Test
    @DisplayName("✅ Endpoint de beans deve listar beans da aplicação")
    public void testBeansEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/beans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contexts").exists());
    }

    @Test
    @DisplayName("✅ Endpoint de environment deve mostrar propriedades")
    public void testEnvEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/env"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.propertySources").isArray())
                .andExpect(jsonPath("$.propertySources", not(empty())));
    }

    @Test
    @DisplayName("✅ Propriedade específica deve ser acessível")
    public void testSpecificEnvProperty() throws Exception {
        mockMvc.perform(get("/actuator/env/spring.application.name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.property.source").exists())
                .andExpect(jsonPath("$.property.value", is("api-fest-restfull")));
    }

    @Test
    @DisplayName("❌ Endpoint inexistente deve retornar 404")
    public void testNonExistentActuatorEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("✅ Base path do actuator deve estar corretamente configurado")
    public void testActuatorBasePath() throws Exception {
        // Testa se o base path /actuator está funcionando
        mockMvc.perform(get("/actuator"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links").exists())
                .andExpect(jsonPath("$._links.health").exists())
                .andExpect(jsonPath("$._links.info").exists())
                .andExpect(jsonPath("$._links.metrics").exists());
    }
}