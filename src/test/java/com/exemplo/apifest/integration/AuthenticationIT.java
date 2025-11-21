package com.exemplo.apifest.integration;

import com.exemplo.apifest.dto.auth.LoginRequest;
import com.exemplo.apifest.dto.auth.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Testes de integração para o sistema de autenticação JWT.
 * 
 * Este teste verifica todo o fluxo de autenticação da aplicação,
 * incluindo registro de usuários, login e acesso a endpoints protegidos.
 * 
 * CENÁRIOS TESTADOS:
 * - Registro de usuário com sucesso
 * - Login com credenciais válidas
 * - Acesso a endpoints protegidos com token válido
 * - Validação de token inválido
 * - Casos de erro (credenciais inválidas, etc.)
 * 
 * @author DeliveryTech Team
 * @version 1.0 - Roteiro 8
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("🔐 Testes de Integração - Sistema de Autenticação JWT")
public class AuthenticationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String jwtToken;

    @Test
    @Order(1)
    @DisplayName("✅ Deve registrar usuário com sucesso")
    public void testRegisterUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNome("Usuário Teste");
        registerRequest.setEmail("usuario@teste.com");
        registerRequest.setSenha("senha123");
        registerRequest.setTelefone("11999999999");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", containsString("sucesso")));
    }

    @Test
    @Order(2)
    @DisplayName("✅ Deve fazer login com credenciais válidas")
    public void testLoginSuccess() throws Exception {
        // Primeiro registra o usuário
        testRegisterUser();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("usuario@teste.com");
        loginRequest.setSenha("senha123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.usuario.email", is("usuario@teste.com")))
                .andDo(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    // Extrai o token para uso em outros testes
                    jwtToken = objectMapper.readTree(responseBody).get("token").asText();
                });
    }

    @Test
    @Order(3)
    @DisplayName("❌ Deve falhar login com credenciais inválidas")
    public void testLoginFail() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("usuario@inexistente.com");
        loginRequest.setSenha("senhaerrada");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    @DisplayName("✅ Deve acessar endpoint protegido com token válido")
    public void testAccessProtectedEndpointWithValidToken() throws Exception {
        // Primeiro faz login
        testLoginSuccess();

        mockMvc.perform(get("/api/clientes")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    @DisplayName("❌ Deve rejeitar acesso a endpoint protegido sem token")
    public void testAccessProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(6)
    @DisplayName("❌ Deve rejeitar acesso com token inválido")
    public void testAccessProtectedEndpointWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/clientes")
                .header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(7)
    @DisplayName("✅ Deve validar campos obrigatórios no registro")
    public void testRegisterValidation() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        // Campos em branco para testar validação

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    @DisplayName("✅ Deve validar campos obrigatórios no login")
    public void testLoginValidation() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        // Campos em branco para testar validação

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }
}