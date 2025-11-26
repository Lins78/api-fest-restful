package com.exemplo.apifest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * ===============================================================================
 * APLICAÇÃO PRINCIPAL - API FEST RESTFUL
 * ===============================================================================
 * 
 * Classe principal da aplicação Spring Boot para sistema de delivery.
 * 
 * ROTEIRO 10 - FUNCIONALIDADES HABILITADAS:
 * - @EnableCaching: Habilita cache automático com anotações
 * 
 * @author DeliveryTech Development Team  
 * @version 1.0 - Roteiro 10 (Cache + Containerização)
 * @since Java 21 LTS + Spring Boot 3.4.12
 * ===============================================================================
 */
@SpringBootApplication
@EnableCaching
public class ApiFestRestfullApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiFestRestfullApplication.class, args);
    }
}