package br.com.guilhermedealmeidafreitas.creditos.service.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a classe NumberValidationStrategy.
 * 
 * Cobertura atual: 59.3%
 * Meta: > 80%
 */
class NumberValidationStrategyTest {

    private final NumberValidationStrategy strategy = new NumberValidationStrategy();

    // Testes para construtor e getters básicos
    @Test
    @DisplayName("Deve criar estratégia com nome correto")
    void deveCriarEstrategiaComNomeCorreto() {
        assertEquals("NumberValidation", strategy.getStrategyName());
    }

    // Testes para método supports
    @Test
    @DisplayName("Deve suportar classes Number e tipos primitivos")
    void deveSuportarClassesNumberETiposPrimitivos() {
        // Classes Number
        assertTrue(strategy.supports(Number.class));
        assertTrue(strategy.supports(Integer.class));
        assertTrue(strategy.supports(Long.class));
        assertTrue(strategy.supports(Double.class));
        assertTrue(strategy.supports(Float.class));
        assertTrue(strategy.supports(BigDecimal.class));
        assertTrue(strategy.supports(BigInteger.class));

        // Tipos primitivos
        assertTrue(strategy.supports(int.class));
        assertTrue(strategy.supports(long.class));
        assertTrue(strategy.supports(double.class));
        assertTrue(strategy.supports(float.class));
    }

    @Test
    @DisplayName("Não deve suportar outras classes")
    void naoDeveSuportarOutrasClasses() {
        assertFalse(strategy.supports(String.class));
        assertFalse(strategy.supports(Object.class));
        assertFalse(strategy.supports(Boolean.class));
        assertFalse(strategy.supports(boolean.class));
    }

    // Testes para método validate básico
    @Test
    @DisplayName("Deve validar números válidos")
    void deveValidarNumerosValidos() {
        // Não deve lançar exceção
        assertDoesNotThrow(() -> strategy.validate(42));
        assertDoesNotThrow(() -> strategy.validate(42L));
        assertDoesNotThrow(() -> strategy.validate(42.5));
        assertDoesNotThrow(() -> strategy.validate(42.5f));
        assertDoesNotThrow(() -> strategy.validate(new BigDecimal("42.5")));
        assertDoesNotThrow(() -> strategy.validate(new BigInteger("42")));
    }

    @Test
    @DisplayName("Deve lançar exceção para número nulo")
    void deveLancarExcecaoParaNumeroNulo() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> strategy.validate(null));
        
        assertEquals("Número não pode ser nulo", exception.getMessage());
        assertEquals("NumberValidation", exception.getStrategyName());
        assertEquals("number", exception.getFieldName());
    }

    // Testes para método validate com nome do campo
    @Test
    @DisplayName("Deve validar número com nome do campo")
    void deveValidarNumeroComNomeDoCampo() {
        assertDoesNotThrow(() -> strategy.validate(42, "idade"));
        assertDoesNotThrow(() -> strategy.validate(42.5, "valor"));
    }

    @Test
    @DisplayName("Deve lançar exceção para número nulo com nome do campo")
    void deveLancarExcecaoParaNumeroNuloComNomeDoCampo() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> strategy.validate(null, "idade"));
        
        assertEquals("idade não pode ser nulo", exception.getMessage());
        assertEquals("NumberValidation", exception.getStrategyName());
        assertEquals("idade", exception.getFieldName());
    }

    // Testes para método validatePositive
    @Test
    @DisplayName("Deve validar números positivos")
    void deveValidarNumerosPositivos() {
        assertDoesNotThrow(() -> strategy.validatePositive(42, "idade"));
        assertDoesNotThrow(() -> strategy.validatePositive(42.5, "valor"));
        assertDoesNotThrow(() -> strategy.validatePositive(0, "zero"));
        assertDoesNotThrow(() -> strategy.validatePositive(new BigDecimal("42.5"), "valor"));
    }

    @Test
    @DisplayName("Deve lançar exceção para números negativos")
    void deveLancarExcecaoParaNumerosNegativos() {
        ValidationException exception1 = assertThrows(ValidationException.class, 
            () -> strategy.validatePositive(-42, "idade"));
        
        assertEquals("idade deve ser um número positivo", exception1.getMessage());
        assertEquals("NumberValidation", exception1.getStrategyName());
        assertEquals("idade", exception1.getFieldName());

        ValidationException exception2 = assertThrows(ValidationException.class, 
            () -> strategy.validatePositive(-42.5, "valor"));
        
        assertEquals("valor deve ser um número positivo", exception2.getMessage());
        assertEquals("NumberValidation", exception2.getStrategyName());
        assertEquals("valor", exception2.getFieldName());
    }

    @Test
    @DisplayName("Deve lançar exceção para número nulo em validatePositive")
    void deveLancarExcecaoParaNumeroNuloEmValidatePositive() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> strategy.validatePositive(null, "idade"));
        
        assertEquals("idade não pode ser nulo", exception.getMessage());
        assertEquals("NumberValidation", exception.getStrategyName());
        assertEquals("idade", exception.getFieldName());
    }

    // Testes para método validateRange
    @Test
    @DisplayName("Deve validar números dentro do range")
    void deveValidarNumerosDentroDoRange() {
        assertDoesNotThrow(() -> strategy.validateRange(5, 0, 10, "idade"));
        assertDoesNotThrow(() -> strategy.validateRange(0, 0, 10, "minimo"));
        assertDoesNotThrow(() -> strategy.validateRange(10, 0, 10, "maximo"));
        assertDoesNotThrow(() -> strategy.validateRange(5.5, 0.0, 10.0, "valor"));
    }

    @Test
    @DisplayName("Deve lançar exceção para números abaixo do range")
    void deveLancarExcecaoParaNumerosAbaixoDoRange() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> strategy.validateRange(-1, 0, 10, "idade"));
        
        assertTrue(exception.getMessage().contains("idade deve estar entre 0 e 10"));
        assertEquals("NumberValidation", exception.getStrategyName());
        assertEquals("idade", exception.getFieldName());
    }

    @Test
    @DisplayName("Deve lançar exceção para números acima do range")
    void deveLancarExcecaoParaNumerosAcimaDoRange() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> strategy.validateRange(11, 0, 10, "idade"));
        
        assertTrue(exception.getMessage().contains("idade deve estar entre 0 e 10"));
        assertEquals("NumberValidation", exception.getStrategyName());
        assertEquals("idade", exception.getFieldName());
    }

    @Test
    @DisplayName("Deve lançar exceção para número nulo em validateRange")
    void deveLancarExcecaoParaNumeroNuloEmValidateRange() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> strategy.validateRange(null, 0, 10, "idade"));
        
        assertEquals("idade não pode ser nulo", exception.getMessage());
        assertEquals("NumberValidation", exception.getStrategyName());
        assertEquals("idade", exception.getFieldName());
    }

    // Testes para método validatePaginationParams
    @Test
    @DisplayName("Deve validar parâmetros de paginação válidos")
    void deveValidarParametrosDePaginacaoValidos() {
        assertDoesNotThrow(() -> strategy.validatePaginationParams(0, 20, 100));
        assertDoesNotThrow(() -> strategy.validatePaginationParams(5, 50, 100));
        assertDoesNotThrow(() -> strategy.validatePaginationParams(0, 100, 100));
    }

    @Test
    @DisplayName("Deve lançar exceção para página negativa")
    void deveLancarExcecaoParaPaginaNegativa() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> strategy.validatePaginationParams(-1, 20, 100));
        
        assertEquals("Número da página deve ser maior ou igual a 0", exception.getMessage());
        assertEquals("NumberValidation", exception.getStrategyName());
        assertEquals("page", exception.getFieldName());
    }

    @Test
    @DisplayName("Deve lançar exceção para tamanho zero")
    void deveLancarExcecaoParaTamanhoZero() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> strategy.validatePaginationParams(0, 0, 100));
        
        assertEquals("Tamanho da página deve ser maior que 0", exception.getMessage());
        assertEquals("NumberValidation", exception.getStrategyName());
        assertEquals("size", exception.getFieldName());
    }

    @Test
    @DisplayName("Deve lançar exceção para tamanho negativo")
    void deveLancarExcecaoParaTamanhoNegativo() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> strategy.validatePaginationParams(0, -5, 100));
        
        assertEquals("Tamanho da página deve ser maior que 0", exception.getMessage());
        assertEquals("NumberValidation", exception.getStrategyName());
        assertEquals("size", exception.getFieldName());
    }

    @Test
    @DisplayName("Deve lançar exceção para tamanho maior que o máximo")
    void deveLancarExcecaoParaTamanhoMaiorQueMaximo() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> strategy.validatePaginationParams(0, 150, 100));
        
        assertEquals("Tamanho da página não pode ser maior que 100", exception.getMessage());
        assertEquals("NumberValidation", exception.getStrategyName());
        assertEquals("size", exception.getFieldName());
    }

    // Testes com diferentes tipos de números
    @Test
    @DisplayName("Deve funcionar com diferentes tipos de números")
    void deveFuncionarComDiferentesTiposDeNumeros() {
        // Integer
        assertDoesNotThrow(() -> strategy.validate(new Integer(42), "int"));
        assertDoesNotThrow(() -> strategy.validatePositive(new Integer(42), "int"));
        assertDoesNotThrow(() -> strategy.validateRange(new Integer(5), 0, 10, "int"));

        // Long
        assertDoesNotThrow(() -> strategy.validate(new Long(42L), "long"));
        assertDoesNotThrow(() -> strategy.validatePositive(new Long(42L), "long"));
        assertDoesNotThrow(() -> strategy.validateRange(new Long(5L), 0L, 10L, "long"));

        // Double
        assertDoesNotThrow(() -> strategy.validate(new Double(42.5), "double"));
        assertDoesNotThrow(() -> strategy.validatePositive(new Double(42.5), "double"));
        assertDoesNotThrow(() -> strategy.validateRange(new Double(5.5), 0.0, 10.0, "double"));

        // Float
        assertDoesNotThrow(() -> strategy.validate(new Float(42.5f), "float"));
        assertDoesNotThrow(() -> strategy.validatePositive(new Float(42.5f), "float"));
        assertDoesNotThrow(() -> strategy.validateRange(new Float(5.5f), 0.0f, 10.0f, "float"));

        // BigDecimal
        assertDoesNotThrow(() -> strategy.validate(new BigDecimal("42.5"), "bigdecimal"));
        assertDoesNotThrow(() -> strategy.validatePositive(new BigDecimal("42.5"), "bigdecimal"));
        assertDoesNotThrow(() -> strategy.validateRange(new BigDecimal("5.5"), new BigDecimal("0"), new BigDecimal("10"), "bigdecimal"));

        // BigInteger
        assertDoesNotThrow(() -> strategy.validate(new BigInteger("42"), "biginteger"));
        assertDoesNotThrow(() -> strategy.validatePositive(new BigInteger("42"), "biginteger"));
        assertDoesNotThrow(() -> strategy.validateRange(new BigInteger("5"), new BigInteger("0"), new BigInteger("10"), "biginteger"));
    }

    // Testes para casos extremos
    @Test
    @DisplayName("Deve funcionar com zero")
    void deveFuncionarComZero() {
        assertDoesNotThrow(() -> strategy.validate(0, "zero"));
        assertDoesNotThrow(() -> strategy.validatePositive(0, "zero"));
        assertDoesNotThrow(() -> strategy.validateRange(0, 0, 10, "zero"));
    }

    @Test
    @DisplayName("Deve funcionar com números muito pequenos")
    void deveFuncionarComNumerosMuitoPequenos() {
        assertDoesNotThrow(() -> strategy.validate(Double.MIN_VALUE, "min"));
        assertDoesNotThrow(() -> strategy.validatePositive(Double.MIN_VALUE, "min"));
    }

    @Test
    @DisplayName("Deve funcionar com números muito grandes")
    void deveFuncionarComNumerosMuitoGrandes() {
        assertDoesNotThrow(() -> strategy.validate(Double.MAX_VALUE, "max"));
        assertDoesNotThrow(() -> strategy.validatePositive(Double.MAX_VALUE, "max"));
    }
}
