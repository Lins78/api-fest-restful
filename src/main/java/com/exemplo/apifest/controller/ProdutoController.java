package com.exemplo.apifest.controller;

import com.exemplo.apifest.dto.ProdutoDTO;
import com.exemplo.apifest.dto.response.ProdutoResponseDTO;
import com.exemplo.apifest.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;
import java.util.List;

/**
 * ===============================================================================
 * ROTEIRO 4 - CONTROLLER REST DE PRODUTOS
 * ===============================================================================
 * 
 * Controller responsável por expor endpoints RESTful para gerenciamento de produtos.
 * Inclui funcionalidades de filtros por restaurante e categoria.
 * 
 * ENDPOINTS IMPLEMENTADOS:
 * - POST   /api/produtos                           → Cadastrar produto (201)
 * - GET    /api/produtos/{id}                      → Buscar por ID (200)
 * - GET    /api/restaurantes/{restauranteId}/produtos → Por restaurante (200)
 * - PUT    /api/produtos/{id}                      → Atualizar produto (200)
 * - PATCH  /api/produtos/{id}/disponibilidade     → Alterar disponibilidade (200)
 * - GET    /api/produtos/categoria/{categoria}    → Por categoria (200)
 * 
 * @author DeliveryTech Development Team
 * @version 1.0 - Roteiro 4
 * @since Java 21 LTS + Spring Boot 3.4.0
 * ===============================================================================
 */
@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
@Tag(name = "Produtos", description = "Operações relacionadas ao gerenciamento de produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    /**
     * POST /api/produtos - Cadastrar novo produto
     */
    @Operation(summary = "Cadastrar produto", 
               description = "Cadastra um novo produto no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Produto cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('RESTAURANTE')")
    public ResponseEntity<ProdutoResponseDTO> cadastrarProduto(@Valid @RequestBody ProdutoDTO produtoDTO) {
        ProdutoResponseDTO produtoCriado = produtoService.cadastrarProduto(produtoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoCriado);
    }

    /**
     * GET /api/produtos/{id} - Buscar produto por ID
     */
    @Operation(summary = "Buscar produto por ID", 
               description = "Busca um produto pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ProdutoResponseDTO> buscarProdutoPorId(
        @Parameter(description = "ID do produto") @PathVariable Long id) {
        ProdutoResponseDTO produto = produtoService.buscarProdutoPorId(id);
        return ResponseEntity.ok(produto);
    }

    /**
     * GET /api/restaurantes/{restauranteId}/produtos - Produtos do restaurante
     */
    @Operation(summary = "Buscar produtos por restaurante", 
               description = "Lista todos os produtos de um restaurante específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    @GetMapping("/restaurante/{restauranteId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosPorRestaurante(
        @Parameter(description = "ID do restaurante") @PathVariable Long restauranteId) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarProdutosPorRestaurante(restauranteId);
        return ResponseEntity.ok(produtos);
    }

    /**
     * PUT /api/produtos/{id} - Atualizar produto
     */
    @Operation(summary = "Atualizar produto", 
               description = "Atualiza os dados de um produto existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('RESTAURANTE') and @produtoService.pertenceAoRestaurante(#id, authentication.principal.restauranteId))")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(
            @Parameter(description = "ID do produto") @PathVariable Long id,
            @Valid @RequestBody ProdutoDTO produtoDTO) {
        ProdutoResponseDTO produtoAtualizado = produtoService.atualizarProduto(id, produtoDTO);
        return ResponseEntity.ok(produtoAtualizado);
    }

    /**
     * PATCH /api/produtos/{id}/disponibilidade - Alterar disponibilidade
     */
    @Operation(summary = "Alterar disponibilidade do produto", 
               description = "Altera o status de disponibilidade do produto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidade alterada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PatchMapping("/{id}/disponibilidade")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('RESTAURANTE') and @produtoService.pertenceAoRestaurante(#id, authentication.principal.restauranteId))")
    public ResponseEntity<ProdutoResponseDTO> alterarDisponibilidade(
            @Parameter(description = "ID do produto") @PathVariable Long id,
            @Parameter(description = "Status de disponibilidade") @RequestParam boolean disponivel) {
        ProdutoResponseDTO produtoAtualizado = produtoService.alterarDisponibilidade(id, disponivel);
        return ResponseEntity.ok(produtoAtualizado);
    }

    /**
     * GET /api/produtos/categoria/{categoria} - Buscar por categoria
     */
    @Operation(summary = "Buscar produtos por categoria", 
               description = "Lista todos os produtos de uma categoria específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
    })
    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosPorCategoria(
        @Parameter(description = "Categoria do produto") @PathVariable String categoria) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarProdutosPorCategoria(categoria);
        return ResponseEntity.ok(produtos);
    }
}