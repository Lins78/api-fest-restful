package com.exemplo.apifest.dto.auth;

import com.exemplo.apifest.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para requisição de registro de usuário
 * 
 * Contém todos os dados necessários para criar um novo usuário:
 * - Informações pessoais (nome, email, senha)
 * - Role do usuário
 * - ID do restaurante (opcional, apenas para role RESTAURANTE)
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 7 - Sistema de Autenticação JWT
 */
public class RegisterRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter um formato válido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    private String senha;

    @NotNull(message = "Role é obrigatória")
    private Role role;

    private String telefone;

    private Long restauranteId; // Obrigatório apenas para role RESTAURANTE

    public RegisterRequest() {}

    public RegisterRequest(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public RegisterRequest(String nome, String email, String senha, Role role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = role;
    }

    public RegisterRequest(String nome, String email, String senha, Role role, Long restauranteId) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = role;
        this.restauranteId = restauranteId;
    }

    // Getters e Setters
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getRestauranteId() {
        return restauranteId;
    }

    public void setRestauranteId(Long restauranteId) {
        this.restauranteId = restauranteId;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", senha='[PROTECTED]'" +
                ", role=" + role +
                ", restauranteId=" + restauranteId +
                '}';
    }
}