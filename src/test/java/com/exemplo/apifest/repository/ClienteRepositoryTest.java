package com.exemplo.apifest.repository;

import com.exemplo.apifest.model.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para ClienteRepository.
 * 
 * Este teste verifica todas as operações de persistência
 * relacionadas à entidade Cliente, incluindo consultas
 * customizadas e operações CRUD básicas.
 * 
 * CENÁRIOS TESTADOS:
 * - Salvar e buscar cliente
 * - Buscar por email
 * - Buscar clientes ativos
 * - Verificar existência por email
 * - Operações de update
 * 
 * @author DeliveryTech Team
 * @version 1.0 - Roteiro 8
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("👤 Testes Repository - ClienteRepository")
public class ClienteRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClienteRepository clienteRepository;

    @Test
    @DisplayName("✅ Deve salvar e buscar cliente por ID")
    public void testSaveAndFindById() {
        // Given
        Cliente cliente = createTestCliente();

        // When
        Cliente savedCliente = clienteRepository.save(cliente);
        Optional<Cliente> foundCliente = clienteRepository.findById(savedCliente.getId());

        // Then
        assertThat(foundCliente).isPresent();
        assertThat(foundCliente.get().getNome()).isEqualTo("João Silva");
        assertThat(foundCliente.get().getEmail()).isEqualTo("joao@teste.com");
    }

    @Test
    @DisplayName("✅ Deve buscar cliente por email")
    public void testFindByEmail() {
        // Given
        Cliente cliente = createTestCliente();
        entityManager.persistAndFlush(cliente);

        // When
        Optional<Cliente> foundCliente = clienteRepository.findByEmail("joao@teste.com");

        // Then
        assertThat(foundCliente).isPresent();
        assertThat(foundCliente.get().getNome()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("✅ Deve verificar se email já existe")
    public void testExistsByEmail() {
        // Given
        Cliente cliente = createTestCliente();
        entityManager.persistAndFlush(cliente);

        // When & Then
        assertThat(clienteRepository.existsByEmail("joao@teste.com")).isTrue();
        assertThat(clienteRepository.existsByEmail("inexistente@teste.com")).isFalse();
    }

    @Test
    @DisplayName("✅ Deve buscar apenas clientes ativos")
    public void testFindByAtivoTrue() {
        // Given
        Cliente clienteAtivo = createTestCliente();
        clienteAtivo.setAtivo(true);

        Cliente clienteInativo = createTestCliente();
        clienteInativo.setEmail("inativo@teste.com");
        clienteInativo.setAtivo(false);

        entityManager.persistAndFlush(clienteAtivo);
        entityManager.persistAndFlush(clienteInativo);

        // When
        List<Cliente> clientesAtivos = clienteRepository.findByAtivoTrue();

        // Then
        assertThat(clientesAtivos).hasSize(1);
        assertThat(clientesAtivos.get(0).getEmail()).isEqualTo("joao@teste.com");
        assertThat(clientesAtivos.get(0).getAtivo()).isTrue();
    }

    @Test
    @DisplayName("✅ Deve buscar clientes por nome (contendo)")
    public void testFindByNomeContainingIgnoreCase() {
        // Given
        Cliente cliente1 = createTestCliente();
        cliente1.setNome("João Silva");

        Cliente cliente2 = createTestCliente();
        cliente2.setEmail("maria@teste.com");
        cliente2.setNome("Maria João");

        entityManager.persistAndFlush(cliente1);
        entityManager.persistAndFlush(cliente2);

        // When
        List<Cliente> clientesComJoao = clienteRepository.findByNomeContainingIgnoreCase("joão");

        // Then
        assertThat(clientesComJoao).hasSize(2);
    }

    @Test
    @DisplayName("✅ Deve atualizar cliente existente")
    public void testUpdateCliente() {
        // Given
        Cliente cliente = createTestCliente();
        Cliente savedCliente = entityManager.persistAndFlush(cliente);

        // When
        savedCliente.setNome("João Silva Atualizado");
        savedCliente.setTelefone("11888888888");
        Cliente updatedCliente = clienteRepository.save(savedCliente);

        // Then
        assertThat(updatedCliente.getNome()).isEqualTo("João Silva Atualizado");
        assertThat(updatedCliente.getTelefone()).isEqualTo("11888888888");
        assertThat(updatedCliente.getId()).isEqualTo(savedCliente.getId());
    }

    @Test
    @DisplayName("✅ Deve excluir cliente por ID")
    public void testDeleteById() {
        // Given
        Cliente cliente = createTestCliente();
        Cliente savedCliente = entityManager.persistAndFlush(cliente);

        // When
        clienteRepository.deleteById(savedCliente.getId());

        // Then
        Optional<Cliente> deletedCliente = clienteRepository.findById(savedCliente.getId());
        assertThat(deletedCliente).isNotPresent();
    }

    @Test
    @DisplayName("✅ Deve contar total de clientes")
    public void testCountClientes() {
        // Given
        Cliente cliente1 = createTestCliente();
        Cliente cliente2 = createTestCliente();
        cliente2.setEmail("outro@teste.com");

        entityManager.persistAndFlush(cliente1);
        entityManager.persistAndFlush(cliente2);

        // When
        long count = clienteRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("✅ Deve listar todos os clientes")
    public void testFindAll() {
        // Given
        Cliente clienteA = createTestCliente();
        clienteA.setNome("Ana Santos");

        Cliente clienteB = createTestCliente();
        clienteB.setEmail("bruno@teste.com");
        clienteB.setNome("Bruno Silva");

        entityManager.persistAndFlush(clienteA);
        entityManager.persistAndFlush(clienteB);

        // When
        List<Cliente> clientes = clienteRepository.findAll();

        // Then
        assertThat(clientes).hasSize(2);
    }

    private Cliente createTestCliente() {
        Cliente cliente = new Cliente();
        cliente.setNome("João Silva");
        cliente.setEmail("joao@teste.com");
        cliente.setTelefone("11999999999");
        cliente.setEndereco("Rua Teste, 123");
        cliente.setDataCadastro(LocalDateTime.now());
        cliente.setAtivo(true);
        return cliente;
    }
}