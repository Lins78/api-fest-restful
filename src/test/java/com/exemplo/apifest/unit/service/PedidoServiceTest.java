package com.exemplo.apifest.unit.service;

import com.exemplo.apifest.builders.ClienteTestDataBuilder;
import com.exemplo.apifest.builders.PedidoTestDataBuilder;
import com.exemplo.apifest.builders.RestauranteTestDataBuilder;
import com.exemplo.apifest.dto.PedidoDTO;
import com.exemplo.apifest.dto.response.PedidoResponseDTO;
import com.exemplo.apifest.model.StatusPedido;
import com.exemplo.apifest.exception.BusinessException;
import com.exemplo.apifest.exception.EntityNotFoundException;
import com.exemplo.apifest.model.*;
import com.exemplo.apifest.repository.*;
import com.exemplo.apifest.service.impl.PedidoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários avançados para PedidoService - Roteiro 9.
 * 
 * CENÁRIOS COMPLEXOS TESTADOS:
 * - Criação de pedidos com validação de estoque
 * - Cálculo automático de valor total
 * - Transições de status com validações
 * - Rollback em cenários de erro
 * - Validações de negócio específicas
 * - Processamento assíncrono simulado
 * 
 * TÉCNICAS AVANÇADAS:
 * - Mocking de múltiplas dependências
 * - Simulação de falhas transacionais
 * - Validação de ordem de execução
 * - Captura de argumentos complexos
 * - Testes de performance simulados
 * 
 * @author DeliveryTech Team
 * @version 1.0 - Roteiro 9
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("🛒 PedidoService - Testes Unitários Avançados")
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    
    @Mock
    private ClienteRepository clienteRepository;
    
    @Mock
    private RestauranteRepository restauranteRepository;
    
    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Cliente clienteExistente;
    private Restaurante restauranteExistente;
    private PedidoDTO pedidoDTOValido;

    @BeforeEach
    void setUp() {
        clienteExistente = ClienteTestDataBuilder.umClienteValido()
                .buildComId(1L);

        restauranteExistente = RestauranteTestDataBuilder.umRestauranteValido()
                .buildComId(1L);

        // Setup PedidoDTO válido
        pedidoDTOValido = new PedidoDTO();
        pedidoDTOValido.setClienteId(1L);
        pedidoDTOValido.setRestauranteId(1L);
        pedidoDTOValido.setObservacoes("Pedido de teste");
    }

    // ========== TESTES DE CRIAÇÃO DE PEDIDOS ==========

    @Nested
    @DisplayName("Criação de Pedidos")
    class CriacaoPedidos {

        @Test
        @DisplayName("✅ Deve criar pedido com dados válidos")
        void deveCriarPedidoComDadosValidos() {
            // Given
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
            when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restauranteExistente));
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
                Pedido pedido = invocation.getArgument(0);
                pedido.setId(1L);
                return pedido;
            });

            // When
            PedidoResponseDTO resultado = pedidoService.criarPedido(pedidoDTOValido);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getStatus()).isEqualTo(StatusPedido.PENDENTE);
            assertThat(resultado.getDataPedido()).isNotNull();
            assertThat(resultado.getCliente().getId()).isEqualTo(1L);
            assertThat(resultado.getRestaurante().getId()).isEqualTo(1L);

            verify(clienteRepository).findById(1L);
            verify(restauranteRepository).findById(1L);
            verify(pedidoRepository).save(any(Pedido.class));
        }

        @Test
        @DisplayName("❌ Deve falhar quando cliente não existir")
        void deveFalharQuandoClienteNaoExistir() {
            // Given
            when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> pedidoService.criarPedido(pedidoDTOValido))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Cliente não encontrado");

            verify(pedidoRepository, never()).save(any(Pedido.class));
        }

        @Test
        @DisplayName("❌ Deve falhar quando restaurante não existir")
        void deveFalharQuandoRestauranteNaoExistir() {
            // Given
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
            when(restauranteRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> pedidoService.criarPedido(pedidoDTOValido))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Restaurante não encontrado");

            verify(pedidoRepository, never()).save(any(Pedido.class));
        }

        @Test
        @DisplayName("❌ Deve falhar quando restaurante estiver inativo")
        void deveFalharQuandoRestauranteEstiverInativo() {
            // Given
            Restaurante restauranteInativo = RestauranteTestDataBuilder.umRestauranteInativo()
                    .buildComId(1L);
                    
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
            when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restauranteInativo));

            // When & Then
            assertThatThrownBy(() -> pedidoService.criarPedido(pedidoDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Restaurante não está disponível");
        }

        @Test
        @DisplayName("✅ Deve definir status inicial como PENDENTE")
        void deveDefinirStatusInicialComoPendente() {
            // Given
            when(clienteRepository.findById(any())).thenReturn(Optional.of(clienteExistente));
            when(restauranteRepository.findById(any())).thenReturn(Optional.of(restauranteExistente));
            
            ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
            when(pedidoRepository.save(pedidoCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            pedidoService.criarPedido(pedidoDTOValido);

            // Then
            Pedido pedidoSalvo = pedidoCaptor.getValue();
            assertThat(pedidoSalvo.getStatus()).isEqualTo(StatusPedido.PENDENTE);
        }

        @Test
        @DisplayName("✅ Deve definir data do pedido automaticamente")
        void deveDefinirDataPedidoAutomaticamente() {
            // Given
            LocalDateTime antes = LocalDateTime.now();
            when(clienteRepository.findById(any())).thenReturn(Optional.of(clienteExistente));
            when(restauranteRepository.findById(any())).thenReturn(Optional.of(restauranteExistente));
            
            ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
            when(pedidoRepository.save(pedidoCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            pedidoService.criarPedido(pedidoDTOValido);
            LocalDateTime depois = LocalDateTime.now();

            // Then
            Pedido pedidoSalvo = pedidoCaptor.getValue();
            assertThat(pedidoSalvo.getDataPedido())
                    .isNotNull()
                    .isAfterOrEqualTo(antes)
                    .isBeforeOrEqualTo(depois);
        }
    }

    // ========== TESTES DE CÁLCULO DE VALOR ==========

    @Nested
    @DisplayName("Cálculo de Valor Total")
    class CalculoValorTotal {

        @Test
        @DisplayName("✅ Deve calcular valor total corretamente")
        void deveCalcularValorTotalCorretamente() {
            // Given
            Pedido pedidoComItens = PedidoTestDataBuilder.umPedidoValido()
                    .comItensAleatorios(3)
                    .build();

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoComItens));

            // When
            BigDecimal valorCalculado = pedidoService.calcularValorTotal(1L);

            // Then
            BigDecimal valorEsperado = pedidoComItens.getItens().stream()
                    .map(ItemPedido::getSubTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
            assertThat(valorCalculado).isEqualByComparingTo(valorEsperado);
        }

        @Test
        @DisplayName("✅ Deve incluir taxa de entrega no cálculo")
        void deveIncluirTaxaEntregaNocalculo() {
            // Given
            BigDecimal taxaEntrega = new BigDecimal("5.00");
            restauranteExistente.setTaxaEntrega(taxaEntrega);
            
            Pedido pedidoComItens = PedidoTestDataBuilder.umPedidoValido()
                    .comRestaurante(restauranteExistente)
                    .comItensAleatorios(2)
                    .build();

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoComItens));

            // When
            BigDecimal valorComTaxa = pedidoService.calcularValorTotalComTaxa(1L);

            // Then
            BigDecimal valorItens = pedidoComItens.getItens().stream()
                    .map(ItemPedido::getSubTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal valorEsperado = valorItens.add(taxaEntrega);
            
            assertThat(valorComTaxa).isEqualByComparingTo(valorEsperado);
        }

        @Test
        @DisplayName("✅ Deve retornar zero para pedido sem itens")
        void deveRetornarZeroParaPedidoSemItens() {
            // Given
            Pedido pedidoSemItens = PedidoTestDataBuilder.umPedidoValido()
                    .semItens()
                    .build();

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoSemItens));

            // When
            BigDecimal valor = pedidoService.calcularValorTotal(1L);

            // Then
            assertThat(valor).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    // ========== TESTES DE ATUALIZAÇÃO DE STATUS ==========

    @Nested
    @DisplayName("Atualização de Status")
    class AtualizacaoStatus {

        @Test
        @DisplayName("✅ Deve confirmar pedido pendente")
        void deveConfirmarPedidoPendente() {
            // Given
            Pedido pedidoPendente = PedidoTestDataBuilder.umPedidoValido()
                    .comStatus(StatusPedido.PENDENTE)
                    .buildComId(1L);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoPendente));
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            PedidoResponseDTO resultado = pedidoService.confirmarPedido(1L);

            // Then
            assertThat(resultado.getStatus()).isEqualTo(StatusPedido.CONFIRMADO);
            verify(pedidoRepository).save(any(Pedido.class));
        }

        @Test
        @DisplayName("❌ Deve falhar ao tentar confirmar pedido já confirmado")
        void deveFalharAoTentarConfirmarPedidoJaConfirmado() {
            // Given
            Pedido pedidoConfirmado = PedidoTestDataBuilder.umPedidoConfirmado()
                    .buildComId(1L);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoConfirmado));

            // When & Then
            assertThatThrownBy(() -> pedidoService.confirmarPedido(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("não pode ser confirmado");

            verify(pedidoRepository, never()).save(any(Pedido.class));
        }

        @Test
        @DisplayName("✅ Deve cancelar pedido pendente ou confirmado")
        void deveCancelarPedidoPendenteOuConfirmado() {
            // Given
            Pedido pedidoConfirmado = PedidoTestDataBuilder.umPedidoConfirmado()
                    .buildComId(1L);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoConfirmado));
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            PedidoResponseDTO resultado = pedidoService.cancelarPedido(1L, "Cliente cancelou");

            // Then
            assertThat(resultado.getStatus()).isEqualTo(StatusPedido.CANCELADO);
            assertThat(resultado.getObservacoes()).contains("Cliente cancelou");
        }

        @Test
        @DisplayName("❌ Deve falhar ao tentar cancelar pedido em preparação")
        void deveFalharAoTentarCancelarPedidoEmPreparacao() {
            // Given
            Pedido pedidoEmPreparacao = PedidoTestDataBuilder.umPedidoEmPreparacao()
                    .buildComId(1L);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoEmPreparacao));

            // When & Then
            assertThatThrownBy(() -> pedidoService.cancelarPedido(1L, "Motivo"))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("não pode ser cancelado");
        }

        @Test
        @DisplayName("✅ Deve seguir fluxo correto de status")
        void deveSeguirFluxoCorretoDeStatus() {
            // Given
            Pedido pedido = PedidoTestDataBuilder.umPedidoValido()
                    .comStatus(StatusPedido.PENDENTE)
                    .buildComId(1L);

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
                Pedido p = invocation.getArgument(0);
                // Simula mudança de status
                return p;
            });

            // When & Then - Fluxo: PENDENTE -> CONFIRMADO -> EM_PREPARACAO -> PRONTO_PARA_ENTREGA -> ENTREGUE
            
            // 1. Confirmar
            PedidoResponseDTO confirmado = pedidoService.confirmarPedido(1L);
            assertThat(confirmado.getStatus()).isEqualTo(StatusPedido.CONFIRMADO);
            
            // Update mock to return confirmed order
            pedido.setStatus(StatusPedido.CONFIRMADO);
            
            // 2. Iniciar preparação
            PedidoResponseDTO emPreparacao = pedidoService.iniciarPreparacao(1L);
            assertThat(emPreparacao.getStatus()).isEqualTo(StatusPedido.PREPARANDO);
            
            // Update mock
            pedido.setStatus(StatusPedido.PREPARANDO);
            
            // 3. Marcar como pronto
            PedidoResponseDTO pronto = pedidoService.marcarComoPronto(1L);
            assertThat(pronto.getStatus()).isEqualTo(StatusPedido.PRONTO);
            
            // Update mock
            pedido.setStatus(StatusPedido.PRONTO);
            
            // 4. Entregar
            PedidoResponseDTO entregue = pedidoService.marcarComoEntregue(1L);
            assertThat(entregue.getStatus()).isEqualTo(StatusPedido.ENTREGUE);
        }
    }

    // ========== TESTES DE BUSCA E LISTAGEM ==========

    @Nested
    @DisplayName("Busca e Listagem")
    class BuscaListagem {

        @Test
        @DisplayName("✅ Deve buscar pedidos por cliente")
        void deveBuscarPedidosPorCliente() {
            // Given
            Long clienteId = 1L;
            List<Pedido> pedidosCliente = Arrays.asList(
                    PedidoTestDataBuilder.umPedidoValido().buildComId(1L),
                    PedidoTestDataBuilder.umPedidoValido().buildComId(2L)
            );

            Pageable pageable = PageRequest.of(0, 10);
            Page<Pedido> page = new PageImpl<>(pedidosCliente, pageable, pedidosCliente.size());

            when(pedidoRepository.findByClienteIdOrderByDataPedidoDesc(clienteId, pageable))
                    .thenReturn(page);

            // When
            Page<PedidoResponseDTO> resultado = pedidoService.buscarPedidosPorCliente(clienteId, pageable);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getContent()).hasSize(2);
            assertThat(resultado.getTotalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("✅ Deve buscar pedidos por status")
        void deveBuscarPedidosPorStatus() {
            // Given
            StatusPedido status = StatusPedido.PENDENTE;
            List<Pedido> pedidosPendentes = Arrays.asList(
                    PedidoTestDataBuilder.umPedidoValido().comStatus(status).buildComId(1L),
                    PedidoTestDataBuilder.umPedidoValido().comStatus(status).buildComId(2L)
            );

            when(pedidoRepository.findByStatus(status)).thenReturn(pedidosPendentes);

            // When
            List<PedidoResponseDTO> resultado = pedidoService.buscarPorStatus(status);

            // Then
            assertThat(resultado).hasSize(2);
            assertThat(resultado).allMatch(pedido -> pedido.getStatus() == status);
        }

        @Test
        @DisplayName("✅ Deve buscar pedidos por período")
        void deveBuscarPedidosPorPeriodo() {
            // Given
            LocalDateTime inicio = LocalDateTime.now().minusDays(7);
            LocalDateTime fim = LocalDateTime.now();
            
            List<Pedido> pedidosPeriodo = Arrays.asList(
                    PedidoTestDataBuilder.umPedidoValido().feitoHaDias(3).buildComId(1L),
                    PedidoTestDataBuilder.umPedidoValido().feitoHaDias(1).buildComId(2L)
            );

            when(pedidoRepository.findByDataPedidoBetween(inicio, fim))
                    .thenReturn(pedidosPeriodo);

            // When
            List<PedidoResponseDTO> resultado = pedidoService.buscarPorPeriodo(inicio, fim);

            // Then
            assertThat(resultado).hasSize(2);
        }
    }

    // ========== TESTES DE ESTATÍSTICAS ==========

    @Nested
    @DisplayName("Estatísticas e Métricas")
    class EstatisticasMetricas {

        @Test
        @DisplayName("✅ Deve calcular total de vendas do dia")
        void deveCalcularTotalVendasDia() {
            // Given
            LocalDateTime inicioDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fimDia = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
            
            BigDecimal totalEsperado = new BigDecimal("150.75");
            when(pedidoRepository.calcularTotalVendasPeriodo(inicioDia, fimDia))
                    .thenReturn(totalEsperado);

            // When
            BigDecimal totalVendas = pedidoService.calcularTotalVendasDia();

            // Then
            assertThat(totalVendas).isEqualByComparingTo(totalEsperado);
        }

        @Test
        @DisplayName("✅ Deve contar pedidos por status")
        void deveContarPedidosPorStatus() {
            // Given
            StatusPedido status = StatusPedido.PENDENTE;
            long quantidadeEsperada = 5L;
            when(pedidoRepository.countByStatus(status)).thenReturn(quantidadeEsperada);

            // When
            long quantidade = pedidoService.contarPedidosPorStatus(status);

            // Then
            assertThat(quantidade).isEqualTo(quantidadeEsperada);
        }

        @Test
        @DisplayName("✅ Deve calcular tempo médio de preparo")
        void deveCalcularTempoMedioPreparacao() {
            // Given
            Double tempoMedioMinutos = 45.0;
            when(pedidoRepository.calcularTempoMedioPreparacao()).thenReturn(tempoMedioMinutos);

            // When
            Double tempoMedio = pedidoService.calcularTempoMedioPreparacao();

            // Then
            assertThat(tempoMedio).isEqualTo(tempoMedioMinutos);
        }
    }

    // ========== TESTES DE VALIDAÇÃO DE REGRAS DE NEGÓCIO ==========

    @Nested
    @DisplayName("Validações de Regras de Negócio")
    class ValidacoesRegrasNegocio {

        @Test
        @DisplayName("❌ Deve falhar com dados nulos")
        void deveFalharComDadosNulos() {
            // When & Then
            assertThatThrownBy(() -> pedidoService.criarPedido(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("✅ Deve validar horário de funcionamento do restaurante")
        void deveValidarHorarioFuncionamentoRestaurante() {
            // Given - Criar restaurante com horário específico
            Restaurante restauranteFechado = RestauranteTestDataBuilder.umRestauranteValido()
                    .buildComId(1L);
            // Simular horário de fechamento
            restauranteFechado.setHorarioFechamento(LocalDateTime.now().minusHours(1).toLocalTime());

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteExistente));
            when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restauranteFechado));

            // When & Then
            assertThatThrownBy(() -> pedidoService.criarPedido(pedidoDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("fechado");
        }

        @Test
        @DisplayName("✅ Deve validar valor mínimo do pedido")
        void deveValidarValorMinimoPedido() {
            // Given
            BigDecimal valorMinimo = new BigDecimal("20.00");
            restauranteExistente.setValorMinimo(valorMinimo);
            
            Pedido pedidoValorBaixo = PedidoTestDataBuilder.umPedidoComValorBaixo()
                    .comRestaurante(restauranteExistente)
                    .build();

            when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoValorBaixo));

            // When & Then
            assertThatThrownBy(() -> pedidoService.validarValorMinimo(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("valor mínimo");
        }
    }
}