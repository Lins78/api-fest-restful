package com.exemplo.apifest.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ROTEIRO 3 - ENTIDADE CLIENTE
 * 
 * Entidade JPA que representa um cliente no sistema de delivery DeliveryTech.
 * Esta classe mapeia para a tabela 'clientes' no banco de dados e contém
 * todas as informações necessárias para identificar e contatar um cliente.
 * 
 * Relacionamentos:
 * - Um cliente pode ter vários pedidos (OneToMany com Pedido)
 * 
 * @author DeliveryTech Development Team
 * @version 1.0
 * @since Roteiro 3 - Implementação da Camada de Dados
 */
@Entity
@Table(name = "clientes")
public class Cliente {
    /** Identificador único do cliente (chave primária) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** Nome completo do cliente */
    private String nome;
    
    /** Email único do cliente (usado para login e contato) */
    private String email;
    
    /** Telefone de contato do cliente */
    private String telefone;
    
    /** Endereço de entrega do cliente */
    private String endereco;
    
    /** Data e hora de cadastro do cliente no sistema */
    private LocalDateTime dataCadastro;
    
    /** Flag indicando se o cliente está ativo no sistema */
    private Boolean ativo;

    // Constructors
    
    /**
     * Construtor padrão necessário para o JPA
     */
    public Cliente() {}
    
    /**
     * Construtor para criação de novo cliente
     * Define automaticamente a data de cadastro como agora e o cliente como ativo
     * 
     * @param nome Nome completo do cliente
     * @param email Email único do cliente
     * @param telefone Telefone de contato
     * @param endereco Endereço de entrega
     */
    public Cliente(String nome, String email, String telefone, String endereco) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
        this.dataCadastro = LocalDateTime.now();
        this.ativo = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    // Business methods
    public void inativar() {
        this.ativo = false;
    }
}