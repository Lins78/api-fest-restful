package com.exemplo.apifest.dto.response;

import com.exemplo.apifest.model.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de resposta resumido para Pedido
 * 
 * Este DTO contém apenas as informações essenciais de um pedido,
 * usado para listagens onde não é necessário carregar todos os detalhes.
 * 
 * Otimizado para performance em consultas que retornam muitos pedidos.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 4 - Camada de Serviços e Controllers REST
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResumoDTO {

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
     * Valor total do pedido
     */
    private BigDecimal valor;

    /**
     * Nome do cliente (para identificação rápida)
     */
    private String clienteNome;

    /**
     * Nome do restaurante (para identificação rápida)
     */
    private String restauranteNome;

    /**
     * Quantidade de itens no pedido
     */
    private Integer quantidadeItens;
}