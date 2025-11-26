package com.exemplo.apifest.integration.controller;

import com.exemplo.apifest.builders.UserTestDataBuilder;
import com.exemplo.apifest.dto.auth.LoginRequestDTO;
import com.exemplo.apifest.dto.auth.RegisterRequestDTO;
import com.exemplo.apifest.model.User;
import com.exemplo.apifest.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de Integração para AuthController - Roteiro 9.
 * 
 * CENÁRIOS DE AUTENTICAÇÃO TESTADOS:
 * - Login com credenciais válidas/inválidas
 * - Registro de novos usuários
 * - Validação de tokens JWT
 * - Refresh de tokens
 * - Logout e invalidação de sessões
 * - Validações de segurança em endpoints protegidos
 * - Rate limiting e tentativas de login
 * 
 * ASPECTOS DE SEGURANÇA TESTADOS:
 * - Hash de senhas com bcrypt
 * - Geração e validação de JWT tokens
 * - Headers de autorização
 * - Proteção contra ataques de força bruta
 * - Validação de dados de entrada
 * - Sanitização de responses
 * 
 * TECNOLOGIAS UTILIZADAS:
 * - TestContainers para PostgreSQL real
 * - MockMvc para simulação de requests HTTP
 * - Spring Security Test para autenticação
 * - JWT para tokens de autorização
 * - BCrypt para hash de senhas
 * 
 * @author DeliveryTech Team
 * @version 1.0 - Roteiro 9
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test-advanced")
@Testcontainers
@Transactional
@DisplayName("🔐 AuthController - Testes de Integração")
class AuthControllerIT {

    @Container
    @SuppressWarnings("resource") // Container é gerenciado pelo TestContainers framework
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User usuarioExistente;
    private LoginRequestDTO loginValido;
    private RegisterRequestDTO registroValido;

    @BeforeEach
    void setUp() {
        // Limpar dados antes de cada teste
        userRepository.deleteAll();

        // Criar usuário existente para testes de login
        usuarioExistente = UserTestDataBuilder.umUsuarioValido()
                .comEmail("usuario.teste@email.com")
                .comPassword(passwordEncoder.encode("SenhaSegura123!"))
                .comAtivo(true)
                .build();
        usuarioExistente = userRepository.save(usuarioExistente);

        // Setup LoginRequestDTO válido
        loginValido = new LoginRequestDTO();
        loginValido.setEmail("usuario.teste@email.com");
        loginValido.setPassword("SenhaSegura123!");

        // Setup RegisterRequestDTO válido
        registroValido = new RegisterRequestDTO();
        registroValido.setNome("Novo Usuário");
        registroValido.setEmail("novo.usuario@email.com");
        registroValido.setPassword("MinhaSenh@123");
        registroValido.setConfirmPassword("MinhaSenh@123");
    }

    // ========== TESTES DE LOGIN (POST /auth/login) ==========

    @Nested
    @DisplayName("POST /auth/login - Autenticação de Usuários")
    class LoginUsuarios {

        @Test
        @DisplayName("✅ Deve fazer login com credenciais válidas")
        void deveFazerLoginComCredenciaisValidas() throws Exception {
            // When & Then
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginValido)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.accessToken").exists())
                    .andExpect(jsonPath("$.refreshToken").exists())
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.expiresIn").exists())
                    .andExpect(jsonPath("$.user.id").value(usuarioExistente.getId()))
                    .andExpect(jsonPath("$.user.nome").value(usuarioExistente.getNome()))
                    .andExpect(jsonPath("$.user.email").value(usuarioExistente.getEmail()))
                    .andExpect(jsonPath("$.user.roles").isArray());
        }

        @Test
        @DisplayName("❌ Deve falhar com email inexistente")
        void deveFalharComEmailInexistente() throws Exception {
            // Given
            loginValido.setEmail("inexistente@email.com");

            // When & Then
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginValido)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(containsString("Credenciais inválidas")))
                    .andExpect(jsonPath("$.accessToken").doesNotExist());
        }

        @Test
        @DisplayName("❌ Deve falhar com senha incorreta")
        void deveFalharComSenhaIncorreta() throws Exception {
            // Given
            loginValido.setPassword("SenhaErrada123!");

            // When & Then
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginValido)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(containsString("Credenciais inválidas")))
                    .andExpect(jsonPath("$.accessToken").doesNotExist());
        }

        @Test
        @DisplayName("❌ Deve falhar com usuário inativo")
        void deveFalharComUsuarioInativo() throws Exception {
            // Given
            usuarioExistente.setAtivo(false);
            userRepository.save(usuarioExistente);

            // When & Then
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginValido)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(containsString("Usuário inativo")))
                    .andExpect(jsonPath("$.accessToken").doesNotExist());
        }

        @Test
        @DisplayName("❌ Deve falhar com dados obrigatórios ausentes")
        void deveFalharComDadosObrigatoriosAusentes() throws Exception {
            // Given
            LoginRequestDTO loginIncompleto = new LoginRequestDTO();
            loginIncompleto.setEmail(""); // Email vazio
            loginIncompleto.setPassword(""); // Password vazio

            // When & Then
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginIncompleto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").isArray())
                    .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))));
        }

        @Test
        @DisplayName("❌ Deve falhar com email em formato inválido")
        void deveFalharComEmailFormatoInvalido() throws Exception {
            // Given
            loginValido.setEmail("email-invalido");

            // When & Then
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginValido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("Email inválido")));
        }

        @Test
        @DisplayName("🔒 Deve implementar rate limiting em tentativas de login")
        void deveImplementarRateLimitingTentativasLogin() throws Exception {
            // Given
            loginValido.setPassword("SenhaErrada123!");

            // When - Fazer múltiplas tentativas de login com senha errada
            for (int i = 0; i < 5; i++) {
                mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginValido)))
                        .andExpect(status().isUnauthorized());
            }

            // Then - Sexta tentativa deve ser bloqueada
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginValido)))
                    .andExpect(status().isTooManyRequests())
                    .andExpect(jsonPath("$.message").value(containsString("Muitas tentativas de login")));
        }
    }

    // ========== TESTES DE REGISTRO (POST /auth/register) ==========

    @Nested
    @DisplayName("POST /auth/register - Registro de Usuários")
    class RegistroUsuarios {

        @Test
        @DisplayName("✅ Deve registrar usuário com dados válidos")
        void deveRegistrarUsuarioComDadosValidos() throws Exception {
            // When & Then
            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registroValido)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.accessToken").exists())
                    .andExpect(jsonPath("$.refreshToken").exists())
                    .andExpect(jsonPath("$.user.nome").value(registroValido.getNome()))
                    .andExpect(jsonPath("$.user.email").value(registroValido.getEmail()))
                    .andExpect(jsonPath("$.user.ativo").value(true))
                    .andExpect(jsonPath("$.user.password").doesNotExist()); // Senha não deve ser retornada
        }

        @Test
        @DisplayName("❌ Deve falhar com email já cadastrado")
        void deveFalharComEmailJaCadastrado() throws Exception {
            // Given
            registroValido.setEmail(usuarioExistente.getEmail());

            // When & Then
            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registroValido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("já está em uso")));
        }

        @Test
        @DisplayName("❌ Deve falhar com senhas não coincidentes")
        void deveFalharComSenhasNaoCoincidentes() throws Exception {
            // Given
            registroValido.setConfirmPassword("SenhasDiferentes123!");

            // When & Then
            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registroValido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("Senhas não coincidem")));
        }

        @Test
        @DisplayName("❌ Deve falhar com senha fraca")
        void deveFalharComSenhaFraca() throws Exception {
            // Given
            registroValido.setPassword("123"); // Senha muito fraca
            registroValido.setConfirmPassword("123");

            // When & Then
            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registroValido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("Senha deve ter pelo menos")));
        }

        @Test
        @DisplayName("❌ Deve falhar com nome muito curto")
        void deveFalharComNomeMuitoCurto() throws Exception {
            // Given
            registroValido.setNome("A"); // Nome muito curto

            // When & Then
            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registroValido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("Nome deve ter pelo menos")));
        }

        @Test
        @DisplayName("✅ Deve criptografar senha do usuário registrado")
        void deveCriptografarSenhaUsuarioRegistrado() throws Exception {
            // When
            mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registroValido)))
                    .andExpect(status().isCreated());

            // Then - Verificar que a senha foi criptografada
            User usuarioCriado = userRepository.findByEmail(registroValido.getEmail()).orElseThrow();
            assert !usuarioCriado.getPassword().equals(registroValido.getPassword()); // Senha não deve estar em texto plano
            assert passwordEncoder.matches(registroValido.getPassword(), usuarioCriado.getPassword()); // Deve bater com o hash
        }
    }

    // ========== TESTES DE REFRESH TOKEN ==========

    @Nested
    @DisplayName("POST /auth/refresh - Renovação de Tokens")
    class RefreshToken {

        private String obterRefreshToken() throws Exception {
            String response = mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginValido)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            return objectMapper.readTree(response).get("refreshToken").asText();
        }

        @Test
        @DisplayName("✅ Deve renovar token com refresh token válido")
        void deveRenovarTokenComRefreshTokenValido() throws Exception {
            // Given
            String refreshToken = obterRefreshToken();

            // When & Then
            mockMvc.perform(post("/auth/refresh")
                    .header("Authorization", "Bearer " + refreshToken)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").exists())
                    .andExpect(jsonPath("$.refreshToken").exists())
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.expiresIn").exists());
        }

        @Test
        @DisplayName("❌ Deve falhar com refresh token inválido")
        void deveFalharComRefreshTokenInvalido() throws Exception {
            // When & Then
            mockMvc.perform(post("/auth/refresh")
                    .header("Authorization", "Bearer token-invalido")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(containsString("Token inválido")));
        }

        @Test
        @DisplayName("❌ Deve falhar sem header de autorização")
        void deveFalharSemHeaderAutorizacao() throws Exception {
            // When & Then
            mockMvc.perform(post("/auth/refresh")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(containsString("Token não fornecido")));
        }

        @Test
        @DisplayName("❌ Deve falhar com refresh token expirado")
        void deveFalharComRefreshTokenExpirado() throws Exception {
            // Given - Token expirado (simulado com token antigo)
            String tokenExpirado = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0ZSIsImV4cCI6MTYzMjQ2ODQwMH0.invalid";

            // When & Then
            mockMvc.perform(post("/auth/refresh")
                    .header("Authorization", "Bearer " + tokenExpirado)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(containsString("Token expirado")));
        }
    }

    // ========== TESTES DE LOGOUT ==========

    @Nested
    @DisplayName("POST /auth/logout - Logout de Usuários")
    class LogoutUsuarios {

        private String obterAccessToken() throws Exception {
            String response = mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginValido)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            return objectMapper.readTree(response).get("accessToken").asText();
        }

        @Test
        @DisplayName("✅ Deve fazer logout com token válido")
        void deveFazerLogoutComTokenValido() throws Exception {
            // Given
            String accessToken = obterAccessToken();

            // When & Then
            mockMvc.perform(post("/auth/logout")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(containsString("Logout realizado com sucesso")));
        }

        @Test
        @DisplayName("❌ Deve falhar logout sem token")
        void deveFalharLogoutSemToken() throws Exception {
            // When & Then
            mockMvc.perform(post("/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(containsString("Token não fornecido")));
        }

        @Test
        @DisplayName("🔒 Deve invalidar token após logout")
        void deveInvalidarTokenAposLogout() throws Exception {
            // Given
            String accessToken = obterAccessToken();

            // When - Fazer logout
            mockMvc.perform(post("/auth/logout")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Then - Token deve estar inválido para acessar endpoints protegidos
            mockMvc.perform(get("/api/clientes")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(containsString("Token inválido")));
        }
    }

    // ========== TESTES DE VALIDAÇÃO DE TOKEN ==========

    @Nested
    @DisplayName("GET /auth/validate - Validação de Tokens")
    class ValidacaoToken {

        private String obterAccessToken() throws Exception {
            String response = mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginValido)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            return objectMapper.readTree(response).get("accessToken").asText();
        }

        @Test
        @DisplayName("✅ Deve validar token válido")
        void deveValidarTokenValido() throws Exception {
            // Given
            String accessToken = obterAccessToken();

            // When & Then
            mockMvc.perform(get("/auth/validate")
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valid").value(true))
                    .andExpect(jsonPath("$.user.id").value(usuarioExistente.getId()))
                    .andExpect(jsonPath("$.user.email").value(usuarioExistente.getEmail()))
                    .andExpect(jsonPath("$.expiresIn").exists());
        }

        @Test
        @DisplayName("❌ Deve invalidar token inválido")
        void deveInvalidarTokenInvalido() throws Exception {
            // When & Then
            mockMvc.perform(get("/auth/validate")
                    .header("Authorization", "Bearer token-invalido")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.valid").value(false))
                    .andExpect(jsonPath("$.message").value(containsString("Token inválido")));
        }

        @Test
        @DisplayName("❌ Deve falhar sem token")
        void deveFalharSemToken() throws Exception {
            // When & Then
            mockMvc.perform(get("/auth/validate")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.valid").value(false))
                    .andExpect(jsonPath("$.message").value(containsString("Token não fornecido")));
        }
    }

    // ========== TESTES DE SEGURANÇA ==========

    @Nested
    @DisplayName("Testes de Segurança Avançados")
    class TestesSeguranca {

        @Test
        @DisplayName("🔒 Deve proteger endpoints sensíveis")
        void deveProtegerEndpointsSensiveis() throws Exception {
            // When & Then - Tentar acessar endpoints protegidos sem autenticação
            mockMvc.perform(get("/api/clientes")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());

            mockMvc.perform(post("/api/pedidos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isUnauthorized());

            mockMvc.perform(delete("/api/clientes/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("🔒 Deve validar formato do header Authorization")
        void deveValidarFormatoHeaderAuthorization() throws Exception {
            // When & Then - Header sem 'Bearer '
            mockMvc.perform(get("/auth/validate")
                    .header("Authorization", "token-sem-bearer")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(containsString("Formato de token inválido")));
        }

        @Test
        @DisplayName("🔒 Deve sanitizar dados de resposta")
        void deveSanitizarDadosResposta() throws Exception {
            // When
            String response = mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginValido)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            // Then - Verificar que dados sensíveis não são retornados
            assert !response.contains("password");
            assert !response.contains("senha");
            assert !response.contains(usuarioExistente.getPassword());
        }

        @Test
        @DisplayName("🔒 Deve prevenir ataques de SQL Injection em login")
        void devePreveniAtaquesSqlInjectionLogin() throws Exception {
            // Given
            LoginRequestDTO loginComSqlInjection = new LoginRequestDTO();
            loginComSqlInjection.setEmail("' OR '1'='1' --");
            loginComSqlInjection.setPassword("qualquer-senha");

            // When & Then
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginComSqlInjection)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(containsString("Credenciais inválidas")));
        }

        @Test
        @DisplayName("🔒 Deve implementar timeout de sessão")
        void deveImplementarTimeoutSessao() throws Exception {
            // Given - Simular token com tempo de expiração curto
            String tokenExpirando = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0ZSIsImV4cCI6MTYzMjQ2ODQwMH0.short";

            // When & Then
            mockMvc.perform(get("/auth/validate")
                    .header("Authorization", "Bearer " + tokenExpirando)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value(containsString("Token expirado")));
        }
    }

    // ========== TESTES DE PERFORMANCE ==========

    @Nested
    @DisplayName("Testes de Performance de Autenticação")
    class TestesPerformance {

        @Test
        @DisplayName("⚡ Deve processar login em tempo adequado")
        void deveProcessarLoginTempoAdequado() throws Exception {
            // Given
            long inicioTempo = System.currentTimeMillis();

            // When
            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginValido)))
                    .andExpect(status().isOk());

            // Then
            long tempoProcessamento = System.currentTimeMillis() - inicioTempo;
            assert tempoProcessamento < 1000; // Deve processar em menos de 1 segundo
        }

        @Test
        @DisplayName("⚡ Deve validar múltiplos tokens simultaneamente")
        void deveValidarMultiplosTokensSimultaneamente() throws Exception {
            // Given
            String accessToken = mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginValido)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            String token = objectMapper.readTree(accessToken).get("accessToken").asText();

            // When & Then - Fazer múltiplas validações simultâneas
            for (int i = 0; i < 10; i++) {
                mockMvc.perform(get("/auth/validate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.valid").value(true));
            }
        }
    }
}
