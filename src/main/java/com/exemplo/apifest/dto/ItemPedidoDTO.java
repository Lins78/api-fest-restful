package com.exemplo.apifest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar um item de pedido
 * 
 * Este DTO é usado tanto para criação de pedidos quanto para cálculos de total.
 * Contém as validações necessárias para garantir que os itens sejam válidos.
 * 
 * Validações implementadas:
 * - Produto ID: deve referenciar produto existente e disponível
 * - Quantidade: deve ser maior que zero
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 4 - Camada de Serviços e Controllers REST
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDTO {

    /**
     * ID do produto a ser adicionado ao pedido
     * Deve referenciar um produto existente e disponível
     */
    @NotNull(message = "Produto ID é obrigatório")
    private Long produtoId;

    /**
     * Quantidade do produto no pedido
     * Deve ser maior que zero
     */
    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    private Integer quantidade;
}