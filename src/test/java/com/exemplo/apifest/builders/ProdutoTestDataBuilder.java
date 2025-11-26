package com.exemplo.apifest.builders;

import com.exemplo.apifest.model.Produto;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Test Data Builder para Produto - Roteiro 9.
 * 
 * Este builder facilita a criação de objetos Produto para testes,
 * fornecendo dados realistas baseados na entidade real.
 * 
 * ENTIDADE REAL CAMPOS:
 * - Long id
 * - String nome
 * - String descricao
 * - BigDecimal preco
 * - String categoria
 * - Boolean disponivel
 * - Boolean ativo
 * 
 * @author DeliveryTech Team
 * @version 2.0 - Correção Roteiro 9
 */
public class ProdutoTestDataBuilder {

    private static final Faker faker = new Faker(Locale.of("pt", "BR"));

    private String nome = faker.food().dish();
    private String descricao = faker.lorem().sentence(10);
    private BigDecimal preco = new BigDecimal("25.90");
    private String categoria = "Pizza"; // String, não enum
    private Boolean disponivel = true;
    private Boolean ativo = true;

    /**
     * Cria um builder com dados padrão válidos.
     */
    public static ProdutoTestDataBuilder umProduto() {
        return new ProdutoTestDataBuilder();
    }

    /**
     * Cria um builder com dados específicos para produto válido.
     */
    public static ProdutoTestDataBuilder umProdutoValido() {
        return new ProdutoTestDataBuilder()
                .comNome("Pizza Margherita")
                .comDescricao("Deliciosa pizza com molho de tomate, mozzarella e manjericão")
                .comPreco(new BigDecimal("32.90"))
                .comCategoria("Pizza")
                .disponivel()
                .ativo();
    }

    /**
     * Cria um builder para produto indisponível.
     */
    public static ProdutoTestDataBuilder umProdutoIndisponivel() {
        return new ProdutoTestDataBuilder()
                .comNome("Produto Esgotado")
                .comCategoria("Bebida")
                .comPreco(new BigDecimal("8.90"))
                .indisponivel();
    }

    /**
     * Cria um builder para produto inativo.
     */
    public static ProdutoTestDataBuilder umProdutoInativo() {
        return new ProdutoTestDataBuilder()
                .comNome("Produto Descontinuado")
                .comCategoria("Sobremesa")
                .inativo();
    }

    /**
     * Cria um builder para bebida.
     */
    public static ProdutoTestDataBuilder umaBebida() {
        return new ProdutoTestDataBuilder()
                .comNome("Coca-Cola 350ml")
                .comDescricao("Refrigerante gelado")
                .comCategoria("Bebida")
                .comPreco(new BigDecimal("5.50"));
    }

    // ========== MÉTODOS DE CONFIGURAÇÃO ==========

    public ProdutoTestDataBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public ProdutoTestDataBuilder comDescricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public ProdutoTestDataBuilder comPreco(BigDecimal preco) {
        this.preco = preco;
        return this;
    }

    public ProdutoTestDataBuilder comCategoria(String categoria) {
        this.categoria = categoria;
        return this;
    }

    public ProdutoTestDataBuilder disponivel() {
        this.disponivel = true;
        return this;
    }

    public ProdutoTestDataBuilder indisponivel() {
        this.disponivel = false;
        return this;
    }

    public ProdutoTestDataBuilder ativo() {
        this.ativo = true;
        return this;
    }

    public ProdutoTestDataBuilder inativo() {
        this.ativo = false;
        return this;
    }

    // ========== MÉTODOS DE CONSTRUÇÃO ==========

    /**
     * Constrói o objeto Produto com os dados configurados.
     */
    public Produto build() {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setPreco(preco);
        produto.setCategoria(categoria);
        produto.setDisponivel(disponivel);
        produto.setAtivo(ativo);
        return produto;
    }

    /**
     * Constrói e retorna o objeto Produto (alias para build()).
     */
    public Produto agora() {
        return build();
    }
    
    // ========== MÉTODOS DE COMPATIBILIDADE ==========
    
    /**
     * Configura produto com restaurante específico.
     */
    public ProdutoTestDataBuilder comRestaurante(Object restaurante) {
        // Para compatibilidade com testes legados
        return this;
    }
    
    /**
     * Configura quantidade de estoque.
     */
    public ProdutoTestDataBuilder comEstoque(int quantidade) {
        // Para compatibilidade com testes legados
        return this;
    }
    
    /**
     * Cria produto esgotado.
     */
    public static ProdutoTestDataBuilder umProdutoEsgotado() {
        return new ProdutoTestDataBuilder()
                .comNome("Produto Esgotado")
                .indisponivel();
    }
    
    /**
     * Constrói produto com ID específico.
     */
    public Produto buildComId(long id) {
        Produto produto = build();
        produto.setId(id);
        return produto;
    }
}