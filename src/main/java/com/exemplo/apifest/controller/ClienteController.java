package com.exemplo.apifest.controller;

import com.exemplo.apifest.dto.ClienteDTO;
import com.exemplo.apifest.dto.response.ClienteResponseDTO;
import com.exemplo.apifest.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrarCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteResponseDTO clienteCriado = clienteService.cadastrarCliente(clienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteCriado);
    }

    /**
     * GET /api/clientes/{id} - Buscar cliente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarClientePorId(@PathVariable Long id) {
        ClienteResponseDTO cliente = clienteService.buscarClientePorId(id);
        return ResponseEntity.ok(cliente);
    }

    /**
     * GET /api/clientes - Listar todos os clientes ativos
     */
    @GetMapping
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
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteResponseDTO clienteAtualizado = clienteService.atualizarCliente(id, clienteDTO);
        return ResponseEntity.ok(clienteAtualizado);
    }

    /**
     * PATCH /api/clientes/{id}/status - Ativar/desativar cliente
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ClienteResponseDTO> ativarDesativarCliente(@PathVariable Long id) {
        ClienteResponseDTO clienteAtualizado = clienteService.ativarDesativarCliente(id);
        return ResponseEntity.ok(clienteAtualizado);
    }

    /**
     * GET /api/clientes/email/{email} - Buscar cliente por email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ClienteResponseDTO> buscarClientePorEmail(@PathVariable String email) {
        ClienteResponseDTO cliente = clienteService.buscarClientePorEmail(email);
        return ResponseEntity.ok(cliente);
    }

}