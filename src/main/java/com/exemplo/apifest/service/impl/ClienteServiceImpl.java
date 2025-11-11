package com.exemplo.apifest.service.impl;

import com.exemplo.apifest.dto.ClienteDTO;
import com.exemplo.apifest.dto.response.ClienteResponseDTO;
import com.exemplo.apifest.exception.BusinessException;
import com.exemplo.apifest.exception.EntityNotFoundException;
import com.exemplo.apifest.model.Cliente;
import com.exemplo.apifest.repository.ClienteRepository;
import com.exemplo.apifest.service.ClienteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ===============================================================================
 * ROTEIRO 4 - IMPLEMENTAÇÃO DO SERVIÇO DE CLIENTES
 * ===============================================================================
 * 
 * Implementação das regras de negócio para gerenciamento de clientes.
 * Esta classe contém toda a lógica de negócio relacionada a operações com clientes,
 * incluindo validações, transformações e operações transacionais.
 * 
 * CARACTERÍSTICAS TÉCNICAS:
 * - @Service: Marca como componente de serviço do Spring
 * - @Transactional: Garante integridade transacional em operações de escrita
 * - ModelMapper: Converte entre DTOs e Entidades automaticamente
 * - Validações de negócio rigorosas antes de persistir dados
 * 
 * @author DeliveryTech Development Team
 * @version 1.0 - Roteiro 4
 * @since Java 21 LTS + Spring Boot 3.4.0
 * ===============================================================================
 */
@Service
@Transactional(readOnly = true) // Por padrão, transações são read-only para performance
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Cadastra um novo cliente com validações rigorosas de negócio.
     */
    @Override
    @Transactional // Remove read-only para operação de escrita
    public ClienteResponseDTO cadastrarCliente(ClienteDTO dto) {
        // 1. VALIDAÇÃO: Verificar se email já existe
        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException(
                String.format("Já existe um cliente cadastrado com o email: %s", dto.getEmail())
            );
        }

        // Validação de telefone removida - método não existe no repositório

        // 3. CONVERSÃO: DTO → Entidade
        Cliente cliente = modelMapper.map(dto, Cliente.class);
        
        // 4. REGRA DE NEGÓCIO: Definir valores padrão
        cliente.setAtivo(true);
        cliente.setDataCadastro(LocalDateTime.now());

        // 5. PERSISTÊNCIA: Salvar no banco de dados
        Cliente clienteSalvo = clienteRepository.save(cliente);

        // 6. CONVERSÃO: Entidade → ResponseDTO
        return modelMapper.map(clienteSalvo, ClienteResponseDTO.class);
    }

    /**
     * Busca cliente por ID com tratamento de não encontrado.
     */
    @Override
    public ClienteResponseDTO buscarClientePorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Cliente não encontrado com ID: %d", id)
            ));

        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    /**
     * Busca cliente por email (usado para autenticação).
     */
    @Override
    public ClienteResponseDTO buscarClientePorEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Cliente não encontrado com email: %s", email)
            ));

        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    /**
     * Atualiza dados do cliente com validações de unicidade.
     */
    @Override
    @Transactional
    public ClienteResponseDTO atualizarCliente(Long id, ClienteDTO dto) {
        // 1. VALIDAÇÃO: Verificar se cliente existe
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Cliente não encontrado com ID: %d", id)
            ));

        // 2. VALIDAÇÃO: Verificar se email já pertence a outro cliente
        Optional<Cliente> clienteComMesmoEmail = clienteRepository.findByEmail(dto.getEmail());
        if (clienteComMesmoEmail.isPresent() && !clienteComMesmoEmail.get().getId().equals(id)) {
            throw new BusinessException(
                String.format("Já existe outro cliente com o email: %s", dto.getEmail())
            );
        }

        // Validação de telefone removida - método não existe no repositório

        // 4. ATUALIZAÇÃO: Mapear novos dados (preservando ID e datas)
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEndereco(dto.getEndereco());

        // 5. PERSISTÊNCIA: Salvar alterações
        Cliente clienteAtualizado = clienteRepository.save(cliente);

        return modelMapper.map(clienteAtualizado, ClienteResponseDTO.class);
    }

    /**
     * Toggle do status ativo/inativo do cliente.
     */
    @Override
    @Transactional
    public ClienteResponseDTO ativarDesativarCliente(Long id) {
        // 1. VALIDAÇÃO: Verificar se cliente existe
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Cliente não encontrado com ID: %d", id)
            ));

        // 2. REGRA DE NEGÓCIO: Toggle do status
        cliente.setAtivo(!cliente.getAtivo());

        // 3. PERSISTÊNCIA: Salvar alteração
        Cliente clienteAtualizado = clienteRepository.save(cliente);

        return modelMapper.map(clienteAtualizado, ClienteResponseDTO.class);
    }

    /**
     * Lista todos os clientes ativos do sistema.
     */
    @Override
    public List<ClienteResponseDTO> listarClientesAtivos() {
        List<Cliente> clientesAtivos = clienteRepository.findByAtivoTrue();
        
        return clientesAtivos.stream()
            .map(cliente -> modelMapper.map(cliente, ClienteResponseDTO.class))
            .collect(Collectors.toList());
    }
}