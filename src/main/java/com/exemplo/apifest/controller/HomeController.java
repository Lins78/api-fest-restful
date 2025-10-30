package com.exemplo.apifest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

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

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("database", "H2 (development)");
        status.put("timestamp", LocalDateTime.now().toString());
        return status;
    }
}