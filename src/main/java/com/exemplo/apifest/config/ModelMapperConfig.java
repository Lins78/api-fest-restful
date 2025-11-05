package com.exemplo.apifest.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do ModelMapper para conversão entre DTOs e Entidades
 * 
 * O ModelMapper é uma biblioteca que facilita o mapeamento automático entre objetos,
 * especialmente útil para converter entidades JPA em DTOs e vice-versa.
 * 
 * Esta configuração garante que o mapeamento seja feito de forma precisa e eficiente.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 4 - Camada de Serviços e Controllers REST
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Cria e configura uma instância do ModelMapper como bean do Spring
     * 
     * O ModelMapper é configurado com:
     * - MatchingStrategies.STRICT: Garante que apenas campos com nomes exatos sejam mapeados
     * - Validação de configuração habilitada para detectar problemas de mapeamento
     * 
     * @return ModelMapper configurado e pronto para uso
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        
        // Estratégia de mapeamento rigorosa - apenas campos com nomes exatos
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        
        return mapper;
    }
}