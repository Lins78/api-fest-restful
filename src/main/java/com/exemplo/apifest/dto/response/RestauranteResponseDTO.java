package com.exemplo.apifest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de resposta para Restaurante
 * 
 * Este DTO contém todos os dados do restaurante que podem ser expostos
 * via API para clientes e parceiros.
 * 
 * Inclui informações completas como localização, categoria e taxa de entrega
 * para facilitar a escolha do cliente.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 4 - Camada de Serviços e Controllers REST
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteResponseDTO {

    /**
     * Identificador único do restaurante
     */
    private Long id;

    /**
     * Nome do restaurante
     */
    private String nome;

    /**
     * Categoria do restaurante
     * Ex: Pizzaria, Hambúrguer, Japonês, etc.
     */
    private String categoria;

    /**
     * Endereço completo do restaurante
     */
    private String endereco;

    /**
     * Taxa de entrega base do restaurante
     */
    private BigDecimal taxaEntrega;

    /**
     * Indica se o restaurante está ativo e aceitando pedidos
     */
    private Boolean ativo;
}