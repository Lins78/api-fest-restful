package com.exemplo.apifest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Sistema", description = "Endpoints de status e informações do sistema")
public class HomeController {

    @Operation(summary = "Página inicial da API", 
               description = "Retorna informações gerais sobre a API e seus endpoints")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações da API retornadas com sucesso")
    })
    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "🍽️ API FEST RESTful - Sistema de Restaurante");
        response.put("version", "1.0.0");
        response.put("status", "RUNNING");
        response.put("timestamp", LocalDateTime.now());
        response.put("endpoints", Map.of(
            "clientes", "/api/clientes",
            "produtos", "/api/produtos",
            "pedidos", "/api/pedidos",
            "restaurantes", "/api/restaurantes",
            "h2_console", "/h2-console"
        ));
        return response;
    }

    @Operation(summary = "Health check da API", 
               description = "Retorna o status de saúde da API e seus componentes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status de saúde retornado com sucesso")
    })
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("database", "H2 (development)");
        status.put("timestamp", LocalDateTime.now().toString());
        return status;
    }
}