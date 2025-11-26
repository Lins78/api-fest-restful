package com.exemplo.apifest.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * ROTEIRO 3 - ENTIDADE RESTAURANTE
 * 
 * Entidade JPA que representa um restaurante no sistema de delivery DeliveryTech.
 * Esta classe mapeia para a tabela 'restaurantes' no banco de dados e contém
 * informações sobre estabelecimentos parceiros que fornecem produtos.
 * 
 * Funcionalidades do Roteiro 3:
 * - Adicionado campo 'categoria' para classificar tipos de restaurante
 * - Adicionado campo 'taxaEntrega' para consultas por faixa de preço
 * - Suporte a consultas por categoria e taxa de entrega
 * 
 * Relacionamentos:
 * - Um restaurante pode ter vários produtos (OneToMany com Produto)
 * 
 * @author DeliveryTech Development Team
 * @version 1.0
 * @since Roteiro 3 - Implementação da Camada de Dados
 */
@Entity
@Table(name = "restaurantes")
public class Restaurante {
    /** Identificador único do restaurante (chave primária) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** Nome do restaurante */
    private String nome;
    
    /** Endereço físico do restaurante */
    private String endereco;
    
    /** Telefone de contato do restaurante */
    private String telefone;
    
    /** Email de contato do restaurante */
    @Column(unique = true)
    private String email;
    
    /** Categoria do restaurante (Ex: Italiana, Japonesa, Fast Food, etc.) - ROTEIRO 3 */
    private String categoria;
    
    /** Taxa de entrega cobrada pelo restaurante em reais - ROTEIRO 3 */
    private BigDecimal taxaEntrega;
    
    /** Horário de abertura do restaurante */
    private LocalTime horarioAbertura;
    
    /** Horário de fechamento do restaurante */
    private LocalTime horarioFechamento;
    
    /** Valor mínimo para pedido */
    private BigDecimal valorMinimo;
    
    /** Flag indicando se o restaurante está ativo e aceitando pedidos */
    private Boolean ativo;

    // Constructors
    public Restaurante() {}
    
    public Restaurante(String nome, String endereco, String telefone) {
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.ativo = true;
    }

    public Restaurante(String nome, String endereco, String telefone, String categoria, BigDecimal taxaEntrega) {
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.categoria = categoria;
        this.taxaEntrega = taxaEntrega;
        this.ativo = true;
    }

    public Restaurante(String nome, String endereco, String telefone, String email, String categoria, BigDecimal taxaEntrega) {
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
        this.categoria = categoria;
        this.taxaEntrega = taxaEntrega;
        this.ativo = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public BigDecimal getTaxaEntrega() { return taxaEntrega; }
    public void setTaxaEntrega(BigDecimal taxaEntrega) { this.taxaEntrega = taxaEntrega; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalTime getHorarioAbertura() { return horarioAbertura; }
    public void setHorarioAbertura(LocalTime horarioAbertura) { this.horarioAbertura = horarioAbertura; }

    public LocalTime getHorarioFechamento() { return horarioFechamento; }
    public void setHorarioFechamento(LocalTime horarioFechamento) { this.horarioFechamento = horarioFechamento; }

    public BigDecimal getValorMinimo() { return valorMinimo; }
    public void setValorMinimo(BigDecimal valorMinimo) { this.valorMinimo = valorMinimo; }

    // Business methods
    public void inativar() {
        this.ativo = false;
    }
    
    // ========== MÉTODOS DE COMPATIBILIDADE PARA TESTES ==========
    
    /**
     * Campos de compatibilidade para funcionalidades de status.
     */
    private String status = "ATIVO"; // Status como String para compatibilidade
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}