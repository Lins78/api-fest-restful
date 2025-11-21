package com.exemplo.apifest.controller;

import com.exemplo.apifest.dto.ClienteDTO;
import com.exemplo.apifest.dto.response.ClienteResponseDTO;
import com.exemplo.apifest.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;
import java.util.List;

/**
 * ===============================================================================
 * ROTEIRO 4 - CONTROLLER REST DE CLIENTES
 * ===============================================================================
 * 
 * Controller responsável por expor endpoints RESTful para gerenciamento de clientes.
 * Segue convenções HTTP e implementa status codes apropriados para cada operação.
 * 
 * ENDPOINTS IMPLEMENTADOS:
 * - POST   /api/clientes           → Cadastrar cliente (201 Created)
 * - GET    /api/clientes/{id}      → Buscar por ID (200 OK)
 * - GET    /api/clientes           → Listar clientes ativos (200 OK)
 * - PUT    /api/clientes/{id}      → Atualizar cliente (200 OK)
 * - PATCH  /api/clientes/{id}/status → Ativar/desativar (200 OK)
 * - GET    /api/clientes/email/{email} → Buscar por email (200 OK)
 * 
 * STATUS CODES UTILIZADOS:
 * - 200 OK: Operação realizada com sucesso
 * - 201 Created: Recurso criado com sucesso
 * - 400 Bad Request: Dados inválidos (Bean Validation)
 * - 404 Not Found: Cliente não encontrado
 * - 409 Conflict: Email já cadastrado (BusinessException)
 * 
 * @author DeliveryTech Development Team
 * @version 1.0 - Roteiro 4
 * @since Java 21 LTS + Spring Boot 3.4.0
 * ===============================================================================
 */
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*") // Permitir CORS para desenvolvimento
@Tag(name = "Clientes", description = "Operações relacionadas ao gerenciamento de clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    /**
     * POST /api/clientes - Cadastrar novo cliente
     * 
     * EXEMPLO DE REQUEST:
     * {
     *   "nome": "João Silva",
     *   "email": "joao@email.com",
     *   "telefone": "11999999999",
     *   "endereco": "Rua A, 123"
     * }
     */
    @Operation(summary = "Cadastrar cliente", 
               description = "Cadastra um novo cliente no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Email já cadastrado")
    })
    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<ClienteResponseDTO> cadastrarCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteResponseDTO clienteCriado = clienteService.cadastrarCliente(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteCriado);
    }

    /**
     * GET /api/clientes/{id} - Buscar cliente por ID
     */
    @Operation(summary = "Buscar cliente por ID", 
               description = "Busca um cliente pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('CLIENTE') and authentication.principal.id == #id)")
    public ResponseEntity<ClienteResponseDTO> buscarClientePorId(
        @Parameter(description = "ID do cliente") @PathVariable Long id) {
        ClienteResponseDTO cliente = clienteService.buscarClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    /**
     * GET /api/clientes - Listar todos os clientes ativos
     */
    @Operation(summary = "Listar clientes ativos", 
               description = "Lista todos os clientes ativos do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ClienteResponseDTO>> listarClientesAtivos() {
        List<ClienteResponseDTO> clientes = clienteService.listarClientesAtivos();
        return ResponseEntity.ok(clientes);
    }

    /**
     * PUT /api/clientes/{id} - Atualizar dados do cliente
     * 
     * EXEMPLO DE REQUEST:
     * {
     *   "nome": "João Silva Santos",
     *   "email": "joao.santos@email.com",
     *   "telefone": "11888888888",
     *   "endereco": "Rua B, 456"
     * }
     */
    @Operation(summary = "Atualizar cliente", 
               description = "Atualiza os dados de um cliente existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
        @ApiResponse(responseCode = "409", description = "Email já cadastrado por outro cliente")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('CLIENTE') and authentication.principal.id == #id)")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long id,
            @Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteResponseDTO clienteAtualizado = clienteService.atualizarCliente(id, clienteDTO);
        return ResponseEntity.ok(clienteAtualizado);
    }

    /**
     * PATCH /api/clientes/{id}/status - Ativar/desativar cliente
     */
    @Operation(summary = "Ativar/Desativar cliente", 
               description = "Alterna o status de ativo/inativo do cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status do cliente alterado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ClienteResponseDTO> ativarDesativarCliente(
        @Parameter(description = "ID do cliente") @PathVariable Long id) {
        ClienteResponseDTO clienteAtualizado = clienteService.ativarDesativarCliente(id);
        return ResponseEntity.ok(clienteAtualizado);
    }

    /**
     * GET /api/clientes/email/{email} - Buscar cliente por email
     */
    @Operation(summary = "Buscar cliente por email", 
               description = "Busca um cliente pelo seu email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ClienteResponseDTO> buscarClientePorEmail(
        @Parameter(description = "Email do cliente") @PathVariable String email) {
        ClienteResponseDTO cliente = clienteService.buscarClientePorEmail(email);
        return ResponseEntity.ok(cliente);
    }

}