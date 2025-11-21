package com.exemplo.apifest.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes dos Validadores Customizados")
class ValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Validador de CEP")
    class ValidCEPValidatorTest {

        private CEPValidator validator;

        @BeforeEach
        void setUp() {
            validator = new CEPValidator();
        }

        @Test
        @DisplayName("Deve aceitar CEP válido com hífen")
        void deveAceitarCepValidoComHifen() {
            assertTrue(validator.isValid("01234-567", context));
        }

        @Test
        @DisplayName("Deve aceitar CEP válido sem hífen")
        void deveAceitarCepValidoSemHifen() {
            assertTrue(validator.isValid("01234567", context));
        }

        @Test
        @DisplayName("Deve aceitar valor nulo")
        void deveAceitarValorNulo() {
            assertTrue(validator.isValid(null, context));
        }

        @Test
        @DisplayName("Deve rejeitar CEP com menos de 8 dígitos")
        void deveRejeitarCepComMenosDe8Digitos() {
            assertFalse(validator.isValid("1234567", context));
            assertFalse(validator.isValid("123-4567", context));
        }

        @Test
        @DisplayName("Deve rejeitar CEP com mais de 8 dígitos")
        void deveRejeitarCepComMaisDe8Digitos() {
            assertFalse(validator.isValid("123456789", context));
            assertFalse(validator.isValid("12345-6789", context));
        }

        @Test
        @DisplayName("Deve rejeitar CEP com caracteres inválidos")
        void deveRejeitarCepComCaracteresInvalidos() {
            assertFalse(validator.isValid("abcde-fgh", context));
            assertFalse(validator.isValid("12abc-567", context));
            assertFalse(validator.isValid("12345@567", context));
        }
    }

    @Nested
    @DisplayName("Validador de Telefone")
    class ValidTelefoneValidatorTest {

        private TelefoneValidator validator;

        @BeforeEach
        void setUp() {
            validator = new TelefoneValidator();
        }

        @Test
        @DisplayName("Deve aceitar telefone celular válido")
        void deveAceitarTelefoneCelularValido() {
            assertTrue(validator.isValid("11987654321", context));
            assertTrue(validator.isValid("21999887766", context));
            assertTrue(validator.isValid("85912345678", context));
        }

        @Test
        @DisplayName("Deve aceitar telefone fixo válido")
        void deveAceitarTelefoneFixoValido() {
            assertTrue(validator.isValid("1133334444", context));
            assertTrue(validator.isValid("2122223333", context));
            assertTrue(validator.isValid("8533334444", context));
        }

        @Test
        @DisplayName("Deve aceitar valor nulo")
        void deveAceitarValorNulo() {
            assertTrue(validator.isValid(null, context));
        }

        @Test
        @DisplayName("Deve rejeitar telefone com menos de 10 dígitos")
        void deveRejeitarTelefoneComMenosDe10Digitos() {
            assertFalse(validator.isValid("119876543", context));
            assertFalse(validator.isValid("1133334", context));
        }

        @Test
        @DisplayName("Deve rejeitar telefone com mais de 11 dígitos")
        void deveRejeitarTelefoneComMaisDe11Digitos() {
            assertFalse(validator.isValid("119876543210", context));
            assertFalse(validator.isValid("11333344445", context));
        }

        @Test
        @DisplayName("Deve rejeitar telefone celular com dígito inválido")
        void deveRejeitarTelefoneCelularComDigitoInvalido() {
            // Celular deve ter 9 como terceiro dígito
            assertFalse(validator.isValid("11887654321", context));
            assertFalse(validator.isValid("11787654321", context));
        }

        @Test
        @DisplayName("Deve rejeitar DDD inválido")
        void deveRejeitarDddInvalido() {
            assertFalse(validator.isValid("0987654321", context));
            assertFalse(validator.isValid("0133334444", context));
        }

        @Test
        @DisplayName("Deve rejeitar telefone com caracteres não numéricos")
        void deveRejeitarTelefoneComCaracteresNaoNumericos() {
            assertFalse(validator.isValid("11abc654321", context));
            assertFalse(validator.isValid("11-98765-4321", context));
        }
    }

    @Nested
    @DisplayName("Validador de Categoria")
    class ValidCategoriaValidatorTest {

        private CategoriaValidator validator;

        @BeforeEach
        void setUp() {
            validator = new CategoriaValidator();
        }

        @Test
        @DisplayName("Deve aceitar categorias válidas")
        void deveAceitarCategoriasValidas() {
            assertTrue(validator.isValid("LANCHE", context));
            assertTrue(validator.isValid("BEBIDA", context));
            assertTrue(validator.isValid("SOBREMESA", context));
            assertTrue(validator.isValid("ACOMPANHAMENTO", context));
        }

        @Test
        @DisplayName("Deve aceitar valor nulo")
        void deveAceitarValorNulo() {
            assertTrue(validator.isValid(null, context));
        }

        @Test
        @DisplayName("Deve rejeitar categorias inválidas")
        void deveRejeitarCategoriasInvalidas() {
            assertFalse(validator.isValid("CATEGORIA_INEXISTENTE", context));
            assertFalse(validator.isValid("lanche", context)); // case sensitive
            assertFalse(validator.isValid("PIZZA", context));
            assertFalse(validator.isValid("COMIDA", context));
        }

        @Test
        @DisplayName("Deve rejeitar string vazia")
        void deveRejeitarStringVazia() {
            assertFalse(validator.isValid("", context));
            assertFalse(validator.isValid("   ", context));
        }
    }

    @Nested
    @DisplayName("Validador de Horário de Funcionamento")
    class ValidHorarioFuncionamentoValidatorTest {

        private HorarioFuncionamentoValidator validator;

        @BeforeEach
        void setUp() {
            validator = new HorarioFuncionamentoValidator();
        }

        @Test
        @DisplayName("Deve aceitar horários válidos")
        void deveAceitarHorariosValidos() {
            assertTrue(validator.isValid("08:00-18:00", context));
            assertTrue(validator.isValid("23:59-00:00", context));
            assertTrue(validator.isValid("00:00-23:59", context));
            assertTrue(validator.isValid("12:30-22:00", context));
        }

        @Test
        @DisplayName("Deve aceitar valor nulo")
        void deveAceitarValorNulo() {
            assertTrue(validator.isValid(null, context));
        }

        @Test
        @DisplayName("Deve rejeitar formato inválido")
        void deveRejeitarFormatoInvalido() {
            assertFalse(validator.isValid("8:00-18:00", context)); // sem zero à esquerda
            assertFalse(validator.isValid("08:0-18:00", context)); // minutos incompletos
            assertFalse(validator.isValid("8:0-18:00", context)); // ambos incompletos
            assertFalse(validator.isValid("08-00-18:00", context)); // separador errado
            assertFalse(validator.isValid("0800-1800", context)); // sem separador
            assertFalse(validator.isValid("08:00", context)); // sem horário de fechamento
        }

        @Test
        @DisplayName("Deve rejeitar horas inválidas")
        void deveRejeitarHorasInvalidas() {
            assertFalse(validator.isValid("24:00-18:00", context));
            assertFalse(validator.isValid("25:30-18:00", context));
            assertFalse(validator.isValid("-1:00-18:00", context));
        }

        @Test
        @DisplayName("Deve rejeitar minutos inválidos")
        void deveRejeitarMinutosInvalidos() {
            assertFalse(validator.isValid("08:60-18:00", context));
            assertFalse(validator.isValid("12:99-18:00", context));
            assertFalse(validator.isValid("10:-5-18:00", context));
        }

        @Test
        @DisplayName("Deve rejeitar caracteres não numéricos")
        void deveRejeitarCaracteresNaoNumericos() {
            assertFalse(validator.isValid("ab:cd-18:00", context));
            assertFalse(validator.isValid("08:ab-18:00", context));
            assertFalse(validator.isValid("ab:30-18:00", context));
        }
    }
}