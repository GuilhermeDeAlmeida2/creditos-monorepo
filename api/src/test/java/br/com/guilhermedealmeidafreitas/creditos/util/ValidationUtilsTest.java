package br.com.guilhermedealmeidafreitas.creditos.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    @Test
    @DisplayName("parseInteger deve converter Number para int")
    void parseIntegerDeveConverterNumberParaInt() {
        // Arrange
        Integer value = 42;

        // Act
        int result = ValidationUtils.parseInteger(value, "testField");

        // Assert
        assertEquals(42, result);
    }

    @Test
    @DisplayName("parseInteger deve converter String para int")
    void parseIntegerDeveConverterStringParaInt() {
        // Arrange
        String value = "42";

        // Act
        int result = ValidationUtils.parseInteger(value, "testField");

        // Assert
        assertEquals(42, result);
    }

    @Test
    @DisplayName("parseInteger deve lançar exceção para String inválida")
    void parseIntegerDeveLancarExcecaoParaStringInvalida() {
        // Arrange
        String value = "notANumber";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ValidationUtils.parseInteger(value, "testField");
        });

        assertEquals("Parâmetro 'testField' deve ser um número inteiro", exception.getMessage());
    }

    @Test
    @DisplayName("parseInteger deve lançar exceção para tipo inválido")
    void parseIntegerDeveLancarExcecaoParaTipoInvalido() {
        // Arrange
        Object value = new Object();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ValidationUtils.parseInteger(value, "testField");
        });

        assertEquals("Parâmetro 'testField' deve ser um número", exception.getMessage());
    }

    @Test
    @DisplayName("parseIntegerWithDefault deve retornar valor padrão para null")
    void parseIntegerWithDefaultDeveRetornarValorPadraoParaNull() {
        // Arrange
        Object value = null;
        int defaultValue = 10;

        // Act
        int result = ValidationUtils.parseIntegerWithDefault(value, "testField", defaultValue);

        // Assert
        assertEquals(10, result);
    }

    @Test
    @DisplayName("parseString deve converter String e fazer trim")
    void parseStringDeveConverterStringEFazerTrim() {
        // Arrange
        String value = "  test  ";

        // Act
        String result = ValidationUtils.parseString(value, "testField");

        // Assert
        assertEquals("test", result);
    }

    @Test
    @DisplayName("parseString deve retornar null para null")
    void parseStringDeveRetornarNullParaNull() {
        // Arrange
        Object value = null;

        // Act
        String result = ValidationUtils.parseString(value, "testField");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("parseString deve lançar exceção para tipo inválido")
    void parseStringDeveLancarExcecaoParaTipoInvalido() {
        // Arrange
        Object value = new Object();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ValidationUtils.parseString(value, "testField");
        });

        assertEquals("Parâmetro 'testField' deve ser uma string", exception.getMessage());
    }

    @Test
    @DisplayName("parseStringWithDefault deve retornar valor padrão para null")
    void parseStringWithDefaultDeveRetornarValorPadraoParaNull() {
        // Arrange
        Object value = null;
        String defaultValue = "default";

        // Act
        String result = ValidationUtils.parseStringWithDefault(value, "testField", defaultValue);

        // Assert
        assertEquals("default", result);
    }

    @Test
    @DisplayName("parseNumber deve converter Number")
    void parseNumberDeveConverterNumber() {
        // Arrange
        Double value = 42.5;

        // Act
        Number result = ValidationUtils.parseNumber(value, "testField");

        // Assert
        assertEquals(42.5, result.doubleValue());
    }

    @Test
    @DisplayName("parseNumber deve converter String para Integer")
    void parseNumberDeveConverterStringParaInteger() {
        // Arrange
        String value = "42";

        // Act
        Number result = ValidationUtils.parseNumber(value, "testField");

        // Assert
        assertEquals(42, result.intValue());
    }

    @Test
    @DisplayName("parseNumber deve converter String para Double")
    void parseNumberDeveConverterStringParaDouble() {
        // Arrange
        String value = "42.5";

        // Act
        Number result = ValidationUtils.parseNumber(value, "testField");

        // Assert
        assertEquals(42.5, result.doubleValue());
    }

    @Test
    @DisplayName("parseNumber deve lançar exceção para String inválida")
    void parseNumberDeveLancarExcecaoParaStringInvalida() {
        // Arrange
        String value = "notANumber";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ValidationUtils.parseNumber(value, "testField");
        });

        assertEquals("Parâmetro 'testField' deve ser um número", exception.getMessage());
    }

    @Test
    @DisplayName("parseNumberWithDefault deve retornar valor padrão para null")
    void parseNumberWithDefaultDeveRetornarValorPadraoParaNull() {
        // Arrange
        Object value = null;
        Number defaultValue = 10;

        // Act
        Number result = ValidationUtils.parseNumberWithDefault(value, "testField", defaultValue);

        // Assert
        assertEquals(10, result);
    }

    @Test
    @DisplayName("isNullOrEmpty deve retornar true para null")
    void isNullOrEmptyDeveRetornarTrueParaNull() {
        // Act
        boolean result = ValidationUtils.isNullOrEmpty(null);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isNullOrEmpty deve retornar true para string vazia")
    void isNullOrEmptyDeveRetornarTrueParaStringVazia() {
        // Act
        boolean result = ValidationUtils.isNullOrEmpty("");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isNullOrEmpty deve retornar true para string com espaços")
    void isNullOrEmptyDeveRetornarTrueParaStringComEspacos() {
        // Act
        boolean result = ValidationUtils.isNullOrEmpty("   ");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isNullOrEmpty deve retornar false para string válida")
    void isNullOrEmptyDeveRetornarFalseParaStringValida() {
        // Act
        boolean result = ValidationUtils.isNullOrEmpty("test");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isNullOrBlank deve retornar true para null")
    void isNullOrBlankDeveRetornarTrueParaNull() {
        // Act
        boolean result = ValidationUtils.isNullOrBlank(null);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isNullOrBlank deve retornar true para string vazia")
    void isNullOrBlankDeveRetornarTrueParaStringVazia() {
        // Act
        boolean result = ValidationUtils.isNullOrBlank("");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isNullOrBlank deve retornar true para string com espaços")
    void isNullOrBlankDeveRetornarTrueParaStringComEspacos() {
        // Act
        boolean result = ValidationUtils.isNullOrBlank("   ");

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isNullOrBlank deve retornar false para string válida")
    void isNullOrBlankDeveRetornarFalseParaStringValida() {
        // Act
        boolean result = ValidationUtils.isNullOrBlank("test");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isNull deve retornar true para null")
    void isNullDeveRetornarTrueParaNull() {
        // Act
        boolean result = ValidationUtils.isNull(null);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isNull deve retornar false para objeto válido")
    void isNullDeveRetornarFalseParaObjetoValido() {
        // Act
        boolean result = ValidationUtils.isNull("test");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("clamp deve limitar valor entre min e max")
    void clampDeveLimitarValorEntreMinEMax() {
        // Act & Assert
        assertEquals(5, ValidationUtils.clamp(3, 5, 10));
        assertEquals(10, ValidationUtils.clamp(15, 5, 10));
        assertEquals(7, ValidationUtils.clamp(7, 5, 10));
    }

    @Test
    @DisplayName("clamp double deve limitar valor entre min e max")
    void clampDoubleDeveLimitarValorEntreMinEMax() {
        // Act & Assert
        assertEquals(5.0, ValidationUtils.clamp(3.0, 5.0, 10.0));
        assertEquals(10.0, ValidationUtils.clamp(15.0, 5.0, 10.0));
        assertEquals(7.5, ValidationUtils.clamp(7.5, 5.0, 10.0));
    }

    @Test
    @DisplayName("Deve ser uma classe utilitária (não instanciável)")
    void deveSerUmaClasseUtilitaria() {
        // Arrange & Act & Assert
        assertThrows(Exception.class, () -> {
            // Tenta instanciar a classe usando reflexão
            Constructor<ValidationUtils> constructor = ValidationUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }, "Classe utilitária deve lançar exceção ao tentar instanciar");
    }
}
