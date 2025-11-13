package com.exemplo.apifest.controller;

import com.exemplo.apifest.dto.ClienteDTO;
import com.exemplo.apifest.dto.response.ClienteResponseDTO;
import com.exemplo.apifest.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.exemplo.apifest.config.SecurityConfig;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
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
@WebMvcTest(ClienteController.class)
@DisplayName("ClienteController - Testes de Integração")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SecurityConfig.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteDTO clienteDTO;
    private ClienteResponseDTO clienteResponseDTO;

    @BeforeEach
    void setUp() {
        clienteDTO = new ClienteDTO();
        clienteDTO.setNome("João Silva");
        clienteDTO.setEmail("joao@email.com");
        clienteDTO.setTelefone("11999999999");
        clienteDTO.setEndereco("Rua A, 123");

        clienteResponseDTO = new ClienteResponseDTO();
        clienteResponseDTO.setId(1L);
        clienteResponseDTO.setNome("João Silva");
        clienteResponseDTO.setEmail("joao@email.com");
        clienteResponseDTO.setTelefone("11999999999");
        clienteResponseDTO.setEndereco("Rua A, 123");
        clienteResponseDTO.setAtivo(true);
    }

    @Test
    @DisplayName("POST /api/clientes - Deve cadastrar cliente com sucesso")
    void deveCadastrarClienteComSucesso() throws Exception {
        // Arrange
        when(clienteService.cadastrarCliente(any(ClienteDTO.class)))
            .thenReturn(clienteResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.ativo").value(true));

        verify(clienteService).cadastrarCliente(any(ClienteDTO.class));
    }

    @Test
    @DisplayName("GET /api/clientes/{id} - Deve buscar cliente por ID")
    void deveBuscarClientePorId() throws Exception {
        // Arrange
        when(clienteService.buscarClientePorId(1L))
            .thenReturn(clienteResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));

        verify(clienteService).buscarClientePorId(1L);
    }

    @Test
    @DisplayName("GET /api/clientes - Deve listar clientes ativos")
    void deveListarClientesAtivos() throws Exception {
        // Arrange
        List<ClienteResponseDTO> clientes = Arrays.asList(clienteResponseDTO);
        when(clienteService.listarClientesAtivos()).thenReturn(clientes);

        // Act & Assert
        mockMvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("João Silva"));

        verify(clienteService).listarClientesAtivos();
    }

    @Test
    @DisplayName("PUT /api/clientes/{id} - Deve atualizar cliente")
    void deveAtualizarCliente() throws Exception {
        // Arrange
        ClienteDTO clienteAtualizado = new ClienteDTO();
        clienteAtualizado.setNome("João Santos");
        clienteAtualizado.setEmail("joao.santos@email.com");
        clienteAtualizado.setTelefone("11888888888");
        clienteAtualizado.setEndereco("Rua B, 456");

        ClienteResponseDTO clienteAtualizadoResponse = new ClienteResponseDTO();
        clienteAtualizadoResponse.setId(1L);
        clienteAtualizadoResponse.setNome("João Santos");
        clienteAtualizadoResponse.setEmail("joao.santos@email.com");

        when(clienteService.atualizarCliente(eq(1L), any(ClienteDTO.class)))
            .thenReturn(clienteAtualizadoResponse);

        // Act & Assert
        mockMvc.perform(put("/api/clientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("João Santos"));

        verify(clienteService).atualizarCliente(eq(1L), any(ClienteDTO.class));
    }

    @Test
    @DisplayName("PATCH /api/clientes/{id}/status - Deve alterar status do cliente")
    void deveAlterarStatusCliente() throws Exception {
        // Arrange
        ClienteResponseDTO clienteInativo = new ClienteResponseDTO();
        clienteInativo.setId(1L);
        clienteInativo.setNome("João Silva");
        clienteInativo.setAtivo(false);

        when(clienteService.ativarDesativarCliente(1L))
            .thenReturn(clienteInativo);

        // Act & Assert
        mockMvc.perform(patch("/api/clientes/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.ativo").value(false));

        verify(clienteService).ativarDesativarCliente(1L);
    }

    @Test
    @DisplayName("GET /api/clientes/email/{email} - Deve buscar cliente por email")
    void deveBuscarClientePorEmail() throws Exception {
        // Arrange
        when(clienteService.buscarClientePorEmail("joao@email.com"))
            .thenReturn(clienteResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/clientes/email/joao@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.nome").value("João Silva"));

        verify(clienteService).buscarClientePorEmail("joao@email.com");
    }

    @Test
    @DisplayName("POST /api/clientes - Deve retornar 400 para dados inválidos")
    void deveRetornar400ParaDadosInvalidos() throws Exception {
        // Arrange - Cliente sem nome (campo obrigatório)
        ClienteDTO clienteInvalido = new ClienteDTO();
        clienteInvalido.setEmail("joao@email.com");
        // Nome é obrigatório, então deve falhar na validação

        // Act & Assert
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteInvalido)))
                .andExpect(status().isBadRequest());

        verify(clienteService, never()).cadastrarCliente(any(ClienteDTO.class));
    }
}