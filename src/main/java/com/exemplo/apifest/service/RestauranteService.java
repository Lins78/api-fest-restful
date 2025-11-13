package com.exemplo.apifest.service;

import com.exemplo.apifest.dto.RestauranteDTO;
import com.exemplo.apifest.dto.response.RestauranteResponseDTO;
import java.math.BigDecimal;
import java.util.List;

/**
 * ===============================================================================
 * ROTEIRO 4 - INTERFACE DO SERVIÇO DE RESTAURANTES
 * ===============================================================================
 * 
 * Esta interface define o contrato para o serviço de restaurantes, contendo todas as
 * operações de negócio relacionadas ao gerenciamento de restaurantes na API FEST.
 * 
 * Responsabilidades:
 * - Definir operações CRUD para restaurantes
 * - Estabelecer contratos para validações de negócio
 * - Padronizar métodos de busca e filtros por categoria
 * - Calcular taxas de entrega baseadas em localização
 * 
 * @author DeliveryTech Development Team
 * @version 1.0 - Roteiro 4
 * @since Java 21 LTS + Spring Boot 3.4.0
 * ===============================================================================
 */
public interface RestauranteService {

    /**
     * Cadastra um novo restaurante no sistema com validações completas.
     * 
     * REGRAS DE NEGÓCIO:
     * - Nome deve ser único no sistema
     * - CNPJ deve ser válido e único
     * - Categoria deve ser uma das categorias válidas
     * - Endereço deve ser completo e válido
     * - Taxa de entrega deve ser >= 0
     * - Restaurante é criado com status ATIVO por padrão
     * 
     * @param dto Dados do restaurante a ser cadastrado
     * @return RestauranteResponseDTO Restaurante cadastrado com ID gerado
     * @throws BusinessException Se dados inválidos ou CNPJ já existir
     */
    RestauranteResponseDTO cadastrarRestaurante(RestauranteDTO dto);

    /**
     * Busca um restaurante por ID com validação de existência.
     * 
     * @param id ID do restaurante a ser buscado
     * @return RestauranteResponseDTO Restaurante encontrado
     * @throws EntityNotFoundException Se restaurante não existir
     */
    RestauranteResponseDTO buscarRestaurantePorId(Long id);

    /**
     * Busca um restaurante por ID (alias para compatibilidade)
     */
    RestauranteResponseDTO buscarPorId(Long id);

    /**
     * Busca restaurantes por categoria específica.
     * 
     * CATEGORIAS VÁLIDAS:
     * - PIZZA, HAMBURGER, JAPONESA, ITALIANA, BRASILEIRA, MEXICANA, VEGANA, etc.
     * 
     * @param categoria Categoria dos restaurantes
     * @return List<RestauranteResponseDTO> Lista de restaurantes da categoria
     */
    List<RestauranteResponseDTO> buscarRestaurantesPorCategoria(String categoria);

    /**
     * Busca restaurantes por categoria (alias para compatibilidade)
     */
    List<RestauranteResponseDTO> buscarPorCategoria(String categoria);

    /**
     * Lista todos os restaurantes disponíveis (ativos).
     * 
     * @return List<RestauranteResponseDTO> Lista de restaurantes ativos
     */
    List<RestauranteResponseDTO> buscarRestaurantesDisponiveis();

    /**
     * Lista restaurantes disponíveis (alias para compatibilidade)
     */
    List<RestauranteResponseDTO> listarRestaurantesDisponiveis();

    /**
     * Atualiza dados de um restaurante existente.
     * 
     * REGRAS DE NEGÓCIO:
     * - Restaurante deve existir
     * - CNPJ deve continuar único (exceto para o próprio restaurante)
     * - Validações de formato e categoria
     * 
     * @param id ID do restaurante a ser atualizado
     * @param dto Novos dados do restaurante
     * @return RestauranteResponseDTO Restaurante atualizado
     * @throws EntityNotFoundException Se restaurante não existir
     * @throws BusinessException Se CNPJ já pertencer a outro restaurante
     */
    RestauranteResponseDTO atualizarRestaurante(Long id, RestauranteDTO dto);

    /**
     * Calcula a taxa de entrega baseada na localização do cliente.
     * 
     * ALGORITMO DE CÁLCULO:
     * - CEP mesma região: Taxa base do restaurante
     * - CEP região próxima: Taxa base + R$ 2,00
     * - CEP região distante: Taxa base + R$ 5,00
     * - CEP muito distante: Não entrega (exceção)
     * 
     * @param restauranteId ID do restaurante
     * @param cep CEP de entrega
     * @return BigDecimal Taxa de entrega calculada
     * @throws EntityNotFoundException Se restaurante não existir
     * @throws BusinessException Se não entrega no CEP informado
     */
    BigDecimal calcularTaxaEntrega(Long restauranteId, String cep);
}