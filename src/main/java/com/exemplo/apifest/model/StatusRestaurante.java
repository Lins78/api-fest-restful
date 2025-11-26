package com.exemplo.apifest.model;

/**
 * Enum para Status do Restaurante
 * 
 * Define os possíveis estados de um restaurante no sistema:
 * - ATIVO: Restaurante operando normalmente, aceitando pedidos
 * - INATIVO: Restaurante temporariamente indisponível
 * - BLOQUEADO: Restaurante suspenso por violação de políticas
 * - PENDENTE: Restaurante aguardando aprovação ou verificação
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Sistema de Gestão de Restaurantes
 */
public enum StatusRestaurante {
    
    /**
     * Restaurante ativo e operando normalmente
     * Aceita pedidos e está visível para clientes
     */
    ATIVO("Ativo"),
    
    /**
     * Restaurante inativo temporariamente
     * Não aceita pedidos mas pode ser reativado
     */
    INATIVO("Inativo"),
    
    /**
     * Restaurante bloqueado por violação
     * Requer intervenção administrativa
     */
    BLOQUEADO("Bloqueado"),
    
    /**
     * Restaurante aguardando aprovação
     * Ainda não foi liberado para operação
     */
    PENDENTE("Pendente");
    
    private final String descricao;
    
    StatusRestaurante(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    @Override
    public String toString() {
        return descricao;
    }
}