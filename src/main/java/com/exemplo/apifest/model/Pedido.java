package com.exemplo.apifest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ROTEIRO 3 - ENTIDADE PEDIDO
 * 
 * Entidade JPA que representa um pedido no sistema de delivery DeliveryTech.
 * Esta classe mapeia para a tabela 'pedidos' no banco de dados e gerencia
 * o ciclo completo de um pedido desde a criação até a entrega.
 * 
 * Funcionalidades do Roteiro 3:
 * - Utiliza enum StatusPedido para controle de fluxo
 * - Relacionamento com Cliente através de chave estrangeira
 * - Suporte a consultas por data e status
 * - Controle de ativação/inativação de pedidos
 * 
 * Relacionamentos:
 * - Muitos pedidos pertencem a um cliente (ManyToOne com Cliente)
 * - Um pedido pode ter vários itens (OneToMany com ItemPedido)
 * 
 * @author DeliveryTech Development Team
 * @version 1.0
 * @since Roteiro 3 - Implementação da Camada de Dados
 */
@Entity
@Table(name = "pedidos")
public class Pedido {
    /** Identificador único do pedido (chave primária) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** Descrição geral do pedido */
    private String descricao;
    
    /** Valor total do pedido em reais */
    private Double valor;
    
    /** Data e hora em que o pedido foi realizado */
    private LocalDateTime dataPedido;

    /** Cliente que realizou o pedido - ROTEIRO 3 */
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    /** Status atual do pedido usando enum StatusPedido - ROTEIRO 3 */
    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    /** Lista de itens do pedido - RELACIONAMENTO ONETOMANY */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemPedido> itens = new ArrayList<>();

    /** Flag indicando se o pedido está ativo no sistema */
    private Boolean ativo;

    // Constructors
    
    /**
     * Construtor padrão necessário para o JPA
     */
    public Pedido() {}
    
    /**
     * Construtor para criação de novo pedido
     * Define automaticamente a data como agora, status como PENDENTE e ativo como true
     * 
     * @param descricao Descrição geral do pedido
     * @param valor Valor total do pedido
     * @param cliente Cliente que está realizando o pedido
     */
    public Pedido(String descricao, Double valor, Cliente cliente) {
        this.descricao = descricao;
        this.valor = valor;
        this.cliente = cliente;
        this.dataPedido = LocalDateTime.now();
        this.status = StatusPedido.PENDENTE;
        this.ativo = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public LocalDateTime getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDateTime dataPedido) { this.dataPedido = dataPedido; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }

    // Business methods
    
    /**
     * Inativa o pedido no sistema
     * Pedido inativo não aparece nas consultas padrão
     */
    public void inativar() {
        this.ativo = false;
    }

    /**
     * Confirma o pedido, mudando status de PENDENTE para CONFIRMADO
     */
    public void confirmar() {
        this.status = StatusPedido.CONFIRMADO;
    }

    /**
     * Cancela o pedido, mudando status para CANCELADO
     */
    public void cancelar() {
        this.status = StatusPedido.CANCELADO;
    }

    /**
     * Marca o pedido como entregue, mudando status para ENTREGUE
     */
    public void entregar() {
        this.status = StatusPedido.ENTREGUE;
    }
}