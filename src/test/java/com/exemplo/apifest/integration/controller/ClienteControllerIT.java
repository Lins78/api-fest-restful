package com.exemplo.apifest.integration.controller;

import com.exemplo.apifest.builders.ClienteTestDataBuilder;
import com.exemplo.apifest.dto.ClienteDTO;
import com.exemplo.apifest.model.Cliente;
import com.exemplo.apifest.repository.ClienteRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de Integração para ClienteController - Roteiro 9.
 * 
 * CENÁRIOS DE INTEGRAÇÃO TESTADOS:
 * - CRUD completo via REST API
 * - Validações de dados em requests reais
 * - Persistência em banco PostgreSQL via TestContainers
 * - Serialização/deserialização JSON
 * - Códigos de status HTTP apropriados
 * - Validações de segurança e autorização
 * 
 * TECNOLOGIAS UTILIZADAS:
 * - TestContainers para PostgreSQL real
 * - MockMvc para simulação de requests HTTP
 * - JsonPath para validação de responses
 * - Transactional para rollback automático
 * - SpringBootTest com perfil de teste
 * 
 * @author DeliveryTech Team
 * @version 1.0 - Roteiro 9
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test-advanced")
@Testcontainers
@Transactional
@DisplayName("🧑‍💼 ClienteController - Testes de Integração")
class ClienteControllerIT {

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
    private ClienteRepository clienteRepository;

    private Cliente clienteExistente;
    private ClienteDTO clienteDTOValido;

    @BeforeEach
    void setUp() {
        // Limpar dados antes de cada teste
        clienteRepository.deleteAll();

        // Criar cliente existente para testes de busca/atualização
        clienteExistente = ClienteTestDataBuilder.umClienteValido()
                .comEmail("cliente.existente@email.com")
                .build();
        clienteExistente = clienteRepository.save(clienteExistente);

        // Setup ClienteDTO válido para testes de criação
        clienteDTOValido = new ClienteDTO();
        clienteDTOValido.setNome("João da Silva");
        clienteDTOValido.setEmail("joao.silva@email.com");
        clienteDTOValido.setCpf("123.456.789-09");
        clienteDTOValido.setTelefone("(11) 99999-9999");
        clienteDTOValido.setCep("01310-100");
        clienteDTOValido.setLogradouro("Av. Paulista");
        clienteDTOValido.setNumero("1000");
        clienteDTOValido.setBairro("Bela Vista");
        clienteDTOValido.setCidade("São Paulo");
        clienteDTOValido.setUf("SP");
    }

    // ========== TESTES DE CRIAÇÃO (POST) ==========

    @Nested
    @DisplayName("POST /api/clientes - Criação de Clientes")
    class CriacaoClientes {

        @Test
        @DisplayName("✅ Deve criar cliente com dados válidos")
        void deveCriarClienteComDadosValidos() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/clientes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clienteDTOValido)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.nome").value(clienteDTOValido.getNome()))
                    .andExpect(jsonPath("$.email").value(clienteDTOValido.getEmail()))
                    .andExpect(jsonPath("$.cpf").value(clienteDTOValido.getCpf()))
                    .andExpect(jsonPath("$.telefone").value(clienteDTOValido.getTelefone()))
                    .andExpect(jsonPath("$.endereco.cep").value(clienteDTOValido.getCep()))
                    .andExpect(jsonPath("$.endereco.logradouro").value(clienteDTOValido.getLogradouro()))
                    .andExpect(jsonPath("$.endereco.numero").value(clienteDTOValido.getNumero()))
                    .andExpect(jsonPath("$.endereco.bairro").value(clienteDTOValido.getBairro()))
                    .andExpect(jsonPath("$.endereco.cidade").value(clienteDTOValido.getCidade()))
                    .andExpect(jsonPath("$.endereco.uf").value(clienteDTOValido.getUf()));
        }

        @Test
        @DisplayName("❌ Deve falhar com email duplicado")
        void deveFalharComEmailDuplicado() throws Exception {
            // Given - Email já existente
            clienteDTOValido.setEmail(clienteExistente.getEmail());

            // When & Then
            mockMvc.perform(post("/api/clientes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clienteDTOValido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("já cadastrado")));
        }

        @Test
        @DisplayName("❌ Deve falhar com CPF inválido")
        void deveFalharComCpfInvalido() throws Exception {
            // Given
            clienteDTOValido.setCpf("111.111.111-11"); // CPF inválido

            // When & Then
            mockMvc.perform(post("/api/clientes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clienteDTOValido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("CPF inválido")));
        }

        @Test
        @DisplayName("❌ Deve falhar com dados obrigatórios ausentes")
        void deveFalharComDadosObrigatoriosAusentes() throws Exception {
            // Given
            ClienteDTO clienteIncompleto = new ClienteDTO();
            clienteIncompleto.setNome(""); // Nome vazio
            clienteIncompleto.setEmail(""); // Email vazio

            // When & Then
            mockMvc.perform(post("/api/clientes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clienteIncompleto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors").isArray())
                    .andExpect(jsonPath("$.errors", hasSize(greaterThan(0))));
        }

        @Test
        @DisplayName("❌ Deve falhar com CEP inválido")
        void deveFalharComCepInvalido() throws Exception {
            // Given
            clienteDTOValido.setCep("12345"); // CEP inválido

            // When & Then
            mockMvc.perform(post("/api/clientes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clienteDTOValido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("CEP inválido")));
        }

        @Test
        @DisplayName("❌ Deve falhar com telefone inválido")
        void deveFalharComTelefoneInvalido() throws Exception {
            // Given
            clienteDTOValido.setTelefone("123456"); // Telefone inválido

            // When & Then
            mockMvc.perform(post("/api/clientes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clienteDTOValido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("Telefone inválido")));
        }
    }

    // ========== TESTES DE BUSCA (GET) ==========

    @Nested
    @DisplayName("GET /api/clientes - Busca de Clientes")
    class BuscaClientes {

        @Test
        @DisplayName("✅ Deve buscar cliente por ID existente")
        void deveBuscarClientePorIdExistente() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/clientes/{id}", clienteExistente.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(clienteExistente.getId()))
                    .andExpect(jsonPath("$.nome").value(clienteExistente.getNome()))
                    .andExpect(jsonPath("$.email").value(clienteExistente.getEmail()))
                    .andExpect(jsonPath("$.cpf").value(clienteExistente.getCpf()));
        }

        @Test
        @DisplayName("❌ Deve falhar ao buscar cliente inexistente")
        void deveFalharAoBuscarClienteInexistente() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/clientes/{id}", 999L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("não encontrado")));
        }

        @Test
        @DisplayName("✅ Deve listar todos os clientes")
        void deveListarTodosClientes() throws Exception {
            // Given - Criar mais alguns clientes
            Cliente cliente2 = ClienteTestDataBuilder.umClienteValido()
                    .comEmail("cliente2@email.com")
                    .build();
            clienteRepository.save(cliente2);

            Cliente cliente3 = ClienteTestDataBuilder.umClienteValido()
                    .comEmail("cliente3@email.com")
                    .build();
            clienteRepository.save(cliente3);

            // When & Then
            mockMvc.perform(get("/api/clientes")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(3)))
                    .andExpect(jsonPath("$.totalElements").value(3));
        }

        @Test
        @DisplayName("✅ Deve buscar clientes por nome")
        void deveBuscarClientesPorNome() throws Exception {
            // Given
            String termoBusca = clienteExistente.getNome().substring(0, 4);

            // When & Then
            mockMvc.perform(get("/api/clientes/buscar")
                    .param("nome", termoBusca)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(greaterThan(0))));
        }

        @Test
        @DisplayName("✅ Deve retornar lista vazia para busca sem resultados")
        void deveRetornarListaVaziaParaBuscaSemResultados() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/clientes/buscar")
                    .param("nome", "NomeQueNaoExiste")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements").value(0));
        }
    }

    // ========== TESTES DE ATUALIZAÇÃO (PUT) ==========

    @Nested
    @DisplayName("PUT /api/clientes/{id} - Atualização de Clientes")
    class AtualizacaoClientes {

        @Test
        @DisplayName("✅ Deve atualizar cliente com dados válidos")
        void deveAtualizarClienteComDadosValidos() throws Exception {
            // Given
            ClienteDTO atualizacao = new ClienteDTO();
            atualizacao.setNome("Nome Atualizado");
            atualizacao.setEmail("email.atualizado@email.com");
            atualizacao.setCpf(clienteExistente.getCpf()); // Manter CPF
            atualizacao.setTelefone("(11) 88888-8888");
            atualizacao.setCep("04038-001");
            atualizacao.setLogradouro("Rua Nova");
            atualizacao.setNumero("500");
            atualizacao.setBairro("Bairro Novo");
            atualizacao.setCidade("São Paulo");
            atualizacao.setUf("SP");

            // When & Then
            mockMvc.perform(put("/api/clientes/{id}", clienteExistente.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(atualizacao)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(clienteExistente.getId()))
                    .andExpect(jsonPath("$.nome").value(atualizacao.getNome()))
                    .andExpect(jsonPath("$.email").value(atualizacao.getEmail()))
                    .andExpect(jsonPath("$.telefone").value(atualizacao.getTelefone()))
                    .andExpect(jsonPath("$.endereco.cep").value(atualizacao.getCep()));
        }

        @Test
        @DisplayName("❌ Deve falhar ao atualizar cliente inexistente")
        void deveFalharAoAtualizarClienteInexistente() throws Exception {
            // When & Then
            mockMvc.perform(put("/api/clientes/{id}", 999L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clienteDTOValido)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("não encontrado")));
        }

        @Test
        @DisplayName("❌ Deve falhar ao tentar usar email de outro cliente")
        void deveFalharAoTentarUsarEmailOutroCliente() throws Exception {
            // Given - Criar outro cliente
            Cliente outroCliente = ClienteTestDataBuilder.umClienteValido()
                    .comEmail("outro@email.com")
                    .build();
            outroCliente = clienteRepository.save(outroCliente);

            // Tentar atualizar com email do outro cliente
            clienteDTOValido.setEmail(outroCliente.getEmail());

            // When & Then
            mockMvc.perform(put("/api/clientes/{id}", clienteExistente.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(clienteDTOValido)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("já está em uso")));
        }
    }

    // ========== TESTES DE EXCLUSÃO (DELETE) ==========

    @Nested
    @DisplayName("DELETE /api/clientes/{id} - Exclusão de Clientes")
    class ExclusaoClientes {

        @Test
        @DisplayName("✅ Deve excluir cliente existente")
        void deveExcluirClienteExistente() throws Exception {
            // When & Then
            mockMvc.perform(delete("/api/clientes/{id}", clienteExistente.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            // Verificar se foi excluído
            mockMvc.perform(get("/api/clientes/{id}", clienteExistente.getId())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("❌ Deve falhar ao excluir cliente inexistente")
        void deveFalharAoExcluirClienteInexistente() throws Exception {
            // When & Then
            mockMvc.perform(delete("/api/clientes/{id}", 999L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("não encontrado")));
        }
    }

    // ========== TESTES DE VALIDAÇÃO DE JSON ==========

    @Nested
    @DisplayName("Validação de Estrutura JSON")
    class ValidacaoJson {

        @Test
        @DisplayName("❌ Deve falhar com JSON malformado")
        void deveFalharComJsonMalformado() throws Exception {
            // Given
            String jsonMalformado = "{ nome: 'João', email: }"; // JSON inválido

            // When & Then
            mockMvc.perform(post("/api/clientes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonMalformado))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("❌ Deve falhar com Content-Type inválido")
        void deveFalharComContentTypeInvalido() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/clientes")
                    .contentType(MediaType.TEXT_PLAIN) // Content-Type errado
                    .content(objectMapper.writeValueAsString(clienteDTOValido)))
                    .andExpect(status().isUnsupportedMediaType());
        }

        @Test
        @DisplayName("❌ Deve falhar com body vazio")
        void deveFalharComBodyVazio() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/clientes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("")) // Body vazio
                    .andExpect(status().isBadRequest());
        }
    }

    // ========== TESTES DE PAGINAÇÃO ==========

    @Nested
    @DisplayName("Paginação e Ordenação")
    class PaginacaoOrdenacao {

        @Test
        @DisplayName("✅ Deve paginar resultados corretamente")
        void devePaginarResultadosCorretamente() throws Exception {
            // Given - Criar mais clientes para testar paginação
            for (int i = 1; i <= 15; i++) {
                Cliente cliente = ClienteTestDataBuilder.umClienteValido()
                        .comEmail("cliente" + i + "@email.com")
                        .build();
                clienteRepository.save(cliente);
            }

            // When & Then - Primeira página
            mockMvc.perform(get("/api/clientes")
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(10)))
                    .andExpect(jsonPath("$.number").value(0))
                    .andExpect(jsonPath("$.size").value(10))
                    .andExpect(jsonPath("$.totalPages").value(2))
                    .andExpect(jsonPath("$.totalElements").value(16)); // 15 + 1 existente

            // Segunda página
            mockMvc.perform(get("/api/clientes")
                    .param("page", "1")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(6))) // Resto: 6 elementos
                    .andExpect(jsonPath("$.number").value(1));
        }

        @Test
        @DisplayName("✅ Deve ordenar resultados por nome")
        void deveOrdenarResultadosPorNome() throws Exception {
            // Given
            Cliente clienteA = ClienteTestDataBuilder.umClienteValido()
                    .comNome("Alberto Silva")
                    .comEmail("alberto@email.com")
                    .build();
            clienteRepository.save(clienteA);

            Cliente clienteZ = ClienteTestDataBuilder.umClienteValido()
                    .comNome("Zilda Santos")
                    .comEmail("zilda@email.com")
                    .build();
            clienteRepository.save(clienteZ);

            // When & Then - Ordem crescente
            mockMvc.perform(get("/api/clientes")
                    .param("sort", "nome,asc")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].nome").value("Alberto Silva"));

            // Ordem decrescente
            mockMvc.perform(get("/api/clientes")
                    .param("sort", "nome,desc")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].nome").value("Zilda Santos"));
        }
    }
}
