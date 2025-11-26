package com.exemplo.apifest.controller.v1;

import com.exemplo.apifest.dto.ClienteDTO;
import com.exemplo.apifest.dto.response.ClienteResponseDTO;
import com.exemplo.apifest.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciamento de clientes - Versão 1.0
 * Implementa operações CRUD completas para clientes
 * 
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Cliente V1", description = "API para gerenciamento de clientes - Versão 1.0")
public class ClienteV1Controller {

    @Autowired
    private ClienteService clienteService;

    /**
     * Lista todos os clientes com paginação
     */
    @GetMapping
    @Operation(summary = "Listar clientes", description = "Retorna lista de todos os clientes ativos")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {
        List<ClienteResponseDTO> clientes = clienteService.listarClientesAtivos();
        return ResponseEntity.ok(clientes);
    }

    /**
     * Busca cliente por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna dados de um cliente específico")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
        ClienteResponseDTO cliente = clienteService.buscarClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    /**
     * Cria novo cliente
     */
    @PostMapping
    @Operation(summary = "Criar cliente", description = "Cria um novo cliente no sistema")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDTO> criar(@Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteResponseDTO clienteCriado = clienteService.cadastrarCliente(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteCriado);
    }

    /**
     * Atualiza cliente existente
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza dados de um cliente existente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteResponseDTO clienteAtualizado = clienteService.atualizarCliente(id, clienteDTO);
        return ResponseEntity.ok(clienteAtualizado);
    }

    /**
     * Exclui cliente
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar cliente", description = "Desativa um cliente do sistema")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDTO> excluir(@PathVariable Long id) {
        ClienteResponseDTO clienteDesativado = clienteService.ativarDesativarCliente(id);
        return ResponseEntity.ok(clienteDesativado);
    }
}