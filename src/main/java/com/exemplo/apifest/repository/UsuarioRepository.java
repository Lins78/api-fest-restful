package com.exemplo.apifest.repository;

import com.exemplo.apifest.model.Role;
import com.exemplo.apifest.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para a entidade Usuario
 * 
 * Fornece operações de acesso a dados para usuários,
 * incluindo métodos customizados para autenticação e autorização.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 7 - Sistema de Autenticação JWT
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca usuário por email (usado na autenticação)
     * 
     * @param email Email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se existe usuário com o email informado
     * 
     * @param email Email a ser verificado
     * @return true se existe, false caso contrário
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuários por role
     * 
     * @param role Role dos usuários
     * @return Lista de usuários com a role especificada
     */
    List<Usuario> findByRole(Role role);

    /**
     * Busca usuários ativos por role
     * 
     * @param role Role dos usuários
     * @param ativo Status de ativação
     * @return Lista de usuários ativos com a role especificada
     */
    List<Usuario> findByRoleAndAtivo(Role role, Boolean ativo);

    /**
     * Busca usuário por restauranteId (para usuários do tipo RESTAURANTE)
     * 
     * @param restauranteId ID do restaurante
     * @return Optional contendo o usuário proprietário do restaurante
     */
    Optional<Usuario> findByRestauranteId(Long restauranteId);

    /**
     * Busca todos os usuários ativos
     * 
     * @param ativo Status de ativação
     * @return Lista de usuários ativos
     */
    List<Usuario> findByAtivo(Boolean ativo);

    /**
     * Query customizada para buscar usuários com informações do restaurante
     * 
     * @param restauranteId ID do restaurante
     * @return Lista de usuários do restaurante
     */
    @Query("SELECT u FROM Usuario u WHERE u.role = :role AND u.restauranteId = :restauranteId AND u.ativo = true")
    List<Usuario> findActiveRestauranteUsers(@Param("role") Role role, @Param("restauranteId") Long restauranteId);
}