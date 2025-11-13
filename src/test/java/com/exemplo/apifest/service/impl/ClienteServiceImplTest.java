package com.exemplo.apifest.service.impl;

import com.exemplo.apifest.dto.ClienteDTO;
import com.exemplo.apifest.dto.response.ClienteResponseDTO;
import com.exemplo.apifest.exception.BusinessException;
import com.exemplo.apifest.exception.EntityNotFoundException;
import com.exemplo.apifest.model.Cliente;
import com.exemplo.apifest.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ClienteServiceImpl
 * 
 * Testa todas as regras de negócio e validações do serviço de clientes,
 * incluindo cenários de sucesso e falha.
 * 
 * @author API FEST RESTful Team
 * @version 1.0 - Roteiro 5 - Testes Unitários
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService - Testes Unitários")
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private ClienteDTO clienteDTO;
    private Cliente cliente;
    private ClienteResponseDTO clienteResponseDTO;

    @BeforeEach
    void setUp() {
        // Setup dos objetos de teste
        clienteDTO = new ClienteDTO();
        clienteDTO.setNome("João Silva");
        clienteDTO.setEmail("joao@email.com");
        clienteDTO.setTelefone("11999999999");
        clienteDTO.setEndereco("Rua A, 123");

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEmail("joao@email.com");
        cliente.setTelefone("11999999999");
        cliente.setEndereco("Rua A, 123");
        cliente.setAtivo(true);
        cliente.setDataCadastro(LocalDateTime.now());

        clienteResponseDTO = new ClienteResponseDTO();
        clienteResponseDTO.setId(1L);
        clienteResponseDTO.setNome("João Silva");
        clienteResponseDTO.setEmail("joao@email.com");
        clienteResponseDTO.setTelefone("11999999999");
        clienteResponseDTO.setEndereco("Rua A, 123");
        clienteResponseDTO.setAtivo(true);
    }

    @Test
    @DisplayName("Deve cadastrar cliente com sucesso")
    void deveCadastrarClienteComSucesso() {
        // Arrange
        when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
        when(modelMapper.map(clienteDTO, Cliente.class)).thenReturn(cliente);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(clienteResponseDTO);

        // Act
        ClienteResponseDTO resultado = clienteService.cadastrarCliente(clienteDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertEquals("joao@email.com", resultado.getEmail());
        assertTrue(resultado.getAtivo());
        
        verify(clienteRepository).existsByEmail("joao@email.com");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar cliente com email duplicado")
    void deveLancarExcecaoEmailDuplicado() {
        // Arrange
        when(clienteRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> clienteService.cadastrarCliente(clienteDTO));
        
        assertEquals("Já existe um cliente cadastrado com o email: joao@email.com", 
                     exception.getMessage());
        
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    void deveBuscarClientePorIdComSucesso() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(clienteResponseDTO);

        // Act
        ClienteResponseDTO resultado = clienteService.buscarClientePorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("João Silva", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar cliente inexistente")
    void deveLancarExcecaoClienteNaoEncontrado() {
        // Arrange
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> clienteService.buscarClientePorId(999L));
        
        assertEquals("Cliente não encontrado com ID: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Deve buscar cliente por email com sucesso")
    void deveBuscarClientePorEmailComSucesso() {
        // Arrange
        when(clienteRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(cliente));
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(clienteResponseDTO);

        // Act
        ClienteResponseDTO resultado = clienteService.buscarClientePorEmail("joao@email.com");

        // Assert
        assertNotNull(resultado);
        assertEquals("joao@email.com", resultado.getEmail());
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void deveAtualizarClienteComSucesso() {
        // Arrange
        ClienteDTO clienteAtualizado = new ClienteDTO();
        clienteAtualizado.setNome("João Santos");
        clienteAtualizado.setEmail("joao.santos@email.com");
        clienteAtualizado.setTelefone("11888888888");
        clienteAtualizado.setEndereco("Rua B, 456");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.findByEmail("joao.santos@email.com")).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(clienteResponseDTO);

        // Act
        ClienteResponseDTO resultado = clienteService.atualizarCliente(1L, clienteAtualizado);

        // Assert
        assertNotNull(resultado);
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve ativar/desativar cliente com sucesso")
    void deveAtivarDesativarClienteComSucesso() {
        // Arrange
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(clienteResponseDTO);

        // Act
        ClienteResponseDTO resultado = clienteService.ativarDesativarCliente(1L);

        // Assert
        assertNotNull(resultado);
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve listar clientes ativos com sucesso")
    void deveListarClientesAtivosComSucesso() {
        // Arrange
        List<Cliente> clientesAtivos = Arrays.asList(cliente);
        when(clienteRepository.findByAtivoTrue()).thenReturn(clientesAtivos);
        when(modelMapper.map(cliente, ClienteResponseDTO.class)).thenReturn(clienteResponseDTO);

        // Act
        List<ClienteResponseDTO> resultado = clienteService.listarClientesAtivos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("João Silva", resultado.get(0).getNome());
    }
}