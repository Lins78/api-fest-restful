package com.exemplo.apifest.dto;

import com.exemplo.apifest.validation.ValidCategoria;
import com.exemplo.apifest.validation.ValidTelefone;
import com.exemplo.apifest.validation.ValidHorarioFuncionamento;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO para criação e atualização de Restaurante
 * 
 * Este DTO contém validações robustas seguindo os requisitos do Roteiro 6:
 * - Nome: obrigatório, entre 2 e 100 caracteres
 * - Categoria: obrigatória, valores permitidos definidos
 * - Telefone: formato brasileiro válido (10-11 dígitos)
 * - Endereço: obrigatório para localização
 * - Taxa de entrega: valor positivo, máximo R$ 50,00
 * - Tempo de entrega: entre 10 e 120 minutos
 * - Horário de funcionamento: formato HH:MM-HH:MM
 * 
 * @author DeliveryTech Team
 * @version 2.0
 * @since Roteiro 6 - Sistema Robusto de Validações
 */
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
     * Valores permitidos: PIZZA, HAMBURGUER, JAPONESA, ITALIANA, BRASILEIRA, etc.
     */
    @NotBlank(message = "Categoria é obrigatória")
    @ValidCategoria(message = "Categoria deve ser uma das opções válidas: PIZZA, HAMBURGUER, JAPONESA, ITALIANA, BRASILEIRA, MEXICANA, CHINESA, VEGETARIANA, DOCES, LANCHES")
    private String categoria;

    /**
     * Telefone do restaurante
     * Deve seguir formato brasileiro: (11) 99999-9999 ou 11999999999
     */
    @NotBlank(message = "Telefone é obrigatório")
    @ValidTelefone(message = "Telefone deve estar no formato brasileiro válido")
    private String telefone;

    /**
     * Endereço completo do restaurante
     * Usado para cálculo de distância e taxa de entrega
     */
    @NotBlank(message = "Endereço é obrigatório")
    @Size(min = 10, max = 200, message = "Endereço deve ter entre 10 e 200 caracteres")
    private String endereco;

    /**
     * Taxa de entrega base do restaurante
     * Valor entre R$ 0,00 e R$ 50,00
     */
    @NotNull(message = "Taxa de entrega é obrigatória")
    @DecimalMin(value = "0.0", inclusive = true, message = "Taxa de entrega deve ser maior ou igual a zero")
    @DecimalMax(value = "50.0", inclusive = true, message = "Taxa de entrega não pode exceder R$ 50,00")
    private BigDecimal taxaEntrega;

    /**
     * Tempo médio de entrega em minutos
     * Deve estar entre 10 e 120 minutos
     */
    @NotNull(message = "Tempo de entrega é obrigatório")
    @Min(value = 10, message = "Tempo de entrega deve ser no mínimo 10 minutos")
    @Max(value = 120, message = "Tempo de entrega não pode exceder 120 minutos")
    private Integer tempoEntregaMinutos;

    /**
     * Horário de funcionamento
     * Formato: HH:MM-HH:MM (ex: 08:00-22:00)
     */
    @NotBlank(message = "Horário de funcionamento é obrigatório")
    @ValidHorarioFuncionamento(message = "Horário deve estar no formato HH:MM-HH:MM")
    private String horarioFuncionamento;

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public BigDecimal getTaxaEntrega() {
        return taxaEntrega;
    }

    public void setTaxaEntrega(BigDecimal taxaEntrega) {
        this.taxaEntrega = taxaEntrega;
    }

    public Integer getTempoEntregaMinutos() {
        return tempoEntregaMinutos;
    }

    public void setTempoEntregaMinutos(Integer tempoEntregaMinutos) {
        this.tempoEntregaMinutos = tempoEntregaMinutos;
    }

    public String getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(String horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }
}