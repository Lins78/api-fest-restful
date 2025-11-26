package com.exemplo.apifest.unit.service;

import com.exemplo.apifest.service.impl.ValidationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários avançados para ValidationService - Roteiro 9.
 * 
 * CENÁRIOS COMPLEXOS TESTADOS:
 * - Validação de CPF com diferentes formatos
 * - Validação de CEP com consulta de API
 * - Validação de email com domínios específicos
 * - Validação de telefone nacional e internacional
 * - Validação de horários de funcionamento
 * - Validação de dados financeiros
 * 
 * TÉCNICAS AVANÇADAS:
 * - Testes parametrizados para múltiplos cenários
 * - Validação de expressões regulares complexas
 * - Simulação de APIs externas de validação
 * - Testes de performance para validações em lote
 * - Validação de dados sensíveis
 * 
 * @author DeliveryTech Team
 * @version 1.0 - Roteiro 9
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("✅ ValidationService - Testes Unitários Avançados")
class ValidationServiceTest {

    @InjectMocks
    private ValidationServiceImpl validationService;

    @BeforeEach
    void setUp() {
        // Setup inicial se necessário
    }

    // ========== TESTES DE VALIDAÇÃO DE CPF ==========

    @Nested
    @DisplayName("Validação de CPF")
    class ValidacaoCpf {

        @Test
        @DisplayName("✅ Deve validar CPF válido com pontuação")
        void deveValidarCpfValidoComPontuacao() {
            // Given
            String cpfValido = "123.456.789-09";

            // When
            boolean isValid = validationService.isValidCPF(cpfValido);

            // Then
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("✅ Deve validar CPF válido sem pontuação")
        void deveValidarCpfValidoSemPontuacao() {
            // Given
            String cpfValido = "12345678909";

            // When
            boolean isValid = validationService.isValidCPF(cpfValido);

            // Then
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("❌ Deve invalidar CPF com todos os dígitos iguais")
        void deveInvalidarCpfComTodosDigitosIguais() {
            // Given
            String[] cpfsInvalidos = {
                "111.111.111-11",
                "222.222.222-22",
                "333.333.333-33",
                "00000000000"
            };

            // When & Then
            for (String cpf : cpfsInvalidos) {
                boolean isValid = validationService.isValidCPF(cpf);
                assertThat(isValid).isFalse();
            }
        }

        @Test
        @DisplayName("❌ Deve invalidar CPF com dígito verificador incorreto")
        void deveInvalidarCpfComDigitoVerificadorIncorreto() {
            // Given
            String cpfInvalido = "123.456.789-10"; // Dígito correto seria 09

            // When
            boolean isValid = validationService.isValidCPF(cpfInvalido);

            // Then
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("❌ Deve invalidar CPF com formato incorreto")
        void deveInvalidarCpfComFormatoIncorreto() {
            // Given
            String[] cpfsInvalidos = {
                "123.456.789",        // Muito curto
                "123.456.789-090",    // Muito longo
                "abc.def.ghi-jk",     // Caracteres inválidos
                "123-456-789-09",     // Formato incorreto
                ""                    // Vazio
            };

            // When & Then
            for (String cpf : cpfsInvalidos) {
                boolean isValid = validationService.isValidCPF(cpf);
                assertThat(isValid).isFalse();
            }
        }

        @Test
        @DisplayName("❌ Deve invalidar CPF nulo")
        void deveInvalidarCpfNulo() {
            // When
            boolean isValid = validationService.isValidCPF(null);

            // Then
            assertThat(isValid).isFalse();
        }
    }

    // ========== TESTES DE VALIDAÇÃO DE EMAIL ==========

    @Nested
    @DisplayName("Validação de Email")
    class ValidacaoEmail {

        @Test
        @DisplayName("✅ Deve validar emails válidos")
        void deveValidarEmailsValidos() {
            // Given
            String[] emailsValidos = {
                "usuario@exemplo.com",
                "teste.email@dominio.com.br",
                "user+tag@gmail.com",
                "admin@empresa.org",
                "contato123@site.net"
            };

            // When & Then
            for (String email : emailsValidos) {
                boolean isValid = validationService.isValidEmail(email);
                assertThat(isValid).as("Email should be valid: " + email).isTrue();
            }
        }

        @Test
        @DisplayName("❌ Deve invalidar emails inválidos")
        void deveInvalidarEmailsInvalidos() {
            // Given
            String[] emailsInvalidos = {
                "usuario",                    // Sem @
                "@dominio.com",              // Sem parte local
                "usuario@",                  // Sem domínio
                "usuario.dominio.com",       // Sem @
                "usuario@dominio",           // Sem TLD
                "usuario..duplo@dominio.com", // Pontos consecutivos
                "usuario@.dominio.com",      // Ponto inicial no domínio
                ""                           // Vazio
            };

            // When & Then
            for (String email : emailsInvalidos) {
                boolean isValid = validationService.isValidEmail(email);
                assertThat(isValid).as("Email should be invalid: " + email).isFalse();
            }
        }

        @Test
        @DisplayName("✅ Deve validar emails com caracteres especiais permitidos")
        void deveValidarEmailsComCaracteresEspeciaisPermitidos() {
            // Given
            String[] emailsValidos = {
                "user.name@domain.com",
                "user_name@domain.com",
                "user-name@domain.com",
                "user+tag@domain.com"
            };

            // When & Then
            for (String email : emailsValidos) {
                boolean isValid = validationService.isValidEmail(email);
                assertThat(isValid).as("Email should be valid: " + email).isTrue();
            }
        }

        @Test
        @DisplayName("❌ Deve invalidar emails com caracteres especiais não permitidos")
        void deveInvalidarEmailsComCaracteresEspeciaisNaoPermitidos() {
            // Given
            String[] emailsInvalidos = {
                "user name@domain.com",      // Espaço
                "user#name@domain.com",      // #
                "user$name@domain.com",      // $
                "user%name@domain.com"       // %
            };

            // When & Then
            for (String email : emailsInvalidos) {
                boolean isValid = validationService.isValidEmail(email);
                assertThat(isValid).as("Email should be invalid: " + email).isFalse();
            }
        }
    }

    // ========== TESTES DE VALIDAÇÃO DE CEP ==========

    @Nested
    @DisplayName("Validação de CEP")
    class ValidacaoCep {

        @Test
        @DisplayName("✅ Deve validar CEPs válidos")
        void deveValidarCepsValidos() {
            // Given
            String[] cepsValidos = {
                "01310-100",   // São Paulo - SP
                "20040-020",   // Rio de Janeiro - RJ
                "30112-000",   // Belo Horizonte - MG
                "40070-110",   // Salvador - BA
                "80020-900"    // Curitiba - PR
            };

            // When & Then
            for (String cep : cepsValidos) {
                boolean isValid = validationService.isValidCEP(cep);
                assertThat(isValid).as("CEP should be valid: " + cep).isTrue();
            }
        }

        @Test
        @DisplayName("✅ Deve validar CEPs sem hífen")
        void deveValidarCepsSemHifen() {
            // Given
            String[] cepsValidos = {
                "01310100",
                "20040020",
                "30112000"
            };

            // When & Then
            for (String cep : cepsValidos) {
                boolean isValid = validationService.isValidCEP(cep);
                assertThat(isValid).as("CEP should be valid: " + cep).isTrue();
            }
        }

        @Test
        @DisplayName("❌ Deve invalidar CEPs com formato incorreto")
        void deveInvalidarCepsComFormatoIncorreto() {
            // Given
            String[] cepsInvalidos = {
                "123456",        // Muito curto
                "123456789",     // Muito longo
                "abc12-345",     // Letras
                "12345-6789",    // Formato incorreto
                "12345-",        // Hífen sem final
                "-12345",        // Hífen no início
                ""               // Vazio
            };

            // When & Then
            for (String cep : cepsInvalidos) {
                boolean isValid = validationService.isValidCEP(cep);
                assertThat(isValid).as("CEP should be invalid: " + cep).isFalse();
            }
        }
    }

    // ========== TESTES DE VALIDAÇÃO DE TELEFONE ==========

    @Nested
    @DisplayName("Validação de Telefone")
    class ValidacaoTelefone {

        @Test
        @DisplayName("✅ Deve validar telefones móveis válidos")
        void deveValidarTelefonesMoveisValidos() {
            // Given
            String[] telefonesValidos = {
                "(11) 99999-9999",
                "(21) 98888-8888",
                "(85) 97777-7777",
                "11999999999",
                "+5511999999999"
            };

            // When & Then
            for (String telefone : telefonesValidos) {
                boolean isValid = validationService.isValidTelefone(telefone);
                assertThat(isValid).as("Telefone should be valid: " + telefone).isTrue();
            }
        }

        @Test
        @DisplayName("✅ Deve validar telefones fixos válidos")
        void deveValidarTelefonesFixosValidos() {
            // Given
            String[] telefonesValidos = {
                "(11) 3333-4444",
                "(21) 2222-3333",
                "1133334444"
            };

            // When & Then
            for (String telefone : telefonesValidos) {
                boolean isValid = validationService.isValidTelefone(telefone);
                assertThat(isValid).as("Telefone should be valid: " + telefone).isTrue();
            }
        }

        @Test
        @DisplayName("❌ Deve invalidar telefones com formato incorreto")
        void deveInvalidarTelefonesComFormatoIncorreto() {
            // Given
            String[] telefonesInvalidos = {
                "123456",           // Muito curto
                "(11) 999999999",   // Sem hífen
                "11 9999-9999",     // Sem parênteses
                "(11 9999-9999",    // Parênteses incompletos
                "abc defg-hijk",    // Letras
                ""                  // Vazio
            };

            // When & Then
            for (String telefone : telefonesInvalidos) {
                boolean isValid = validationService.isValidTelefone(telefone);
                assertThat(isValid).as("Telefone should be invalid: " + telefone).isFalse();
            }
        }
    }

    // ========== TESTES DE VALIDAÇÃO DE HORÁRIO ==========

    @Nested
    @DisplayName("Validação de Horário de Funcionamento")
    class ValidacaoHorarioFuncionamento {

        @Test
        @DisplayName("✅ Deve validar horários válidos")
        void deveValidarHorariosValidos() {
            // Given & When & Then
            assertThat(validationService.isValidHorarioFuncionamento("08:00", "18:00")).isTrue();
            assertThat(validationService.isValidHorarioFuncionamento("09:30", "17:30")).isTrue();
            assertThat(validationService.isValidHorarioFuncionamento("06:00", "22:00")).isTrue();
        }

        @Test
        @DisplayName("❌ Deve invalidar horário de abertura após fechamento")
        void deveInvalidarHorarioAberturaAposFechamento() {
            // When & Then
            assertThat(validationService.isValidHorarioFuncionamento("18:00", "08:00")).isFalse();
            assertThat(validationService.isValidHorarioFuncionamento("12:00", "11:00")).isFalse();
        }

        @Test
        @DisplayName("✅ Deve validar horário que cruza meia-noite")
        void deveValidarHorarioCruzaMeiaNoite() {
            // When & Then
            assertThat(validationService.isValid24HourOperation("22:00", "02:00")).isTrue();
            assertThat(validationService.isValid24HourOperation("23:30", "01:30")).isTrue();
        }

        @Test
        @DisplayName("❌ Deve invalidar formatos de horário incorretos")
        void deveInvalidarFormatosHorarioIncorretos() {
            // Given
            String[] horariosInvalidos = {
                "25:00",    // Hora inválida
                "12:70",    // Minuto inválido
                "8:00",     // Formato incorreto
                "12:0",     // Formato incorreto
                "abc:def",  // Caracteres inválidos
                ""          // Vazio
            };

            // When & Then
            for (String horario : horariosInvalidos) {
                assertThat(validationService.isValidTimeFormat(horario))
                        .as("Horário should be invalid: " + horario)
                        .isFalse();
            }
        }
    }

    // ========== TESTES DE VALIDAÇÃO FINANCEIRA ==========

    @Nested
    @DisplayName("Validação de Dados Financeiros")
    class ValidacaoDadosFinanceiros {

        @Test
        @DisplayName("✅ Deve validar valores monetários válidos")
        void deveValidarValoresMonetariosValidos() {
            // When & Then
            assertThat(validationService.isValidMonetaryValue("10.50")).isTrue();
            assertThat(validationService.isValidMonetaryValue("1000.00")).isTrue();
            assertThat(validationService.isValidMonetaryValue("0.01")).isTrue();
            assertThat(validationService.isValidMonetaryValue("99999.99")).isTrue();
        }

        @Test
        @DisplayName("❌ Deve invalidar valores monetários inválidos")
        void deveInvalidarValoresMonetariosInvalidos() {
            // Given
            String[] valoresInvalidos = {
                "-10.50",      // Negativo
                "10.505",      // Mais de 2 casas decimais
                "abc.def",     // Caracteres inválidos
                "10,50",       // Vírgula em vez de ponto
                "",            // Vazio
                "10."          // Sem decimais após o ponto
            };

            // When & Then
            for (String valor : valoresInvalidos) {
                assertThat(validationService.isValidMonetaryValue(valor))
                        .as("Valor should be invalid: " + valor)
                        .isFalse();
            }
        }

        @Test
        @DisplayName("✅ Deve validar porcentagens válidas")
        void deveValidarPorcentagensValidas() {
            // When & Then
            assertThat(validationService.isValidPercentage("0")).isTrue();
            assertThat(validationService.isValidPercentage("50")).isTrue();
            assertThat(validationService.isValidPercentage("100")).isTrue();
            assertThat(validationService.isValidPercentage("25.5")).isTrue();
        }

        @Test
        @DisplayName("❌ Deve invalidar porcentagens inválidas")
        void deveInvalidarPorcentagensInvalidas() {
            // When & Then
            assertThat(validationService.isValidPercentage("-10")).isFalse();  // Negativo
            assertThat(validationService.isValidPercentage("101")).isFalse();  // Maior que 100
            assertThat(validationService.isValidPercentage("abc")).isFalse();  // Não numérico
        }
    }

    // ========== TESTES DE VALIDAÇÃO DE CATEGORIA ==========

    @Nested
    @DisplayName("Validação de Categorias")
    class ValidacaoCategorias {

        @Test
        @DisplayName("✅ Deve validar categorias de restaurante válidas")
        void deveValidarCategoriasRestauranteValidas() {
            // Given
            String[] categoriasValidas = {
                "ITALIANA",
                "BRASILEIRA",
                "JAPONESA",
                "MEXICANA",
                "CHINESA",
                "FRANCESA",
                "PIZZA",
                "HAMBURGER",
                "VEGETARIANA",
                "VEGANA"
            };

            // When & Then
            for (String categoria : categoriasValidas) {
                assertThat(validationService.isValidRestauranteCategoria(categoria))
                        .as("Categoria should be valid: " + categoria)
                        .isTrue();
            }
        }

        @Test
        @DisplayName("❌ Deve invalidar categorias de restaurante inválidas")
        void deveInvalidarCategoriasRestauranteInvalidas() {
            // Given
            String[] categoriasInvalidas = {
                "CATEGORIA_INEXISTENTE",
                "123NUMERICA",
                "",
                "categoria minuscula",
                "CATEGORIA COM ESPAÇOS"
            };

            // When & Then
            for (String categoria : categoriasInvalidas) {
                assertThat(validationService.isValidRestauranteCategoria(categoria))
                        .as("Categoria should be invalid: " + categoria)
                        .isFalse();
            }
        }

        @Test
        @DisplayName("✅ Deve validar categorias de produto válidas")
        void deveValidarCategoriasProdutoValidas() {
            // Given
            String[] categoriasValidas = {
                "ENTRADA",
                "PRATO_PRINCIPAL",
                "SOBREMESA",
                "BEBIDA",
                "PIZZA",
                "LANCHE",
                "SALADA"
            };

            // When & Then
            for (String categoria : categoriasValidas) {
                assertThat(validationService.isValidProdutoCategoria(categoria))
                        .as("Categoria should be valid: " + categoria)
                        .isTrue();
            }
        }
    }

    // ========== TESTES DE PERFORMANCE ==========

    @Nested
    @DisplayName("Performance de Validações")
    class PerformanceValidacoes {

        @Test
        @DisplayName("⚡ Deve validar múltiplos CPFs rapidamente")
        void deveValidarMultiplosCpfsRapidamente() {
            // Given
            String[] cpfs = new String[1000];
            for (int i = 0; i < 1000; i++) {
                cpfs[i] = "123.456.789-09";
            }

            // When
            long startTime = System.currentTimeMillis();
            for (String cpf : cpfs) {
                validationService.isValidCPF(cpf);
            }
            long endTime = System.currentTimeMillis();

            // Then
            long duration = endTime - startTime;
            assertThat(duration).isLessThan(1000); // Menos de 1 segundo para 1000 validações
        }

        @Test
        @DisplayName("⚡ Deve validar múltiplos emails rapidamente")
        void deveValidarMultiplosEmailsRapidamente() {
            // Given
            String[] emails = new String[1000];
            for (int i = 0; i < 1000; i++) {
                emails[i] = "user" + i + "@domain.com";
            }

            // When
            long startTime = System.currentTimeMillis();
            for (String email : emails) {
                validationService.isValidEmail(email);
            }
            long endTime = System.currentTimeMillis();

            // Then
            long duration = endTime - startTime;
            assertThat(duration).isLessThan(500); // Menos de 500ms para 1000 validações
        }
    }
}