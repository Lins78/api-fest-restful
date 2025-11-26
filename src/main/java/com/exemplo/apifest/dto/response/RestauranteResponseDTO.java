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
     * Email do restaurante
     */
    private String email;

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

    /**
     * Status do restaurante
     */
    private String status;
    
    // Getters e Setters manuais devido ao problema do Lombok
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    
    public java.math.BigDecimal getTaxaEntrega() { return taxaEntrega; }
    public void setTaxaEntrega(java.math.BigDecimal taxaEntrega) { this.taxaEntrega = taxaEntrega; }
    
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}