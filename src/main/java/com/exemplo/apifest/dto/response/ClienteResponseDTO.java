package com.exemplo.apifest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de resposta para Cliente
 * 
 * Este DTO contém apenas os dados seguros do cliente que podem ser expostos
 * via API, excluindo informações sensíveis.
 * 
 * Dados incluídos:
 * - ID, nome, email, telefone, endereço, status ativo
 * 
 * Dados excluídos:
 * - Senhas, dados de auditoria interna, etc.
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 4 - Camada de Serviços e Controllers REST
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {

    /**
     * Identificador único do cliente
     */
    private Long id;

    /**
     * Nome completo do cliente
     */
    private String nome;

    /**
     * Email do cliente
     */
    private String email;

    /**
     * Telefone para contato
     */
    private String telefone;

    /**
     * Endereço completo do cliente
     */
    private String endereco;

    /**
     * Indica se o cliente está ativo no sistema
     */
    private Boolean ativo;
}