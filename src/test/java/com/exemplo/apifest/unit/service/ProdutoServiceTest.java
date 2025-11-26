package com.exemplo.apifest.unit.service;

import com.exemplo.apifest.builders.ProdutoTestDataBuilder;
import com.exemplo.apifest.builders.RestauranteTestDataBuilder;
import com.exemplo.apifest.dto.ProdutoDTO;
import com.exemplo.apifest.dto.response.ProdutoResponseDTO;
import com.exemplo.apifest.model.StatusProduto;
import com.exemplo.apifest.exception.BusinessException;
import com.exemplo.apifest.exception.ResourceNotFoundException;
import com.exemplo.apifest.model.Produto;
import com.exemplo.apifest.model.Restaurante;
import com.exemplo.apifest.repository.ProdutoRepository;
import com.exemplo.apifest.repository.RestauranteRepository;
import com.exemplo.apifest.service.impl.ProdutoServiceImpl;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários avançados para ProdutoService - Roteiro 9.
 * 
 * CENÁRIOS COMPLEXOS TESTADOS:
 * - Gestão de estoque e disponibilidade
 * - Validações de preço e categoria
 * - Controle de status do produto
 * - Associação com restaurantes
 * - Regras de negócio específicas
 * - Cálculo de promoções
 * 
 * TÉCNICAS AVANÇADAS:
 * - Testes com múltiplas dependências
 * - Validação de regras de estoque
 * - Simulação de cenários de produto esgotado
 * - Testes de performance para catálogos
 * - Validação de dados monetários
 * 
 * @author DeliveryTech Team
 * @version 1.0 - Roteiro 9
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("🍕 ProdutoService - Testes Unitários Avançados")
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private RestauranteRepository restauranteRepository;

    @InjectMocks
    private ProdutoServiceImpl produtoService;

    private Produto produtoExistente;
    private Restaurante restauranteExistente;
    private ProdutoDTO produtoDTOValido;

    @BeforeEach
    void setUp() {
        restauranteExistente = RestauranteTestDataBuilder.umRestauranteValido()
                .buildComId(1L);

        produtoExistente = ProdutoTestDataBuilder.umProdutoValido()
                .comRestaurante(restauranteExistente)
                .buildComId(1L);

        // Setup ProdutoDTO válido
        produtoDTOValido = new ProdutoDTO();
        produtoDTOValido.setNome("Pizza Margherita");
        produtoDTOValido.setDescricao("Pizza tradicional com molho de tomate, mussarela e manjericão");
        produtoDTOValido.setCategoria("PIZZA");
        produtoDTOValido.setPreco(new BigDecimal("29.90"));
        produtoDTOValido.setQuantidadeEstoque(50);
        produtoDTOValido.setRestauranteId(1L);
    }

    // ========== TESTES DE CRIAÇÃO DE PRODUTOS ==========

    @Nested
    @DisplayName("Criação de Produtos")
    class CriacaoProdutos {

        @Test
        @DisplayName("✅ Deve criar produto com dados válidos")
        void deveCriarProdutoComDadosValidos() {
            // Given
            when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restauranteExistente));
            when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
                Produto produto = invocation.getArgument(0);
                produto.setId(1L);
                return produto;
            });

            // When
            ProdutoResponseDTO resultado = produtoService.criarProduto(produtoDTOValido);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNome()).isEqualTo(produtoDTOValido.getNome());
            assertThat(resultado.getStatus()).isEqualTo(StatusProduto.DISPONIVEL);
            assertThat(resultado.getRestauranteId()).isEqualTo(1L);

            verify(restauranteRepository).findById(1L);
            verify(produtoRepository).save(any(Produto.class));
        }

        @Test
        @DisplayName("❌ Deve falhar quando restaurante não existir")
        void deveFalharQuandoRestauranteNaoExistir() {
            // Given
            when(restauranteRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> produtoService.criarProduto(produtoDTOValido))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Restaurante não encontrado");

            verify(produtoRepository, never()).save(any(Produto.class));
        }

        @Test
        @DisplayName("❌ Deve falhar com preço negativo")
        void deveFalharComPrecoNegativo() {
            // Given
            produtoDTOValido.setPreco(new BigDecimal("-10.00"));

            // When & Then
            assertThatThrownBy(() -> produtoService.criarProduto(produtoDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Preço deve ser positivo");
        }

        @Test
        @DisplayName("❌ Deve falhar com quantidade de estoque negativa")
        void deveFalharComQuantidadeEstoqueNegativa() {
            // Given
            produtoDTOValido.setQuantidadeEstoque(-5);

            // When & Then
            assertThatThrownBy(() -> produtoService.criarProduto(produtoDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Quantidade em estoque não pode ser negativa");
        }

        @Test
        @DisplayName("❌ Deve falhar com categoria inválida")
        void deveFalharComCategoriaInvalida() {
            // Given
            produtoDTOValido.setCategoria("CATEGORIA_INEXISTENTE");

            // When & Then
            assertThatThrownBy(() -> produtoService.criarProduto(produtoDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Categoria inválida");
        }

        @Test
        @DisplayName("✅ Deve definir status inicial como DISPONÍVEL")
        void deveDefinirStatusInicialComoDisponivel() {
            // Given
            when(restauranteRepository.findById(any())).thenReturn(Optional.of(restauranteExistente));
            
            ArgumentCaptor<Produto> produtoCaptor = ArgumentCaptor.forClass(Produto.class);
            when(produtoRepository.save(produtoCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            produtoService.criarProduto(produtoDTOValido);

            // Then
            Produto produtoSalvo = produtoCaptor.getValue();
            assertThat(produtoSalvo.getStatus()).isEqualTo(StatusProduto.DISPONIVEL);
        }
    }

    // ========== TESTES DE GESTÃO DE ESTOQUE ==========

    @Nested
    @DisplayName("Gestão de Estoque")
    class GestaoEstoque {

        @Test
        @DisplayName("✅ Deve reduzir estoque ao fazer pedido")
        void deveReduzirEstoqueAoFazerPedido() {
            // Given
            Produto produto = ProdutoTestDataBuilder.umProdutoValido()
                    .comEstoque(10)
                    .buildComId(1L);
                    
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
            when(produtoRepository.save(any(Produto.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            produtoService.reduzirEstoque(1L, 3);

            // Then
            verify(produtoRepository).save(any(Produto.class));
            assertThat(produto.getQuantidadeEstoque()).isEqualTo(7);
        }

        @Test
        @DisplayName("❌ Deve falhar ao tentar reduzir estoque insuficiente")
        void deveFalharAoTentarReduzirEstoqueInsuficiente() {
            // Given
            Produto produto = ProdutoTestDataBuilder.umProdutoValido()
                    .comEstoque(2)
                    .buildComId(1L);
                    
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

            // When & Then
            assertThatThrownBy(() -> produtoService.reduzirEstoque(1L, 5))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Estoque insuficiente");

            verify(produtoRepository, never()).save(any(Produto.class));
        }

        @Test
        @DisplayName("✅ Deve aumentar estoque ao cancelar pedido")
        void deveAumentarEstoqueAoCancelarPedido() {
            // Given
            Produto produto = ProdutoTestDataBuilder.umProdutoValido()
                    .comEstoque(5)
                    .buildComId(1L);
                    
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
            when(produtoRepository.save(any(Produto.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            produtoService.aumentarEstoque(1L, 3);

            // Then
            verify(produtoRepository).save(any(Produto.class));
            assertThat(produto.getQuantidadeEstoque()).isEqualTo(8);
        }

        @Test
        @DisplayName("✅ Deve marcar como esgotado quando estoque zerar")
        void deveMarcarComoEsgotadoQuandoEstoqueZerar() {
            // Given
            Produto produto = ProdutoTestDataBuilder.umProdutoValido()
                    .comEstoque(1)
                    .buildComId(1L);
                    
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
            when(produtoRepository.save(any(Produto.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            produtoService.reduzirEstoque(1L, 1);

            // Then
            assertThat(produto.getQuantidadeEstoque()).isEqualTo(0);
            assertThat(produto.getStatus()).isEqualTo(StatusProduto.ESGOTADO);
        }

        @Test
        @DisplayName("✅ Deve reativar produto quando estoque for reposto")
        void deveReativarProdutoQuandoEstoqueForReposto() {
            // Given
            Produto produtoEsgotado = ProdutoTestDataBuilder.umProdutoEsgotado()
                    .buildComId(1L);
                    
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoEsgotado));
            when(produtoRepository.save(any(Produto.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            produtoService.reabastecer(1L, 10);

            // Then
            assertThat(produtoEsgotado.getQuantidadeEstoque()).isEqualTo(10);
            assertThat(produtoEsgotado.getStatus()).isEqualTo(StatusProduto.DISPONIVEL);
        }

        @Test
        @DisplayName("✅ Deve verificar disponibilidade de estoque")
        void deveVerificarDisponibilidadeEstoque() {
            // Given
            Produto produto = ProdutoTestDataBuilder.umProdutoValido()
                    .comEstoque(5)
                    .build();
                    
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

            // When & Then
            boolean disponivelPara3 = produtoService.verificarDisponibilidade(1L, 3);
            assertThat(disponivelPara3).isTrue();

            boolean disponivelPara10 = produtoService.verificarDisponibilidade(1L, 10);
            assertThat(disponivelPara10).isFalse();
        }
    }

    // ========== TESTES DE ATUALIZAÇÃO DE PREÇOS ==========

    @Nested
    @DisplayName("Atualização de Preços")
    class AtualizacaoPrecos {

        @Test
        @DisplayName("✅ Deve atualizar preço do produto")
        void deveAtualizarPrecoProduto() {
            // Given
            BigDecimal novoPreco = new BigDecimal("35.90");
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));
            when(produtoRepository.save(any(Produto.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            ProdutoResponseDTO resultado = produtoService.atualizarPreco(1L, novoPreco);

            // Then
            assertThat(resultado.getPreco()).isEqualByComparingTo(novoPreco);
            verify(produtoRepository).save(any(Produto.class));
        }

        @Test
        @DisplayName("❌ Deve falhar ao tentar definir preço negativo")
        void deveFalharAoTentarDefinirPrecoNegativo() {
            // Given
            BigDecimal precoNegativo = new BigDecimal("-5.00");
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));

            // When & Then
            assertThatThrownBy(() -> produtoService.atualizarPreco(1L, precoNegativo))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Preço deve ser positivo");

            verify(produtoRepository, never()).save(any(Produto.class));
        }

        @Test
        @DisplayName("✅ Deve aplicar desconto percentual")
        void deveAplicarDescontoPercentual() {
            // Given
            BigDecimal precoOriginal = new BigDecimal("100.00");
            produtoExistente.setPreco(precoOriginal);
            
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));
            when(produtoRepository.save(any(Produto.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When - Aplicar 20% de desconto
            ProdutoResponseDTO resultado = produtoService.aplicarDesconto(1L, 20.0);

            // Then
            BigDecimal precoEsperado = new BigDecimal("80.00");
            assertThat(resultado.getPreco()).isEqualByComparingTo(precoEsperado);
        }

        @Test
        @DisplayName("❌ Deve falhar com desconto inválido")
        void deveFalharComDescontoInvalido() {
            // Given
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));

            // When & Then - Desconto maior que 100%
            assertThatThrownBy(() -> produtoService.aplicarDesconto(1L, 150.0))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Desconto deve estar entre 0 e 100");

            // Desconto negativo
            assertThatThrownBy(() -> produtoService.aplicarDesconto(1L, -10.0))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Desconto deve estar entre 0 e 100");
        }
    }

    // ========== TESTES DE BUSCA E FILTROS ==========

    @Nested
    @DisplayName("Busca e Filtros")
    class BuscaFiltros {

        @Test
        @DisplayName("✅ Deve buscar produtos por categoria")
        void deveBuscarProdutosPorCategoria() {
            // Given
            String categoria = "PIZZA";
            List<Produto> pizzas = Arrays.asList(
                    ProdutoTestDataBuilder.umProdutoValido().comCategoria(categoria).buildComId(1L),
                    ProdutoTestDataBuilder.umProdutoValido().comCategoria(categoria).buildComId(2L)
            );

            when(produtoRepository.findByCategoria(categoria)).thenReturn(pizzas);

            // When
            List<ProdutoResponseDTO> resultado = produtoService.buscarPorCategoria(categoria);

            // Then
            assertThat(resultado).hasSize(2);
            assertThat(resultado).allMatch(p -> p.getCategoria().equals(categoria));
        }

        @Test
        @DisplayName("✅ Deve buscar produtos por restaurante")
        void deveBuscarProdutosPorRestaurante() {
            // Given
            Long restauranteId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            
            List<Produto> produtosRestaurante = Arrays.asList(
                    ProdutoTestDataBuilder.umProdutoValido().buildComId(1L),
                    ProdutoTestDataBuilder.umProdutoValido().buildComId(2L)
            );

            when(produtoRepository.findByRestauranteId(restauranteId)).thenReturn(produtosRestaurante);

            // When
            Page<ProdutoResponseDTO> resultado = produtoService.buscarPorRestaurante(restauranteId, pageable);

            // Then
            assertThat(resultado.getContent()).hasSize(2);
            assertThat(resultado.getTotalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("✅ Deve buscar produtos por faixa de preço")
        void deveBuscarProdutosPorFaixaPreco() {
            // Given
            BigDecimal precoMinimo = new BigDecimal("10.00");
            BigDecimal precoMaximo = new BigDecimal("50.00");
            
            List<Produto> produtosFaixaPreco = Arrays.asList(
                    ProdutoTestDataBuilder.umProdutoValido().comPreco(new BigDecimal("15.00")).buildComId(1L),
                    ProdutoTestDataBuilder.umProdutoValido().comPreco(new BigDecimal("35.00")).buildComId(2L)
            );

            when(produtoRepository.findByPrecoBetween(precoMinimo, precoMaximo))
                    .thenReturn(produtosFaixaPreco);

            // When
            List<ProdutoResponseDTO> resultado = produtoService.buscarPorFaixaPreco(precoMinimo, precoMaximo);

            // Then
            assertThat(resultado).hasSize(2);
            assertThat(resultado).allMatch(p -> 
                    p.getPreco().compareTo(precoMinimo) >= 0 && 
                    p.getPreco().compareTo(precoMaximo) <= 0
            );
        }

        @Test
        @DisplayName("✅ Deve buscar apenas produtos disponíveis")
        void deveBuscarApenasProdu_tosDisponiveis() {
            // Given
            List<Produto> produtosDisponiveis = Arrays.asList(
                    ProdutoTestDataBuilder.umProdutoValido().buildComId(1L),
                    ProdutoTestDataBuilder.umProdutoValido().buildComId(2L)
            );

            when(produtoRepository.findByStatus(StatusProduto.DISPONIVEL))
                    .thenReturn(produtosDisponiveis);

            // When
            List<ProdutoResponseDTO> resultado = produtoService.listarProdutosDisponiveis();

            // Then
            assertThat(resultado).hasSize(2);
            assertThat(resultado).allMatch(p -> "DISPONIVEL".equals(p.getStatus()));
        }

        @Test
        @DisplayName("✅ Deve buscar produtos com estoque baixo")
        void deveBuscarProdutosComEstoqueBaixo() {
            // Given
            int limiteBaixo = 5;
            List<Produto> produtosEstoqueBaixo = Arrays.asList(
                    ProdutoTestDataBuilder.umProdutoValido().comEstoque(2).buildComId(1L),
                    ProdutoTestDataBuilder.umProdutoValido().comEstoque(4).buildComId(2L)
            );

            when(produtoRepository.findByQuantidadeEstoqueLessThan(limiteBaixo))
                    .thenReturn(produtosEstoqueBaixo);

            // When
            List<ProdutoResponseDTO> resultado = produtoService.buscarComEstoqueBaixo(limiteBaixo);

            // Then
            assertThat(resultado).hasSize(2);
            assertThat(resultado).allMatch(p -> p.getQuantidadeEstoque() < limiteBaixo);
        }
    }

    // ========== TESTES DE ATUALIZAÇÃO E STATUS ==========

    @Nested
    @DisplayName("Atualização e Gerenciamento de Status")
    class AtualizacaoStatus {

        @Test
        @DisplayName("✅ Deve atualizar dados do produto")
        void deveAtualizarDadosProduto() {
            // Given
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));
            when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restauranteExistente));
            when(produtoRepository.save(any(Produto.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            ProdutoResponseDTO resultado = produtoService.atualizarProduto(1L, produtoDTOValido);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getNome()).isEqualTo(produtoDTOValido.getNome());
            assertThat(resultado.getDescricao()).isEqualTo(produtoDTOValido.getDescricao());

            verify(produtoRepository).save(any(Produto.class));
        }

        @Test
        @DisplayName("✅ Deve ativar produto inativo")
        void deveAtivarProdutoInativo() {
            // Given
            Produto produtoInativo = ProdutoTestDataBuilder.umProdutoInativo()
                    .buildComId(1L);

            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoInativo));
            when(produtoRepository.save(any(Produto.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            ProdutoResponseDTO resultado = produtoService.ativarProduto(1L);

            // Then
            assertThat(resultado.getStatus()).isEqualTo(StatusProduto.DISPONIVEL);
        }

        @Test
        @DisplayName("✅ Deve desativar produto ativo")
        void deveDesativarProdutoAtivo() {
            // Given
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));
            when(produtoRepository.save(any(Produto.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            ProdutoResponseDTO resultado = produtoService.desativarProduto(1L);

            // Then
            assertThat(resultado.getStatus()).isEqualTo(StatusProduto.INATIVO);
        }
    }

    // ========== TESTES DE VALIDAÇÕES DE REGRAS DE NEGÓCIO ==========

    @Nested
    @DisplayName("Validações de Regras de Negócio")
    class ValidacoesRegrasNegocio {

        @Test
        @DisplayName("❌ Deve falhar com dados nulos")
        void deveFalharComDadosNulos() {
            // When & Then
            assertThatThrownBy(() -> produtoService.criarProduto(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("❌ Deve falhar com nome muito curto")
        void deveFalharComNomeMuitoCurto() {
            // Given
            produtoDTOValido.setNome("AB");

            // When & Then
            assertThatThrownBy(() -> produtoService.criarProduto(produtoDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Nome deve ter pelo menos 3 caracteres");
        }

        @Test
        @DisplayName("❌ Deve falhar com nome muito longo")
        void deveFalharComNomeMuitoLongo() {
            // Given
            String nomeLongo = "A".repeat(101);
            produtoDTOValido.setNome(nomeLongo);

            // When & Then
            assertThatThrownBy(() -> produtoService.criarProduto(produtoDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Nome não pode ter mais que 100 caracteres");
        }

        @Test
        @DisplayName("❌ Deve falhar com preço zero")
        void deveFalharComPrecoZero() {
            // Given
            produtoDTOValido.setPreco(BigDecimal.ZERO);

            // When & Then
            assertThatThrownBy(() -> produtoService.criarProduto(produtoDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Preço deve ser maior que zero");
        }
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Nested
    @DisplayName("Busca por ID")
    class BuscaPorId {

        @Test
        @DisplayName("✅ Deve buscar produto por ID existente")
        void deveBuscarProdutoPorIdExistente() {
            // Given
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));

            // When
            ProdutoResponseDTO resultado = produtoService.buscarPorId(1L);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("❌ Deve falhar ao buscar produto inexistente")
        void deveFalharAoBuscarProdutoInexistente() {
            // Given
            when(produtoRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> produtoService.buscarPorId(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Produto não encontrado");
        }
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Nested
    @DisplayName("Exclusão")
    class Exclusao {

        @Test
        @DisplayName("✅ Deve excluir produto sem pedidos associados")
        void deveExcluirProdutoSemPedidosAssociados() {
            // Given
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));
            when(produtoRepository.temPedidosAssociados(1L)).thenReturn(false);

            // When
            produtoService.excluirProduto(1L);

            // Then
            verify(produtoRepository).delete(produtoExistente);
        }

        @Test
        @DisplayName("❌ Deve falhar ao tentar excluir produto com pedidos")
        void deveFalharAoTentarExcluirProdutoComPedidos() {
            // Given
            when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoExistente));
            when(produtoRepository.temPedidosAssociados(1L)).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> produtoService.excluirProduto(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("possui pedidos associados");

            verify(produtoRepository, never()).delete(any(Produto.class));
        }
    }
}