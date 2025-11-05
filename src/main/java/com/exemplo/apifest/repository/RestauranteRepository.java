package com.exemplo.apifest.repository;

import com.exemplo.apifest.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

/**
 * ROTEIRO 3 - REPOSITORY DO RESTAURANTE
 * 
 * Interface de repositório para a entidade Restaurante usando Spring Data JPA.
 * Esta interface fornece operações CRUD e consultas específicas para
 * gerenciar restaurantes parceiros no sistema DeliveryTech.
 * 
 * Funcionalidades implementadas conforme Roteiro 3:
 * - Busca por categoria de restaurante
 * - Filtro por restaurantes ativos
 * - Consultas por faixa de taxa de entrega
 * - Ranking de restaurantes por nome
 * - Consultas customizadas com @Query para relatórios
 * 
 * @author DeliveryTech Development Team
 * @version 1.0
 * @since Roteiro 3 - Implementação da Camada de Dados
 */
@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
    
    // ========== MÉTODOS DERIVADOS DO SPRING DATA JPA ==========
    
    /**
     * Busca restaurantes por categoria específica
     * Ex: "Italiana", "Japonesa", "Fast Food"
     * 
     * @param categoria Categoria do restaurante
     * @return Lista de restaurantes da categoria especificada
     */
    List<Restaurante> findByCategoria(String categoria);
    
    /**
     * Busca todos os restaurantes ativos no sistema
     * Utilizado para listagens públicas e relatórios gerenciais
     * 
     * @return Lista de restaurantes ativos
     */
    List<Restaurante> findByAtivoTrue();
    
    /**
     * Busca restaurantes com taxa de entrega menor ou igual ao valor especificado
     * Permite filtrar opções mais econômicas para o cliente
     * 
     * @param taxa Valor máximo da taxa de entrega
     * @return Lista de restaurantes com taxa dentro do limite
     */
    List<Restaurante> findByTaxaEntregaLessThanEqual(BigDecimal taxa);
    
    /**
     * Busca os 5 primeiros restaurantes ordenados alfabeticamente
     * Útil para destacar restaurantes em promoções ou página inicial
     * 
     * @return Lista com os 5 primeiros restaurantes por ordem alfabética
     */
    List<Restaurante> findTop5ByOrderByNomeAsc();
    
    /**
     * Busca restaurantes por nome (busca parcial, case insensitive)
     * Permite pesquisar restaurantes digitando parte do nome
     * 
     * @param nome Parte do nome a ser buscado
     * @return Lista de restaurantes que contém o texto no nome
     */
    List<Restaurante> findByNomeContainingIgnoreCase(String nome);

    // ========== CONSULTAS CUSTOMIZADAS COM @QUERY ==========
    
    /**
     * Consulta customizada: Busca restaurantes ativos com taxa baixa, ordenados por taxa
     * Combina filtros de status ativo e faixa de taxa em uma única consulta otimizada
     * 
     * @param taxa Valor máximo da taxa de entrega
     * @return Lista de restaurantes ativos ordenados por taxa crescente
     */
    @Query("SELECT r FROM Restaurante r WHERE r.ativo = true AND r.taxaEntrega <= :taxa ORDER BY r.taxaEntrega ASC")
    List<Restaurante> findRestaurantesComTaxaBaixa(BigDecimal taxa);

    /**
     * Consulta de relatório: Conta quantos restaurantes existem por categoria
     * Retorna dados agregados para dashboards e análises de mercado
     * 
     * @return Array de Objects contendo [categoria, quantidade]
     */
    @Query("SELECT r.categoria, COUNT(r) FROM Restaurante r WHERE r.ativo = true GROUP BY r.categoria")
    List<Object[]> countRestaurantesPorCategoria();
}