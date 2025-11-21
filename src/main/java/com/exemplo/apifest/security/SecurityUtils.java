package com.exemplo.apifest.security;

import com.exemplo.apifest.model.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utilitário para operações de segurança e acesso ao usuário logado
 * 
 * Esta classe fornece métodos convenientes para:
 * - Obter informações do usuário autenticado
 * - Verificar roles e permissões
 * - Acessar dados do contexto de segurança
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 7 - Sistema de Autenticação JWT
 */
@Component
public class SecurityUtils {

    /**
     * Obtém o usuário atualmente autenticado
     * 
     * @return Usuario logado ou null se não estiver autenticado
     */
    public static Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            authentication.getPrincipal() instanceof Usuario) {
            return (Usuario) authentication.getPrincipal();
        }
        
        return null;
    }

    /**
     * Obtém o ID do usuário atualmente autenticado
     * 
     * @return ID do usuário logado ou null se não estiver autenticado
     */
    public static Long getCurrentUserId() {
        Usuario currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getId() : null;
    }

    /**
     * Obtém o email do usuário atualmente autenticado
     * 
     * @return Email do usuário logado ou null se não estiver autenticado
     */
    public static String getCurrentUserEmail() {
        Usuario currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getEmail() : null;
    }

    /**
     * Verifica se o usuário atual possui uma role específica
     * 
     * @param role Role a ser verificada (ex: "ADMIN", "CLIENTE")
     * @return true se o usuário possui a role, false caso contrário
     */
    public static boolean hasRole(String role) {
        Usuario currentUser = getCurrentUser();
        return currentUser != null && role.equals(currentUser.getRole().name());
    }

    /**
     * Verifica se o usuário atual é ADMIN
     * 
     * @return true se o usuário é ADMIN, false caso contrário
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Verifica se o usuário atual é CLIENTE
     * 
     * @return true se o usuário é CLIENTE, false caso contrário
     */
    public static boolean isCliente() {
        return hasRole("CLIENTE");
    }

    /**
     * Verifica se o usuário atual é RESTAURANTE
     * 
     * @return true se o usuário é RESTAURANTE, false caso contrário
     */
    public static boolean isRestaurante() {
        return hasRole("RESTAURANTE");
    }

    /**
     * Verifica se o usuário atual é ENTREGADOR
     * 
     * @return true se o usuário é ENTREGADOR, false caso contrário
     */
    public static boolean isEntregador() {
        return hasRole("ENTREGADOR");
    }

    /**
     * Obtém o ID do restaurante do usuário logado (se for RESTAURANTE)
     * 
     * @return ID do restaurante ou null se não for restaurante ou não tiver ID
     */
    public static Long getCurrentRestauranteId() {
        Usuario currentUser = getCurrentUser();
        return (currentUser != null && isRestaurante()) ? currentUser.getRestauranteId() : null;
    }

    /**
     * Verifica se há um usuário autenticado
     * 
     * @return true se há usuário autenticado, false caso contrário
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getPrincipal());
    }
}