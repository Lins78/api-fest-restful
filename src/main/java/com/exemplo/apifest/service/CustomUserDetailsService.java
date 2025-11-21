package com.exemplo.apifest.service;

import com.exemplo.apifest.model.Usuario;
import com.exemplo.apifest.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço customizado para carregamento de usuários para autenticação Spring Security
 * 
 * Implementa UserDetailsService para integração com o sistema de autenticação
 * do Spring Security, carregando usuários por email (que é usado como username)
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 7 - Sistema de Autenticação JWT
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Carrega usuário por email (username) para autenticação
     * 
     * @param email Email do usuário (usado como username)
     * @return UserDetails do usuário encontrado
     * @throws UsernameNotFoundException Se o usuário não for encontrado
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Tentando carregar usuário por email: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> {
                logger.warn("Usuário não encontrado com email: {}", email);
                return new UsernameNotFoundException("Usuário não encontrado com email: " + email);
            });

        logger.debug("Usuário carregado com sucesso: {} - Role: {}", 
            usuario.getEmail(), usuario.getRole());

        // O próprio Usuario implementa UserDetails, então retornamos diretamente
        return usuario;
    }

    /**
     * Método auxiliar para carregar usuário por ID
     * Útil para operações internas que precisam do objeto Usuario completo
     */
    @Transactional(readOnly = true)
    public Usuario loadUserById(Long userId) {
        logger.debug("Carregando usuário por ID: {}", userId);
        
        return usuarioRepository.findById(userId)
            .orElseThrow(() -> {
                logger.warn("Usuário não encontrado com ID: {}", userId);
                return new UsernameNotFoundException("Usuário não encontrado com ID: " + userId);
            });
    }

    /**
     * Verifica se um email já está em uso
     * Útil para validação durante registro
     */
    @Transactional(readOnly = true)
    public boolean isEmailInUse(String email) {
        boolean inUse = usuarioRepository.existsByEmail(email);
        logger.debug("Email {} está em uso: {}", email, inUse);
        return inUse;
    }
}