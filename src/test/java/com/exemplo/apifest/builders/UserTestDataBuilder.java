package com.exemplo.apifest.builders;

import com.exemplo.apifest.model.User;
import com.exemplo.apifest.model.Role;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Test Data Builder para User - Sistema de Autenticação.
 * 
 * Este builder facilita a criação de objetos User para testes,
 * fornecendo dados realistas e permitindo customização específica
 * para cada cenário de teste.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Correção Sistema - Autenticação
 */
public class UserTestDataBuilder {

    private static final Faker faker = new Faker(Locale.of("pt", "BR"));

    private String nome = faker.name().fullName();
    private String email = faker.internet().emailAddress();
    private String senha = "senha123";
    private Role role = Role.CLIENTE;
    private Boolean ativo = true;
    private LocalDateTime dataCadastro = LocalDateTime.now();

    /**
     * Cria um builder com dados padrão válidos.
     */
    public static UserTestDataBuilder umUser() {
        return new UserTestDataBuilder();
    }

    /**
     * Cria um builder com dados específicos para usuário válido.
     */
    public static UserTestDataBuilder umUserValido() {
        return new UserTestDataBuilder()
                .comNome("João Silva")
                .comEmail("joao.silva@email.com")
                .comSenha("senha123")
                .comRole(Role.CLIENTE)
                .ativo();
    }

    /**
     * Alias para umUserValido() - compatibilidade com testes que usam umUsuarioValido()
     */
    public static UserTestDataBuilder umUsuarioValido() {
        return umUserValido();
    }

    /**
     * Cria um builder para usuário admin.
     */
    public static UserTestDataBuilder umAdmin() {
        return new UserTestDataBuilder()
                .comNome("Admin Sistema")
                .comEmail("admin@email.com")
                .comSenha("admin123")
                .comRole(Role.ADMIN)
                .ativo();
    }

    /**
     * Cria um builder para usuário inativo.
     */
    public static UserTestDataBuilder umUserInativo() {
        return new UserTestDataBuilder()
                .comNome("User Inativo")
                .comEmail("inativo@email.com")
                .inativo();
    }

    // ========== MÉTODOS DE CONFIGURAÇÃO ==========

    public UserTestDataBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public UserTestDataBuilder comEmail(String email) {
        this.email = email;
        return this;
    }

    public UserTestDataBuilder comSenha(String senha) {
        this.senha = senha;
        return this;
    }

    // Alias para compatibilidade com testes que usam "Password"
    public UserTestDataBuilder comPassword(String password) {
        this.senha = password;
        return this;
    }

    public UserTestDataBuilder comRole(Role role) {
        this.role = role;
        return this;
    }

    public UserTestDataBuilder ativo() {
        this.ativo = true;
        return this;
    }

    // Alias para compatibilidade com testes que usam comAtivo()
    public UserTestDataBuilder comAtivo(Boolean ativo) {
        this.ativo = ativo;
        return this;
    }

    public UserTestDataBuilder inativo() {
        this.ativo = false;
        return this;
    }

    public UserTestDataBuilder comDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
        return this;
    }

    // ========== MÉTODOS DE CONSTRUÇÃO ==========

    /**
     * Constrói o objeto User com os dados configurados.
     */
    public User build() {
        User user = new User(nome, email, senha, role);
        user.setAtivo(ativo);
        user.setDataCadastro(dataCadastro);
        return user;
    }

    /**
     * Constrói e retorna o objeto User (alias para build()).
     */
    public User agora() {
        return build();
    }
}