package com.exemplo.apifest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para criação e atualização de Produto
 * 
 * Este DTO contém as validações necessárias para garantir que os dados
 * do produto estejam corretos antes de serem processados.
 * 
 * Validações implementadas:
 * - Nome: obrigatório, mínimo 2 caracteres, máximo 100
 * - Descrição: obrigatória para detalhes do produto
 * - Preço: deve ser valor positivo maior que zero
 * - Categoria: obrigatória para organização
 * - Restaurante ID: deve referenciar restaurante válido
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 4 - Camada de Serviços e Controllers REST
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDTO {

    /**
     * Nome do produto
     * Deve ser descritivo e único por restaurante
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    /**
     * Descrição detalhada do produto
     * Ingredientes, características especiais, etc.
     */
    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 5, max = 500, message = "Descrição deve ter entre 5 e 500 caracteres")
    private String descricao;

    /**
     * Preço do produto
     * Deve ser maior que zero
     */
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    private BigDecimal preco;

    /**
     * Categoria do produto
     * Exemplos: Pizza, Hambúrguer, Bebida, Sobremesa, etc.
     */
    @NotBlank(message = "Categoria é obrigatória")
    @Size(min = 3, max = 50, message = "Categoria deve ter entre 3 e 50 caracteres")
    private String categoria;

    /**
     * ID do restaurante ao qual o produto pertence
     * Deve referenciar um restaurante existente e ativo
     */
    @NotNull(message = "Restaurante ID é obrigatório")
    private Long restauranteId;
}