package com.exemplo.apifest.dto;

import com.exemplo.apifest.validation.ValidCEP;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para criação de Pedido
 * 
 * Validações robustas implementadas conforme Roteiro 6:
 * - Cliente ID: obrigatório (referência validada)
 * - Restaurante ID: obrigatório (referência validada)
 * - Endereço de entrega: obrigatório para delivery
 * - CEP: formato brasileiro válido para cálculo de taxa
 * - Itens: lista não vazia com validações individuais
 * 
 * @author DeliveryTech Team
 * @version 2.0
 * @since Roteiro 6 - Sistema Robusto de Validações
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

    /**
     * ID do cliente que está fazendo o pedido
     * Deve referenciar um cliente existente e ativo
     */
    @NotNull(message = "Cliente ID é obrigatório")
    private Long clienteId;

    /**
     * ID do restaurante do qual o pedido está sendo feito
     * Deve referenciar um restaurante existente e ativo
     * Todos os produtos devem pertencer a este restaurante
     */
    @NotNull(message = "Restaurante ID é obrigatório")
    private Long restauranteId;

    /**
     * Endereço de entrega do pedido
     * Pode ser diferente do endereço cadastrado do cliente
     */
    @NotBlank(message = "Endereço de entrega é obrigatório")
    @Size(min = 10, max = 200, message = "Endereço de entrega deve ter entre 10 e 200 caracteres")
    private String enderecoEntrega;

    /**
     * CEP do endereço de entrega
     * Obrigatório para cálculo da taxa de entrega
     */
    @NotBlank(message = "CEP é obrigatório para calcular a taxa de entrega")
    @ValidCEP(message = "CEP deve estar no formato válido (12345-678 ou 12345678)")
    private String cep;

    /**
     * Lista de itens do pedido
     * Deve conter pelo menos um item
     * Cada item é validado individualmente
     */
    @NotEmpty(message = "Pedido deve conter pelo menos um item")
    @Valid
    private List<ItemPedidoDTO> itens;

    /**
     * Observações adicionais do pedido (opcional)
     * Exemplo: "Sem cebola", "Ponto da carne mal passado", etc.
     */
    @Size(max = 500, message = "Observações devem ter no máximo 500 caracteres")
    private String observacoes;

    // Getters e Setters manuais devido ao problema do Lombok
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    
    public Long getRestauranteId() { return restauranteId; }
    public void setRestauranteId(Long restauranteId) { this.restauranteId = restauranteId; }
    
    public String getEnderecoEntrega() { return enderecoEntrega; }
    public void setEnderecoEntrega(String enderecoEntrega) { this.enderecoEntrega = enderecoEntrega; }
    
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    
    public List<ItemPedidoDTO> getItens() { return itens; }
    public void setItens(List<ItemPedidoDTO> itens) { this.itens = itens; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    /**
     * DTO para representar um item do pedido
     * Classe aninhada que valida cada produto e quantidade
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemPedidoDTO {

        /**
         * ID do produto sendo pedido
         * Deve referenciar um produto existente e ativo
         * Deve pertencer ao restaurante do pedido
         */
        @NotNull(message = "Produto ID é obrigatório")
        private Long produtoId;

        /**
         * Quantidade do produto
         * Deve ser positiva e não exceder o estoque disponível
         */
        @NotNull(message = "Quantidade é obrigatória")
        private Integer quantidade;

        /**
         * Observações específicas para este item (opcional)
         * Exemplo: "Sem molho", "Extra queijo", etc.
         */
        @Size(max = 200, message = "Observações do item devem ter no máximo 200 caracteres")
        private String observacoes;
        
        /**
         * Preço unitário do produto (para compatibilidade com testes)
         */
        private java.math.BigDecimal precoUnitario;
        
        // Métodos de compatibilidade
        public Long getProdutoId() { return produtoId; }
        public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
        
        public Integer getQuantidade() { return quantidade; }
        public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
        
        public String getObservacoes() { return observacoes; }
        public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
        
        public void setPrecoUnitario(java.math.BigDecimal precoUnitario) {
            this.precoUnitario = precoUnitario;
        }
        
        public java.math.BigDecimal getPrecoUnitario() {
            return precoUnitario;
        }
    }
}