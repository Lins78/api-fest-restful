package com.exemplo.apifest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de resposta para Item de Pedido
 * 
 * Este DTO representa um item individual dentro de um pedido,
 * incluindo dados do produto e valores calculados.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 4 - Camada de Serviços e Controllers REST
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoResponseDTO {

    /**
     * Identificador único do item do pedido
     */
    private Long id;

    /**
     * Quantidade do produto no pedido
     */
    private Integer quantidade;

    /**
     * Preço unitário do produto no momento do pedido
     */
    private BigDecimal precoUnitario;

    /**
     * Preço total do item (quantidade × preço unitário)
     */
    private BigDecimal precoTotal;

    /**
     * Dados completos do produto
     */
    private ProdutoResponseDTO produto;
}