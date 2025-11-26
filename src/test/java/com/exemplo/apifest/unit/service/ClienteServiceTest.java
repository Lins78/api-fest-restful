package com.exemplo.apifest.unit.service;

import com.exemplo.apifest.builders.ClienteTestDataBuilder;
import com.exemplo.apifest.dto.ClienteDTO;
import com.exemplo.apifest.dto.response.ClienteResponseDTO;
import com.exemplo.apifest.exception.BusinessException;
import com.exemplo.apifest.exception.EntityNotFoundException;
import com.exemplo.apifest.model.Cliente;
import com.exemplo.apifest.repository.ClienteRepository;
import com.exemplo.apifest.service.impl.ClienteServiceImpl;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários avançados para ClienteService - Roteiro 9.
 * 
 * CENÁRIOS TESTADOS:
 * - Operações CRUD completas
 * - Validações de negócio
 * - Tratamento de exceções
 * - Cenários edge cases
 * - Verificação de interações com repository
 * 
 * TÉCNICAS UTILIZADAS:
 * - Mocking com Mockito
 * - ArgumentCaptor para validação de parâmetros
 * - AssertJ para assertions fluentes
 * - Test Data Builders para dados de teste
 * - Nested classes para organização
 * 
 * @author DeliveryTech Team
 * @version 1.0 - Roteiro 9
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("👤 ClienteService - Testes Unitários Avançados")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente clienteExistente;
    private ClienteDTO clienteDTOValido;

    @BeforeEach
    void setUp() {
        clienteExistente = ClienteTestDataBuilder.umClienteValido()
                .buildComId(1L);

        clienteDTOValido = new ClienteDTO();
        clienteDTOValido.setNome("João Silva");
        clienteDTOValido.setEmail("joao.silva@email.com");
        clienteDTOValido.setTelefone("11987654321");
        clienteDTOValido.setEndereco("Rua das Flores, 123");
    }

    // ========== TESTES DE CRIAÇÃO ==========

    @Nested
    @DisplayName("Criação de Clientes")
    class CriacaoClientes {

        @Test
        @DisplayName("✅ Deve criar cliente com dados válidos")
        void deveCriarClienteComDadosValidos() {
            // Given
            when(clienteRepository.existsByEmail(clienteDTOValido.getEmail())).thenReturn(false);
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
                Cliente cliente = invocation.getArgument(0);
                cliente.setId(1L);
                return cliente;
            });

            // When
            ClienteResponseDTO resultado = clienteService.criarCliente(clienteDTOValido);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNome()).isEqualTo(clienteDTOValido.getNome());
            assertThat(resultado.getEmail()).isEqualTo(clienteDTOValido.getEmail());
            assertThat(resultado.getAtivo()).isTrue();
            assertThat(resultado.getDataCadastro()).isNotNull();

            // Verificar interações
            verify(clienteRepository).existsByEmail(clienteDTOValido.getEmail());
            verify(clienteRepository).save(any(Cliente.class));
        }

        @Test
        @DisplayName("❌ Deve falhar ao tentar criar cliente com email duplicado")
        void deveFalharAoTentarCriarClienteComEmailDuplicado() {
            // Given
            when(clienteRepository.existsByEmail(clienteDTOValido.getEmail())).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> clienteService.criarCliente(clienteDTOValido))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("já cadastrado");

            // Verificar que save não foi chamado
            verify(clienteRepository, never()).save(any(Cliente.class));
        }

        @Test
        @DisplayName("✅ Deve definir data de cadastro automaticamente")
        void deveDefinirDataCadastroAutomaticamente() {
            // Given
            LocalDateTime antes = LocalDateTime.now();
            when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
            ArgumentCaptor<Cliente> clienteCaptor = ArgumentCaptor.forClass(Cliente.class);
            when(clienteRepository.save(clienteCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            clienteService.criarCliente(clienteDTOValido);
            LocalDateTime depois = LocalDateTime.now();

            // Then
            Cliente clienteSalvo = clienteCaptor.getValue();
            assertThat(clienteSalvo.getDataCadastro())
                    .isNotNull()
                    .isAfterOrEqualTo(antes)
                    .isBeforeOrEqualTo(depois);
        }

        @Test
        @DisplayName("✅ Deve criar cliente com status ativo por padrão")
        void deveCriarClienteComStatusAtivoPorPadrao() {
            // Given
            when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
            ArgumentCaptor<Cliente> clienteCaptor = ArgumentCaptor.forClass(Cliente.class);
            when(clienteRepository.save(clienteCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            clienteService.criarCliente(clienteDTOValido);

            // Then
            Cliente clienteSalvo = clienteCaptor.getValue();
            assertThat(clienteSalvo.getAtivo()).isTrue();
        }
    }

    // ========== TESTES DE BUSCA ==========

    @Nested
    @DisplayName("Busca de Clientes")
    class BuscaClientes {

        @Test
        @DisplayName("✅ Deve buscar cliente por ID quando existir")
        void deveBuscarClientePorIdQuandoExistir() {
            // Given
            Long clienteId = 1L;
            when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteExistente));

            // When
            ClienteResponseDTO resultado = clienteService.buscarPorId(clienteId);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(clienteId);
            assertThat(resultado.getNome()).isEqualTo(clienteExistente.getNome());
            assertThat(resultado.getEmail()).isEqualTo(clienteExistente.getEmail());
        }

        @Test
        @DisplayName("❌ Deve lançar exceção quando cliente não existir")
        void deveLancarExcecaoQuandoClienteNaoExistir() {
            // Given
            Long clienteIdInexistente = 999L;
            when(clienteRepository.findById(clienteIdInexistente)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> clienteService.buscarPorId(clienteIdInexistente))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Cliente não encontrado");
        }

        @Test
        @DisplayName("✅ Deve buscar cliente por email quando existir")
        void deveBuscarClientePorEmailQuandoExistir() {
            // Given
            String email = "joao.silva@email.com";
            when(clienteRepository.findByEmail(email)).thenReturn(Optional.of(clienteExistente));

            // When
            Optional<ClienteResponseDTO> resultado = clienteService.buscarPorEmail(email);

            // Then
            assertThat(resultado).isPresent();
            assertThat(resultado.get().getEmail()).isEqualTo(email);
        }

        @Test
        @DisplayName("✅ Deve retornar empty quando cliente não existir por email")
        void deveRetornarEmptyQuandoClienteNaoExistirPorEmail() {
            // Given
            String emailInexistente = "inexistente@email.com";
            when(clienteRepository.findByEmail(emailInexistente)).thenReturn(Optional.empty());

            // When
            Optional<ClienteResponseDTO> resultado = clienteService.buscarPorEmail(emailInexistente);

            // Then
            assertThat(resultado).isEmpty();
        }
    }

    // ========== TESTES DE LISTAGEM ==========

    @Nested
    @DisplayName("Listagem de Clientes")
    class ListagemClientes {

        @Test
        @DisplayName("✅ Deve listar todos os clientes com paginação")
        void deveListarTodosOsClientesComPaginacao() {
            // Given
            List<Cliente> clientes = Arrays.asList(
                    ClienteTestDataBuilder.umClienteValido().buildComId(1L),
                    ClienteTestDataBuilder.umClienteValido().comEmail("outro@email.com").buildComId(2L)
            );
            
            Pageable pageable = PageRequest.of(0, 10);
            Page<Cliente> page = new PageImpl<>(clientes, pageable, clientes.size());
            
            when(clienteRepository.findAll(pageable)).thenReturn(page);

            // When
            Page<ClienteResponseDTO> resultado = clienteService.listarTodos(pageable);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getContent()).hasSize(2);
            assertThat(resultado.getTotalElements()).isEqualTo(2);
            assertThat(resultado.getContent().get(0).getNome()).isNotBlank();
        }

        @Test
        @DisplayName("✅ Deve listar apenas clientes ativos")
        void deveListarApenasClientesAtivos() {
            // Given
            List<Cliente> clientesAtivos = Arrays.asList(
                    ClienteTestDataBuilder.umClienteValido().buildComId(1L),
                    ClienteTestDataBuilder.umClienteValido().comEmail("ativo2@email.com").buildComId(2L)
            );
            
            when(clienteRepository.findByAtivoTrue()).thenReturn(clientesAtivos);

            // When
            List<ClienteResponseDTO> resultado = clienteService.listarAtivos();

            // Then
            assertThat(resultado).hasSize(2);
            assertThat(resultado).allMatch(cliente -> cliente.getAtivo());
        }

        @Test
        @DisplayName("✅ Deve buscar clientes por nome")
        void deveBuscarClientesPorNome() {
            // Given
            String termoBusca = "João";
            List<Cliente> clientesEncontrados = Arrays.asList(
                    ClienteTestDataBuilder.umClienteValido().comNome("João Silva").buildComId(1L),
                    ClienteTestDataBuilder.umClienteValido().comNome("João Santos").buildComId(2L)
            );
            
            when(clienteRepository.findByNomeContainingIgnoreCase(termoBusca)).thenReturn(clientesEncontrados);

            // When
            List<ClienteResponseDTO> resultado = clienteService.buscarPorNome(termoBusca);

            // Then
            assertThat(resultado).hasSize(2);
            assertThat(resultado).allMatch(cliente -> cliente.getNome().contains("João"));
        }
    }

    // ========== TESTES DE ATUALIZAÇÃO ==========

    @Nested
    @DisplayName("Atualização de Clientes")
    class AtualizacaoClientes {

        @Test
        @DisplayName("✅ Deve atualizar cliente existente com dados válidos")
        void deveAtualizarClienteExistenteComDadosValidos() {
            // Given
            Long clienteId = 1L;
            ClienteDTO dadosAtualizacao = new ClienteDTO();
            dadosAtualizacao.setNome("João Silva Atualizado");
            dadosAtualizacao.setEmail("joao.atualizado@email.com");
            dadosAtualizacao.setTelefone("11999888777");
            dadosAtualizacao.setEndereco("Nova Rua, 456");

            when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteExistente));
            when(clienteRepository.existsByEmail(dadosAtualizacao.getEmail())).thenReturn(false);
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            ClienteResponseDTO resultado = clienteService.atualizarCliente(clienteId, dadosAtualizacao);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getNome()).isEqualTo(dadosAtualizacao.getNome());
            assertThat(resultado.getEmail()).isEqualTo(dadosAtualizacao.getEmail());
            assertThat(resultado.getTelefone()).isEqualTo(dadosAtualizacao.getTelefone());
            assertThat(resultado.getEndereco()).isEqualTo(dadosAtualizacao.getEndereco());
        }

        @Test
        @DisplayName("❌ Deve falhar ao tentar atualizar com email já existente")
        void deveFalharAoTentarAtualizarComEmailJaExistente() {
            // Given
            Long clienteId = 1L;
            ClienteDTO dadosAtualizacao = new ClienteDTO();
            dadosAtualizacao.setEmail("email.existente@email.com");

            when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteExistente));
            when(clienteRepository.existsByEmail(dadosAtualizacao.getEmail())).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> clienteService.atualizarCliente(clienteId, dadosAtualizacao))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("já cadastrado");

            verify(clienteRepository, never()).save(any(Cliente.class));
        }

        @Test
        @DisplayName("✅ Deve permitir atualizar mantendo o mesmo email")
        void devePermitirAtualizarMantendoOMesmoEmail() {
            // Given
            Long clienteId = 1L;
            String emailAtual = clienteExistente.getEmail();
            
            ClienteDTO dadosAtualizacao = new ClienteDTO();
            dadosAtualizacao.setNome("Nome Atualizado");
            dadosAtualizacao.setEmail(emailAtual); // Mesmo email atual

            when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteExistente));
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            ClienteResponseDTO resultado = clienteService.atualizarCliente(clienteId, dadosAtualizacao);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getEmail()).isEqualTo(emailAtual);
            assertThat(resultado.getNome()).isEqualTo("Nome Atualizado");
        }
    }

    // ========== TESTES DE EXCLUSÃO ==========

    @Nested
    @DisplayName("Exclusão de Clientes")
    class ExclusaoClientes {

        @Test
        @DisplayName("✅ Deve excluir cliente existente")
        void deveExcluirClienteExistente() {
            // Given
            Long clienteId = 1L;
            when(clienteRepository.existsById(clienteId)).thenReturn(true);

            // When
            assertThatNoException().isThrownBy(() -> clienteService.excluirCliente(clienteId));

            // Then
            verify(clienteRepository).deleteById(clienteId);
        }

        @Test
        @DisplayName("❌ Deve falhar ao tentar excluir cliente inexistente")
        void deveFalharAoTentarExcluirClienteInexistente() {
            // Given
            Long clienteIdInexistente = 999L;
            when(clienteRepository.existsById(clienteIdInexistente)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> clienteService.excluirCliente(clienteIdInexistente))
                    .isInstanceOf(EntityNotFoundException.class);

            verify(clienteRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("✅ Deve desativar cliente ao invés de excluir fisicamente")
        void deveDesativarClienteAoInvesDeExcluirFisicamente() {
            // Given
            Long clienteId = 1L;
            when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteExistente));
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            ClienteResponseDTO resultado = clienteService.desativarCliente(clienteId);

            // Then
            assertThat(resultado.getAtivo()).isFalse();
            verify(clienteRepository).save(any(Cliente.class));
        }
    }

    // ========== TESTES DE VALIDAÇÃO ==========

    @Nested
    @DisplayName("Validações de Negócio")
    class ValidacoesNegocio {

        @Test
        @DisplayName("❌ Deve falhar com dados nulos")
        void deveFalharComDadosNulos() {
            // When & Then
            assertThatThrownBy(() -> clienteService.criarCliente(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("✅ Deve verificar se email existe")
        void deveVerificarSeEmailExiste() {
            // Given
            String email = "teste@email.com";
            when(clienteRepository.existsByEmail(email)).thenReturn(true);

            // When
            boolean existe = clienteService.emailJaExiste(email);

            // Then
            assertThat(existe).isTrue();
            verify(clienteRepository).existsByEmail(email);
        }

        @Test
        @DisplayName("✅ Deve contar total de clientes")
        void deveContarTotalDeClientes() {
            // Given
            long totalEsperado = 42L;
            when(clienteRepository.count()).thenReturn(totalEsperado);

            // When
            long total = clienteService.contarTotalClientes();

            // Then
            assertThat(total).isEqualTo(totalEsperado);
        }
    }
}