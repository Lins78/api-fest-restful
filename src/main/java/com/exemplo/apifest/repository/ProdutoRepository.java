package com.exemplo.apifest.repository;

import com.exemplo.apifest.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

/**
 * ROTEIRO 3 - REPOSITORY DO PRODUTO
 * 
 * Interface de repositório para a entidade Produto usando Spring Data JPA.
 * Esta interface fornece operações CRUD e consultas específicas para
 * gerenciar produtos dos restaurantes no sistema DeliveryTech.
 * 
 * Funcionalidades implementadas conforme Roteiro 3:
 * - Busca produtos por restaurante (relacionamento)
 * - Filtro por disponibilidade e categoria
 * - Consultas por faixa de preço
 * - Busca por nome (wildcards)
 * - Consultas customizadas com @Query para cenários complexos
 * 
 * @author DeliveryTech Development Team
 * @version 1.0
 * @since Roteiro 3 - Implementação da Camada de Dados
 */
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    // ========== MÉTODOS DERIVADOS DO SPRING DATA JPA ==========
    
    /**
     * Busca todos os produtos de um restaurante específico
     * Utilizado para exibir cardápio de um restaurante
     * 
     * @param restauranteId ID do restaurante
     * @return Lista de produtos do restaurante
     */
    List<Produto> findByRestauranteId(Long restauranteId);
    
    /**
     * Busca todos os produtos disponíveis para pedido
     * Utilizado para filtrar apenas produtos que podem ser vendidos
     * 
     * @return Lista de produtos disponíveis
     */
    List<Produto> findByDisponivelTrue();
    
    /**
     * Busca produtos por categoria específica
     * Ex: "Pizza", "Hambúrguer", "Bebida"
     * 
     * @param categoria Categoria do produto
     * @return Lista de produtos da categoria especificada
     */
    List<Produto> findByCategoria(String categoria);
    
    /**
     * Busca produtos com preço menor ou igual ao valor especificado
     * Permite filtrar produtos por faixa de preço
     * 
     * @param preco Valor máximo do preço
     * @return Lista de produtos dentro da faixa de preço
     */
    List<Produto> findByPrecoLessThanEqual(BigDecimal preco);
    
    /**
     * Busca todos os produtos ativos no sistema
     * Utilizado para relatórios e listagens gerenciais
     * 
     * @return Lista de produtos ativos
     */
    List<Produto> findByAtivoTrue();
    
    /**
     * Busca produtos por nome (busca parcial, case insensitive)
     * Permite pesquisar produtos digitando parte do nome
     * 
     * @param nome Parte do nome a ser buscado
     * @return Lista de produtos que contém o texto no nome
     */
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // ========== CONSULTAS CUSTOMIZADAS COM @QUERY ==========
    
    /**
     * Consulta customizada: Produtos disponíveis de um restaurante específico
     * Combina filtros de restaurante, disponibilidade e status ativo
     * CENÁRIO OBRIGATÓRIO 2 DO ROTEIRO 3
     * 
     * @param restauranteId ID do restaurante
     * @return Lista de produtos disponíveis do restaurante
     */
    @Query("SELECT p FROM Produto p WHERE p.restaurante.id = :restauranteId AND p.disponivel = true AND p.ativo = true")
    List<Produto> findProdutosDisponiveisPorRestaurante(@Param("restauranteId") Long restauranteId);

    /**
     * Consulta customizada: Produtos por categoria ordenados por preço
     * Facilita comparação de preços dentro da mesma categoria
     * 
     * @param categoria Categoria dos produtos
     * @return Lista de produtos ordenados por preço crescente
     */
    @Query("SELECT p FROM Produto p WHERE p.categoria = :categoria AND p.disponivel = true ORDER BY p.preco ASC")
    List<Produto> findProdutosPorCategoriaOrdenadoPorPreco(@Param("categoria") String categoria);

    /**
     * Consulta de relatório: Conta quantos produtos existem por categoria
     * Retorna dados agregados para dashboards e análises de estoque
     * 
     * @return Array de Objects contendo [categoria, quantidade]
     */
    @Query("SELECT p.categoria, COUNT(p) FROM Produto p WHERE p.ativo = true GROUP BY p.categoria")
    List<Object[]> countProdutosPorCategoria();
}