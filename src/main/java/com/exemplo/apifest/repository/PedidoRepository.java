package com.exemplo.apifest.repository;

import com.exemplo.apifest.model.Pedido;
import com.exemplo.apifest.model.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ROTEIRO 3 - REPOSITORY DO PEDIDO
 * 
 * Interface de repositório para a entidade Pedido usando Spring Data JPA.
 * Esta interface fornece operações CRUD e consultas específicas para
 * gerenciar pedidos no sistema de delivery DeliveryTech.
 * 
 * Funcionalidades implementadas conforme Roteiro 3:
 * - Busca pedidos por cliente (histórico do cliente)
 * - Filtro por status do pedido (enum StatusPedido)
 * - Consultas por período de datas
 * - Ranking de pedidos mais recentes
 * - Relatórios de faturamento e estatísticas
 * 
 * @author DeliveryTech Development Team
 * @version 1.0
 * @since Roteiro 3 - Implementação da Camada de Dados
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    // ========== MÉTODOS DERIVADOS DO SPRING DATA JPA ==========
    
    /**
     * Busca todos os pedidos de um cliente específico
     * Utilizado para exibir histórico de pedidos do cliente
     * 
     * @param clienteId ID do cliente
     * @return Lista de pedidos do cliente
     */
    List<Pedido> findByClienteId(Long clienteId);
    
    /**
     * Busca pedidos por status específico
     * Ex: PENDENTE, CONFIRMADO, ENTREGUE, etc.
     * 
     * @param status Status do pedido (enum StatusPedido)
     * @return Lista de pedidos com o status especificado
     */
    List<Pedido> findByStatus(StatusPedido status);
    
    /**
     * Busca os 10 pedidos mais recentes
     * CENÁRIO OBRIGATÓRIO 3 DO ROTEIRO 3
     * 
     * @return Lista dos 10 pedidos mais recentes ordenados por data
     */
    List<Pedido> findTop10ByOrderByDataPedidoDesc();
    
    /**
     * Busca pedidos em um período específico
     * Utilizado para relatórios de vendas por período
     * 
     * @param inicio Data/hora de início do período
     * @param fim Data/hora de fim do período
     * @return Lista de pedidos no período especificado
     */
    List<Pedido> findByDataPedidoBetween(LocalDateTime inicio, LocalDateTime fim);
    
    /**
     * Busca todos os pedidos ativos no sistema
     * Utilizado para relatórios e listagens gerenciais
     * 
     * @return Lista de pedidos ativos
     */
    List<Pedido> findByAtivoTrue();

    // ========== CONSULTAS CUSTOMIZADAS COM @QUERY ==========
    
    /**
     * Consulta customizada: Pedidos com valor acima do especificado
     * Ordena por valor decrescente para destacar pedidos de maior valor
     * 
     * @param valor Valor mínimo do pedido
     * @return Lista de pedidos ordenados por valor decrescente
     */
    @Query("SELECT p FROM Pedido p WHERE p.valor > :valor AND p.ativo = true ORDER BY p.valor DESC")
    List<Pedido> findPedidosComValorAcimaDe(@Param("valor") Double valor);

    /**
     * Consulta customizada: Pedidos por período e status específicos
     * Combina filtros de data e status em uma consulta otimizada
     * 
     * @param inicio Data/hora de início do período
     * @param fim Data/hora de fim do período
     * @param status Status do pedido
     * @return Lista de pedidos no período com o status especificado
     */
    @Query("SELECT p FROM Pedido p WHERE p.dataPedido BETWEEN :inicio AND :fim AND p.status = :status")
    List<Pedido> findPedidosPorPeriodoEStatus(@Param("inicio") LocalDateTime inicio, 
                                              @Param("fim") LocalDateTime fim, 
                                              @Param("status") StatusPedido status);

    /**
     * Consulta de relatório: Total de pedidos entregues e faturamento
     * Retorna dados agregados para dashboards financeiros
     * 
     * @return Array contendo [quantidade_pedidos, faturamento_total]
     */
    @Query("SELECT COUNT(p), SUM(p.valor) FROM Pedido p WHERE p.status = 'ENTREGUE' AND p.ativo = true")
    Object[] getTotalPedidosEntreguesEFaturamento();

    /**
     * Consulta de relatório: Ranking de clientes por número de pedidos
     * Identifica clientes mais frequentes para programas de fidelidade
     * 
     * @return Array de Objects contendo [nome_cliente, quantidade_pedidos]
     */
    @Query("SELECT c.nome, COUNT(p) FROM Pedido p JOIN p.cliente c WHERE p.ativo = true GROUP BY c.id, c.nome ORDER BY COUNT(p) DESC")
    List<Object[]> rankingClientesPorNumeroPedidos();
}