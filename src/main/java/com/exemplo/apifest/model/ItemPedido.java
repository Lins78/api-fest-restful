package com.exemplo.apifest.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * ROTEIRO 3 - ENTIDADE ITEM PEDIDO
 * 
 * Entidade JPA que representa um item específico dentro de um pedido no sistema DeliveryTech.
 * Esta classe mapeia para a tabela 'itens_pedido' no banco de dados e controla
 * a relação entre pedidos e produtos, incluindo quantidade e preços.
 * 
 * Funcionalidades do Roteiro 3:
 * - Relacionamento Many-to-One com Pedido e Produto
 * - Cálculo automático do preço total baseado em quantidade e preço unitário
 * - Suporte a observações específicas do item
 * - Controle de preços no momento do pedido (preserva preço histórico)
 * 
 * Relacionamentos:
 * - Muitos itens pertencem a um pedido (ManyToOne com Pedido)
 * - Muitos itens referenciam um produto (ManyToOne com Produto)
 * 
 * @author DeliveryTech Development Team
 * @version 1.0
 * @since Roteiro 3 - Implementação da Camada de Dados
 */
@Entity
@Table(name = "itens_pedido")
public class ItemPedido {
    /** Identificador único do item de pedido (chave primária) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Pedido ao qual este item pertence - ROTEIRO 3 */
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    /** Produto referenciado neste item - ROTEIRO 3 */
    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    /** Quantidade do produto neste item */
    private Integer quantidade;
    
    /** Preço unitário do produto no momento do pedido (preserva histórico) */
    private BigDecimal precoUnitario;
    
    /** Preço total do item (quantidade × preço unitário) */
    private BigDecimal precoTotal;
    
    /** Observações específicas para este item (ex: sem cebola, ponto da carne) */
    private String observacoes;

    // Constructors
    
    /**
     * Construtor padrão necessário para o JPA
     */
    public ItemPedido() {}

    /**
     * Construtor para criação de item de pedido
     * Calcula automaticamente o preço total baseado na quantidade e preço do produto
     * 
     * @param pedido Pedido ao qual este item será adicionado
     * @param produto Produto referenciado neste item
     * @param quantidade Quantidade do produto
     */
    public ItemPedido(Pedido pedido, Produto produto, Integer quantidade) {
        this.pedido = pedido;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = produto.getPreco();
        this.precoTotal = this.precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    /**
     * Construtor completo para criação de item de pedido com observações
     * 
     * @param pedido Pedido ao qual este item será adicionado
     * @param produto Produto referenciado neste item  
     * @param quantidade Quantidade do produto
     * @param observacoes Observações específicas do item
     */
    public ItemPedido(Pedido pedido, Produto produto, Integer quantidade, String observacoes) {
        this(pedido, produto, quantidade);
        this.observacoes = observacoes;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { 
        this.quantidade = quantidade;
        calcularPrecoTotal();
    }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { 
        this.precoUnitario = precoUnitario;
        calcularPrecoTotal();
    }

    public BigDecimal getPrecoTotal() { return precoTotal; }
    public void setPrecoTotal(BigDecimal precoTotal) { this.precoTotal = precoTotal; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    // Business methods
    
    /**
     * Calcula automaticamente o preço total do item
     * Multiplica preço unitário pela quantidade quando ambos estão definidos
     */
    private void calcularPrecoTotal() {
        if (this.precoUnitario != null && this.quantidade != null) {
            this.precoTotal = this.precoUnitario.multiply(BigDecimal.valueOf(this.quantidade));
        }
    }

    /**
     * Método alias para getPrecoTotal() - usado em testes
     * Retorna o subtotal do item (quantidade * preço unitário)
     */
    public BigDecimal getSubTotal() {
        return getPrecoTotal();
    }
}