package com.exemplo.apifest.dto;

import com.exemplo.apifest.validation.ValidCategoria;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para criação e atualização de Produto
 * 
 * Validações robustas implementadas conforme Roteiro 6:
 * - Nome: obrigatório, entre 2 e 50 caracteres
 * - Preço: obrigatório, maior que zero, máximo R$ 500,00
 * - Categoria: obrigatória, valores pré-definidos
 * - Descrição: mínimo 10 caracteres para qualidade
 * - Restaurante ID: referência obrigatória
 * 
 * @author DeliveryTech Team
 * @version 2.0
 * @since Roteiro 6 - Sistema Robusto de Validações
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
    @Size(min = 2, max = 50, message = "Nome deve ter entre 2 e 50 caracteres")
    private String nome;

    /**
     * Preço do produto
     * Deve ser maior que zero e não exceder R$ 500,00
     */
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @DecimalMax(value = "500.00", message = "Preço não pode exceder R$ 500,00")
    private BigDecimal preco;

    /**
     * Categoria do produto
     * Valores permitidos: PRATO_PRINCIPAL, ENTRADA, BEBIDA, SOBREMESA, etc.
     */
    @NotBlank(message = "Categoria é obrigatória")
    @ValidCategoria(message = "Categoria deve ser uma das opções válidas: PRATO_PRINCIPAL, ENTRADA, BEBIDA, SOBREMESA, LANCHE, PIZZA, HAMBURGUER, SALADA, MASSA, CARNE, PEIXE, VEGETARIANO")
    private String categoria;

    /**
     * Descrição detalhada do produto
     * Ingredientes, características especiais, etc. (mínimo 10 caracteres)
     */
    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
    private String descricao;

    /**
     * ID do restaurante ao qual o produto pertence
     * Deve referenciar um restaurante existente e ativo
     */
    @NotNull(message = "Restaurante ID é obrigatório")
    private Long restauranteId;
}