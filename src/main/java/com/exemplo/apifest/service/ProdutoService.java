package com.exemplo.apifest.service;

import com.exemplo.apifest.dto.ProdutoDTO;
import com.exemplo.apifest.dto.response.ProdutoResponseDTO;
import java.util.List;

/**
 * ===============================================================================
 * ROTEIRO 4 - INTERFACE DO SERVIÇO DE PRODUTOS
 * ===============================================================================
 * 
 * Esta interface define o contrato para o serviço de produtos, contendo todas as
 * operações de negócio relacionadas ao gerenciamento de produtos na API FEST.
 * 
 * Responsabilidades:
 * - Definir operações CRUD para produtos
 * - Estabelecer contratos para controle de disponibilidade
 * - Padronizar métodos de busca por restaurante e categoria
 * - Garantir integridade referencial com restaurantes
 * 
 * @author DeliveryTech Development Team
 * @version 1.0 - Roteiro 4
 * @since Java 21 LTS + Spring Boot 3.4.0
 * ===============================================================================
 */
public interface ProdutoService {

    /**
     * Cadastra um novo produto no sistema com validações de negócio.
     * 
     * REGRAS DE NEGÓCIO:
     * - Restaurante deve existir e estar ativo
     * - Nome do produto deve ser único dentro do restaurante
     * - Preço deve ser maior que zero
     * - Categoria deve ser uma das categorias válidas
     * - Produto é criado como DISPONÍVEL por padrão
     * 
     * @param dto Dados do produto a ser cadastrado
     * @return ProdutoResponseDTO Produto cadastrado com ID gerado
     * @throws EntityNotFoundException Se restaurante não existir
     * @throws BusinessException Se dados inválidos ou nome já existir no restaurante
     */
    ProdutoResponseDTO cadastrarProduto(ProdutoDTO dto);

    /**
     * Busca produtos por restaurante (apenas disponíveis).
     * 
     * @param restauranteId ID do restaurante
     * @return List<ProdutoResponseDTO> Lista de produtos disponíveis do restaurante
     * @throws EntityNotFoundException Se restaurante não existir
     */
    List<ProdutoResponseDTO> buscarProdutosPorRestaurante(Long restauranteId);

    /**
     * Busca um produto por ID com validação de disponibilidade.
     * 
     * @param id ID do produto a ser buscado
     * @return ProdutoResponseDTO Produto encontrado
     * @throws EntityNotFoundException Se produto não existir
     * @throws BusinessException Se produto não estiver disponível
     */
    ProdutoResponseDTO buscarProdutoPorId(Long id);

    /**
     * Atualiza dados de um produto existente.
     * 
     * REGRAS DE NEGÓCIO:
     * - Produto deve existir
     * - Restaurante deve continuar existindo e ativo
     * - Nome deve continuar único dentro do restaurante
     * - Preço deve ser maior que zero
     * 
     * @param id ID do produto a ser atualizado
     * @param dto Novos dados do produto
     * @return ProdutoResponseDTO Produto atualizado
     * @throws EntityNotFoundException Se produto não existir
     * @throws BusinessException Se dados inválidos
     */
    ProdutoResponseDTO atualizarProduto(Long id, ProdutoDTO dto);

    /**
     * Altera a disponibilidade de um produto (toggle disponível/indisponível).
     * 
     * REGRAS DE NEGÓCIO:
     * - Produto deve existir
     * - Não permite tornar indisponível produto em pedidos pendentes
     * 
     * @param id ID do produto
     * @param disponivel Novo status de disponibilidade
     * @return ProdutoResponseDTO Produto com disponibilidade atualizada
     * @throws EntityNotFoundException Se produto não existir
     * @throws BusinessException Se produto estiver em pedidos pendentes
     */
    ProdutoResponseDTO alterarDisponibilidade(Long id, boolean disponivel);

    /**
     * Busca produtos por categoria específica.
     * 
     * CATEGORIAS VÁLIDAS:
     * - ENTRADA, PRATO_PRINCIPAL, SOBREMESA, BEBIDA, LANCHE, PIZZA, etc.
     * 
     * @param categoria Categoria dos produtos
     * @return List<ProdutoResponseDTO> Lista de produtos disponíveis da categoria
     */
    List<ProdutoResponseDTO> buscarProdutosPorCategoria(String categoria);
}