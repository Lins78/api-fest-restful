package com.exemplo.apifest.repository;

import com.exemplo.apifest.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * ROTEIRO 3 - REPOSITORY DO CLIENTE
 * 
 * Interface de repositório para a entidade Cliente usando Spring Data JPA.
 * Esta interface fornece operações CRUD básicas e consultas derivadas personalizadas
 * para gerenciar clientes no sistema DeliveryTech.
 * 
 * Funcionalidades implementadas conforme Roteiro 3:
 * - Busca por email (login/autenticação)
 * - Verificação de existência por email (validação de cadastro)
 * - Listagem de clientes ativos
 * - Busca por nome (pesquisa com wildcards)
 * 
 * Herda métodos CRUD básicos do JpaRepository:
 * - save(), findById(), findAll(), delete(), etc.
 * 
 * @author DeliveryTech Development Team
 * @version 1.0
 * @since Roteiro 3 - Implementação da Camada de Dados
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    /**
     * Busca um cliente pelo email (case sensitive)
     * Utilizado para login e verificações de unicidade
     * 
     * @param email Email do cliente a ser buscado
     * @return Optional contendo o cliente se encontrado
     */
    Optional<Cliente> findByEmail(String email);
    
    /**
     * Verifica se já existe um cliente com o email informado
     * Utilizado na validação de cadastro para evitar emails duplicados
     * 
     * @param email Email a ser verificado
     * @return true se existe, false caso contrário
     */
    boolean existsByEmail(String email);
    
    /**
     * Busca todos os clientes que estão ativos no sistema
     * Útil para relatórios e listagens gerenciais
     * 
     * @return Lista de clientes ativos
     */
    List<Cliente> findByAtivoTrue();
    
    /**
     * Busca clientes por nome (busca parcial, case insensitive)
     * Permite pesquisar clientes digitando parte do nome
     * 
     * @param nome Parte do nome a ser buscado
     * @return Lista de clientes que contém o texto no nome
     */
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
}