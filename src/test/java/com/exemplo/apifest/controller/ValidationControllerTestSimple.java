package com.exemplo.apifest.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes de Validação de Controllers")
class ValidationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    @DisplayName("Deve retornar erro 400 ao cadastrar cliente com dados inválidos")
    void deveRetornarErro400AoCadastrarClienteComDadosInvalidos() throws Exception {
        String clienteInvalidoJson = """
            {
                "nome": "",
                "email": "email-invalido",
                "telefone": "123",
                "endereco": "Rua"
            }
            """;

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteInvalidoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Erro de validação nos dados enviados"))
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    @WithMockUser  
    @DisplayName("Deve retornar erro 400 ao cadastrar produto com categoria inválida")
    void deveRetornarErro400AoCadastrarProdutoComCategoriaInvalida() throws Exception {
        String produtoInvalidoJson = """
            {
                "nome": "Produto Teste",
                "descricao": "Descrição do produto",
                "preco": 10.00,
                "categoria": "CATEGORIA_INEXISTENTE",
                "restauranteId": 1
            }
            """;

        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produtoInvalidoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Erro de validação nos dados enviados"))
                .andExpect(jsonPath("$.details.categoria").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar erro 400 para produto com preço negativo")
    void deveRetornarErro400ParaProdutoComPrecoNegativo() throws Exception {
        String produtoInvalidoJson = """
            {
                "nome": "Produto Teste",
                "descricao": "Descrição do produto",
                "preco": -10.00,
                "categoria": "LANCHE",
                "restauranteId": 1
            }
            """;

        mockMvc.perform(post("/api/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produtoInvalidoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.details.preco").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve aceitar dados válidos sem retornar erro de validação")
    void deveAceitarDadosValidosSemRetornarErroDeValidacao() throws Exception {
        String clienteValidoJson = """
            {
                "nome": "Cliente Teste Silva",
                "email": "cliente@teste.com",
                "telefone": "11987654321",
                "endereco": "Rua das Flores, 123, São Paulo - SP"
            }
            """;

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clienteValidoJson))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status == 400) {
                        throw new AssertionError("Não deveria retornar erro 400 para dados válidos, mas retornou: " + 
                                                result.getResponse().getContentAsString());
                    }
                });
    }
}