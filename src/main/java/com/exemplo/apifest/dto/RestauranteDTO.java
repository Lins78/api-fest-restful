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
 * DTO para criação e atualização de Restaurante
 * 
 * Este DTO contém as validações necessárias para garantir que os dados
 * do restaurante estejam corretos antes de serem processados.
 * 
 * Validações implementadas:
 * - Nome: obrigatório, mínimo 2 caracteres, máximo 100
 * - Categoria: obrigatória, predefinida no sistema
 * - Endereço: obrigatório para localização e cálculo de entrega
 * - Taxa de entrega: deve ser valor positivo ou zero
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 4 - Camada de Serviços e Controllers REST
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteDTO {

    /**
     * Nome do restaurante
     * Deve ser único e identificativo
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    /**
     * Categoria do restaurante
     * Exemplos: Pizzaria, Hambúrguer, Japonês, Italiano, etc.
     */
    @NotBlank(message = "Categoria é obrigatória")
    @Size(min = 3, max = 50, message = "Categoria deve ter entre 3 e 50 caracteres")
    private String categoria;

    /**
     * Endereço completo do restaurante
     * Usado para cálculo de distância e taxa de entrega
     */
    @NotBlank(message = "Endereço é obrigatório")
    @Size(min = 10, max = 200, message = "Endereço deve ter entre 10 e 200 caracteres")
    private String endereco;

    /**
     * Taxa de entrega base do restaurante
     * Valor mínimo R$ 0,00 (entrega gratuita permitida)
     */
    @NotNull(message = "Taxa de entrega é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true, message = "Taxa de entrega deve ser maior ou igual a zero")
    private BigDecimal taxaEntrega;
}