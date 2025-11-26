package com.exemplo.apifest.config;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuração para Rate Limiting usando Bucket4j
 * Implementa controle de taxa de requisições por IP
 */
@Configuration
@EnableCaching
@EnableAspectJAutoProxy
public class RateLimitingConfig {

    private final Map<String, Bucket> bucketMap = new ConcurrentHashMap<>();

    /**
     * Cria um bucket para controle de rate limiting por IP
     * Permite 100 requisições por minuto por IP
     * 
     * @param ipAddress endereço IP do cliente
     * @return bucket configurado para o IP
     */
    public Bucket createNewBucket(String ipAddress) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(100)
                .refillIntervally(100, Duration.ofMinutes(1))
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Obtém o bucket para um IP específico
     * Cria um novo bucket se não existir
     * 
     * @param ipAddress endereço IP do cliente
     * @return bucket para o IP
     */
    public Bucket resolveBucket(String ipAddress) {
        return bucketMap.computeIfAbsent(ipAddress, this::createNewBucket);
    }

    /**
     * Configuração de bucket para endpoints administrativos
     * Limite mais restritivo: 20 requisições por minuto
     * 
     * @param ipAddress endereço IP do cliente
     * @return bucket configurado para endpoints admin
     */
    public Bucket resolveAdminBucket(String ipAddress) {
        String adminKey = "admin_" + ipAddress;
        return bucketMap.computeIfAbsent(adminKey, ip -> {
            Bandwidth limit = Bandwidth.builder()
                    .capacity(20)
                    .refillIntervally(20, Duration.ofMinutes(1))
                    .build();
            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        });
    }

    /**
     * Configuração de bucket para endpoints de autenticação
     * Limite mais restritivo: 10 tentativas por minuto
     * 
     * @param ipAddress endereço IP do cliente
     * @return bucket configurado para endpoints de auth
     */
    public Bucket resolveAuthBucket(String ipAddress) {
        String authKey = "auth_" + ipAddress;
        return bucketMap.computeIfAbsent(authKey, ip -> {
            Bandwidth limit = Bandwidth.builder()
                    .capacity(10)
                    .refillIntervally(10, Duration.ofMinutes(1))
                    .build();
            return Bucket.builder()
                    .addLimit(limit)
                    .build();
        });
    }
}