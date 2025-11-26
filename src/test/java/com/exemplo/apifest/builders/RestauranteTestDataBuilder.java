package com.exemplo.apifest.builders;

import com.exemplo.apifest.model.Restaurante;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Test Data Builder para Restaurante - Roteiro 9.
 * 
 * Este builder facilita a criação de objetos Restaurante para testes,
 * fornecendo dados realistas baseados na entidade real.
 * 
 * ENTIDADE REAL CAMPOS:
 * - Long id
 * - String nome  
 * - String endereco
 * - String telefone
 * - String categoria
 * - BigDecimal taxaEntrega
 * - Boolean ativo
 * 
 * @author DeliveryTech Team
 * @version 2.0 - Correção Roteiro 9
 */
public class RestauranteTestDataBuilder {

    private static final Faker faker = new Faker(Locale.of("pt", "BR"));

    private String nome = faker.company().name() + " Restaurante";
    private String endereco = faker.address().streetAddress() + ", " + faker.address().buildingNumber();
    private String telefone = faker.phoneNumber().phoneNumber();
    private String categoria = "Italiana"; // String, não enum
    private BigDecimal taxaEntrega = new BigDecimal("5.00");
    private Boolean ativo = true;

    /**
     * Cria um builder com dados padrão válidos.
     */
    public static RestauranteTestDataBuilder umRestaurante() {
        return new RestauranteTestDataBuilder();
    }

    /**
     * Cria um builder com dados específicos para restaurante válido.
     */
    public static RestauranteTestDataBuilder umRestauranteValido() {
        return new RestauranteTestDataBuilder()
                .comNome("Pizzaria Bella Vista")
                .comEndereco("Av. Paulista, 1000")
                .comTelefone("11987654321")
                .comCategoria("Italiana")
                .comTaxaEntrega(new BigDecimal("8.00"))
                .ativo();
    }

    /**
     * Cria um builder para restaurante inativo.
     */
    public static RestauranteTestDataBuilder umRestauranteInativo() {
        return new RestauranteTestDataBuilder()
                .comNome("Restaurante Fechado")
                .comCategoria("Brasileira")
                .inativo();
    }

    /**
     * Cria um builder para restaurante japonês.
     */
    public static RestauranteTestDataBuilder umRestauranteJapones() {
        return new RestauranteTestDataBuilder()
                .comNome("Sushi Yamato")
                .comCategoria("Japonesa")
                .comTaxaEntrega(new BigDecimal("12.00"));
    }

    // ========== MÉTODOS DE CONFIGURAÇÃO ==========

    public RestauranteTestDataBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public RestauranteTestDataBuilder comEndereco(String endereco) {
        this.endereco = endereco;
        return this;
    }

    public RestauranteTestDataBuilder comTelefone(String telefone) {
        this.telefone = telefone;
        return this;
    }

    public RestauranteTestDataBuilder comCategoria(String categoria) {
        this.categoria = categoria;
        return this;
    }

    public RestauranteTestDataBuilder comTaxaEntrega(BigDecimal taxaEntrega) {
        this.taxaEntrega = taxaEntrega;
        return this;
    }

    public RestauranteTestDataBuilder ativo() {
        this.ativo = true;
        return this;
    }

    public RestauranteTestDataBuilder inativo() {
        this.ativo = false;
        return this;
    }

    // ========== MÉTODOS DE CONSTRUÇÃO ==========

    /**
     * Constrói o objeto Restaurante com os dados configurados.
     */
    public Restaurante build() {
        Restaurante restaurante = new Restaurante(nome, endereco, telefone, categoria, taxaEntrega);
        restaurante.setAtivo(ativo);
        return restaurante;
    }

    /**
     * Constrói e retorna o objeto Restaurante (alias para build()).
     */
    public Restaurante agora() {
        return build();
    }

    /**
     * Constrói o objeto Restaurante com ID específico.
     */
    public Restaurante buildComId(Long id) {
        Restaurante restaurante = build();
        restaurante.setId(id);
        return restaurante;
    }
    
    // ========== MÉTODOS DE COMPATIBILIDADE ==========
    
    /**
     * Configura horários de funcionamento do restaurante.
     */
    public RestauranteTestDataBuilder comHorarios(Object abertura, Object fechamento) {
        // Para compatibilidade com testes legados
        return this;
    }
}