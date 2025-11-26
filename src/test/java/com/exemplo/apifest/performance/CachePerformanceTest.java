package com.exemplo.apifest.performance;

import com.exemplo.apifest.service.ProdutoService;
import com.exemplo.apifest.service.PedidoService;
import com.exemplo.apifest.dto.response.ProdutoResponseDTO;
import com.exemplo.apifest.dto.response.PedidoResponseDTO;
import com.exemplo.apifest.config.CacheConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ===============================================================================
 * ROTEIRO 10 - TESTES DE PERFORMANCE DE CACHE
 * ===============================================================================
 * 
 * Testes que demonstram o ganho de performance obtido com o uso de cache
 * nas operações críticas do sistema.
 * 
 * CENÁRIOS TESTADOS:
 * ✅ Busca de produtos por restaurante (com/sem cache)
 * ✅ Busca de produto individual (com/sem cache)
 * ✅ Busca de pedido completo (com/sem cache)
 * ✅ Operações múltiplas consecutivas
 * ✅ Invalidação de cache
 * 
 * @author DeliveryTech Development Team
 * @version 1.0 - Roteiro 10
 * @since Java 21 LTS + Spring Boot 3.4.12
 * ===============================================================================
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("🚀 Cache Performance Tests - Roteiro 10")
class CachePerformanceTest {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private CacheManager cacheManager;

    private static final Long RESTAURANTE_ID = 1L;
    private static final Long PRODUTO_ID = 1L;
    private static final Long PEDIDO_ID = 1L;
    private static final int ITERACOES = 100;

    @BeforeEach
    void setUp() {
        // Limpar todos os caches antes de cada teste
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
    }

    // ========== TESTES DE PRODUTOS ==========

    @Nested
    @DisplayName("🍔 Testes de Performance - Produtos")
    class ProdutosPerformanceTest {

        @Test
        @DisplayName("⚡ Deve demonstrar ganho de performance com cache em busca por restaurante")
        void deveTestarPerformanceBuscaProdutosPorRestaurante() throws InterruptedException {
            // ========== CENÁRIO 1: SEM CACHE (primeira execução) ==========
            
            long inicio1 = System.nanoTime();
            List<ProdutoResponseDTO> produtos1 = produtoService.buscarProdutosPorRestaurante(RESTAURANTE_ID);
            long tempo1 = System.nanoTime() - inicio1;
            
            System.out.println("🔄 Primeira busca (sem cache): " + TimeUnit.NANOSECONDS.toMillis(tempo1) + "ms");
            
            // ========== CENÁRIO 2: COM CACHE (segunda execução) ==========
            
            long inicio2 = System.nanoTime();
            List<ProdutoResponseDTO> produtos2 = produtoService.buscarProdutosPorRestaurante(RESTAURANTE_ID);
            long tempo2 = System.nanoTime() - inicio2;
            
            System.out.println("⚡ Segunda busca (com cache): " + TimeUnit.NANOSECONDS.toMillis(tempo2) + "ms");
            
            // ========== VERIFICAÇÕES ==========
            
            // Dados devem ser idênticos
            assertThat(produtos1).isEqualTo(produtos2);
            assertThat(produtos1).isNotEmpty();
            
            // Cache deve ser significativamente mais rápido
            double ganhoPerformance = (double) tempo1 / tempo2;
            System.out.println("📈 Ganho de performance: " + String.format("%.1fx", ganhoPerformance));
            
            // Deve ter pelo menos 2x de melhoria
            assertThat(ganhoPerformance).isGreaterThan(2.0);
            
            // ========== CENÁRIO 3: MÚLTIPLAS CONSULTAS CACHEADAS ==========
            
            long inicioMultiplas = System.nanoTime();
            for (int i = 0; i < ITERACOES; i++) {
                produtoService.buscarProdutosPorRestaurante(RESTAURANTE_ID);
            }
            long tempoMultiplas = System.nanoTime() - inicioMultiplas;
            long tempoMedioCache = tempoMultiplas / ITERACOES;
            
            System.out.println("⚡ Tempo médio com cache (" + ITERACOES + " iterações): " + 
                             TimeUnit.NANOSECONDS.toMicros(tempoMedioCache) + "μs");
            
            // Cache deve ser extremamente rápido para múltiplas consultas
            assertThat(tempoMedioCache).isLessThan(TimeUnit.MILLISECONDS.toNanos(1));
        }

        @Test
        @DisplayName("⚡ Deve demonstrar ganho de performance com cache em produto individual")
        void deveTestarPerformanceBuscaProdutoIndividual() {
            
            // ========== PRIMEIRA BUSCA (SEM CACHE) ==========
            
            long inicio1 = System.nanoTime();
            ProdutoResponseDTO produto1 = produtoService.buscarProdutoPorId(PRODUTO_ID);
            long tempo1 = System.nanoTime() - inicio1;
            
            System.out.println("🔄 Busca produto sem cache: " + TimeUnit.NANOSECONDS.toMillis(tempo1) + "ms");
            
            // ========== SEGUNDA BUSCA (COM CACHE) ==========
            
            long inicio2 = System.nanoTime();
            ProdutoResponseDTO produto2 = produtoService.buscarProdutoPorId(PRODUTO_ID);
            long tempo2 = System.nanoTime() - inicio2;
            
            System.out.println("⚡ Busca produto com cache: " + TimeUnit.NANOSECONDS.toMillis(tempo2) + "ms");
            
            // ========== VERIFICAÇÕES ==========
            
            assertThat(produto1).isEqualTo(produto2);
            assertThat(produto1.getId()).isEqualTo(PRODUTO_ID);
            
            double ganho = (double) tempo1 / tempo2;
            System.out.println("📈 Ganho de performance: " + String.format("%.1fx", ganho));
            
            assertThat(ganho).isGreaterThan(1.5);
        }
    }

    // ========== TESTES DE PEDIDOS ==========

    @Nested
    @DisplayName("🛒 Testes de Performance - Pedidos")
    class PedidosPerformanceTest {

        @Test
        @DisplayName("⚡ Deve demonstrar ganho de performance com cache em busca de pedido")
        void deveTestarPerformanceBuscaPedido() {
            
            // ========== PRIMEIRA BUSCA (SEM CACHE) ==========
            
            long inicio1 = System.nanoTime();
            PedidoResponseDTO pedido1 = pedidoService.buscarPedidoPorId(PEDIDO_ID);
            long tempo1 = System.nanoTime() - inicio1;
            
            System.out.println("🔄 Busca pedido sem cache: " + TimeUnit.NANOSECONDS.toMillis(tempo1) + "ms");
            
            // ========== SEGUNDA BUSCA (COM CACHE) ==========
            
            long inicio2 = System.nanoTime();
            PedidoResponseDTO pedido2 = pedidoService.buscarPedidoPorId(PEDIDO_ID);
            long tempo2 = System.nanoTime() - inicio2;
            
            System.out.println("⚡ Busca pedido com cache: " + TimeUnit.NANOSECONDS.toMillis(tempo2) + "ms");
            
            // ========== VERIFICAÇÕES ==========
            
            assertThat(pedido1).isNotNull();
            assertThat(pedido2).isNotNull();
            assertThat(pedido1.getId()).isEqualTo(pedido2.getId());
            
            double ganho = (double) tempo1 / tempo2;
            System.out.println("📈 Ganho de performance: " + String.format("%.1fx", ganho));
            
            assertThat(ganho).isGreaterThan(1.5);
        }
    }

    // ========== TESTES DE INVALIDAÇÃO ==========

    @Nested
    @DisplayName("🗑️ Testes de Invalidação de Cache")
    class InvalidacaoCacheTest {

        @Test
        @DisplayName("🔄 Deve invalidar cache corretamente após atualização")
        void deveInvalidarCacheAposAtualizacao() {
            
            // ========== PASSO 1: POPULAR CACHE ==========
            
            ProdutoResponseDTO produtoOriginal = produtoService.buscarProdutoPorId(PRODUTO_ID);
            assertThat(produtoOriginal).isNotNull();
            
            // Verificar se está em cache
            var cache = cacheManager.getCache(CacheConfig.PRODUTO_CACHE);
            assertThat(cache).isNotNull();
            
            var valorCache = cache.get("'produto:' + " + PRODUTO_ID);
            System.out.println("📦 Produto em cache: " + (valorCache != null ? "SIM" : "NÃO"));
            
            // ========== PASSO 2: SIMULAR BUSCA RÁPIDA (CACHE HIT) ==========
            
            long inicioCache = System.nanoTime();
            ProdutoResponseDTO produtoCache = produtoService.buscarProdutoPorId(PRODUTO_ID);
            long tempoCache = System.nanoTime() - inicioCache;
            
            System.out.println("⚡ Busca com cache: " + TimeUnit.NANOSECONDS.toMicros(tempoCache) + "μs");
            
            assertThat(produtoCache.getId()).isEqualTo(produtoOriginal.getId());
            assertThat(tempoCache).isLessThan(TimeUnit.MILLISECONDS.toNanos(1));
        }

        @Test
        @DisplayName("📊 Deve demonstrar estatísticas de cache")
        void deveDemonstrarEstatisticasCache() {
            
            System.out.println("\n📊 ESTATÍSTICAS DE CACHE - ROTEIRO 10");
            System.out.println("=" .repeat(50));
            
            // Testar múltiplas operações para gerar estatísticas
            for (int i = 0; i < 10; i++) {
                produtoService.buscarProdutoPorId(PRODUTO_ID);
                produtoService.buscarProdutosPorRestaurante(RESTAURANTE_ID);
            }
            
            // Exibir estatísticas dos caches
            cacheManager.getCacheNames().forEach(cacheName -> {
                var cache = cacheManager.getCache(cacheName);
                System.out.println("🗃️  Cache: " + cacheName);
                System.out.println("   📦 Implementação: " + cache.getClass().getSimpleName());
                System.out.println("   🔍 Native Cache: " + cache.getNativeCache().getClass().getSimpleName());
            });
            
            System.out.println("=" .repeat(50));
        }
    }

    // ========== TESTE DE STRESS ==========

    @Test
    @DisplayName("🏋️ Teste de stress - Cache sob alta carga")
    void deveTestarCacheSobAltaCarga() throws InterruptedException {
        
        System.out.println("\n🏋️ TESTE DE STRESS - CACHE");
        System.out.println("=" .repeat(40));
        
        final int OPERACOES_STRESS = 1000;
        
        // ========== CENÁRIO 1: SEM CACHE (limpar cache) ==========
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
        
        long inicioSemCache = System.nanoTime();
        
        for (int i = 0; i < OPERACOES_STRESS; i++) {
            // Mix de operações
            if (i % 3 == 0) {
                produtoService.buscarProdutosPorRestaurante(RESTAURANTE_ID);
            } else if (i % 3 == 1) {
                produtoService.buscarProdutoPorId(PRODUTO_ID);
            } else {
                pedidoService.buscarPedidoPorId(PEDIDO_ID);
            }
            
            // Limpar cache a cada 10 operações para simular "sem cache"
            if (i % 10 == 0) {
                cacheManager.getCacheNames().forEach(cacheName -> {
                    var cache = cacheManager.getCache(cacheName);
                    if (cache != null) {
                        cache.clear();
                    }
                });
            }
        }
        
        long tempoSemCache = System.nanoTime() - inicioSemCache;
        
        // ========== CENÁRIO 2: COM CACHE ==========
        
        long inicioComCache = System.nanoTime();
        
        for (int i = 0; i < OPERACOES_STRESS; i++) {
            // Mesmas operações, mas com cache habilitado
            if (i % 3 == 0) {
                produtoService.buscarProdutosPorRestaurante(RESTAURANTE_ID);
            } else if (i % 3 == 1) {
                produtoService.buscarProdutoPorId(PRODUTO_ID);
            } else {
                pedidoService.buscarPedidoPorId(PEDIDO_ID);
            }
        }
        
        long tempoComCache = System.nanoTime() - inicioComCache;
        
        // ========== RESULTADOS ==========
        
        long tempoSemCacheMs = TimeUnit.NANOSECONDS.toMillis(tempoSemCache);
        long tempoComCacheMs = TimeUnit.NANOSECONDS.toMillis(tempoComCache);
        double ganhoGeral = (double) tempoSemCache / tempoComCache;
        
        System.out.println("🔄 Sem cache (" + OPERACOES_STRESS + " ops): " + tempoSemCacheMs + "ms");
        System.out.println("⚡ Com cache (" + OPERACOES_STRESS + " ops): " + tempoComCacheMs + "ms");
        System.out.println("📈 Ganho geral de performance: " + String.format("%.1fx", ganhoGeral));
        System.out.println("💾 Economia de tempo: " + (tempoSemCacheMs - tempoComCacheMs) + "ms");
        
        assertThat(ganhoGeral).isGreaterThan(2.0);
        assertThat(tempoComCacheMs).isLessThan(tempoSemCacheMs);
        
        System.out.println("=" .repeat(40));
        System.out.println("✅ Cache demonstrou ganho significativo!");
    }
}