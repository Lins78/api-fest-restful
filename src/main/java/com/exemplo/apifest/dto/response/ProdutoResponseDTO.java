package com.exemplo.apifest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de resposta para Produto
 * 
 * Este DTO contém todos os dados do produto incluindo informações de
 * disponibilidade e do restaurante ao qual pertence.
 * 
 * Usado para exibir produtos em listagens e detalhes individuais.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 4 - Camada de Serviços e Controllers REST
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoResponseDTO {

    /**
     * Identificador único do produto
     */
    private Long id;

    /**
     * Nome do produto
     */
    private String nome;

    /**
     * Descrição detalhada do produto
     */
    private String descricao;

    /**
     * Preço atual do produto
     */
    private BigDecimal preco;

    /**
     * Categoria do produto
     */
    private String categoria;

    /**
     * Indica se o produto está disponível para pedidos
     */
    private Boolean disponivel;

    /**
     * ID do restaurante ao qual o produto pertence
     */
    private Long restauranteId;

    /**
     * Nome do restaurante (para facilitar exibição)
     */
    private String restauranteNome;
}