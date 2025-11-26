package com.exemplo.apifest.service.impl;

import com.exemplo.apifest.dto.LoginDTO;
import com.exemplo.apifest.dto.RegisterDTO;
import com.exemplo.apifest.dto.response.AuthResponseDTO;
import com.exemplo.apifest.exception.UnauthorizedException;
import com.exemplo.apifest.exception.ConflictException;
import com.exemplo.apifest.model.User;
import com.exemplo.apifest.repository.UserRepository;
import com.exemplo.apifest.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * IMPLEMENTATION AUTH SERVICE
 * 
 * Implementação do serviço de autenticação para login e registro de usuários.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Correção Sistema - Autenticação
 */
@Service
public class AuthServiceImpl {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    /**
     * Realiza login do usuário
     * 
     * @param loginDTO Dados de login
     * @return Resposta com token e informações do usuário
     * @throws UnauthorizedException se credenciais inválidas
     */
    public AuthResponseDTO login(LoginDTO loginDTO) {
        Optional<User> userOpt = userRepository.findByEmail(loginDTO.getEmail());
        
        if (userOpt.isEmpty()) {
            throw new UnauthorizedException("Email ou senha incorretos");
        }

        User user = userOpt.get();
        
        if (!user.getAtivo()) {
            throw new UnauthorizedException("Usuário inativo");
        }

        // Verificar senha (com encoder se disponível, senão comparação simples)
        boolean senhaCorreta = passwordEncoder != null 
            ? passwordEncoder.matches(loginDTO.getSenha(), user.getSenha())
            : loginDTO.getSenha().equals(user.getSenha());

        if (!senhaCorreta) {
            throw new UnauthorizedException("Email ou senha incorretos");
        }

        // Gerar token
        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponseDTO(token, user.getEmail(), user.getNome());
    }

    /**
     * Registra novo usuário
     * 
     * @param registerDTO Dados de registro
     * @return Resposta com token e informações do usuário
     * @throws ConflictException se email já existe
     */
    public AuthResponseDTO register(RegisterDTO registerDTO) {
        // Verificar se email já existe
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new ConflictException("Email já está em uso");
        }

        // Criar novo usuário
        String senhaHash = passwordEncoder != null 
            ? passwordEncoder.encode(registerDTO.getSenha())
            : registerDTO.getSenha();

        User novoUser = new User(
            registerDTO.getNome(),
            registerDTO.getEmail(),
            senhaHash
        );

        User userSalvo = userRepository.save(novoUser);

        // Gerar token
        String token = jwtUtil.generateToken(userSalvo.getEmail());

        return new AuthResponseDTO(token, userSalvo.getEmail(), userSalvo.getNome());
    }

    /**
     * Busca usuário por email
     * 
     * @param email Email do usuário
     * @return Optional com o usuário
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Verifica se email já existe
     * 
     * @param email Email para verificação
     * @return true se existe, false caso contrário
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Valida token JWT
     * 
     * @param token Token para validação
     * @return true se token válido
     */
    public boolean validateToken(String token) {
        try {
            return jwtUtil.isTokenValid(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Atualiza token (refresh token)
     * 
     * @param refreshToken Token de refresh
     * @return Nova resposta de autenticação
     */
    public AuthResponseDTO refreshToken(String refreshToken) {
        try {
            String email = jwtUtil.extractEmail(refreshToken);
            User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UnauthorizedException("Token inválido")
            );
            
            String newToken = jwtUtil.generateToken(email);
            return new AuthResponseDTO(newToken, user.getEmail(), user.getNome());
        } catch (Exception e) {
            throw new UnauthorizedException("Token de refresh inválido");
        }
    }

    /**
     * Verifica se usuário tem acesso administrativo
     * 
     * @param user Usuário para verificação
     * @return true se tem acesso admin
     */
    public boolean hasAdminAccess(User user) {
        // Implementação simples - pode ser expandida com roles
        return user.getEmail().contains("admin") || user.isAdmin();
    }

    /**
     * Verifica se usuário pode acessar seu próprio recurso
     * 
     * @param user Usuário
     * @param resourceOwnerId ID do dono do recurso
     * @return true se pode acessar
     */
    public boolean canAccessOwnResource(User user, Long resourceOwnerId) {
        return user.getId().equals(resourceOwnerId) || hasAdminAccess(user);
    }
}