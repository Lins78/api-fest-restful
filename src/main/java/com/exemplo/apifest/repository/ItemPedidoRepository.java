package com.exemplo.apifest.repository;

import com.exemplo.apifest.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * ROTEIRO 3 - REPOSITORY DO ITEM PEDIDO
 * 
 * Interface de repositório para a entidade ItemPedido usando Spring Data JPA.
 * Esta interface gerencia a relação many-to-many entre pedidos e produtos,
 * controlando quantidades, preços e relatórios de vendas no sistema DeliveryTech.
 * 
 * Funcionalidades implementadas conforme Roteiro 3:
 * - Busca itens por pedido (detalhamento do pedido)
 * - Busca itens por produto (histórico de vendas do produto)
 * - Relatórios de produtos mais vendidos
 * - Análise de faturamento por restaurante e categoria
 * - Estatísticas agregadas para dashboards
 * 
 * @author DeliveryTech Development Team
 * @version 1.0
 * @since Roteiro 3 - Implementação da Camada de Dados
 */
@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    
    // ========== MÉTODOS DERIVADOS DO SPRING DATA JPA ==========
    
    /**
     * Busca todos os itens de um pedido específico
     * Utilizado para exibir detalhamento completo do pedido
     * 
     * @param pedidoId ID do pedido
     * @return Lista de itens do pedido
     */
    List<ItemPedido> findByPedidoId(Long pedidoId);
    
    /**
     * Busca todos os itens que referenciam um produto específico
     * Utilizado para análise de vendas e histórico do produto
     * 
     * @param produtoId ID do produto
     * @return Lista de itens que contêm o produto
     */
    List<ItemPedido> findByProdutoId(Long produtoId);

    // ========== CONSULTAS CUSTOMIZADAS PARA RELATÓRIOS ==========
    
    /**
     * Relatório: Produtos mais vendidos (por quantidade)
     * Agrupa por produto e soma quantidades apenas de pedidos entregues
     * 
     * @return Array de Objects contendo [nome_produto, total_vendido]
     */
    @Query("SELECT ip.produto.nome, SUM(ip.quantidade) as totalVendido " +
           "FROM ItemPedido ip " +
           "WHERE ip.pedido.status = 'ENTREGUE' " +
           "GROUP BY ip.produto.id, ip.produto.nome " +
           "ORDER BY totalVendido DESC")
    List<Object[]> produtosMaisVendidos();

    /**
     * Relatório: Faturamento total de um restaurante específico
     * Calcula soma dos valores de itens entregues de um restaurante
     * 
     * @param restauranteId ID do restaurante
     * @return Valor total do faturamento do restaurante
     */
    @Query("SELECT SUM(ip.precoTotal) FROM ItemPedido ip " +
           "WHERE ip.pedido.status = 'ENTREGUE' " +
           "AND ip.produto.restaurante.id = :restauranteId")
    Double faturamentoPorRestaurante(@Param("restauranteId") Long restauranteId);

    /**
     * Relatório: Faturamento por categoria de produto
     * Agrupa vendas por categoria e calcula faturamento total
     * 
     * @return Array de Objects contendo [categoria, faturamento_total]
     */
    @Query("SELECT ip.produto.categoria, SUM(ip.precoTotal) " +
           "FROM ItemPedido ip " +
           "WHERE ip.pedido.status = 'ENTREGUE' " +
           "GROUP BY ip.produto.categoria " +
           "ORDER BY SUM(ip.precoTotal) DESC")
    List<Object[]> faturamentoPorCategoria();
}