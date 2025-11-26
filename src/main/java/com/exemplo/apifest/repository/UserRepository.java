package com.exemplo.apifest.repository;

import com.exemplo.apifest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * REPOSITORY USER - Sistema de Autenticação
 * 
 * Interface de repositório para operações de persistência da entidade User.
 * Estende JpaRepository fornecendo operações CRUD básicas e consultas customizadas.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Correção Sistema - Autenticação
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca um usuário pelo email
     * 
     * @param email Email do usuário
     * @return Optional com o usuário encontrado ou vazio se não encontrado
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica se existe um usuário com o email informado
     * 
     * @param email Email para verificação
     * @return true se existe, false caso contrário
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuários ativos
     * 
     * @return Lista de usuários ativos
     */
    List<User> findByAtivoTrue();

    /**
     * Busca usuários por nome (contendo)
     * 
     * @param nome Nome ou parte do nome para busca
     * @return Lista de usuários encontrados
     */
    List<User> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca usuários por role
     * 
     * @param role Role do usuário
     * @return Lista de usuários com a role especificada
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.ativo = true")
    List<User> findByRoleAndAtivo(@Param("role") com.exemplo.apifest.model.Role role);

    /**
     * Conta total de usuários ativos
     * 
     * @return Número total de usuários ativos
     */
    long countByAtivoTrue();
}