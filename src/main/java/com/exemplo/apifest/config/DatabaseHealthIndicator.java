package com.exemplo.apifest.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Health Indicator customizado para verificar a saúde do banco de dados.
 * 
 * Este componente verifica se a conexão com o banco está funcionando
 * corretamente e retorna informações detalhadas sobre o status.
 * 
 * Acessível via: GET /actuator/health
 * 
 * @author DeliveryTech Team
 * @version 1.0 - Roteiro 8
 */
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            // Testa a conexão executando uma query simples
            if (connection.isValid(1)) {
                return Health.up()
                    .withDetail("database", "H2/PostgreSQL")
                    .withDetail("status", "Connection successful")
                    .withDetail("url", connection.getMetaData().getURL())
                    .build();
            } else {
                return Health.down()
                    .withDetail("database", "Connection invalid")
                    .build();
            }
        } catch (SQLException e) {
            return Health.down()
                .withDetail("database", "Connection failed")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}