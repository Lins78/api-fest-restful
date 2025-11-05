package com.exemplo.apifest.dto;

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
 * Este DTO representa um pedido completo com todos os itens.
 * Contém validações rigorosas para garantir que o pedido seja criado corretamente.
 * 
 * Validações implementadas:
 * - Cliente ID: deve referenciar cliente existente e ativo
 * - Restaurante ID: deve referenciar restaurante existente e ativo
 * - Endereço de entrega: obrigatório para delivery
 * - Itens: lista não vazia com itens válidos
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 4 - Camada de Serviços e Controllers REST
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
}