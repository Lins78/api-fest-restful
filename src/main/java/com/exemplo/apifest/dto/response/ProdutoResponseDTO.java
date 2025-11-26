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
    
    // ========== MÉTODOS DE COMPATIBILIDADE ==========
    
    /**
     * Campos de compatibilidade para testes legados.
     */
    private String status = "ATIVO";
    private Integer quantidadeEstoque = 100;
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getQuantidadeEstoque() {
        return quantidadeEstoque;
    }
    
    public void setQuantidadeEstoque(Integer quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }
    
    // Getters e Setters manuais devido ao problema do Lombok
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public java.math.BigDecimal getPreco() { return preco; }
    public void setPreco(java.math.BigDecimal preco) { this.preco = preco; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public Boolean getDisponivel() { return disponivel; }
    public void setDisponivel(Boolean disponivel) { this.disponivel = disponivel; }
    
    public Long getRestauranteId() { return restauranteId; }
    public void setRestauranteId(Long restauranteId) { this.restauranteId = restauranteId; }
    
    public String getRestauranteNome() { return restauranteNome; }
    public void setRestauranteNome(String restauranteNome) { this.restauranteNome = restauranteNome; }
}