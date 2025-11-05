package com.exemplo.apifest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criação e atualização de Cliente
 * 
 * Este DTO contém todas as validações necessárias para garantir que os dados
 * do cliente estejam corretos antes de serem processados pelos services.
 * 
 * Validações implementadas:
 * - Nome: obrigatório, mínimo 2 caracteres, máximo 100
 * - Email: obrigatório, formato válido, único no sistema
 * - Telefone: obrigatório, formato adequado
 * - Endereço: obrigatório para entrega
 * 
 * @author DeliveryTech Team
 * @version 1.0
 * @since Roteiro 4 - Camada de Serviços e Controllers REST
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    /**
     * Nome completo do cliente
     * Deve ter entre 2 e 100 caracteres
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    /**
     * Email do cliente (usado para login/identificação)
     * Deve ser único no sistema e ter formato válido
     */
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
    private String email;

    /**
     * Telefone para contato
     * Formato esperado: (XX) XXXXX-XXXX
     */
    @NotBlank(message = "Telefone é obrigatório")
    @Size(min = 10, max = 15, message = "Telefone deve ter entre 10 e 15 caracteres")
    private String telefone;

    /**
     * Endereço completo do cliente
     * Usado como endereço padrão para entrega
     */
    @NotBlank(message = "Endereço é obrigatório")
    @Size(min = 10, max = 200, message = "Endereço deve ter entre 10 e 200 caracteres")
    private String endereco;
}