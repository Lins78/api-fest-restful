package com.exemplo.apifest.service;

import com.exemplo.apifest.dto.ItemPedidoDTO;
import com.exemplo.apifest.dto.PedidoDTO;
import com.exemplo.apifest.dto.response.PedidoResponseDTO;
import com.exemplo.apifest.dto.response.PedidoResumoDTO;
import com.exemplo.apifest.model.StatusPedido;
import java.math.BigDecimal;
import java.util.List;

/**
 * ===============================================================================
 * ROTEIRO 4 - INTERFACE DO SERVIÇO DE PEDIDOS (MAIS COMPLEXA)
 * ===============================================================================
 * 
 * Esta interface define o contrato para o serviço de pedidos, contendo todas as
 * operações de negócio mais complexas relacionadas ao gerenciamento de pedidos.
 * 
 * Esta é a interface mais crítica do sistema, pois coordena operações transacionais
 * complexas envolvendo múltiplas entidades (Cliente, Restaurante, Produto, Pedido).
 * 
 * Responsabilidades:
 * - Coordenar transações complexas de criação de pedidos
 * - Validar regras de negócio entre múltiplas entidades
 * - Calcular totais com precisão decimal
 * - Controlar transições de status de pedidos
 * - Garantir integridade transacional em operações críticas
 * 
 * @author DeliveryTech Development Team
 * @version 1.0 - Roteiro 4
 * @since Java 21 LTS + Spring Boot 3.4.0
 * ===============================================================================
 */
public interface PedidoService {

    /**
     * Cria um novo pedido com transação complexa e validações rigorosas.
     * 
     * FLUXO TRANSACIONAL COMPLETO:
     * 1. Validar Cliente (existe, ativo, endereço válido)
     * 2. Validar Restaurante (existe, ativo, entrega no endereço)
     * 3. Validar Produtos (existem, disponíveis, pertencem ao restaurante)
     * 4. Calcular Total (itens + taxa de entrega - desconto)
     * 5. Salvar Pedido com status PENDENTE
     * 6. Salvar Itens do Pedido
     * 7. Atualizar Estoque (se aplicável)
     * 8. Registrar Log de Auditoria
     * 
     * IMPORTANTE: Se qualquer etapa falhar, toda a transação é revertida!
     * 
     * @param dto Dados completos do pedido com itens
     * @return PedidoResponseDTO Pedido criado com total calculado
     * @throws EntityNotFoundException Se cliente, restaurante ou produto não existir
     * @throws BusinessException Para violações de regras de negócio
     * @throws TransactionException Para problemas transacionais
     */
    PedidoResponseDTO criarPedido(PedidoDTO dto);

    /**
     * Busca um pedido completo por ID (com todos os itens).
     * 
     * @param id ID do pedido
     * @return PedidoResponseDTO Pedido completo com itens e totais
     * @throws EntityNotFoundException Se pedido não existir
     */
    PedidoResponseDTO buscarPedidoPorId(Long id);

    /**
     * Busca histórico de pedidos de um cliente.
     * 
     * @param clienteId ID do cliente
     * @return List<PedidoResumoDTO> Lista de pedidos do cliente (resumidos)
     * @throws EntityNotFoundException Se cliente não existir
     */
    List<PedidoResumoDTO> buscarPedidosPorCliente(Long clienteId);

    /**
     * Atualiza status do pedido com validação de transições permitidas.
     * 
     * TRANSIÇÕES VÁLIDAS:
     * - PENDENTE → CONFIRMADO
     * - CONFIRMADO → PREPARANDO
     * - PREPARANDO → PRONTO
     * - PRONTO → ENTREGUE
     * - PENDENTE/CONFIRMADO → CANCELADO
     * 
     * TRANSIÇÕES INVÁLIDAS:
     * - ENTREGUE → qualquer outro
     * - CANCELADO → qualquer outro
     * - Pular etapas (ex: PENDENTE → PRONTO)
     * 
     * @param id ID do pedido
     * @param novoStatus Novo status do pedido
     * @return PedidoResponseDTO Pedido com status atualizado
     * @throws EntityNotFoundException Se pedido não existir
     * @throws BusinessException Se transição não for permitida
     */
    PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedido novoStatus);

    /**
     * Calcula o total de um pedido com precisão decimal.
     * 
     * ALGORITMO DE CÁLCULO:
     * 1. Subtotal = Σ (quantidade × preço unitário) para cada item
     * 2. Taxa de entrega = calculada baseada no CEP
     * 3. Desconto = aplicado conforme regras promocionais
     * 4. Total = Subtotal + Taxa de entrega - Desconto
     * 
     * @param itens Lista de itens do pedido
     * @return BigDecimal Total calculado com precisão
     * @throws BusinessException Se algum produto não estiver disponível
     */
    BigDecimal calcularTotalPedido(List<ItemPedidoDTO> itens);

    /**
     * Cancela um pedido (apenas se permitido pelo status atual).
     * 
     * REGRAS DE CANCELAMENTO:
     * - Apenas pedidos PENDENTE ou CONFIRMADO podem ser cancelados
     * - Pedidos PREPARANDO, PRONTO ou ENTREGUE não podem ser cancelados
     * - Pedidos já CANCELADOS não podem ser cancelados novamente
     * - Cliente pode cancelar até 15 minutos após confirmação
     * 
     * @param id ID do pedido a ser cancelado
     * @return PedidoResponseDTO Pedido cancelado
     * @throws EntityNotFoundException Se pedido não existir
     * @throws BusinessException Se cancelamento não for permitido
     */
    PedidoResponseDTO cancelarPedido(Long id);
}