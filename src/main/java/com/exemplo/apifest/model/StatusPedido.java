package com.exemplo.apifest.model;

/**
 * ROTEIRO 3 - ENUM STATUS PEDIDO
 * 
 * Enum que representa os possíveis status de um pedido no sistema de delivery DeliveryTech.
 * Este enum é utilizado para controlar o fluxo de status dos pedidos desde a criação até a entrega.
 * 
 * @author DeliveryTech Development Team
 * @version 1.0
 * @since Roteiro 3 - Implementação da Camada de Dados
 */
public enum StatusPedido {
    
    /** Status inicial quando o pedido é criado pelo cliente */
    PENDENTE("Pendente"),
    
    /** Status quando o restaurante confirma e aceita o pedido */
    CONFIRMADO("Confirmado"),
    
    /** Status quando o restaurante está preparando os itens do pedido */
    PREPARANDO("Preparando"),
    
    /** Status quando o pedido saiu do restaurante para entrega */
    SAIU_PARA_ENTREGA("Saiu para Entrega"),
    
    /** Status final quando o pedido foi entregue ao cliente */
    ENTREGUE("Entregue"),
    
    /** Status quando o pedido foi cancelado por qualquer motivo */
    CANCELADO("Cancelado");

    private final String descricao;

    /**
     * Construtor do enum com a descrição legível do status
     * 
     * @param descricao Descrição amigável do status para exibição
     */
    StatusPedido(String descricao) {
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
     * Retorna a descrição do status quando convertido para String
     * 
     * @return String representando o status
     */
    @Override
    public String toString() {
        return this.descricao;
    }
}