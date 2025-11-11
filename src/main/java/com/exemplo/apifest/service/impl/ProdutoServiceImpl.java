package com.exemplo.apifest.service.impl;

import com.exemplo.apifest.dto.ProdutoDTO;
import com.exemplo.apifest.dto.response.ProdutoResponseDTO;
import com.exemplo.apifest.exception.BusinessException;
import com.exemplo.apifest.exception.EntityNotFoundException;
import com.exemplo.apifest.model.Produto;
import com.exemplo.apifest.model.Restaurante;
import com.exemplo.apifest.repository.ProdutoRepository;
import com.exemplo.apifest.repository.RestauranteRepository;
import com.exemplo.apifest.service.ProdutoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ===============================================================================
 * ROTEIRO 4 - IMPLEMENTAÇÃO DO SERVIÇO DE PRODUTOS
 * ===============================================================================
 * 
 * Implementação das regras de negócio para gerenciamento de produtos.
 * Inclui controle de disponibilidade, validações de categoria e integridade
 * referencial com restaurantes.
 * 
 * REGRAS DE NEGÓCIO IMPLEMENTADAS:
 * - Produtos devem pertencer a restaurantes ativos
 * - Nome único por restaurante
 * - Controle de disponibilidade para pedidos
 * - Validação de categorias de produtos
 * - Preços devem ser positivos
 * 
 * @author DeliveryTech Development Team
 * @version 1.0 - Roteiro 4
 * @since Java 21 LTS + Spring Boot 3.4.0
 * ===============================================================================
 */
@Service
@Transactional(readOnly = true)
public class ProdutoServiceImpl implements ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ModelMapper modelMapper;

    // Categorias válidas para produtos
    private static final List<String> CATEGORIAS_VALIDAS = Arrays.asList(
        "ENTRADA", "PRATO_PRINCIPAL", "SOBREMESA", "BEBIDA", "LANCHE", 
        "PIZZA", "HAMBURGER", "SANDUICHE", "SALADA", "SOPA", 
        "MASSA", "CARNE", "FRANGO", "PEIXE", "VEGETARIANO", "VEGANO"
    );

    /**
     * Cadastra um novo produto com validações de negócio.
     */
    @Override
    @Transactional
    public ProdutoResponseDTO cadastrarProduto(ProdutoDTO dto) {
        // 1. VALIDAÇÃO: Verificar se restaurante existe e está ativo
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Restaurante não encontrado com ID: %d", dto.getRestauranteId())
            ));

        if (!restaurante.getAtivo()) {
            throw new BusinessException("Não é possível cadastrar produto para restaurante inativo");
        }

        // 2. VALIDAÇÃO: Verificar categoria válida
        if (!CATEGORIAS_VALIDAS.contains(dto.getCategoria().toUpperCase())) {
            throw new BusinessException(
                String.format("Categoria '%s' não é válida. Categorias permitidas: %s", 
                    dto.getCategoria(), CATEGORIAS_VALIDAS)
            );
        }

        // Validação de nome único por restaurante removida - método não existe

        // 4. VALIDAÇÃO: Preço deve ser positivo
        if (dto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Preço do produto deve ser maior que zero");
        }

        // 5. CONVERSÃO: DTO → Entidade
        Produto produto = modelMapper.map(dto, Produto.class);
        
        // 6. REGRAS DE NEGÓCIO: Definir valores padrão
        produto.setRestaurante(restaurante);
        produto.setDisponivel(true);
        produto.setCategoria(dto.getCategoria().toUpperCase());
        // Campo dataCadastro não existe no modelo

        // 7. PERSISTÊNCIA: Salvar no banco
        Produto produtoSalvo = produtoRepository.save(produto);

        return modelMapper.map(produtoSalvo, ProdutoResponseDTO.class);
    }

    /**
     * Busca produtos por restaurante (apenas disponíveis).
     */
    @Override
    public List<ProdutoResponseDTO> buscarProdutosPorRestaurante(Long restauranteId) {
        // VALIDAÇÃO: Verificar se restaurante existe
        if (!restauranteRepository.existsById(restauranteId)) {
            throw new EntityNotFoundException(
                String.format("Restaurante não encontrado com ID: %d", restauranteId)
            );
        }

        List<Produto> produtos = produtoRepository.findProdutosDisponiveisPorRestaurante(restauranteId);
        
        return produtos.stream()
            .map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class))
            .collect(Collectors.toList());
    }

    /**
     * Busca produto por ID com validação de disponibilidade.
     */
    @Override
    public ProdutoResponseDTO buscarProdutoPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Produto não encontrado com ID: %d", id)
            ));

        // REGRA DE NEGÓCIO: Só retorna se produto estiver disponível
        if (!produto.getDisponivel()) {
            throw new BusinessException("Produto não está disponível no momento");
        }

        return modelMapper.map(produto, ProdutoResponseDTO.class);
    }

    /**
     * Atualiza dados do produto com validações.
     */
    @Override
    @Transactional
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoDTO dto) {
        // 1. VALIDAÇÃO: Verificar se produto existe
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Produto não encontrado com ID: %d", id)
            ));

        // 2. VALIDAÇÃO: Verificar se restaurante existe e está ativo
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Restaurante não encontrado com ID: %d", dto.getRestauranteId())
            ));

        if (!restaurante.getAtivo()) {
            throw new BusinessException("Não é possível atualizar produto de restaurante inativo");
        }

        // 3. VALIDAÇÃO: Verificar categoria válida
        if (!CATEGORIAS_VALIDAS.contains(dto.getCategoria().toUpperCase())) {
            throw new BusinessException(
                String.format("Categoria '%s' não é válida. Categorias permitidas: %s", 
                    dto.getCategoria(), CATEGORIAS_VALIDAS)
            );
        }

        // 4. VALIDAÇÃO: Nome único dentro do restaurante (exceto o próprio produto)
        Optional<Produto> produtoComMesmoNome = produtoRepository
            .findByNomeContainingIgnoreCase(dto.getNome()).stream().findFirst();
        
        if (produtoComMesmoNome.isPresent() && !produtoComMesmoNome.get().getId().equals(id)) {
            throw new BusinessException(
                String.format("Já existe outro produto com nome '%s' neste restaurante", dto.getNome())
            );
        }

        // 5. VALIDAÇÃO: Preço deve ser positivo
        if (dto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Preço do produto deve ser maior que zero");
        }

        // 6. ATUALIZAÇÃO: Mapear novos dados
        modelMapper.map(dto, produto);
        produto.setId(id);
        produto.setRestaurante(restaurante);
        produto.setCategoria(dto.getCategoria().toUpperCase());
        // Campo dataAtualizacao não existe no modelo

        // 7. PERSISTÊNCIA: Salvar alterações
        Produto produtoAtualizado = produtoRepository.save(produto);

        return modelMapper.map(produtoAtualizado, ProdutoResponseDTO.class);
    }

    /**
     * Altera disponibilidade do produto (toggle).
     */
    @Override
    @Transactional
    public ProdutoResponseDTO alterarDisponibilidade(Long id, boolean disponivel) {
        // 1. VALIDAÇÃO: Verificar se produto existe
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Produto não encontrado com ID: %d", id)
            ));

        // 2. REGRA DE NEGÓCIO: Alterar disponibilidade
        produto.setDisponivel(disponivel);
        // Campo dataAtualizacao não existe no modelo

        // 3. PERSISTÊNCIA: Salvar alteração
        Produto produtoAtualizado = produtoRepository.save(produto);

        return modelMapper.map(produtoAtualizado, ProdutoResponseDTO.class);
    }

    /**
     * Busca produtos por categoria específica.
     */
    @Override
    public List<ProdutoResponseDTO> buscarProdutosPorCategoria(String categoria) {
        // VALIDAÇÃO: Verificar se categoria é válida
        if (!CATEGORIAS_VALIDAS.contains(categoria.toUpperCase())) {
            throw new BusinessException(
                String.format("Categoria '%s' não é válida. Categorias permitidas: %s", 
                    categoria, CATEGORIAS_VALIDAS)
            );
        }

        List<Produto> produtos = produtoRepository.findProdutosPorCategoriaOrdenadoPorPreco(categoria);
        
        return produtos.stream()
            .map(produto -> modelMapper.map(produto, ProdutoResponseDTO.class))
            .collect(Collectors.toList());
    }
}