package com.exemplo.apifest.dto.response;

import com.exemplo.apifest.model.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta completo para Pedido
 * 
 * Este DTO contém todas as informações de um pedido incluindo:
 * - Dados do cliente e restaurante
 * - Lista completa de itens
 * - Valores calculados e status atual
 * 
 * Usado para retornar pedidos completos em consultas individuais.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 4 - Camada de Serviços e Controllers REST
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {

    /**
     * Identificador único do pedido
     */
    private Long id;

    /**
     * Data e hora da criação do pedido
     */
    private LocalDateTime dataPedido;

    /**
     * Status atual do pedido
     */
    private StatusPedido status;

    /**
     * Valor total do pedido (itens + taxa de entrega)
     */
    private BigDecimal valor;

    /**
     * Endereço de entrega específico deste pedido
     */
    private String enderecoEntrega;

    /**
     * Observações adicionais do pedido
     */
    private String observacoes;

    /**
     * Dados do cliente que fez o pedido
     */
    private ClienteResponseDTO cliente;

    /**
     * Dados do restaurante do pedido
     */
    private RestauranteResponseDTO restaurante;

    /**
     * Lista de itens do pedido com detalhes completos
     */
    private List<ItemPedidoResponseDTO> itens;
    
    // Getters e Setters manuais devido ao problema do Lombok
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDateTime getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDateTime dataPedido) { this.dataPedido = dataPedido; }
    
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    
    public String getEnderecoEntrega() { return enderecoEntrega; }
    public void setEnderecoEntrega(String enderecoEntrega) { this.enderecoEntrega = enderecoEntrega; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    
    public ClienteResponseDTO getCliente() { return cliente; }
    public void setCliente(ClienteResponseDTO cliente) { this.cliente = cliente; }
    
    public RestauranteResponseDTO getRestaurante() { return restaurante; }
    public void setRestaurante(RestauranteResponseDTO restaurante) { this.restaurante = restaurante; }
    
    public List<ItemPedidoResponseDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoResponseDTO> itens) { this.itens = itens; }
}