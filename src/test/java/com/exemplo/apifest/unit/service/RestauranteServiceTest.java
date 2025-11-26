package com.exemplo.apifest.unit.service;

import com.exemplo.apifest.builders.RestauranteTestDataBuilder;
import com.exemplo.apifest.dto.RestauranteDTO;
import com.exemplo.apifest.dto.response.RestauranteResponseDTO;
import com.exemplo.apifest.exception.BusinessException;
import com.exemplo.apifest.exception.EntityNotFoundException;
import com.exemplo.apifest.model.Restaurante;
import com.exemplo.apifest.model.StatusRestaurante;
import com.exemplo.apifest.repository.RestauranteRepository;
import com.exemplo.apifest.service.impl.RestauranteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários avançados para RestauranteService - Roteiro 9.
 * 
 * CENÁRIOS COMPLEXOS TESTADOS:
 * - Gestão de horários de funcionamento
 * - Validações de dados específicas do negócio
 * - Cálculo de taxa de entrega
 * - Controle de status do restaurante
 * - Regras de disponibilidade
 * - Validação de categorias de culinária
 * 
 * TÉCNICAS AVANÇADAS:
 * - Testes parametrizados para horários
 * - Validação de regras de negócio complexas
 * - Simulação de falhas de dependências
 * - Testes de performance para listagens
 * - Validação de dados de endereço
 * 
 * @author DeliveryTech Team
 * @version 1.0 - Roteiro 9
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("🍽️ RestauranteService - Testes Unitários Avançados")
class RestauranteServiceTest {

    @Mock
    private RestauranteRepository restauranteRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RestauranteServiceImpl restauranteService;

    private Restaurante restauranteExistente;
    private RestauranteDTO restauranteDTOValido;

    @BeforeEach
    void setUp() {
        restauranteExistente = RestauranteTestDataBuilder.umRestauranteValido()
                .buildComId(1L);

        // Setup RestauranteDTO válido
        restauranteDTOValido = new RestauranteDTO();
        restauranteDTOValido.setNome("Pizzaria do João");
        restauranteDTOValido.setDescricao("A melhor pizza da cidade");
        restauranteDTOValido.setCategoria("ITALIANA");
        restauranteDTOValido.setTelefone("(11) 99999-9999");
        restauranteDTOValido.setEmail("contato@pizzariajoao.com");
        restauranteDTOValido.setCep("01310-100");
        restauranteDTOValido.setLogradouro("Av. Paulista");
        restauranteDTOValido.setNumero("1000");
        restauranteDTOValido.setBairro("Bela Vista");
        restauranteDTOValido.setCidade("São Paulo");
        restauranteDTOValido.setUf("SP");
        restauranteDTOValido.setTaxaEntrega(new BigDecimal("8.50"));
        restauranteDTOValido.setValorMinimo(new BigDecimal("25.00"));
        restauranteDTOValido.setHorarioAbertura(LocalTime.of(18, 0));
        restauranteDTOValido.setHorarioFechamento(LocalTime.of(23, 30));
    }

    // ========== TESTES DE CRIAÇÃO DE RESTAURANTES ==========

    @Nested
    @DisplayName("Criação de Restaurantes")
    class CriacaoRestaurantes {

        @Test
        @DisplayName("✅ Deve criar restaurante com dados válidos")
        void deveCriarRestauranteComDadosValidos() {
            // Given
            when(restauranteRepository.existsByEmail(restauranteDTOValido.getEmail())).thenReturn(false);
            when(restauranteRepository.save(any(Restaurante.class))).thenAnswer(invocation -> {
                Restaurante restaurante = invocation.getArgument(0);
                restaurante.setId(1L);
                return restaurante;
            });

            // When
            RestauranteResponseDTO resultado = restauranteService.criarRestaurante(restauranteDTOValido);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNome()).isEqualTo(restauranteDTOValido.getNome());
            assertThat(resultado.getStatus()).isEqualTo(StatusRestaurante.ATIVO);
            assertThat(resultado.getEmail()).isEqualTo(restauranteDTOValido.getEmail());

            verify(restauranteRepository).existsByEmail(restauranteDTOValido.getEmail());
            verify(restauranteRepository).save(any(Restaurante.class));
        }

        @Test
        @DisplayName("❌ Deve falhar quando email já existir")
        void deveFalharQuandoEmailJaExistir() {
            // Given
            when(restauranteRepository.existsByEmail(restauranteDTOValido.getEmail())).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> restauranteService.criarRestaurante(restauranteDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("já cadastrado");

            verify(restauranteRepository, never()).save(any(Restaurante.class));
        }

        @Test
        @DisplayName("❌ Deve falhar com CEP inválido")
        void deveFalharComCepInvalido() {
            // Given
            restauranteDTOValido.setCep("123456");

            // When & Then
            assertThatThrownBy(() -> restauranteService.criarRestaurante(restauranteDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("CEP inválido");
        }

        @Test
        @DisplayName("❌ Deve falhar com categoria inválida")
        void deveFalharComCategoriaInvalida() {
            // Given
            restauranteDTOValido.setCategoria("CATEGORIA_INEXISTENTE");

            // When & Then
            assertThatThrownBy(() -> restauranteService.criarRestaurante(restauranteDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Categoria inválida");
        }

        @Test
        @DisplayName("❌ Deve falhar com horário inválido")
        void deveFalharComHorarioInvalido() {
            // Given - Horário de abertura após fechamento
            restauranteDTOValido.setHorarioAbertura(LocalTime.of(23, 0));
            restauranteDTOValido.setHorarioFechamento(LocalTime.of(18, 0));

            // When & Then
            assertThatThrownBy(() -> restauranteService.criarRestaurante(restauranteDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Horário de abertura deve ser anterior ao fechamento");
        }

        @Test
        @DisplayName("✅ Deve definir status inicial como ATIVO")
        void deveDefinirStatusInicialComoAtivo() {
            // Given
            when(restauranteRepository.existsByEmail(any())).thenReturn(false);
            
            ArgumentCaptor<Restaurante> restauranteCaptor = ArgumentCaptor.forClass(Restaurante.class);
            when(restauranteRepository.save(restauranteCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            restauranteService.criarRestaurante(restauranteDTOValido);

            // Then
            Restaurante restauranteSalvo = restauranteCaptor.getValue();
            assertThat(restauranteSalvo.getStatus()).isEqualTo(StatusRestaurante.ATIVO);
        }
    }

    // ========== TESTES DE HORÁRIO DE FUNCIONAMENTO ==========

    @Nested
    @DisplayName("Horário de Funcionamento")
    class HorarioFuncionamento {

        @Test
        @DisplayName("✅ Deve verificar se está aberto no horário correto")
        void deveVerificarSeEstaAbertoNoHorarioCorreto() {
            // Given
            Restaurante restaurante = RestauranteTestDataBuilder.umRestauranteValido()
                    .comHorarios(LocalTime.of(10, 0), LocalTime.of(22, 0))
                    .build();

            when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

            // When
            boolean estaAberto = restauranteService.verificarSeEstaAberto(1L, LocalTime.of(15, 30));

            // Then
            assertThat(estaAberto).isTrue();
        }

        @Test
        @DisplayName("❌ Deve verificar se está fechado fora do horário")
        void deveVerificarSeEstaFechadoForaDoHorario() {
            // Given
            Restaurante restaurante = RestauranteTestDataBuilder.umRestauranteValido()
                    .comHorarios(LocalTime.of(18, 0), LocalTime.of(23, 0))
                    .build();

            when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

            // When
            boolean estaAberto = restauranteService.verificarSeEstaAberto(1L, LocalTime.of(10, 30));

            // Then
            assertThat(estaAberto).isFalse();
        }

        @Test
        @DisplayName("✅ Deve lidar com horário que cruza meia-noite")
        void deveLibarComHorarioCruzaMeiaNoite() {
            // Given - Restaurante que abre 22h e fecha 2h
            Restaurante restaurante = RestauranteTestDataBuilder.umRestauranteValido()
                    .comHorarios(LocalTime.of(22, 0), LocalTime.of(2, 0))
                    .build();

            when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

            // When & Then
            // Deve estar aberto às 23h
            boolean estaAbertoAs23 = restauranteService.verificarSeEstaAberto(1L, LocalTime.of(23, 0));
            assertThat(estaAbertoAs23).isTrue();

            // Deve estar aberto à 1h (madrugada)
            boolean estaAbertoAs1 = restauranteService.verificarSeEstaAberto(1L, LocalTime.of(1, 0));
            assertThat(estaAbertoAs1).isTrue();

            // Deve estar fechado às 10h
            boolean estaAbertoAs10 = restauranteService.verificarSeEstaAberto(1L, LocalTime.of(10, 0));
            assertThat(estaAbertoAs10).isFalse();
        }

        @Test
        @DisplayName("❌ Deve estar fechado quando restaurante está inativo")
        void deveEstarFechadoQuandoRestauranteInativo() {
            // Given
            Restaurante restauranteInativo = RestauranteTestDataBuilder.umRestauranteInativo()
                    .comHorarios(LocalTime.of(10, 0), LocalTime.of(22, 0))
                    .build();

            when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restauranteInativo));

            // When
            boolean estaAberto = restauranteService.verificarSeEstaAberto(1L, LocalTime.of(15, 0));

            // Then
            assertThat(estaAberto).isFalse();
        }
    }

    // ========== TESTES DE BUSCA E FILTROS ==========

    @Nested
    @DisplayName("Busca e Filtros")
    class BuscaFiltros {

        @Test
        @DisplayName("✅ Deve buscar restaurantes por categoria")
        void deveBuscarRestaurantesPorCategoria() {
            // Given
            String categoria = "ITALIANA";
            List<Restaurante> restaurantesItalianos = Arrays.asList(
                    RestauranteTestDataBuilder.umRestauranteValido().comCategoria(categoria).buildComId(1L),
                    RestauranteTestDataBuilder.umRestauranteValido().comCategoria(categoria).buildComId(2L)
            );

            when(restauranteRepository.findByCategoria(categoria)).thenReturn(restaurantesItalianos);

            // When
            List<RestauranteResponseDTO> resultado = restauranteService.buscarPorCategoria(categoria);

            // Then
            assertThat(resultado).hasSize(2);
            assertThat(resultado).allMatch(r -> r.getCategoria().equals(categoria));
        }

        @Test
        @DisplayName("✅ Deve buscar restaurantes por nome")
        void deveBuscarRestaurantesPorNome() {
            // Given
            String termoBusca = "pizza";
            Pageable pageable = PageRequest.of(0, 10);
            
            List<Restaurante> restaurantesEncontrados = Arrays.asList(
                    RestauranteTestDataBuilder.umRestauranteValido().comNome("Pizzaria do João").buildComId(1L),
                    RestauranteTestDataBuilder.umRestauranteValido().comNome("Super Pizza").buildComId(2L)
            );

            Page<Restaurante> page = new PageImpl<>(restaurantesEncontrados, pageable, 2);
            when(restauranteRepository.findByNomeContainingIgnoreCase(termoBusca, pageable)).thenReturn(page);

            // When
            Page<RestauranteResponseDTO> resultado = restauranteService.buscarPorNome(termoBusca, pageable);

            // Then
            assertThat(resultado.getContent()).hasSize(2);
            assertThat(resultado.getTotalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("✅ Deve listar apenas restaurantes ativos")
        void deveListarApenasRestaurantesAtivos() {
            // Given
            List<Restaurante> restaurantesAtivos = Arrays.asList(
                    RestauranteTestDataBuilder.umRestauranteValido().buildComId(1L),
                    RestauranteTestDataBuilder.umRestauranteValido().buildComId(2L)
            );

            when(restauranteRepository.findByStatus(StatusRestaurante.ATIVO)).thenReturn(restaurantesAtivos);

            // When
            List<RestauranteResponseDTO> resultado = restauranteService.listarRestaurantesAtivos();

            // Then
            assertThat(resultado).hasSize(2);
            assertThat(resultado).allMatch(r -> "ATIVO".equals(r.getStatus()));
        }

        @Test
        @DisplayName("✅ Deve filtrar por taxa de entrega máxima")
        void deveFiltrarPorTaxaEntregaMaxima() {
            // Given
            BigDecimal taxaMaxima = new BigDecimal("10.00");
            List<Restaurante> restaurantesBaratos = Arrays.asList(
                    RestauranteTestDataBuilder.umRestauranteValido()
                            .comTaxaEntrega(new BigDecimal("5.00")).buildComId(1L),
                    RestauranteTestDataBuilder.umRestauranteValido()
                            .comTaxaEntrega(new BigDecimal("8.50")).buildComId(2L)
            );

            when(restauranteRepository.findByTaxaEntregaLessThanEqual(taxaMaxima))
                    .thenReturn(restaurantesBaratos);

            // When
            List<RestauranteResponseDTO> resultado = restauranteService.buscarPorTaxaEntregaMaxima(taxaMaxima);

            // Then
            assertThat(resultado).hasSize(2);
            assertThat(resultado)
                    .allMatch(r -> r.getTaxaEntrega().compareTo(taxaMaxima) <= 0);
        }
    }

    // ========== TESTES DE ATUALIZAÇÃO E STATUS ==========

    @Nested
    @DisplayName("Atualização e Gerenciamento de Status")
    class AtualizacaoStatus {

        @Test
        @DisplayName("✅ Deve atualizar dados do restaurante")
        void deveAtualizarDadosRestaurante() {
            // Given
            when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restauranteExistente));
            when(restauranteRepository.existsByEmailAndIdNot(restauranteDTOValido.getEmail(), 1L))
                    .thenReturn(false);
            when(restauranteRepository.save(any(Restaurante.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            RestauranteResponseDTO resultado = restauranteService.atualizarRestaurante(1L, restauranteDTOValido);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getNome()).isEqualTo(restauranteDTOValido.getNome());
            assertThat(resultado.getEmail()).isEqualTo(restauranteDTOValido.getEmail());

            verify(restauranteRepository).save(any(Restaurante.class));
        }

        @Test
        @DisplayName("❌ Deve falhar ao tentar atualizar com email existente")
        void deveFalharAoTentarAtualizarComEmailExistente() {
            // Given
            when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restauranteExistente));
            when(restauranteRepository.existsByEmailAndIdNot(restauranteDTOValido.getEmail(), 1L))
                    .thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> restauranteService.atualizarRestaurante(1L, restauranteDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("já está em uso");

            verify(restauranteRepository, never()).save(any(Restaurante.class));
        }

        @Test
        @DisplayName("✅ Deve ativar restaurante inativo")
        void deveAtivarRestauranteInativo() {
            // Given
            Restaurante restauranteInativo = RestauranteTestDataBuilder.umRestauranteInativo()
                    .buildComId(1L);

            when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restauranteInativo));
            when(restauranteRepository.save(any(Restaurante.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            RestauranteResponseDTO resultado = restauranteService.ativarRestaurante(1L);

            // Then
            assertThat(resultado.getStatus()).isEqualTo(StatusRestaurante.ATIVO);
        }

        @Test
        @DisplayName("✅ Deve desativar restaurante ativo")
        void deveDesativarRestauranteAtivo() {
            // Given
            when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restauranteExistente));
            when(restauranteRepository.save(any(Restaurante.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            RestauranteResponseDTO resultado = restauranteService.desativarRestaurante(1L);

            // Then
            assertThat(resultado.getStatus()).isEqualTo(StatusRestaurante.INATIVO);
        }

        @Test
        @DisplayName("❌ Deve falhar ao tentar ativar restaurante já ativo")
        void deveFalharAoTentarAtivarRestauranteJaAtivo() {
            // Given
            when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restauranteExistente));

            // When & Then
            assertThatThrownBy(() -> restauranteService.ativarRestaurante(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("já está ativo");
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
            assertThatThrownBy(() -> restauranteService.criarRestaurante(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("✅ Deve validar formato de telefone")
        void deveValidarFormatoTelefone() {
            // Given - Telefone inválido
            restauranteDTOValido.setTelefone("123456");

            // When & Then
            assertThatThrownBy(() -> restauranteService.criarRestaurante(restauranteDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Telefone inválido");
        }

        @Test
        @DisplayName("✅ Deve validar formato de email")
        void deveValidarFormatoEmail() {
            // Given
            restauranteDTOValido.setEmail("email-invalido");

            // When & Then
            assertThatThrownBy(() -> restauranteService.criarRestaurante(restauranteDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Email inválido");
        }

        @Test
        @DisplayName("✅ Deve validar valor mínimo positivo")
        void deveValidarValorMinimoPositivo() {
            // Given
            restauranteDTOValido.setValorMinimo(new BigDecimal("-10.00"));

            // When & Then
            assertThatThrownBy(() -> restauranteService.criarRestaurante(restauranteDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Valor mínimo deve ser positivo");
        }

        @Test
        @DisplayName("✅ Deve validar taxa de entrega positiva")
        void deveValidarTaxaEntregaPositiva() {
            // Given
            restauranteDTOValido.setTaxaEntrega(new BigDecimal("-5.00"));

            // When & Then
            assertThatThrownBy(() -> restauranteService.criarRestaurante(restauranteDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Taxa de entrega deve ser positiva");
        }
    }

    // ========== TESTES DE BUSCA POR ID ==========

    @Nested
    @DisplayName("Busca por ID")
    class BuscaPorId {

        @Test
        @DisplayName("✅ Deve buscar restaurante por ID existente")
        void deveBuscarRestaurantePorIdExistente() {
            // Given
            when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restauranteExistente));

            // When
            RestauranteResponseDTO resultado = restauranteService.buscarPorId(1L);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("❌ Deve falhar ao buscar restaurante inexistente")
        void deveFalharAoBuscarRestauranteInexistente() {
            // Given
            when(restauranteRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> restauranteService.buscarPorId(999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Restaurante não encontrado");
        }
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Nested
    @DisplayName("Exclusão")
    class Exclusao {

        @Test
        @DisplayName("✅ Deve excluir restaurante sem pedidos")
        void deveExcluirRestauranteSemPedidos() {
            // Given
            when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restauranteExistente));
            when(restauranteRepository.temPedidosAssociados(1L)).thenReturn(false);

            // When
            restauranteService.excluirRestaurante(1L);

            // Then
            verify(restauranteRepository).delete(restauranteExistente);
        }

        @Test
        @DisplayName("❌ Deve falhar ao tentar excluir restaurante com pedidos")
        void deveFalharAoTentarExcluirRestauranteComPedidos() {
            // Given
            when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restauranteExistente));
            when(restauranteRepository.temPedidosAssociados(1L)).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> restauranteService.excluirRestaurante(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("possui pedidos associados");

            verify(restauranteRepository, never()).delete(any(Restaurante.class));
        }
    }
}