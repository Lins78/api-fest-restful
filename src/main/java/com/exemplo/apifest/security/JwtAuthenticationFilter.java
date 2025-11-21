package com.exemplo.apifest.security;

import com.exemplo.apifest.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT que intercepta todas as requisições para validar tokens
 * 
 * Este filtro:
 * - Extrai o token JWT do header Authorization
 * - Valida o token usando JwtUtil
 * - Carrega o usuário e seta no SecurityContext
 * - Permite que a requisição continue se o token for válido
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 7 - Sistema de Autenticação JWT
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {        try {
            String jwt = parseJwt(request);
            
            if (jwt != null && jwtUtil.validateToken(jwt, getUserDetails(jwt))) {
                String username = jwtUtil.extractUsername(jwt);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Cria o objeto de autenticação
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Seta o usuário autenticado no SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                logger.debug("Usuário autenticado: {} com roles: {}", 
                    username, userDetails.getAuthorities());
            }
        } catch (Exception e) {
            logger.error("Erro na autenticação JWT: {}", e.getMessage());
            // Não lança exceção - deixa a requisição continuar sem autenticação
            // Os endpoints protegidos retornarão 401 automaticamente
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o token JWT do header Authorization
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Remove "Bearer " prefix
        }
        
        return null;
    }

    /**
     * Carrega UserDetails para validação inicial do token
     */
    private UserDetails getUserDetails(String jwt) {
        try {
            String username = jwtUtil.extractUsername(jwt);
            return userDetailsService.loadUserByUsername(username);
        } catch (Exception e) {
            logger.error("Erro ao carregar UserDetails para token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Pula o filtro para endpoints públicos
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        // Lista de paths que não precisam de autenticação
        return path.startsWith("/api/auth/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.startsWith("/api-docs/") ||
               path.startsWith("/h2-console/") ||
               path.startsWith("/actuator/") ||
               path.equals("/api/restaurantes") ||
               path.equals("/api/produtos") ||
               path.equals("/api/v1/home");
    }
}