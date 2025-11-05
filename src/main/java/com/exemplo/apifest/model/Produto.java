package com.exemplo.apifest.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * ROTEIRO 3 - ENTIDADE PRODUTO
 * 
 * Entidade JPA que representa um produto disponível para pedidos no sistema DeliveryTech.
 * Esta classe mapeia para a tabela 'produtos' no banco de dados e contém
 * informações sobre itens oferecidos pelos restaurantes parceiros.
 * 
 * Funcionalidades do Roteiro 3:
 * - Adicionado campo 'categoria' para classificar tipos de produto
 * - Adicionado campo 'disponivel' para controle de disponibilidade
 * - Relacionamento com restaurante através de chave estrangeira
 * - Suporte a consultas por categoria e disponibilidade
 * 
 * Relacionamentos:
 * - Muitos produtos pertencem a um restaurante (ManyToOne com Restaurante)
 * - Um produto pode estar em vários itens de pedido (OneToMany com ItemPedido)
 * 
 * @author DeliveryTech Development Team
 * @version 1.0
 * @since Roteiro 3 - Implementação da Camada de Dados
 */
@Entity
@Table(name = "produtos")
public class Produto {
    /** Identificador único do produto (chave primária) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** Nome do produto */
    private String nome;
    
    /** Descrição detalhada do produto */
    private String descricao;
    
    /** Preço do produto em reais */
    private BigDecimal preco;
    
    /** Categoria do produto (Ex: Pizza, Hambúrguer, Bebida, etc.) - ROTEIRO 3 */
    private String categoria;
    
    /** Flag indicando se o produto está disponível para pedidos - ROTEIRO 3 */
    private Boolean disponivel;
    
    /** Flag indicando se o produto está ativo no sistema */
    private Boolean ativo;

    /** Relacionamento com restaurante que oferece este produto - ROTEIRO 3 */
    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    // Constructors
    
    /**
     * Construtor padrão necessário para o JPA
     */
    public Produto() {}
    
    /**
     * Construtor básico para criação de produto
     * Define automaticamente o produto como ativo e disponível
     * 
     * @param nome Nome do produto
     * @param descricao Descrição detalhada do produto
     * @param preco Preço do produto
     */
    public Produto(String nome, String descricao, BigDecimal preco) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.ativo = true;
        this.disponivel = true;
    }

    /**
     * Construtor completo para criação de produto com categoria e restaurante - ROTEIRO 3
     * Define automaticamente o produto como ativo e disponível
     * 
     * @param nome Nome do produto
     * @param descricao Descrição detalhada do produto
     * @param preco Preço do produto
     * @param categoria Categoria do produto (conforme roteiro)
     * @param restaurante Restaurante que oferece este produto
     */
    public Produto(String nome, String descricao, BigDecimal preco, String categoria, Restaurante restaurante) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.categoria = categoria;
        this.restaurante = restaurante;
        this.ativo = true;
        this.disponivel = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Boolean getDisponivel() { return disponivel; }
    public void setDisponivel(Boolean disponivel) { this.disponivel = disponivel; }

    public Restaurante getRestaurante() { return restaurante; }
    public void setRestaurante(Restaurante restaurante) { this.restaurante = restaurante; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    // Business methods
    
    /**
     * Inativa o produto no sistema
     * Produto inativo não aparece nas consultas de produtos ativos
     */
    public void inativar() {
        this.ativo = false;
    }

    /**
     * Marca o produto como indisponível temporariamente
     * Produto indisponível não pode ser incluído em novos pedidos
     */
    public void indisponibilizar() {
        this.disponivel = false;
    }
}