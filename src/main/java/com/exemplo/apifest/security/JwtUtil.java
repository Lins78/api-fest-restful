package com.exemplo.apifest.security;

import com.exemplo.apifest.model.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilitário para geração e validação de tokens JWT
 * 
 * Responsável por:
 * - Gerar tokens JWT com claims customizados
 * - Validar tokens recebidos nas requisições
 * - Extrair informações dos tokens (username, claims, etc.)
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 7 - Sistema de Autenticação JWT
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * Gera a chave de assinatura baseada no secret
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Gera token JWT para um usuário
     * 
     * @param userDetails Detalhes do usuário
     * @return Token JWT gerado
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        
        // Adiciona claims customizados se o usuário for do tipo Usuario
        if (userDetails instanceof Usuario) {
            Usuario usuario = (Usuario) userDetails;
            claims.put("userId", usuario.getId());
            claims.put("role", usuario.getRole().name());
            claims.put("nome", usuario.getNome());
            
            // Adiciona restauranteId se aplicável
            if (usuario.getRestauranteId() != null) {
                claims.put("restauranteId", usuario.getRestauranteId());
            }
        }
        
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Gera token JWT para um email (compatibilidade com AuthService)
     * 
     * @param email Email do usuário
     * @return Token JWT gerado
     */
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    /**
     * Gera token JWT para um usuário do tipo User (compatibilidade com testes)
     * 
     * @param user Usuário do tipo User
     * @return Token JWT gerado
     */
    public String generateToken(com.exemplo.apifest.model.User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole());
        claims.put("nome", user.getNome());
        return createToken(claims, user.getEmail());
    }

    /**
     * Cria o token JWT com claims e subject
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrai o username (email) do token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrai o email do token (alias para extractUsername para compatibilidade)
     */
    public String extractEmail(String token) {
        return extractUsername(token);
    }

    /**
     * Extrai a data de expiração do token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrai um claim específico do token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrai todos os claims do token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            logger.error("Erro ao extrair claims do token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Verifica se o token está expirado
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (JwtException e) {
            logger.error("Erro ao verificar expiração do token: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Valida o token JWT
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (MalformedJwtException e) {
            logger.error("Token JWT malformado: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT não suportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string está vazia: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Erro na validação do token: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Valida se o token é válido (sobrecarga para compatibilidade)
     */
    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            logger.error("Erro na validação do token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Valida se o token é válido (alias para compatibilidade com testes)
     */
    public boolean validateToken(String token) {
        return isTokenValid(token);
    }

    /**
     * Extrai o userId do token
     */
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Extrai a role do token
     */
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * Extrai o restauranteId do token (se existir)
     */
    public Long extractRestauranteId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("restauranteId", Long.class);
    }

    /**
     * Extrai o nome do usuário do token
     */
    public String extractNome(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("nome", String.class);
    }

    /**
     * Retorna o tempo de expiração em segundos
     */
    public long getExpirationTimeInSeconds() {
        return jwtProperties.getExpiration() / 1000;
    }
}