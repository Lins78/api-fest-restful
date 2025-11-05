package com.exemplo.apifest.service;

import com.exemplo.apifest.dto.ClienteDTO;
import com.exemplo.apifest.dto.response.ClienteResponseDTO;
import java.util.List;

/**
 * ===============================================================================
 * ROTEIRO 4 - INTERFACE DO SERVIÇO DE CLIENTES
 * ===============================================================================
 * 
 * Esta interface define o contrato para o serviço de clientes, contendo todas as
 * operações de negócio relacionadas ao gerenciamento de clientes na API FEST.
 * 
 * Responsabilidades:
 * - Definir operações CRUD para clientes
 * - Estabelecer contratos para validações de negócio
 * - Padronizar métodos de busca e filtros
 * - Garantir operações seguras com DTOs
 * 
 * @author DeliveryTech Development Team
 * @version 1.0 - Roteiro 4
 * @since Java 21 LTS + Spring Boot 3.4.0
 * ===============================================================================
 */
public interface ClienteService {

    /**
     * Cadastra um novo cliente no sistema com validações de negócio.
     * 
     * REGRAS DE NEGÓCIO:
     * - Email deve ser único no sistema
     * - Todos os campos obrigatórios devem estar preenchidos
     * - Telefone deve ter formato válido
     * - Cliente é criado com status ATIVO por padrão
     * 
     * @param dto Dados do cliente a ser cadastrado
     * @return ClienteResponseDTO Cliente cadastrado com ID gerado
     * @throws BusinessException Se email já existir ou dados inválidos
     */
    ClienteResponseDTO cadastrarCliente(ClienteDTO dto);

    /**
     * Busca um cliente por ID com validação de existência.
     * 
     * @param id ID do cliente a ser buscado
     * @return ClienteResponseDTO Cliente encontrado
     * @throws EntityNotFoundException Se cliente não existir
     */
    ClienteResponseDTO buscarClientePorId(Long id);

    /**
     * Busca um cliente por email (usado para login/autenticação).
     * 
     * @param email Email do cliente a ser buscado
     * @return ClienteResponseDTO Cliente encontrado
     * @throws EntityNotFoundException Se cliente não existir
     */
    ClienteResponseDTO buscarClientePorEmail(String email);

    /**
     * Atualiza dados de um cliente existente.
     * 
     * REGRAS DE NEGÓCIO:
     * - Cliente deve existir
     * - Email deve continuar único (exceto para o próprio cliente)
     * - Validações de formato nos campos
     * 
     * @param id ID do cliente a ser atualizado
     * @param dto Novos dados do cliente
     * @return ClienteResponseDTO Cliente atualizado
     * @throws EntityNotFoundException Se cliente não existir
     * @throws BusinessException Se email já pertencer a outro cliente
     */
    ClienteResponseDTO atualizarCliente(Long id, ClienteDTO dto);

    /**
     * Ativa ou desativa um cliente (toggle do status ativo).
     * 
     * REGRAS DE NEGÓCIO:
     * - Cliente deve existir
     * - Não permite desativar cliente com pedidos pendentes
     * 
     * @param id ID do cliente
     * @return ClienteResponseDTO Cliente com status atualizado
     * @throws EntityNotFoundException Se cliente não existir
     * @throws BusinessException Se cliente tiver pedidos pendentes
     */
    ClienteResponseDTO ativarDesativarCliente(Long id);

    /**
     * Lista todos os clientes ativos do sistema.
     * 
     * @return List<ClienteResponseDTO> Lista de clientes ativos
     */
    List<ClienteResponseDTO> listarClientesAtivos();
}