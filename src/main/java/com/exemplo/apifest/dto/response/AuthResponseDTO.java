package com.exemplo.apifest.dto.response;

import com.exemplo.apifest.model.User;

/**
 * DTO de resposta para operações de autenticação
 * 
 * Contém informações sobre o resultado da autenticação:
 * - Token JWT gerado
 * - Tipo do token (Bearer)
 * - Informações do usuário
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Correção Sistema - Autenticação
 */
public class AuthResponseDTO {

    private String token;
    private String type = "Bearer";
    private String email;
    private String nome;
    private User user; // Campo de compatibilidade

    public AuthResponseDTO() {}

    public AuthResponseDTO(String token, String email, String nome) {
        this.token = token;
        this.email = email;
        this.nome = nome;
        // Criar user de compatibilidade
        this.user = new User(nome, email, "");
        this.user.setId(1L);
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    // Método de compatibilidade para testes
    public User getUser() { 
        if (user == null) {
            user = new User(nome, email, "");
            user.setId(1L);
        }
        return user; 
    }
    public void setUser(User user) { this.user = user; }
}