package com.exemplo.apifest.dto.response;

/**
 * DTO de resposta para Cliente
 * 
 * Este DTO contém apenas os dados seguros do cliente que podem ser expostos
 * via API, excluindo informações sensíveis.
 * 
 * Dados incluídos:
 * - ID, nome, email, telefone, endereço, status ativo
 * 
 * Dados excluídos:
 * - Senhas, dados de auditoria interna, etc.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 4 - Camada de Serviços e Controllers REST
 */
public class ClienteResponseDTO {

    /**
     * Identificador único do cliente
     */
    private Long id;

    /**
     * Nome completo do cliente
     */
    private String nome;

    /**
     * Email do cliente
     */
    private String email;

    /**
     * Telefone para contato
     */
    private String telefone;

    /**
     * Endereço completo do cliente
     */
    private String endereco;

    /**
     * Indica se o cliente está ativo no sistema
     */
    private Boolean ativo;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}