package com.exemplo.apifest.model;

/**
 * Enum que define os perfis de usuário no sistema
 * 
 * CLIENTE: Usuários que fazem pedidos
 * RESTAURANTE: Donos de restaurantes que gerenciam produtos
 * ADMIN: Administradores com acesso total
 * ENTREGADOR: Responsáveis pela entrega dos pedidos
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 7 - Sistema de Autenticação JWT
 */
public enum Role {
    CLIENTE("Cliente"),
    RESTAURANTE("Restaurante"), 
    ADMIN("Administrador"),
    ENTREGADOR("Entregador");
    
    private final String descricao;
    
    Role(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    /**
     * Retorna o nome da role com prefixo ROLE_ para Spring Security
     */
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}