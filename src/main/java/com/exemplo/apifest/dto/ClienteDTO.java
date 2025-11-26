package com.exemplo.apifest.dto;

import com.exemplo.apifest.validation.ValidTelefone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação e atualização de Cliente
 * 
 * Este DTO contém todas as validações necessárias para garantir que os dados
 * do cliente estejam corretos antes de serem processados pelos services.
 * 
 * Validações implementadas:
 * - Nome: obrigatório, mínimo 2 caracteres, máximo 100
 * - Email: obrigatório, formato válido, único no sistema
 * - Telefone: obrigatório, formato adequado
 * - Endereço: obrigatório para entrega
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 4 - Camada de Serviços e Controllers REST
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    /**
     * Nome completo do cliente
     * Deve ter entre 2 e 100 caracteres
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    /**
     * Email do cliente (usado para login/identificação)
     * Deve ser único no sistema e ter formato válido
     */
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
    private String email;

    /**
     * Telefone para contato
     * Deve seguir formato brasileiro: (11) 99999-9999 ou 11999999999
     */
    @NotBlank(message = "Telefone é obrigatório")
    @ValidTelefone(message = "Telefone deve estar no formato brasileiro válido")
    private String telefone;

    /**
     * Endereço completo do cliente
     * Usado como endereço padrão para entrega
     */
    @NotBlank(message = "Endereço é obrigatório")
    @Size(min = 10, max = 200, message = "Endereço deve ter entre 10 e 200 caracteres")
    private String endereco;

    // ========== Getters e Setters Manuais ==========
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    
    // ========== Métodos de Compatibilidade para Testes ==========
    
    /**
     * Métodos de compatibilidade para testes legados que esperam campos específicos
     * de CPF e endereço detalhado.
     */
    
    // Compatibilidade CPF
    private String cpf;
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    // Compatibilidade Campos de Endereço Detalhados
    private String cep;
    private String logradouro; 
    private String numero;
    private String bairro;
    private String cidade;
    private String uf;
    
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
}