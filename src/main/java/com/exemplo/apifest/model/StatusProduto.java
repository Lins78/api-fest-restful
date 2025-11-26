package com.exemplo.apifest.model;

/**
 * Enum para Status do Produto
 * 
 * Enum que representa os possíveis status de um produto no sistema de delivery DeliveryTech.
 * Este enum é utilizado para controlar a disponibilidade e estado dos produtos.
 * 
 * @author DeliveryTech Development Team
 * @version 1.0
 * @since Implementação de Produtos
 */
public enum StatusProduto {
    
    /** Status quando o produto está disponível para pedidos */
    DISPONIVEL("Disponível"),
    
    /** Status quando o produto está temporariamente indisponível */
    INDISPONIVEL("Indisponível"),
    
    /** Status quando o produto está esgotado (sem estoque) */
    ESGOTADO("Esgotado"),
    
    /** Status quando o produto está inativo/descontinuado */
    INATIVO("Inativo");

    private final String descricao;

    /**
     * Construtor do enum com a descrição legível do status
     * 
     * @param descricao Descrição amigável do status para exibição
     */
    StatusProduto(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Retorna a descrição legível do status
     * 
     * @return String com a descrição do status
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Verifica se o produto está disponível para pedidos
     * 
     * @return true se o status permitir pedidos
     */
    public boolean isDisponivel() {
        return this == DISPONIVEL;
    }

    /**
     * Verifica se o produto precisa de reabastecimento
     * 
     * @return true se o produto está esgotado
     */
    public boolean isEsgotado() {
        return this == ESGOTADO;
    }
}