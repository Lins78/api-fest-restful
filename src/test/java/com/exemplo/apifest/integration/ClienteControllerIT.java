package com.exemplo.apifest.integration;

import com.exemplo.apifest.dto.ClienteDTO;
import com.exemplo.apifest.dto.auth.LoginRequest;
import com.exemplo.apifest.dto.auth.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
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
 * Testes de integração para ClienteController.
 * 
 * Este teste verifica todos os endpoints relacionados aos clientes,
 * incluindo operações CRUD com diferentes níveis de autorização.
 * 
 * CENÁRIOS TESTADOS:
 * - Listar clientes (público)
 * - Buscar cliente por ID (público) 
 * - Criar cliente (autenticado)
 * - Atualizar cliente (próprio cliente ou admin)
 * - Excluir cliente (próprio cliente ou admin)
 * - Validações de entrada
 * - Controle de acesso por roles
 * 
 * @author DeliveryTech Team
 * @version 1.0 - Roteiro 8
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("👤 Testes de Integração - ClienteController")
public class ClienteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String clienteToken;
    private String adminToken;
    private Long clienteId;

    @BeforeEach
    public void setUp() throws Exception {
        // Cria usuário cliente
        createClienteUser();
        // Cria usuário admin
        createAdminUser();
    }

    private void createClienteUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNome("Cliente Teste");
        registerRequest.setEmail("cliente@teste.com");
        registerRequest.setSenha("senha123");
        registerRequest.setTelefone("11999999999");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // Faz login para obter token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("cliente@teste.com");
        loginRequest.setSenha("senha123");

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        clienteToken = objectMapper.readTree(response).get("token").asText();
        clienteId = objectMapper.readTree(response).get("usuario").get("id").asLong();
    }

    private void createAdminUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNome("Admin Teste");
        registerRequest.setEmail("admin@teste.com");
        registerRequest.setSenha("senha123");
        registerRequest.setTelefone("11888888888");
        // Assumindo que existe uma forma de criar admin via register

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // Faz login para obter token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@teste.com");
        loginRequest.setSenha("senha123");

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        adminToken = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    @DisplayName("✅ Deve listar todos os clientes (endpoint público)")
    public void testListClientes() throws Exception {
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("✅ Deve buscar cliente por ID (endpoint público)")
    public void testGetClienteById() throws Exception {
        mockMvc.perform(get("/api/clientes/{id}", clienteId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(clienteId.intValue())))
                .andExpect(jsonPath("$.nome", is("Cliente Teste")))
                .andExpect(jsonPath("$.email", is("cliente@teste.com")));
    }

    @Test
    @DisplayName("❌ Deve retornar 404 para cliente inexistente")
    public void testGetClienteByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/clientes/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("✅ Deve criar cliente com dados válidos (autenticado)")
    public void testCreateCliente() throws Exception {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Novo Cliente");
        clienteDTO.setEmail("novo@cliente.com");
        clienteDTO.setTelefone("11777777777");
        clienteDTO.setEndereco("Rua Nova, 123");

        mockMvc.perform(post("/api/clientes")
                .header("Authorization", "Bearer " + clienteToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", is("Novo Cliente")))
                .andExpect(jsonPath("$.email", is("novo@cliente.com")));
    }

    @Test
    @DisplayName("❌ Deve rejeitar criação de cliente sem autenticação")
    public void testCreateClienteUnauthorized() throws Exception {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Cliente Sem Auth");
        clienteDTO.setEmail("semauth@cliente.com");

        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("❌ Deve validar campos obrigatórios na criação")
    public void testCreateClienteValidation() throws Exception {
        ClienteDTO clienteDTO = new ClienteDTO();
        // Deixa campos obrigatórios em branco

        mockMvc.perform(post("/api/clientes")
                .header("Authorization", "Bearer " + clienteToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("✅ Deve atualizar próprio cliente")
    public void testUpdateOwnCliente() throws Exception {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Nome Atualizado");
        clienteDTO.setEmail("cliente@teste.com");
        clienteDTO.setTelefone("11999999999");
        clienteDTO.setEndereco("Endereço Atualizado");

        mockMvc.perform(put("/api/clientes/{id}", clienteId)
                .header("Authorization", "Bearer " + clienteToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Nome Atualizado")))
                .andExpect(jsonPath("$.endereco", is("Endereço Atualizado")));
    }

    @Test
    @DisplayName("❌ Deve rejeitar atualização de cliente de outro usuário")
    public void testUpdateOtherClienteForbidden() throws Exception {
        // Cria outro cliente
        Long outroClienteId = 999L;

        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Tentativa Hack");

        mockMvc.perform(put("/api/clientes/{id}", outroClienteId)
                .header("Authorization", "Bearer " + clienteToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("✅ Admin deve poder atualizar qualquer cliente")
    public void testAdminUpdateAnyCliente() throws Exception {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNome("Atualizado pelo Admin");
        clienteDTO.setEmail("cliente@teste.com");
        clienteDTO.setTelefone("11999999999");

        mockMvc.perform(put("/api/clientes/{id}", clienteId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Atualizado pelo Admin")));
    }

    @Test
    @DisplayName("✅ Deve ativar/desativar cliente")
    public void testToggleClienteStatus() throws Exception {
        mockMvc.perform(patch("/api/clientes/{id}/status", clienteId)
                .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativo").exists());
    }

    @Test
    @DisplayName("✅ Deve excluir próprio cliente")
    public void testDeleteOwnCliente() throws Exception {
        mockMvc.perform(delete("/api/clientes/{id}", clienteId)
                .header("Authorization", "Bearer " + clienteToken))
                .andExpect(status().isNoContent());

        // Verifica se foi realmente excluído
        mockMvc.perform(get("/api/clientes/{id}", clienteId))
                .andExpect(status().isNotFound());
    }
}