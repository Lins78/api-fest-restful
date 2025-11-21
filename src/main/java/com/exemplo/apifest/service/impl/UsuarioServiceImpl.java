package com.exemplo.apifest.service.impl;

import com.exemplo.apifest.dto.auth.RegisterRequest;
import com.exemplo.apifest.exception.EntityNotFoundException;
import com.exemplo.apifest.exception.BusinessException;
import com.exemplo.apifest.model.Usuario;
import com.exemplo.apifest.model.Role;
import com.exemplo.apifest.repository.UsuarioRepository;
import com.exemplo.apifest.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementação dos serviços relacionados ao usuário
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 7 - Sistema de Autenticação JWT
 */
@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    public Usuario createUsuario(RegisterRequest registerRequest) {
        logger.info("Criando novo usuário com email: {}", registerRequest.getEmail());

        // Validações
        if (existsByEmail(registerRequest.getEmail())) {
            throw new BusinessException("Email já está em uso: " + registerRequest.getEmail());
        }

        // Valida role
        Role role = registerRequest.getRole();
        if (role == null) {
            throw new BusinessException("Role é obrigatória");
        }

        // Cria novo usuário
        Usuario usuario = new Usuario();
        usuario.setNome(registerRequest.getNome());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setSenha(passwordEncoder.encode(registerRequest.getSenha()));
        usuario.setTelefone(registerRequest.getTelefone());
        usuario.setRole(role);
        usuario.setAtivo(true);

        // Se for RESTAURANTE, pode ter restauranteId
        if (role == Role.RESTAURANTE && registerRequest.getRestauranteId() != null) {
            usuario.setRestauranteId(registerRequest.getRestauranteId());
        }

        usuario = usuarioRepository.save(usuario);
        
        logger.info("Usuário criado com sucesso: {} (ID: {})", usuario.getEmail(), usuario.getId());
        return usuario;
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + id));
    }

    @Override
    public Usuario save(Usuario usuario) {
        logger.info("Salvando usuário: {}", usuario.getEmail());
        return usuarioRepository.save(usuario);
    }
}