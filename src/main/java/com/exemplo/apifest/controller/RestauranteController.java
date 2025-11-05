package com.exemplo.apifest.controller;

import com.exemplo.apifest.dto.RestauranteDTO;
import com.exemplo.apifest.dto.response.RestauranteResponseDTO;
import com.exemplo.apifest.service.RestauranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * ===============================================================================
 * ROTEIRO 4 - CONTROLLER REST DE RESTAURANTES
 * ===============================================================================
 * 
 * Controller responsável por expor endpoints RESTful para gerenciamento de restaurantes.
 * Inclui funcionalidades de filtros por categoria e cálculo de taxa de entrega.
 * 
 * ENDPOINTS IMPLEMENTADOS:
 * - POST   /api/restaurantes                        → Cadastrar restaurante (201)
 * - GET    /api/restaurantes/{id}                   → Buscar por ID (200)
 * - GET    /api/restaurantes                        → Listar disponíveis (200)
 * - GET    /api/restaurantes/categoria/{categoria}  → Por categoria (200)
 * - PUT    /api/restaurantes/{id}                   → Atualizar restaurante (200)
 * - GET    /api/restaurantes/{id}/taxa-entrega/{cep} → Calcular taxa (200)
 * 
 * @author DeliveryTech Development Team
 * @version 1.0 - Roteiro 4
 * @since Java 21 LTS + Spring Boot 3.4.0
 * ===============================================================================
 */
@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    /**
     * POST /api/restaurantes - Cadastrar novo restaurante
     */
    @PostMapping
    public ResponseEntity<RestauranteResponseDTO> cadastrarRestaurante(@Valid @RequestBody RestauranteDTO restauranteDTO) {
        RestauranteResponseDTO restauranteCriado = restauranteService.cadastrarRestaurante(restauranteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(restauranteCriado);
    }

    /**
     * GET /api/restaurantes/{id} - Buscar restaurante por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> buscarRestaurantePorId(@PathVariable Long id) {
        RestauranteResponseDTO restaurante = restauranteService.buscarRestaurantePorId(id);
        return ResponseEntity.ok(restaurante);
    }

    /**
     * GET /api/restaurantes - Listar restaurantes disponíveis
     */
    @GetMapping
    public ResponseEntity<List<RestauranteResponseDTO>> buscarRestaurantesDisponiveis() {
        List<RestauranteResponseDTO> restaurantes = restauranteService.buscarRestaurantesDisponiveis();
        return ResponseEntity.ok(restaurantes);
    }

    /**
     * GET /api/restaurantes/categoria/{categoria} - Buscar por categoria
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<RestauranteResponseDTO>> buscarPorCategoria(@PathVariable String categoria) {
        List<RestauranteResponseDTO> restaurantes = restauranteService.buscarRestaurantesPorCategoria(categoria);
        return ResponseEntity.ok(restaurantes);
    }

    /**
     * PUT /api/restaurantes/{id} - Atualizar restaurante
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> atualizarRestaurante(
            @PathVariable Long id,
            @Valid @RequestBody RestauranteDTO restauranteDTO) {
        RestauranteResponseDTO restauranteAtualizado = restauranteService.atualizarRestaurante(id, restauranteDTO);
        return ResponseEntity.ok(restauranteAtualizado);
    }

    /**
     * GET /api/restaurantes/{id}/taxa-entrega/{cep} - Calcular taxa de entrega
     */
    @GetMapping("/{id}/taxa-entrega/{cep}")
    public ResponseEntity<BigDecimal> calcularTaxaEntrega(
            @PathVariable Long id,
            @PathVariable String cep) {
        BigDecimal taxa = restauranteService.calcularTaxaEntrega(id, cep);
        return ResponseEntity.ok(taxa);
    }
}