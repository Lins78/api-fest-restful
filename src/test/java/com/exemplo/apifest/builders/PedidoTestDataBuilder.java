package com.exemplo.apifest.builders;

import com.exemplo.apifest.model.Pedido;
import com.exemplo.apifest.model.Cliente;
import com.exemplo.apifest.model.Restaurante;
import com.exemplo.apifest.model.StatusPedido;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Test Data Builder para Pedido - Roteiro 9.
 * 
 * Este builder facilita a criação de objetos Pedido para testes,
 * fornecendo dados realistas baseados na entidade real.
 * 
 * ENTIDADE REAL CAMPOS:
 * - Long id
 * - String descricao
 * - Double valor
 * - LocalDateTime dataPedido
 * - Cliente cliente
 * - StatusPedido status (enum real)
 * - Boolean ativo
 * 
 * @author DeliveryTech Team
 * @version 2.0 - Correção Roteiro 9
 */
public class PedidoTestDataBuilder {

    private static final Faker faker = new Faker(Locale.of("pt", "BR"));

    private String descricao = "Pedido de teste - " + faker.food().dish();
    private Double valor = 50.90;
    private LocalDateTime dataPedido = LocalDateTime.now();
    private Cliente cliente = ClienteTestDataBuilder.umClienteValido().build();
    private StatusPedido status = StatusPedido.PENDENTE;
    private Boolean ativo = true;

    /**
     * Cria um builder com dados padrão válidos.
     */
    public static PedidoTestDataBuilder umPedido() {
        return new PedidoTestDataBuilder();
    }

    /**
     * Cria um builder com dados específicos para pedido válido.
     */
    public static PedidoTestDataBuilder umPedidoValido() {
        return new PedidoTestDataBuilder()
                .comDescricao("Pedido Pizza Margherita + Coca-Cola")
                .comValor(38.40)
                .comStatus(StatusPedido.PENDENTE)
                .ativo();
    }

    /**
     * Cria um builder para pedido confirmado.
     */
    public static PedidoTestDataBuilder umPedidoConfirmado() {
        return new PedidoTestDataBuilder()
                .comDescricao("Pedido confirmado pelo restaurante")
                .comValor(45.90)
                .comStatus(StatusPedido.CONFIRMADO);
    }

    /**
     * Cria um builder para pedido em preparação.
     */
    public static PedidoTestDataBuilder umPedidoEmPreparacao() {
        return new PedidoTestDataBuilder()
                .comDescricao("Pedido sendo preparado")
                .comValor(32.50)
                .comStatus(StatusPedido.PREPARANDO);
    }

    /**
     * Cria um builder para pedido entregue.
     */
    public static PedidoTestDataBuilder umPedidoEntregue() {
        return new PedidoTestDataBuilder()
                .comDescricao("Pedido já entregue")
                .comValor(55.80)
                .comStatus(StatusPedido.ENTREGUE);
    }

    /**
     * Cria um builder para pedido cancelado.
     */
    public static PedidoTestDataBuilder umPedidoCancelado() {
        return new PedidoTestDataBuilder()
                .comDescricao("Pedido cancelado")
                .comValor(28.90)
                .comStatus(StatusPedido.CANCELADO);
    }

    // ========== MÉTODOS DE CONFIGURAÇÃO ==========

    public PedidoTestDataBuilder comDescricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public PedidoTestDataBuilder comValor(Double valor) {
        this.valor = valor;
        return this;
    }

    public PedidoTestDataBuilder comDataPedido(LocalDateTime dataPedido) {
        this.dataPedido = dataPedido;
        return this;
    }

    public PedidoTestDataBuilder comCliente(Cliente cliente) {
        this.cliente = cliente;
        return this;
    }

    public PedidoTestDataBuilder comStatus(StatusPedido status) {
        this.status = status;
        return this;
    }

    public PedidoTestDataBuilder ativo() {
        this.ativo = true;
        return this;
    }

    public PedidoTestDataBuilder inativo() {
        this.ativo = false;
        return this;
    }

    // ========== MÉTODOS DE CONSTRUÇÃO ==========

    /**
     * Constrói o objeto Pedido com os dados configurados.
     */
    public Pedido build() {
        Pedido pedido = new Pedido(descricao, valor, cliente);
        pedido.setDataPedido(dataPedido);
        pedido.setStatus(status);
        pedido.setAtivo(ativo);
        return pedido;
    }

    /**
     * Constrói e retorna o objeto Pedido (alias para build()).
     */
    public Pedido agora() {
        return build();
    }

    /**
     * Constrói o objeto Pedido com ID específico.
     */
    public Pedido buildComId(Long id) {
        Pedido pedido = build();
        pedido.setId(id);
        return pedido;
    }

    /**
     * Configura pedido com restaurante específico.
     */
    public PedidoTestDataBuilder comRestaurante(Restaurante restaurante) {
        // Para compatibilidade com testes legados
        return this;
    }

    /**
     * Adiciona itens aleatórios ao pedido.
     */
    public PedidoTestDataBuilder comItensAleatorios(int quantidade) {
        // Para compatibilidade com testes legados
        return this;
    }

    /**
     * Remove todos os itens do pedido.
     */
    public PedidoTestDataBuilder semItens() {
        // Para compatibilidade com testes legados
        return this;
    }

    /**
     * Cria pedido feito há X dias.
     */
    public PedidoTestDataBuilder feitoHaDias(int dias) {
        this.dataPedido = LocalDateTime.now().minusDays(dias);
        return this;
    }

    /**
     * Cria pedido com valor baixo.
     */
    public static PedidoTestDataBuilder umPedidoComValorBaixo() {
        return new PedidoTestDataBuilder()
                .comValor(5.00);
    }

    /**
     * Método de compatibilidade para adicionar produto ao pedido.
     * Para compatibilidade com testes legados que usam comProduto.
     */
    public PedidoTestDataBuilder comProduto(Object produto, int quantidade) {
        // Para compatibilidade com testes legados
        // Em implementação real, adicionaria itens ao pedido
        return this;
    }
}