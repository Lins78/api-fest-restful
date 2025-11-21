package com.exemplo.apifest.controller;

import com.exemplo.apifest.dto.auth.LoginRequest;
import com.exemplo.apifest.dto.auth.LoginResponse;
import com.exemplo.apifest.dto.auth.RegisterRequest;
import com.exemplo.apifest.dto.response.UserResponse;
import com.exemplo.apifest.model.Usuario;
import com.exemplo.apifest.security.JwtUtil;
import com.exemplo.apifest.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller para operações de autenticação
 * 
 * Endpoints:
 * - POST /api/auth/login - Login do usuário
 * - POST /api/auth/register - Registro de novo usuário
 * - GET /api/auth/me - Dados do usuário logado
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 7 - Sistema de Autenticação JWT
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Login do usuário
     * 
     * @param loginRequest Dados de login (email e senha)
     * @return ResponseEntity com token JWT e dados do usuário
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Tentativa de login para email: {}", loginRequest.getEmail());

            // Autentica as credenciais
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getSenha()
                )
            );

            // Obtém o usuário autenticado
            Usuario usuario = (Usuario) authentication.getPrincipal();
            
            // Gera token JWT
            String token = jwtUtil.generateToken(usuario);

            // Prepara resposta
            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setTipo("Bearer");
            response.setExpiresIn(jwtUtil.getExpirationTimeInSeconds());
            response.setUsuario(UserResponse.fromUsuario(usuario));

            logger.info("Login realizado com sucesso para usuário: {}", usuario.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erro no login para email: {}", loginRequest.getEmail(), e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Credenciais inválidas"));
        }
    }

    /**
     * Registro de novo usuário
     * 
     * @param registerRequest Dados para registro
     * @return ResponseEntity com dados do usuário criado (sem token)
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            logger.info("Tentativa de registro para email: {}", registerRequest.getEmail());

            // Verifica se email já existe
            if (usuarioService.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email já está em uso"));
            }

            // Cria novo usuário
            Usuario usuario = usuarioService.createUsuario(registerRequest);
            
            UserResponse response = UserResponse.fromUsuario(usuario);
            
            logger.info("Usuário registrado com sucesso: {}", usuario.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erro no registro para email: {}", registerRequest.getEmail(), e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Erro ao registrar usuário: " + e.getMessage()));
        }
    }

    /**
     * Retorna dados do usuário logado
     * 
     * @return ResponseEntity com dados do usuário atual
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Usuario usuario = (Usuario) authentication.getPrincipal();
            UserResponse response = UserResponse.fromUsuario(usuario);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Erro ao obter usuário atual", e);
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Erro ao obter dados do usuário"));
        }
    }
}