package com.exemplo.apifest.controller;

import com.exemplo.apifest.dto.ItemPedidoDTO;
import com.exemplo.apifest.dto.PedidoDTO;
import com.exemplo.apifest.dto.response.PedidoResponseDTO;
import com.exemplo.apifest.dto.response.PedidoResumoDTO;
import com.exemplo.apifest.model.StatusPedido;
import com.exemplo.apifest.service.PedidoService;
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
import java.math.BigDecimal;
import java.util.List;

/**
 * ===============================================================================
 * ROTEIRO 4 - CONTROLLER REST DE PEDIDOS (MAIS COMPLEXO)
 * ===============================================================================
 * 
 * Controller responsável por expor endpoints RESTful para gerenciamento de pedidos.
 * Este é o controller mais crítico, lidando com operações transacionais complexas.
 * 
 * ENDPOINTS IMPLEMENTADOS:
 * - POST   /api/pedidos                    → Criar pedido (transação complexa) (201)
 * - GET    /api/pedidos/{id}               → Buscar pedido completo (200)
 * - GET    /api/clientes/{clienteId}/pedidos → Histórico do cliente (200)
 * - PATCH  /api/pedidos/{id}/status        → Atualizar status (200)
 * - DELETE /api/pedidos/{id}               → Cancelar pedido (200)
 * - POST   /api/pedidos/calcular           → Calcular total (200)
 * 
 * @author DeliveryTech Development Team
 * @version 1.0 - Roteiro 4
 * @since Java 21 LTS + Spring Boot 3.4.0
 * ===============================================================================
 */
@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
@Tag(name = "Pedidos", description = "Operações relacionadas ao gerenciamento de pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    /**
     * POST /api/pedidos - Criar novo pedido (OPERAÇÃO MAIS CRÍTICA)
     * 
     * EXEMPLO DE REQUEST:
     * {
     *   "clienteId": 1,
     *   "restauranteId": 1,
     *   "enderecoEntrega": "Rua B, 456",
     *   "itens": [
     *     {"produtoId": 1, "quantidade": 2},
     *     {"produtoId": 2, "quantidade": 1}
     *   ]
     * }
     */
    @Operation(summary = "Criar pedido", 
               description = "Cria um novo pedido com transação complexa incluindo múltiplos itens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Cliente ou produto não encontrado")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CLIENTE')")
    public ResponseEntity<PedidoResponseDTO> criarPedido(@Valid @RequestBody PedidoDTO pedidoDTO) {
        PedidoResponseDTO pedidoCriado = pedidoService.criarPedido(pedidoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoCriado);
    }

    /**
     * GET /api/pedidos/{id} - Buscar pedido completo com itens
     */
    @Operation(summary = "Buscar pedido por ID", 
               description = "Busca um pedido completo incluindo todos os itens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or @pedidoService.podeVerPedido(#id, authentication.principal)")
    public ResponseEntity<PedidoResponseDTO> buscarPedidoPorId(
        @Parameter(description = "ID do pedido") @PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.buscarPedidoPorId(id);
        return ResponseEntity.ok(pedido);
    }

    /**
     * GET /api/clientes/{clienteId}/pedidos - Histórico de pedidos do cliente
     */
    @Operation(summary = "Buscar pedidos por cliente", 
               description = "Lista o histórico de pedidos de um cliente específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('CLIENTE') and authentication.principal.id == #clienteId)")
    public ResponseEntity<List<PedidoResumoDTO>> buscarPedidosPorCliente(
        @Parameter(description = "ID do cliente") @PathVariable Long clienteId) {
        List<PedidoResumoDTO> pedidos = pedidoService.buscarPedidosPorCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * PATCH /api/pedidos/{id}/status - Atualizar status do pedido
     * 
     * EXEMPLO DE REQUEST:
     * {
     *   "status": "CONFIRMADO"
     * }
     */
    @Operation(summary = "Atualizar status do pedido", 
               description = "Atualiza o status do pedido (PENDENTE, CONFIRMADO, PREPARANDO, etc.)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
        @ApiResponse(responseCode = "400", description = "Status inválido")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('RESTAURANTE')")
    public ResponseEntity<PedidoResponseDTO> atualizarStatusPedido(
            @Parameter(description = "ID do pedido") @PathVariable Long id,
            @Parameter(description = "Novo status do pedido") @RequestParam StatusPedido status) {
        PedidoResponseDTO pedidoAtualizado = pedidoService.atualizarStatusPedido(id, status);
        return ResponseEntity.ok(pedidoAtualizado);
    }

    /**
     * DELETE /api/pedidos/{id} - Cancelar pedido
     */
    @Operation(summary = "Cancelar pedido", 
               description = "Cancela um pedido existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
        @ApiResponse(responseCode = "400", description = "Pedido não pode ser cancelado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or @pedidoService.podeVerPedido(#id, authentication.principal)")
    public ResponseEntity<PedidoResponseDTO> cancelarPedido(
        @Parameter(description = "ID do pedido") @PathVariable Long id) {
        PedidoResponseDTO pedidoCancelado = pedidoService.cancelarPedido(id);
        return ResponseEntity.ok(pedidoCancelado);
    }

    /**
     * POST /api/pedidos/calcular - Calcular total do pedido sem salvar
     * 
     * EXEMPLO DE REQUEST:
     * [
     *   {"produtoId": 1, "quantidade": 2},
     *   {"produtoId": 2, "quantidade": 1}
     * ]
     */
    @Operation(summary = "Calcular total do pedido", 
               description = "Calcula o valor total de um pedido sem persistir no banco")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total calculado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Lista de itens inválida")
    })
    @PostMapping("/calcular")
    @PreAuthorize("permitAll()")
    public ResponseEntity<BigDecimal> calcularTotalPedido(@Valid @RequestBody List<ItemPedidoDTO> itens) {
        BigDecimal total = pedidoService.calcularTotalPedido(itens);
        return ResponseEntity.ok(total);
    }
}