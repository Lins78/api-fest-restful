package com.exemplo.apifest.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * Entidade Usuario que implementa UserDetails para integração com Spring Security
 * 
 * Representa os usuários do sistema com diferentes perfis de acesso.
 * Implementa UserDetails para permitir autenticação e autorização automática.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 7 - Sistema de Autenticação JWT
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(length = 20)
    private String telefone;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    /**
     * ID do restaurante (apenas para usuários com role RESTAURANTE)
     * Permite que um usuário restaurante gerencie apenas seus próprios produtos
     */
    @Column(name = "restaurante_id")
    private Long restauranteId;

    // ========== Implementação UserDetails ==========

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getAuthority()));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.ativo;
    }

    // ========== Métodos de Conveniência ==========

    /**
     * Verifica se o usuário tem uma role específica
     */
    public boolean hasRole(Role role) {
        return this.role == role;
    }

    /**
     * Verifica se o usuário é dono de um restaurante específico
     */
    public boolean isOwnerOfRestaurante(Long restauranteId) {
        return this.hasRole(Role.RESTAURANTE) && 
               this.restauranteId != null && 
               this.restauranteId.equals(restauranteId);
    }

    /**
     * Verifica se o usuário é administrador
     */
    public boolean isAdmin() {
        return this.hasRole(Role.ADMIN);
    }

    @PrePersist
    protected void onCreate() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }
}