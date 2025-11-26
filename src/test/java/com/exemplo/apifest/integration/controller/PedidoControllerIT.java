package com.exemplo.apifest.integration.controller;

import com.exemplo.apifest.builders.ClienteTestDataBuilder;
import com.exemplo.apifest.builders.PedidoTestDataBuilder;
import com.exemplo.apifest.builders.ProdutoTestDataBuilder;
import com.exemplo.apifest.dto.PedidoDTO;
import com.exemplo.apifest.model.Cliente;
import com.exemplo.apifest.model.Pedido;
import com.exemplo.apifest.model.Produto;
import com.exemplo.apifest.model.StatusPedido;
import com.exemplo.apifest.repository.ClienteRepository;
import com.exemplo.apifest.repository.PedidoRepository;
import com.exemplo.apifest.repository.ProdutoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de Integração para PedidoController - Roteiro 9.
 * 
 * CENÁRIOS DE INTEGRAÇÃO TESTADOS:
 * - CRUD completo de pedidos via REST API
 * - Validações de regras de negócio em cenários reais
 * - Relacionamentos entre Cliente, Pedido e Produto
 * - Cálculos de totais e subtotais
 * - Gerenciamento de status de pedidos
 * - Persistência com PostgreSQL via TestContainers
 * - Transações e rollback automático
 * 
 * REGRAS DE NEGÓCIO TESTADAS:
 * - Cliente deve existir para criar pedido
 * - Produtos devem existir e ter estoque
 * - Cálculo automático de valores
 * - Mudanças de status válidas
 * - Validações de dados obrigatórios
 * 
 * @author DeliveryTech Team
 * @version 1.0 - Roteiro 9
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test-advanced")
@Testcontainers
@Transactional
@DisplayName("🛒 PedidoController - Testes de Integração")
class PedidoControllerIT {

    @Container
    @SuppressWarnings("resource") // Container é gerenciado pelo TestContainers framework
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    private Cliente clienteExistente;
    private Produto produtoExistente1;
    private Produto produtoExistente2;
    private Pedido pedidoExistente;
    private PedidoDTO pedidoDTOValido;

    @BeforeEach
    void setUp() {
        // Limpar dados antes de cada teste
        pedidoRepository.deleteAll();
        produtoRepository.deleteAll();
        clienteRepository.deleteAll();

        // Criar cliente para usar nos pedidos
        clienteExistente = ClienteTestDataBuilder.umClienteValido()
                .comEmail("cliente.pedidos@email.com")
                .build();
        clienteExistente = clienteRepository.save(clienteExistente);

        // Criar produtos para usar nos pedidos
        produtoExistente1 = ProdutoTestDataBuilder.umProdutoValido()
                .comNome("Pizza Margherita")
                .comPreco(new BigDecimal("35.50"))
                .comEstoque(10)
                .build();
        produtoExistente1 = produtoRepository.save(produtoExistente1);

        produtoExistente2 = ProdutoTestDataBuilder.umProdutoValido()
                .comNome("Refrigerante 2L")
                .comPreco(new BigDecimal("8.00"))
                .comEstoque(20)
                .build();
        produtoExistente2 = produtoRepository.save(produtoExistente2);

        // Criar pedido existente para testes de busca/atualização
        pedidoExistente = PedidoTestDataBuilder.umPedidoValido()
                .comCliente(clienteExistente)
                .comProduto(produtoExistente1, 2)
                .build();
        pedidoExistente = pedidoRepository.save(pedidoExistente);

        // Setup PedidoDTO válido para testes de criação
        pedidoDTOValido = new PedidoDTO();
        pedidoDTOValido.setClienteId(clienteExistente.getId());
        pedidoDTOValido.setObservacoes("Pedido de teste");

        // Adicionar itens do pedido
        PedidoDTO.ItemPedidoDTO item1 = new PedidoDTO.ItemPedidoDTO();
        item1.setProdutoId(produtoExistente1.getId());
        item1.setQuantidade(1);
        item1.setPrecoUnitario(produtoExistente1.getPreco());

        PedidoDTO.ItemPedidoDTO item2 = new PedidoDTO.ItemPedidoDTO();
        item2.setProdutoId(produtoExistente2.getId());
        item2.setQuantidade(2);
        item2.setPrecoUnitario(produtoExistente2.getPreco());

        pedidoDTOValido.setItens(List.of(item1, item2));
    }

    // ========== TESTES DE CRIAÇÃO (POST) ==========

    @Nested
    @DisplayName("POST /api/pedidos - Criação de Pedidos")
    class CriacaoPedidos {

        @Test
        @DisplayName("✅ Deve criar pedido com dados válidos")
        void deveCriarPedidoComDadosValidos() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/pedidos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pedidoDTOValido)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.clienteId").value(clienteExistente.getId()))
                    .andExpect(jsonPath("$.status").value(StatusPedido.PENDENTE.toString()))
                    .andExpect(jsonPath("$.observacoes").value(pedidoDTOValido.getObservacoes()))
                    .andExpect(jsonPath("$.itens", hasSize(2)))
                    .andExpect(jsonPath("$.itens[0].produtoId").value(produtoExistente1.getId()))
                    .andExpect(jsonPath("$.itens[0].quantidade").value(1))
                    .andExpect(jsonPath("$.itens[1].produtoId").value(produtoExistente2.getId()))
                    .andExpect(jsonPath("$.itens[1].quantidade").value(2))
                    .andExpect(jsonPath("$.valorTotal").value(51.50)); // 35.50 + (8.00 * 2)
        }

        @Test
        @DisplayName("✅ Deve calcular valor total automaticamente")
        void deveCalcularValorTotalAutomaticamente() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/pedidos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pedidoDTOValido)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.valorTotal").value(51.50))
                    .andExpect(jsonPath("$.itens[0].subtotal").value(35.50))
                    .andExpect(jsonPath("$.itens[1].subtotal").value(16.00));
        }

        @Test
        @DisplayName("❌ Deve falhar com cliente inexistente")
        void deveFalharComClienteInexistente() throws Exception {
            // Given
            pedidoDTOValido.setClienteId(999L); // Cliente inexistente

            // When & Then
            mockMvc.perform(post("/api/pedidos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pedidoDTOValido)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("Cliente não encontrado")));
        }

        @Test
        @DisplayName("❌ Deve falhar com produto inexistente")
        void deveFalharComProdutoInexistente() throws Exception {
            // Given
            pedidoDTOValido.getItens().get(0).setProdutoId(999L); // Produto inexistente

            // When & Then
            mockMvc.perform(post("/api/pedidos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pedidoDTOValido)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("Produto não encontrado")));
        }

        @Test
        @DisplayName("❌ Deve falhar com quantidade inválida")
        void deveFalharComQuantidadeInvalida() throws Exception {
            // Given
            pedidoDTOValido.getItens().get(0).setQuantidade(0); // Quantidade inválida

            // When & Then
            mockMvc.perform(post("/api/pedidos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pedidoDTOValido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("Quantidade deve ser maior que zero")));
        }

        @Test
        @DisplayName("❌ Deve falhar sem itens no pedido")
        void deveFalharSemItensNoPedido() throws Exception {
            // Given
            pedidoDTOValido.setItens(List.of()); // Lista vazia

            // When & Then
            mockMvc.perform(post("/api/pedidos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pedidoDTOValido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("Pedido deve ter pelo menos um item")));
        }

        @Test
        @DisplayName("❌ Deve falhar com estoque insuficiente")
        void deveFalharComEstoqueInsuficiente() throws Exception {
            // Given
            pedidoDTOValido.getItens().get(0).setQuantidade(50); // Quantidade maior que estoque (10)

            // When & Then
            mockMvc.perform(post("/api/pedidos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pedidoDTOValido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("Estoque insuficiente")));
        }

        @Test
        @DisplayName("✅ Deve criar pedido com observações opcionais")
        void deveCriarPedidoComObservacoesOpcionais() throws Exception {
            // Given
            pedidoDTOValido.setObservacoes(null); // Sem observações

            // When & Then
            mockMvc.perform(post("/api/pedidos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pedidoDTOValido)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.observacoes").isEmpty());
        }
    }

    // ========== TESTES DE BUSCA (GET) ==========

    @Nested
    @DisplayName("GET /api/pedidos - Busca de Pedidos")
    class BuscaPedidos {

        @Test
        @DisplayName("✅ Deve buscar pedido por ID existente")
        void deveBuscarPedidoPorIdExistente() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/pedidos/{id}", pedidoExistente.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(pedidoExistente.getId()))
                    .andExpect(jsonPath("$.clienteId").value(clienteExistente.getId()))
                    .andExpect(jsonPath("$.status").value(pedidoExistente.getStatus().toString()))
                    .andExpect(jsonPath("$.itens").isArray())
                    .andExpect(jsonPath("$.valorTotal").exists());
        }

        @Test
        @DisplayName("❌ Deve falhar ao buscar pedido inexistente")
        void deveFalharAoBuscarPedidoInexistente() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/pedidos/{id}", 999L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("não encontrado")));
        }

        @Test
        @DisplayName("✅ Deve listar todos os pedidos")
        void deveListarTodosPedidos() throws Exception {
            // Given - Criar mais pedidos
            Pedido pedido2 = PedidoTestDataBuilder.umPedidoValido()
                    .comCliente(clienteExistente)
                    .comProduto(produtoExistente2, 1)
                    .build();
            pedidoRepository.save(pedido2);

            // When & Then
            mockMvc.perform(get("/api/pedidos")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.totalElements").value(2));
        }

        @Test
        @DisplayName("✅ Deve buscar pedidos por cliente")
        void deveBuscarPedidosPorCliente() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/pedidos/cliente/{clienteId}", clienteExistente.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].clienteId").value(clienteExistente.getId()));
        }

        @Test
        @DisplayName("✅ Deve buscar pedidos por status")
        void deveBuscarPedidosPorStatus() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/pedidos/status/{status}", StatusPedido.PENDENTE)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                    .andExpect(jsonPath("$.content[0].status").value(StatusPedido.PENDENTE.toString()));
        }

        @Test
        @DisplayName("✅ Deve retornar lista vazia para cliente sem pedidos")
        void deveRetornarListaVaziaParaClienteSemPedidos() throws Exception {
            // Given - Cliente sem pedidos
            Cliente clienteSemPedidos = ClienteTestDataBuilder.umClienteValido()
                    .comEmail("sem.pedidos@email.com")
                    .build();
            clienteSemPedidos = clienteRepository.save(clienteSemPedidos);

            // When & Then
            mockMvc.perform(get("/api/pedidos/cliente/{clienteId}", clienteSemPedidos.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements").value(0));
        }
    }

    // ========== TESTES DE ATUALIZAÇÃO (PUT) ==========

    @Nested
    @DisplayName("PUT /api/pedidos/{id} - Atualização de Pedidos")
    class AtualizacaoPedidos {

        @Test
        @DisplayName("✅ Deve atualizar observações do pedido")
        void deveAtualizarObservacoesDoPedido() throws Exception {
            // Given
            PedidoDTO atualizacao = new PedidoDTO();
            atualizacao.setObservacoes("Observações atualizadas");
            atualizacao.setClienteId(clienteExistente.getId());
            atualizacao.setItens(pedidoDTOValido.getItens());

            // When & Then
            mockMvc.perform(put("/api/pedidos/{id}", pedidoExistente.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(atualizacao)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(pedidoExistente.getId()))
                    .andExpect(jsonPath("$.observacoes").value("Observações atualizadas"));
        }

        @Test
        @DisplayName("❌ Deve falhar ao atualizar pedido inexistente")
        void deveFalharAoAtualizarPedidoInexistente() throws Exception {
            // When & Then
            mockMvc.perform(put("/api/pedidos/{id}", 999L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pedidoDTOValido)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("não encontrado")));
        }

        @Test
        @DisplayName("❌ Deve falhar ao atualizar pedido finalizado")
        void deveFalharAoAtualizarPedidoFinalizado() throws Exception {
            // Given - Alterar status para finalizado
            pedidoExistente.setStatus(StatusPedido.ENTREGUE);
            pedidoRepository.save(pedidoExistente);

            // When & Then
            mockMvc.perform(put("/api/pedidos/{id}", pedidoExistente.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pedidoDTOValido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("não pode ser alterado")));
        }
    }

    // ========== TESTES DE ALTERAÇÃO DE STATUS ==========

    @Nested
    @DisplayName("PATCH /api/pedidos/{id}/status - Alteração de Status")
    class AlteracaoStatus {

        @Test
        @DisplayName("✅ Deve alterar status para CONFIRMADO")
        void deveAlterarStatusParaConfirmado() throws Exception {
            // When & Then
            mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoExistente.getId())
                    .param("novoStatus", StatusPedido.CONFIRMADO.toString())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(StatusPedido.CONFIRMADO.toString()));
        }

        @Test
        @DisplayName("✅ Deve seguir fluxo completo de status")
        void deveSeguirFluxoCompletoDeStatus() throws Exception {
            Long pedidoId = pedidoExistente.getId();

            // PENDENTE -> CONFIRMADO
            mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoId)
                    .param("novoStatus", StatusPedido.CONFIRMADO.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(StatusPedido.CONFIRMADO.toString()));

            // CONFIRMADO -> PREPARANDO
            mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoId)
                    .param("novoStatus", StatusPedido.PREPARANDO.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(StatusPedido.PREPARANDO.toString()));

            // PREPARANDO -> PRONTO
            mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoId)
                    .param("novoStatus", StatusPedido.PRONTO.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(StatusPedido.PRONTO.toString()));

            // PRONTO -> SAIU_PARA_ENTREGA
            mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoId)
                    .param("novoStatus", StatusPedido.SAIU_PARA_ENTREGA.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(StatusPedido.SAIU_PARA_ENTREGA.toString()));

            // SAIU_PARA_ENTREGA -> ENTREGUE
            mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoId)
                    .param("novoStatus", StatusPedido.ENTREGUE.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(StatusPedido.ENTREGUE.toString()));
        }

        @Test
        @DisplayName("❌ Deve falhar com transição de status inválida")
        void deveFalharComTransicaoStatusInvalida() throws Exception {
            // Given - Tentar pular etapas: PENDENTE -> PREPARANDO (sem passar por CONFIRMADO)
            
            // When & Then
            mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoExistente.getId())
                    .param("novoStatus", StatusPedido.PREPARANDO.toString())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("Transição de status inválida")));
        }

        @Test
        @DisplayName("❌ Deve falhar ao alterar status de pedido cancelado")
        void deveFalharAoAlterarStatusPedidoCancelado() throws Exception {
            // Given
            pedidoExistente.setStatus(StatusPedido.CANCELADO);
            pedidoRepository.save(pedidoExistente);

            // When & Then
            mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoExistente.getId())
                    .param("novoStatus", StatusPedido.CONFIRMADO.toString())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("Pedido cancelado não pode ter status alterado")));
        }

        @Test
        @DisplayName("✅ Deve cancelar pedido em qualquer status válido")
        void deveCancelarPedidoEmQualquerStatusValido() throws Exception {
            // When & Then
            mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoExistente.getId())
                    .param("novoStatus", StatusPedido.CANCELADO.toString())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(StatusPedido.CANCELADO.toString()));
        }
    }

    // ========== TESTES DE EXCLUSÃO (DELETE) ==========

    @Nested
    @DisplayName("DELETE /api/pedidos/{id} - Exclusão de Pedidos")
    class ExclusaoPedidos {

        @Test
        @DisplayName("✅ Deve excluir pedido não confirmado")
        void deveExcluirPedidoNaoConfirmado() throws Exception {
            // When & Then
            mockMvc.perform(delete("/api/pedidos/{id}", pedidoExistente.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            // Verificar se foi excluído
            mockMvc.perform(get("/api/pedidos/{id}", pedidoExistente.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("❌ Deve falhar ao excluir pedido confirmado")
        void deveFalharAoExcluirPedidoConfirmado() throws Exception {
            // Given
            pedidoExistente.setStatus(StatusPedido.CONFIRMADO);
            pedidoRepository.save(pedidoExistente);

            // When & Then
            mockMvc.perform(delete("/api/pedidos/{id}", pedidoExistente.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("Pedidos confirmados não podem ser excluídos")));
        }

        @Test
        @DisplayName("❌ Deve falhar ao excluir pedido inexistente")
        void deveFalharAoExcluirPedidoInexistente() throws Exception {
            // When & Then
            mockMvc.perform(delete("/api/pedidos/{id}", 999L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("não encontrado")));
        }
    }

    // ========== TESTES DE RELATÓRIOS ==========

    @Nested
    @DisplayName("Relatórios e Estatísticas")
    class RelatoriosEstatisticas {

        @Test
        @DisplayName("✅ Deve buscar pedidos por período")
        void deveBuscarPedidosPorPeriodo() throws Exception {
            // Given
            LocalDateTime dataInicio = LocalDateTime.now().minusDays(1);
            LocalDateTime dataFim = LocalDateTime.now().plusDays(1);

            // When & Then
            mockMvc.perform(get("/api/pedidos/periodo")
                    .param("dataInicio", dataInicio.toString())
                    .param("dataFim", dataFim.toString())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
        }

        @Test
        @DisplayName("✅ Deve calcular estatísticas de pedidos")
        void deveCalcularEstatisticasPedidos() throws Exception {
            // Given - Criar mais pedidos para estatísticas
            for (int i = 1; i <= 5; i++) {
                Pedido pedido = PedidoTestDataBuilder.umPedidoValido()
                        .comCliente(clienteExistente)
                        .comProduto(produtoExistente1, i)
                        .build();
                pedidoRepository.save(pedido);
            }

            // When & Then
            mockMvc.perform(get("/api/pedidos/estatisticas")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalPedidos").value(greaterThan(0)))
                    .andExpect(jsonPath("$.valorTotalVendas").exists())
                    .andExpect(jsonPath("$.ticketMedio").exists())
                    .andExpect(jsonPath("$.pedidosPorStatus").exists());
        }

        @Test
        @DisplayName("✅ Deve gerar relatório de produtos mais vendidos")
        void deveGerarRelatorioProdutosMaisVendidos() throws Exception {
            // Given - Criar pedidos com diferentes produtos
            for (int i = 1; i <= 3; i++) {
                Pedido pedido = PedidoTestDataBuilder.umPedidoValido()
                        .comCliente(clienteExistente)
                        .comProduto(produtoExistente1, i)
                        .comProduto(produtoExistente2, i * 2)
                        .build();
                pedidoRepository.save(pedido);
            }

            // When & Then
            mockMvc.perform(get("/api/pedidos/relatorio/produtos-vendidos")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                    .andExpect(jsonPath("$[0].produtoNome").exists())
                    .andExpect(jsonPath("$[0].quantidadeVendida").exists())
                    .andExpect(jsonPath("$[0].valorTotal").exists());
        }
    }

    // ========== TESTES DE PAGINAÇÃO E FILTROS ==========

    @Nested
    @DisplayName("Paginação e Filtros Avançados")
    class PaginacaoFiltros {

        @Test
        @DisplayName("✅ Deve paginar pedidos corretamente")
        void devePaginarPedidosCorretamente() throws Exception {
            // Given - Criar mais pedidos
            for (int i = 1; i <= 15; i++) {
                Pedido pedido = PedidoTestDataBuilder.umPedidoValido()
                        .comCliente(clienteExistente)
                        .comProduto(produtoExistente1, 1)
                        .build();
                pedidoRepository.save(pedido);
            }

            // When & Then - Primeira página
            mockMvc.perform(get("/api/pedidos")
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(10)))
                    .andExpect(jsonPath("$.number").value(0))
                    .andExpect(jsonPath("$.size").value(10))
                    .andExpect(jsonPath("$.totalPages").value(2))
                    .andExpect(jsonPath("$.totalElements").value(16)); // 15 + 1 existente
        }

        @Test
        @DisplayName("✅ Deve ordenar pedidos por data")
        void deveOrdenarPedidosPorData() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/pedidos")
                    .param("sort", "dataPedido,desc")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("✅ Deve filtrar pedidos por valor mínimo")
        void deveFiltrarPedidosPorValorMinimo() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/pedidos/filtrar")
                    .param("valorMinimo", "30.00")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }
    }
}
