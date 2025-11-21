package com.exemplo.apifest.dto.response;

import com.exemplo.apifest.model.Usuario;
import com.exemplo.apifest.model.Role;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * DTO para resposta com dados do usuário
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 7 - Sistema de Autenticação JWT
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private Role role;
    private boolean ativo;
    private Long restauranteId;

    // Construtor padrão
    public UserResponse() {}

    // Método factory para criar a partir de Usuario
    public static UserResponse fromUsuario(Usuario usuario) {
        UserResponse response = new UserResponse();
        response.setId(usuario.getId());
        response.setNome(usuario.getNome());
        response.setEmail(usuario.getEmail());
        response.setTelefone(usuario.getTelefone());
        response.setRole(usuario.getRole());
        response.setAtivo(usuario.getAtivo());
        response.setRestauranteId(usuario.getRestauranteId());
        return response;
    }

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Long getRestauranteId() {
        return restauranteId;
    }

    public void setRestauranteId(Long restauranteId) {
        this.restauranteId = restauranteId;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", role=" + role +
                ", ativo=" + ativo +
                ", restauranteId=" + restauranteId +
                '}';
    }
}