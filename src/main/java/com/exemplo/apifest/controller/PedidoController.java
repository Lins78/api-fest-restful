package com.exemplo.apifest.controller;

import com.exemplo.apifest.dto.ItemPedidoDTO;
import com.exemplo.apifest.dto.PedidoDTO;
import com.exemplo.apifest.dto.response.PedidoResponseDTO;
import com.exemplo.apifest.dto.response.PedidoResumoDTO;
import com.exemplo.apifest.model.StatusPedido;
import com.exemplo.apifest.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(@Valid @RequestBody PedidoDTO pedidoDTO) {
        PedidoResponseDTO pedidoCriado = pedidoService.criarPedido(pedidoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoCriado);
    }

    /**
     * GET /api/pedidos/{id} - Buscar pedido completo com itens
     */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPedidoPorId(@PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.buscarPedidoPorId(id);
        return ResponseEntity.ok(pedido);
    }

    /**
     * GET /api/clientes/{clienteId}/pedidos - Histórico de pedidos do cliente
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<PedidoResumoDTO>> buscarPedidosPorCliente(@PathVariable Long clienteId) {
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
    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> atualizarStatusPedido(
            @PathVariable Long id,
            @RequestParam StatusPedido status) {
        PedidoResponseDTO pedidoAtualizado = pedidoService.atualizarStatusPedido(id, status);
        return ResponseEntity.ok(pedidoAtualizado);
    }

    /**
     * DELETE /api/pedidos/{id} - Cancelar pedido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> cancelarPedido(@PathVariable Long id) {
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
    @PostMapping("/calcular")
    public ResponseEntity<BigDecimal> calcularTotalPedido(@Valid @RequestBody List<ItemPedidoDTO> itens) {
        BigDecimal total = pedidoService.calcularTotalPedido(itens);
        return ResponseEntity.ok(total);
    }
}