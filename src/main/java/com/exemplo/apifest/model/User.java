package com.exemplo.apifest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ENTIDADE USER - Sistema de Autenticação
 * 
 * Entidade JPA que representa um usuário no sistema de autenticação.
 * Esta classe mapeia para a tabela 'users' no banco de dados.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Correção Sistema - Autenticação
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(length = 20)
    private String telefone;

    @Enumerated(EnumType.STRING)
    private Role role = Role.CLIENTE;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro = LocalDateTime.now();

    // Constructors
    public User() {}

    public User(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = Role.CLIENTE;
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
    }

    public User(String nome, String email, String senha, Role role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = role;
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    // Método getPassword para compatibilidade com testes (retorna senha)
    public String getPassword() { return senha; }

    // Business methods
    public void inativar() {
        this.ativo = false;
    }

    public void ativar() {
        this.ativo = true;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }
}