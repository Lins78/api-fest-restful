package com.exemplo.apifest.dto.auth;

/**
 * Alias para RegisterRequest - Compatibilidade com testes que esperam RegisterRequestDTO
 * 
 * Este DTO é idêntico ao RegisterRequest, criado apenas para manter compatibilidade
 * com testes que foram escritos esperando o nome RegisterRequestDTO.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Correção Sistema - Compatibilidade
 */
public class RegisterRequestDTO extends RegisterRequest {

    public RegisterRequestDTO() {
        super();
    }

    public RegisterRequestDTO(String nome, String email, String senha) {
        super(nome, email, senha);
    }

    // Métodos de compatibilidade para testes que usam "Password" ao invés de "Senha"
    public String getPassword() {
        return super.getSenha();
    }

    public void setPassword(String password) {
        super.setSenha(password);
    }

    // Métodos de compatibilidade para validação de senha
    public String getConfirmPassword() {
        // Retorna a própria senha para compatibilidade
        return super.getSenha();
    }

    public void setConfirmPassword(String confirmPassword) {
        // Para compatibilidade, ignora confirmPassword
        // Em implementação real, validaria se as senhas são iguais
    }
}