package com.exemplo.apifest.service;

import com.exemplo.apifest.dto.auth.RegisterRequest;
import com.exemplo.apifest.model.Usuario;

/**
 * Interface para serviços relacionados ao usuário
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 7 - Sistema de Autenticação JWT
 */
public interface UsuarioService {
    
    /**
     * Verifica se existe usuário com o email informado
     * 
     * @param email Email para verificação
     * @return true se existe, false caso contrário
     */
    boolean existsByEmail(String email);
    
    /**
     * Cria um novo usuário baseado nos dados de registro
     * 
     * @param registerRequest Dados para criação do usuário
     * @return Usuario criado
     */
    Usuario createUsuario(RegisterRequest registerRequest);
    
    /**
     * Busca usuário por email
     * 
     * @param email Email do usuário
     * @return Usuario encontrado
     */
    Usuario findByEmail(String email);
    
    /**
     * Busca usuário por ID
     * 
     * @param id ID do usuário
     * @return Usuario encontrado
     */
    Usuario findById(Long id);
    
    /**
     * Salva ou atualiza usuário
     * 
     * @param usuario Usuario para salvar
     * @return Usuario salvo
     */
    Usuario save(Usuario usuario);
}