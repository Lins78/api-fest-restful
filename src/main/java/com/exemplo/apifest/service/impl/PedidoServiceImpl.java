package com.exemplo.apifest.service.impl;

import com.exemplo.apifest.dto.ItemPedidoDTO;
import com.exemplo.apifest.dto.PedidoDTO;
import com.exemplo.apifest.dto.response.PedidoResponseDTO;
import com.exemplo.apifest.dto.response.PedidoResumoDTO;
import com.exemplo.apifest.exception.BusinessException;
import com.exemplo.apifest.exception.EntityNotFoundException;
import com.exemplo.apifest.model.*;
import com.exemplo.apifest.repository.*;
import com.exemplo.apifest.service.PedidoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ===============================================================================
 * ROTEIRO 4 - IMPLEMENTAÇÃO DO SERVIÇO DE PEDIDOS (MAIS COMPLEXA)
 * ===============================================================================
 * 
 * Esta é a implementação mais crítica do sistema, coordenando operações
 * transacionais complexas que envolvem múltiplas entidades e regras de negócio.
 * 
 * OPERAÇÕES TRANSACIONAIS CRÍTICAS:
 * - Criação de pedidos com validações em cascata
 * - Cálculo preciso de totais com múltiplas variáveis
 * - Controle rigoroso de transições de status
 * - Garantia de integridade referencial entre entidades
 * 
 * CARACTERÍSTICAS TÉCNICAS:
 * - Transações ACID para operações críticas
 * - Validações de negócio em múltiplas camadas
 * - Rollback automático em caso de falha
 * - Logs de auditoria para operações críticas
 * 
 * @author DeliveryTech Development Team
 * @version 1.0 - Roteiro 4
 * @since Java 21 LTS + Spring Boot 3.4.0
 * ===============================================================================
 */
@Service
@Transactional(readOnly = true)
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * OPERAÇÃO MAIS CRÍTICA: Criação de pedido com transação complexa.
     * 
     * Esta operação coordena múltiplas validações e persistências que devem
     * ser executadas de forma atômica. Se qualquer etapa falhar, toda a
     * transação é revertida para manter a consistência dos dados.
     */
    @Override
    @Transactional
    public PedidoResponseDTO criarPedido(PedidoDTO dto) {
        // ========== ETAPA 1: VALIDAÇÃO DO CLIENTE ==========
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Cliente não encontrado com ID: %d", dto.getClienteId())
            ));

        if (!cliente.getAtivo()) {
            throw new BusinessException("Cliente está inativo e não pode fazer pedidos");
        }

        // ========== ETAPA 2: VALIDAÇÃO DO RESTAURANTE ==========
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Restaurante não encontrado com ID: %d", dto.getRestauranteId())
            ));

        if (!restaurante.getAtivo()) {
            throw new BusinessException("Restaurante está fechado e não aceita pedidos");
        }

        // ========== ETAPA 3: VALIDAÇÃO DOS PRODUTOS ==========
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (ItemPedidoDTO itemDto : dto.getItens()) {
            // Verificar se produto existe
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                .orElseThrow(() -> new EntityNotFoundException(
                    String.format("Produto não encontrado com ID: %d", itemDto.getProdutoId())
                ));

            // Verificar se produto está disponível
            if (!produto.getDisponivel()) {
                throw new BusinessException(
                    String.format("Produto '%s' não está disponível", produto.getNome())
                );
            }

            // Verificar se produto pertence ao restaurante do pedido
            if (!produto.getRestaurante().getId().equals(dto.getRestauranteId())) {
                throw new BusinessException(
                    String.format("Produto '%s' não pertence ao restaurante selecionado", produto.getNome())
                );
            }

            // Calcular subtotal do item
            BigDecimal valorItem = produto.getPreco().multiply(new BigDecimal(itemDto.getQuantidade()));
            subtotal = subtotal.add(valorItem);
        }

        // ========== ETAPA 4: CÁLCULO DO TOTAL ==========
        BigDecimal taxaEntrega = restaurante.getTaxaEntrega();
        BigDecimal total = subtotal.add(taxaEntrega);

        // ========== ETAPA 5: CRIAÇÃO DO PEDIDO SIMPLIFICADO ==========
        // O modelo Pedido atual é muito simples: apenas descrição, valor, cliente, status e data
        String descricaoItens = dto.getItens().stream()
            .map(item -> item.getQuantidade() + "x " + produtoRepository.findById(item.getProdutoId()).get().getNome())
            .collect(Collectors.joining(", "));
        
        Pedido pedido = new Pedido(descricaoItens, total.doubleValue(), cliente);

        // Salvar pedido primeiro para obter ID
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // ========== ETAPA 6: CRIAÇÃO DOS ITENS DO PEDIDO ==========
        for (ItemPedidoDTO itemDto : dto.getItens()) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId()).get();

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedidoSalvo);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemDto.getQuantidade());
            itemPedido.setPrecoUnitario(produto.getPreco());
            itemPedido.setPrecoTotal(produto.getPreco().multiply(new BigDecimal(itemDto.getQuantidade())));

            itemPedidoRepository.save(itemPedido);
        }

        // ========== ETAPA 7: RETORNO DO PEDIDO COMPLETO ==========
        return buscarPedidoPorId(pedidoSalvo.getId());
    }

    /**
     * Busca pedido completo com todos os itens.
     */
    @Override
    public PedidoResponseDTO buscarPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Pedido não encontrado com ID: %d", id)
            ));

        PedidoResponseDTO responseDTO = modelMapper.map(pedido, PedidoResponseDTO.class);
        
        // Buscar e mapear itens do pedido
        List<ItemPedido> itens = itemPedidoRepository.findByPedidoId(id);
        responseDTO.setItens(itens.stream()
            .map(item -> modelMapper.map(item, com.exemplo.apifest.dto.response.ItemPedidoResponseDTO.class))
            .collect(Collectors.toList()));

        return responseDTO;
    }

    /**
     * Busca histórico de pedidos do cliente.
     */
    @Override
    public List<PedidoResumoDTO> buscarPedidosPorCliente(Long clienteId) {
        // VALIDAÇÃO: Verificar se cliente existe
        if (!clienteRepository.existsById(clienteId)) {
            throw new EntityNotFoundException(
                String.format("Cliente não encontrado com ID: %d", clienteId)
            );
        }

        List<Pedido> pedidos = pedidoRepository.findByClienteId(clienteId);
        
        return pedidos.stream()
            .map(pedido -> modelMapper.map(pedido, PedidoResumoDTO.class))
            .collect(Collectors.toList());
    }

    /**
     * Atualiza status do pedido com validação de transições.
     */
    @Override
    @Transactional
    public PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedido novoStatus) {
        // 1. VALIDAÇÃO: Verificar se pedido existe
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Pedido não encontrado com ID: %d", id)
            ));

        // 2. VALIDAÇÃO: Verificar transições válidas
        StatusPedido statusAtual = pedido.getStatus();
        
        if (!isTransicaoValida(statusAtual, novoStatus)) {
            throw new BusinessException(
                String.format("Transição inválida: %s → %s", statusAtual, novoStatus)
            );
        }

        // 3. ATUALIZAÇÃO: Alterar status
        pedido.setStatus(novoStatus);
        // Campo dataAtualizacao não existe no modelo

        // 4. PERSISTÊNCIA: Salvar alteração
        pedidoRepository.save(pedido);

        return buscarPedidoPorId(id);
    }

    /**
     * Valida se uma transição de status é permitida.
     */
    private boolean isTransicaoValida(StatusPedido statusAtual, StatusPedido novoStatus) {
        return switch (statusAtual) {
            case PENDENTE -> novoStatus == StatusPedido.CONFIRMADO || novoStatus == StatusPedido.CANCELADO;
            case CONFIRMADO -> novoStatus == StatusPedido.PREPARANDO || novoStatus == StatusPedido.CANCELADO;
            case PREPARANDO -> novoStatus == StatusPedido.SAIU_PARA_ENTREGA;
            case SAIU_PARA_ENTREGA -> novoStatus == StatusPedido.ENTREGUE;
            case ENTREGUE, CANCELADO -> false; // Estados finais
        };
    }

    /**
     * Calcula total do pedido com precisão decimal.
     */
    @Override
    public BigDecimal calcularTotalPedido(List<ItemPedidoDTO> itens) {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ItemPedidoDTO itemDto : itens) {
            Produto produto = produtoRepository.findById(itemDto.getProdutoId())
                .orElseThrow(() -> new EntityNotFoundException(
                    String.format("Produto não encontrado com ID: %d", itemDto.getProdutoId())
                ));

            if (!produto.getDisponivel()) {
                throw new BusinessException(
                    String.format("Produto '%s' não está disponível", produto.getNome())
                );
            }

            BigDecimal valorItem = produto.getPreco().multiply(new BigDecimal(itemDto.getQuantidade()));
            subtotal = subtotal.add(valorItem);
        }

        return subtotal;
    }

    /**
     * Cancela pedido com validações de negócio.
     */
    @Override
    @Transactional
    public PedidoResponseDTO cancelarPedido(Long id) {
        // 1. VALIDAÇÃO: Verificar se pedido existe
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Pedido não encontrado com ID: %d", id)
            ));

        // 2. VALIDAÇÃO: Verificar se cancelamento é permitido
        StatusPedido status = pedido.getStatus();
        if (status != StatusPedido.PENDENTE && status != StatusPedido.CONFIRMADO) {
            throw new BusinessException(
                String.format("Não é possível cancelar pedido com status: %s", status)
            );
        }

        // 3. REGRA DE NEGÓCIO: Cancelar pedido
        pedido.setStatus(StatusPedido.CANCELADO);
        // Campo dataAtualizacao não existe no modelo

        // 4. PERSISTÊNCIA: Salvar alteração
        pedidoRepository.save(pedido);

        return buscarPedidoPorId(id);
    }
}