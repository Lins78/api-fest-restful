package com.exemplo.apifest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configurações de Cache do Sistema
 * Roteiro 10 - Implementação de Cache
 */
@Component
@ConfigurationProperties(prefix = "app.cache")
public class CacheProperties {

    /**
     * Provedor de cache (caffeine ou redis)
     */
    private String provider = "caffeine";

    /**
     * TTL padrão em minutos
     */
    private long defaultTtl = 10;

    /**
     * Tamanho máximo do cache
     */
    private long maxSize = 1000;

    /**
     * Configurações de estatísticas
     */
    private Stats stats = new Stats();

    // Getters e Setters
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public long getDefaultTtl() {
        return defaultTtl;
    }

    public void setDefaultTtl(long defaultTtl) {
        this.defaultTtl = defaultTtl;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    /**
     * Configurações de estatísticas do cache
     */
    public static class Stats {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}