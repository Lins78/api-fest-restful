package com.exemplo.apifest.controller;

import com.exemplo.apifest.dto.ProdutoDTO;
import com.exemplo.apifest.dto.response.ProdutoResponseDTO;
import com.exemplo.apifest.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    /**
     * POST /api/produtos - Cadastrar novo produto
     */
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> cadastrarProduto(@Valid @RequestBody ProdutoDTO produtoDTO) {
        ProdutoResponseDTO produtoCriado = produtoService.cadastrarProduto(produtoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoCriado);
    }

    /**
     * GET /api/produtos/{id} - Buscar produto por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarProdutoPorId(@PathVariable Long id) {
        ProdutoResponseDTO produto = produtoService.buscarProdutoPorId(id);
        return ResponseEntity.ok(produto);
    }

    /**
     * GET /api/restaurantes/{restauranteId}/produtos - Produtos do restaurante
     */
    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosPorRestaurante(@PathVariable Long restauranteId) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarProdutosPorRestaurante(restauranteId);
        return ResponseEntity.ok(produtos);
    }

    /**
     * PUT /api/produtos/{id} - Atualizar produto
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizarProduto(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoDTO produtoDTO) {
        ProdutoResponseDTO produtoAtualizado = produtoService.atualizarProduto(id, produtoDTO);
        return ResponseEntity.ok(produtoAtualizado);
    }

    /**
     * PATCH /api/produtos/{id}/disponibilidade - Alterar disponibilidade
     */
    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<ProdutoResponseDTO> alterarDisponibilidade(
            @PathVariable Long id,
            @RequestParam boolean disponivel) {
        ProdutoResponseDTO produtoAtualizado = produtoService.alterarDisponibilidade(id, disponivel);
        return ResponseEntity.ok(produtoAtualizado);
    }

    /**
     * GET /api/produtos/categoria/{categoria} - Buscar por categoria
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosPorCategoria(@PathVariable String categoria) {
        List<ProdutoResponseDTO> produtos = produtoService.buscarProdutosPorCategoria(categoria);
        return ResponseEntity.ok(produtos);
    }
}