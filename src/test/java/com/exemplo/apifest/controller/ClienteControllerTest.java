package com.exemplo.apifest.controller;

import com.exemplo.apifest.dto.ClienteDTO;
import com.exemplo.apifest.dto.response.ClienteResponseDTO;
import com.exemplo.apifest.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para ClienteController
 * 
 * Testa os endpoints REST do controller de clientes,
 * validando status codes, estrutura de resposta e integração com service.
 * 
 * @author API FEST RESTful Team
 * @version 1.0 - Roteiro 5 - Testes de Integração
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@WithMockUser(roles = "ADMIN")
@DisplayName("ClienteController - Testes de Integração")
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteResponseDTO clienteResponse;
    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        // Setup do cliente response
        clienteResponse = new ClienteResponseDTO();
        clienteResponse.setId(1L);
        clienteResponse.setNome("João Silva");
        clienteResponse.setEmail("joao@teste.com");
        clienteResponse.setTelefone("11987654321");
        clienteResponse.setEndereco("Rua A, 123");
        clienteResponse.setAtivo(true);

        // Setup do cliente DTO
        clienteDTO = new ClienteDTO();
        clienteDTO.setNome("João Silva");
        clienteDTO.setEmail("joao@teste.com");
        clienteDTO.setTelefone("11987654321");
        clienteDTO.setEndereco("Rua A, 123");
    }

    @Test
    @DisplayName("Deve cadastrar cliente com sucesso")
    void deveCadastrarClienteComSucesso() throws Exception {
        // Given
        when(clienteService.cadastrarCliente(any(ClienteDTO.class))).thenReturn(clienteResponse);

        // When & Then
        mockMvc.perform(post("/api/clientes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@teste.com"))
                .andExpect(jsonPath("$.telefone").value("11987654321"))
                .andExpect(jsonPath("$.endereco").value("Rua A, 123"))
                .andExpect(jsonPath("$.ativo").value(true));

        verify(clienteService).cadastrarCliente(any(ClienteDTO.class));
    }

    @Test
    @DisplayName("Deve retornar 400 para dados inválidos")
    void deveRetornar400ParaDadosInvalidos() throws Exception {
        // Given
        ClienteDTO clienteInvalido = new ClienteDTO();
        clienteInvalido.setNome(""); // Nome inválido
        clienteInvalido.setEmail("email-invalido"); // Email inválido

        // When & Then
        mockMvc.perform(post("/api/clientes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve listar clientes ativos")
    void deveListarClientesAtivos() throws Exception {
        // Given
        List<ClienteResponseDTO> clientes = Arrays.asList(clienteResponse);
        when(clienteService.listarClientesAtivos()).thenReturn(clientes);

        // When & Then
        mockMvc.perform(get("/api/clientes/ativos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("João Silva"));

        verify(clienteService).listarClientesAtivos();
    }

    @Test
    @DisplayName("Deve buscar cliente por ID")
    void deveBuscarClientePorId() throws Exception {
        // Given
        when(clienteService.buscarClientePorId(1L)).thenReturn(clienteResponse);

        // When & Then
        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(clienteService).buscarClientePorId(1L);
    }

    @Test
    @DisplayName("Deve buscar cliente por email")
    void deveBuscarClientePorEmail() throws Exception {
        // Given
        when(clienteService.buscarClientePorEmail("joao@teste.com")).thenReturn(clienteResponse);

        // When & Then
        mockMvc.perform(get("/api/clientes/email/joao@teste.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("joao@teste.com"));

        verify(clienteService).buscarClientePorEmail("joao@teste.com");
    }

    @Test
    @DisplayName("Deve atualizar cliente")
    void deveAtualizarCliente() throws Exception {
        // Given
        when(clienteService.atualizarCliente(eq(1L), any(ClienteDTO.class))).thenReturn(clienteResponse);

        // When & Then
        mockMvc.perform(put("/api/clientes/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(clienteService).atualizarCliente(eq(1L), any(ClienteDTO.class));
    }

    @Test
    @DisplayName("Deve alterar status do cliente")
    void deveAlterarStatusCliente() throws Exception {
        // Given
        clienteResponse.setAtivo(false);
        when(clienteService.ativarDesativarCliente(1L)).thenReturn(clienteResponse);

        // When & Then
        mockMvc.perform(patch("/api/clientes/1/status")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ativo").value(false));

        verify(clienteService).ativarDesativarCliente(1L);
    }
}