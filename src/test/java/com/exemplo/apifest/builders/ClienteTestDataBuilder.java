package com.exemplo.apifest.builders;

import com.exemplo.apifest.model.Cliente;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Test Data Builder para Cliente - Roteiro 9.
 * 
 * Este builder facilita a criação de objetos Cliente para testes,
 * fornecendo dados realistas e permitindo customização específica
 * para cada cenário de teste.
 * 
 * PADRÕES IMPLEMENTADOS:
 * - Builder Pattern para flexibilidade
 * - Faker para dados realistas
 * - Métodos específicos para cenários comuns
 * - Dados válidos por padrão
 * 
 * @author DeliveryTech Team
 * @version 1.0 - Roteiro 9
 */
public class ClienteTestDataBuilder {

    private static final Faker faker = new Faker(Locale.of("pt", "BR"));

    private String nome = faker.name().fullName();
    private String email = faker.internet().emailAddress();
    private String telefone = faker.phoneNumber().cellPhone();
    private String endereco = faker.address().streetAddress() + ", " + faker.address().buildingNumber();
    private LocalDateTime dataCadastro = LocalDateTime.now();
    private Boolean ativo = true;

    /**
     * Cria um builder com dados padrão válidos.
     */
    public static ClienteTestDataBuilder umCliente() {
        return new ClienteTestDataBuilder();
    }

    /**
     * Cria um builder com dados específicos para cliente válido.
     */
    public static ClienteTestDataBuilder umClienteValido() {
        return new ClienteTestDataBuilder()
                .comNome("João Silva")
                .comEmail("joao.silva@email.com")
                .comTelefone("11987654321")
                .comEndereco("Rua das Flores, 123")
                .ativo();
    }

    /**
     * Cria um builder para cliente inativo.
     */
    public static ClienteTestDataBuilder umClienteInativo() {
        return new ClienteTestDataBuilder()
                .comNome("Maria Inativa")
                .comEmail("maria.inativa@email.com")
                .inativo();
    }

    /**
     * Cria um builder para cliente com dados inválidos.
     */
    public static ClienteTestDataBuilder umClienteInvalido() {
        return new ClienteTestDataBuilder()
                .comNome("")  // Nome vazio - inválido
                .comEmail("email-invalido")  // Email inválido
                .comTelefone("123");  // Telefone inválido
    }

    /**
     * Cria um builder para novo cliente (sem ID).
     */
    public static ClienteTestDataBuilder umNovoCliente() {
        return new ClienteTestDataBuilder()
                .comNome("Novo Cliente")
                .comEmail("novo.cliente@email.com")
                .comDataCadastro(LocalDateTime.now());
    }

    // ========== MÉTODOS DE CONFIGURAÇÃO ==========

    public ClienteTestDataBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public ClienteTestDataBuilder comEmail(String email) {
        this.email = email;
        return this;
    }

    public ClienteTestDataBuilder comTelefone(String telefone) {
        this.telefone = telefone;
        return this;
    }

    public ClienteTestDataBuilder comEndereco(String endereco) {
        this.endereco = endereco;
        return this;
    }

    public ClienteTestDataBuilder comDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
        return this;
    }

    public ClienteTestDataBuilder ativo() {
        this.ativo = true;
        return this;
    }

    public ClienteTestDataBuilder inativo() {
        this.ativo = false;
        return this;
    }

    public ClienteTestDataBuilder comStatus(Boolean ativo) {
        this.ativo = ativo;
        return this;
    }

    // ========== MÉTODOS ESPECÍFICOS PARA CENÁRIOS ==========

    /**
     * Cliente com email já existente no sistema.
     */
    public ClienteTestDataBuilder comEmailExistente() {
        this.email = "cliente.existente@sistema.com";
        return this;
    }

    /**
     * Cliente com dados de São Paulo.
     */
    public ClienteTestDataBuilder deSaoPaulo() {
        this.telefone = "11" + faker.number().digits(9);
        this.endereco = "Av. Paulista, " + faker.number().numberBetween(1, 3000);
        return this;
    }

    /**
     * Cliente cadastrado há muito tempo.
     */
    public ClienteTestDataBuilder cadastradoHaMeses() {
        this.dataCadastro = LocalDateTime.now().minusMonths(faker.number().numberBetween(1, 12));
        return this;
    }

    /**
     * Cliente recém-cadastrado.
     */
    public ClienteTestDataBuilder recemCadastrado() {
        this.dataCadastro = LocalDateTime.now().minusMinutes(faker.number().numberBetween(1, 30));
        return this;
    }

    // ========== MÉTODO BUILD ==========

    /**
     * Constrói o objeto Cliente com os dados configurados.
     */
    public Cliente build() {
        Cliente cliente = new Cliente();
        cliente.setNome(this.nome);
        cliente.setEmail(this.email);
        cliente.setTelefone(this.telefone);
        cliente.setEndereco(this.endereco);
        cliente.setDataCadastro(this.dataCadastro);
        cliente.setAtivo(this.ativo);
        return cliente;
    }

    /**
     * Constrói o objeto Cliente com ID específico.
     */
    public Cliente buildComId(Long id) {
        Cliente cliente = build();
        cliente.setId(id);
        return cliente;
    }
}