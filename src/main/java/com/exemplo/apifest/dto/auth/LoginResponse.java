package com.exemplo.apifest.dto.auth;

import com.exemplo.apifest.model.Role;

/**
 * DTO para resposta de login
 * 
 * Contém as informações retornadas após autenticação bem-sucedida:
 * - Token JWT
 * - Tipo do token (Bearer)
 * - Informações do usuário logado
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 7 - Sistema de Autenticação JWT
 */
public class LoginResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private Long id;
    private String nome;
    private String email;
    private Role role;
    private Long restauranteId; // Preenchido apenas para usuários RESTAURANTE
    private Long expiresIn; // Tempo de expiração em segundos

    public LoginResponse() {}

    public LoginResponse(String accessToken, Long id, String nome, String email, Role role) {
        this.accessToken = accessToken;
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
    }

    public LoginResponse(String accessToken, Long id, String nome, String email, Role role, Long restauranteId) {
        this.accessToken = accessToken;
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.restauranteId = restauranteId;
    }

    // Getters e Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setToken(String token) {
        this.accessToken = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setTipo(String tipo) {
        this.tokenType = tipo;
    }

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

    public void setUsuario(com.exemplo.apifest.dto.response.UserResponse userResponse) {
        this.id = userResponse.getId();
        this.nome = userResponse.getNome();
        this.email = userResponse.getEmail();
        this.role = userResponse.getRole();
        this.restauranteId = userResponse.getRestauranteId();
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}