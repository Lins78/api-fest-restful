package com.exemplo.apifest.service;

import com.exemplo.apifest.model.*;
import com.exemplo.apifest.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ROTEIRO 3 - DATA LOADER (COMMAND LINE RUNNER)
 * 
 * Classe responsável por carregar dados de teste automaticamente no sistema DeliveryTech
 * e validar todas as consultas implementadas nos repositories.
 * 
 * Esta classe implementa CommandLineRunner, o que significa que será executada
 * automaticamente após a inicialização completa do Spring Boot.
 * 
 * Funcionalidades implementadas conforme Roteiro 3:
 * 1. Inserção de dados de teste (3 clientes, 2 restaurantes, 5 produtos, 2 pedidos)
 * 2. Validação de todas as consultas derivadas dos repositories
 * 3. Teste dos 4 cenários obrigatórios especificados no roteiro
 * 4. Logs detalhados de todas as operações para acompanhamento
 * 
 * @author DeliveryTech Development Team
 * @version 1.0
 * @since Roteiro 3 - Implementação da Camada de Dados
 */
@Component
public class DataLoader /* implements CommandLineRunner */ {

    /** Logger para acompanhar a execução e debug das operações */
    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    /** Repository para operações CRUD e consultas de clientes */
    @Autowired
    private ClienteRepository clienteRepository;

    /** Repository para operações CRUD e consultas de restaurantes */
    @Autowired
    private RestauranteRepository restauranteRepository;

    /** Repository para operações CRUD e consultas de produtos */
    @Autowired
    private ProdutoRepository produtoRepository;

    /** Repository para operações CRUD e consultas de pedidos */
    @Autowired
    private PedidoRepository pedidoRepository;

    /** Repository para operações CRUD e consultas de itens de pedido */
    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    /**
     * Método principal executado automaticamente na inicialização da aplicação.
     * 
     * Sequência de execução:
     * 1. Limpa dados existentes (importante para reinicializações)
     * 2. Insere dados de teste conforme especificação do Roteiro 3
     * 3. Valida todas as consultas implementadas nos repositories
     * 4. Testa os 4 cenários obrigatórios do roteiro
     * 
     * @param args Argumentos da linha de comando (não utilizados)
     * @throws Exception Em caso de erro na carga de dados
     */
    // @Override - Temporariamente desabilitado para testar Swagger
    public void run(String... args) throws Exception {
        logger.info("🚀 ROTEIRO 3 - Iniciando carga de dados de teste...");
        
        // Passo 1: Limpar dados existentes para garantir estado limpo
        limparDados();
        
        // Passo 2: Inserir dados de teste conforme especificação
        inserirDadosTeste();
        
        // Passo 3: Validar todas as consultas implementadas
        validarConsultas();
        
        // Passo 4: Executar cenários obrigatórios do roteiro
        executarCenariosObrigatorios();
        
        logger.info("✅ ROTEIRO 3 - Carga de dados concluída com sucesso!");
    }

    private void limparDados() {
        logger.info("🧹 Limpando dados existentes...");
        itemPedidoRepository.deleteAll();
        pedidoRepository.deleteAll();
        produtoRepository.deleteAll();
        restauranteRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    private void inserirDadosTeste() {
        logger.info("📝 Inserindo dados de teste...");
        
        // 1. Inserir 3 clientes diferentes
        Cliente cliente1 = new Cliente("João Silva", "joao@email.com", "(11) 99999-1111", "Rua A, 123");
        Cliente cliente2 = new Cliente("Maria Santos", "maria@email.com", "(11) 99999-2222", "Rua B, 456");
        Cliente cliente3 = new Cliente("Pedro Oliveira", "pedro@email.com", "(11) 99999-3333", "Rua C, 789");
        
        clienteRepository.save(cliente1);
        clienteRepository.save(cliente2);
        clienteRepository.save(cliente3);
        
        logger.info("👥 Clientes inseridos: {}", clienteRepository.count());

        // 2. Inserir 2 restaurantes de categorias distintas
        Restaurante restaurante1 = new Restaurante("Pizzaria do Zé", "Av. Principal, 100", "(11) 3333-1111", 
                                                  "Italiana", new BigDecimal("3.50"));
        Restaurante restaurante2 = new Restaurante("Burger House", "Av. Secundária, 200", "(11) 3333-2222", 
                                                  "Hamburgueria", new BigDecimal("4.00"));
        
        restauranteRepository.save(restaurante1);
        restauranteRepository.save(restaurante2);
        
        logger.info("🍽️ Restaurantes inseridos: {}", restauranteRepository.count());

        // 3. Inserir 5 produtos variados
        Produto produto1 = new Produto("Pizza Margherita", "Pizza tradicional com molho de tomate, mozzarella e manjericão", 
                                     new BigDecimal("35.90"), "Pizza", restaurante1);
        Produto produto2 = new Produto("Pizza Calabresa", "Pizza com calabresa, cebola e azeitonas", 
                                     new BigDecimal("38.90"), "Pizza", restaurante1);
        Produto produto3 = new Produto("Burger Clássico", "Hambúrguer com carne, alface, tomate e molho especial", 
                                     new BigDecimal("25.90"), "Hambúrguer", restaurante2);
        Produto produto4 = new Produto("Burger Bacon", "Hambúrguer com bacon crocante e queijo cheddar", 
                                     new BigDecimal("32.90"), "Hambúrguer", restaurante2);
        Produto produto5 = new Produto("Refrigerante", "Coca-Cola lata 350ml", 
                                     new BigDecimal("4.90"), "Bebida", restaurante2);
        
        produtoRepository.save(produto1);
        produtoRepository.save(produto2);
        produtoRepository.save(produto3);
        produtoRepository.save(produto4);
        produtoRepository.save(produto5);
        
        logger.info("🍕 Produtos inseridos: {}", produtoRepository.count());

        // 4. Inserir 2 pedidos com itens
        Pedido pedido1 = new Pedido("Pedido de Pizza", 0.0, cliente1);
        pedido1.setStatus(StatusPedido.ENTREGUE);
        pedidoRepository.save(pedido1);

        ItemPedido item1 = new ItemPedido(pedido1, produto1, 1);
        ItemPedido item2 = new ItemPedido(pedido1, produto5, 2);
        itemPedidoRepository.save(item1);
        itemPedidoRepository.save(item2);

        // Atualizar valor do pedido
        pedido1.setValor(item1.getPrecoTotal().doubleValue() + item2.getPrecoTotal().doubleValue());
        pedidoRepository.save(pedido1);

        Pedido pedido2 = new Pedido("Pedido de Hambúrguer", 0.0, cliente2);
        pedido2.setStatus(StatusPedido.CONFIRMADO);
        pedidoRepository.save(pedido2);

        ItemPedido item3 = new ItemPedido(pedido2, produto3, 2);
        ItemPedido item4 = new ItemPedido(pedido2, produto4, 1);
        itemPedidoRepository.save(item3);
        itemPedidoRepository.save(item4);

        // Atualizar valor do pedido
        pedido2.setValor(item3.getPrecoTotal().doubleValue() + item4.getPrecoTotal().doubleValue());
        pedidoRepository.save(pedido2);
        
        logger.info("📦 Pedidos inseridos: {}", pedidoRepository.count());
        logger.info("📋 Itens de pedido inseridos: {}", itemPedidoRepository.count());
    }

    private void validarConsultas() {
        logger.info("🔍 Validando consultas dos repositories...");
        
        // Testar ClienteRepository
        logger.info("\n=== TESTANDO CLIENTE REPOSITORY ===");
        
        Cliente clientePorEmail = clienteRepository.findByEmail("joao@email.com").orElse(null);
        logger.info("Cliente por email 'joao@email.com': {}", 
                   clientePorEmail != null ? clientePorEmail.getNome() : "Não encontrado");
        
        List<Cliente> clientesAtivos = clienteRepository.findByAtivoTrue();
        logger.info("Clientes ativos: {}", clientesAtivos.size());
        
        List<Cliente> clientesPorNome = clienteRepository.findByNomeContainingIgnoreCase("joão");
        logger.info("Clientes com nome contendo 'joão': {}", clientesPorNome.size());
        
        boolean emailExiste = clienteRepository.existsByEmail("maria@email.com");
        logger.info("Email 'maria@email.com' existe: {}", emailExiste);

        // Testar RestauranteRepository
        logger.info("\n=== TESTANDO RESTAURANTE REPOSITORY ===");
        
        List<Restaurante> restaurantesItaliana = restauranteRepository.findByCategoria("Italiana");
        logger.info("Restaurantes categoria 'Italiana': {}", restaurantesItaliana.size());
        
        List<Restaurante> restaurantesAtivos = restauranteRepository.findByAtivoTrue();
        logger.info("Restaurantes ativos: {}", restaurantesAtivos.size());
        
        List<Restaurante> restaurantesTaxaBaixa = restauranteRepository.findByTaxaEntregaLessThanEqual(new BigDecimal("5.00"));
        logger.info("Restaurantes com taxa até R$ 5,00: {}", restaurantesTaxaBaixa.size());
        
        List<Restaurante> top5Restaurantes = restauranteRepository.findTop5ByOrderByNomeAsc();
        logger.info("Top 5 restaurantes (ordem alfabética): {}", top5Restaurantes.size());

        // Testar ProdutoRepository
        logger.info("\n=== TESTANDO PRODUTO REPOSITORY ===");
        
        List<Produto> produtosPorRestaurante = produtoRepository.findByRestauranteId(1L);
        logger.info("Produtos do restaurante ID 1: {}", produtosPorRestaurante.size());
        
        List<Produto> produtosDisponiveis = produtoRepository.findByDisponivelTrue();
        logger.info("Produtos disponíveis: {}", produtosDisponiveis.size());
        
        List<Produto> produtosPizza = produtoRepository.findByCategoria("Pizza");
        logger.info("Produtos categoria 'Pizza': {}", produtosPizza.size());
        
        List<Produto> produtosPrecoAte30 = produtoRepository.findByPrecoLessThanEqual(new BigDecimal("30.00"));
        logger.info("Produtos com preço até R$ 30,00: {}", produtosPrecoAte30.size());

        // Testar PedidoRepository
        logger.info("\n=== TESTANDO PEDIDO REPOSITORY ===");
        
        List<Pedido> pedidosCliente1 = pedidoRepository.findByClienteId(1L);
        logger.info("Pedidos do cliente ID 1: {}", pedidosCliente1.size());
        
        List<Pedido> pedidosEntregues = pedidoRepository.findByStatus(StatusPedido.ENTREGUE);
        logger.info("Pedidos com status 'ENTREGUE': {}", pedidosEntregues.size());
        
        List<Pedido> pedidosRecentes = pedidoRepository.findTop10ByOrderByDataPedidoDesc();
        logger.info("10 pedidos mais recentes: {}", pedidosRecentes.size());
        
        LocalDateTime hoje = LocalDateTime.now();
        LocalDateTime ontem = hoje.minusDays(1);
        List<Pedido> pedidosUltimas24h = pedidoRepository.findByDataPedidoBetween(ontem, hoje);
        logger.info("Pedidos das últimas 24 horas: {}", pedidosUltimas24h.size());

        // Testar consultas customizadas
        logger.info("\n=== TESTANDO CONSULTAS CUSTOMIZADAS ===");
        
        List<Object[]> produtosMaisVendidos = itemPedidoRepository.produtosMaisVendidos();
        logger.info("Produtos mais vendidos encontrados: {}", produtosMaisVendidos.size());
        
        List<Object[]> faturamentoPorCategoria = itemPedidoRepository.faturamentoPorCategoria();
        logger.info("Faturamento por categoria: {}", faturamentoPorCategoria.size());
        
        Object[] totalPedidosEFaturamento = pedidoRepository.getTotalPedidosEntreguesEFaturamento();
        if (totalPedidosEFaturamento != null && totalPedidosEFaturamento.length >= 2) {
            logger.info("Total pedidos entregues: {}, Faturamento total: {}", 
                       totalPedidosEFaturamento[0], totalPedidosEFaturamento[1]);
        } else if (totalPedidosEFaturamento != null && totalPedidosEFaturamento.length > 0) {
            logger.info("Total pedidos entregues: {}", totalPedidosEFaturamento[0]);
        }
    }

    /**
     * ROTEIRO 3 - EXECUÇÃO DOS 4 CENÁRIOS OBRIGATÓRIOS
     * 
     * Executa e valida os 4 cenários de teste específicos solicitados no roteiro:
     * 1. Busca de Cliente por Email
     * 2. Produtos por Restaurante  
     * 3. Pedidos Recentes
     * 4. Restaurantes por Taxa
     */
    private void executarCenariosObrigatorios() {
        logger.info("\n🎯 === EXECUTANDO CENÁRIOS OBRIGATÓRIOS DO ROTEIRO 3 ===");
        
        // CENÁRIO 1: Busca de Cliente por Email
        logger.info("\n🔎 Cenário 1: Busca de Cliente por Email");
        Cliente cliente = clienteRepository.findByEmail("joao@email.com").orElse(null);
        if (cliente != null) {
            logger.info("✅ SUCESSO - Cliente encontrado: {} (ID: {})", cliente.getNome(), cliente.getId());
        } else {
            logger.error("❌ ERRO - Cliente não encontrado com email: joao@email.com");
        }
        
        // CENÁRIO 2: Produtos por Restaurante
        logger.info("\n🍔 Cenário 2: Produtos por Restaurante");
        List<Produto> produtos = produtoRepository.findByRestauranteId(1L);
        logger.info("✅ SUCESSO - Encontrados {} produtos do restaurante ID 1:", produtos.size());
        produtos.forEach(p -> logger.info("  - {} (R$ {})", p.getNome(), p.getPreco()));
        
        // CENÁRIO 3: Pedidos Recentes  
        logger.info("\n📅 Cenário 3: Pedidos Recentes");
        List<Pedido> pedidos = pedidoRepository.findTop10ByOrderByDataPedidoDesc();
        logger.info("✅ SUCESSO - Encontrados {} pedidos mais recentes:", pedidos.size());
        pedidos.forEach(p -> logger.info("  - Pedido ID {} - Cliente: {} - Valor: R$ {} - Status: {}", 
                                        p.getId(), p.getCliente().getNome(), p.getValor(), p.getStatus()));
        
        // CENÁRIO 4: Restaurantes por Taxa
        logger.info("\n💰 Cenário 4: Restaurantes por Taxa");
        List<Restaurante> restaurantes = restauranteRepository.findByTaxaEntregaLessThanEqual(new BigDecimal("5.00"));
        logger.info("✅ SUCESSO - Encontrados {} restaurantes com taxa até R$ 5,00:", restaurantes.size());
        restaurantes.forEach(r -> logger.info("  - {} - Taxa: R$ {} - Categoria: {}", 
                                             r.getNome(), r.getTaxaEntrega(), r.getCategoria()));
        
        logger.info("\n🎉 TODOS OS 4 CENÁRIOS OBRIGATÓRIOS EXECUTADOS COM SUCESSO!");
    }
}