package com.exemplo.apifest.dto.response;

import com.exemplo.apifest.model.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta completo para Pedido
 * 
 * Este DTO contém todas as informações de um pedido incluindo:
 * - Dados do cliente e restaurante
 * - Lista completa de itens
 * - Valores calculados e status atual
 * 
 * Usado para retornar pedidos completos em consultas individuais.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 4 - Camada de Serviços e Controllers REST
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {

    /**
     * Identificador único do pedido
     */
    private Long id;

    /**
     * Data e hora da criação do pedido
     */
    private LocalDateTime dataPedido;

    /**
     * Status atual do pedido
     */
    private StatusPedido status;

    /**
     * Valor total do pedido (itens + taxa de entrega)
     */
    private BigDecimal valor;

    /**
     * Endereço de entrega específico deste pedido
     */
    private String enderecoEntrega;

    /**
     * Observações adicionais do pedido
     */
    private String observacoes;

    /**
     * Dados do cliente que fez o pedido
     */
    private ClienteResponseDTO cliente;

    /**
     * Dados do restaurante do pedido
     */
    private RestauranteResponseDTO restaurante;

    /**
     * Lista de itens do pedido com detalhes completos
     */
    private List<ItemPedidoResponseDTO> itens;
}