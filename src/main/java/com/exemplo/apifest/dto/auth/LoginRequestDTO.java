package com.exemplo.apifest.dto.auth;

/**
 * Alias para LoginRequest - Compatibilidade com testes que esperam LoginRequestDTO
 * 
 * Este DTO é idêntico ao LoginRequest, criado apenas para manter compatibilidade
 * com testes que foram escritos esperando o nome LoginRequestDTO.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Correção Sistema - Compatibilidade
 */
public class LoginRequestDTO extends LoginRequest {

    public LoginRequestDTO() {
        super();
    }

    public LoginRequestDTO(String email, String senha) {
        super(email, senha);
    }

    // Métodos de compatibilidade para testes que usam "Password" ao invés de "Senha"
    public String getPassword() {
        return super.getSenha();
    }

    public void setPassword(String password) {
        super.setSenha(password);
    }
}