package br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers;

import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationRequest;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationResult;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para NumberValidationHandler")
class NumberValidationHandlerTest {

    private NumberValidationHandler handler;

    @BeforeEach
    void setUp() {
        handler = new NumberValidationHandler();
    }

    @Test
    @DisplayName("Deve criar handler com sucesso")
    void deveCriarHandlerComSucesso() {
        assertNotNull(handler);
        assertTrue(handler instanceof NumberValidationHandler);
    }

    @Test
    @DisplayName("Deve aceitar tipos NUMBER_POSITIVE e NUMBER_RANGE")
    void deveAceitarTiposNumberPositiveENumberRange() {
        ValidationRequest requestPositive = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, 123, "testField");

        ValidationRequest requestRange = new ValidationRequest(
            ValidationType.NUMBER_RANGE, 123, "testField");

        assertTrue(handler.canHandle(requestPositive));
        assertTrue(handler.canHandle(requestRange));
    }

    @Test
    @DisplayName("Não deve aceitar outros tipos de validação")
    void naoDeveAceitarOutrosTiposDeValidacao() {
        ValidationRequest requestString = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, "test", "testField");

        ValidationRequest requestPageable = new ValidationRequest(
            ValidationType.PAGEABLE, "test", "testField");

        assertFalse(handler.canHandle(requestString));
        assertFalse(handler.canHandle(requestPageable));
    }

    // Testes para NUMBER_POSITIVE
    @Test
    @DisplayName("Deve validar número positivo com sucesso")
    void deveValidarNumeroPositivoComSucesso() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, 123, "testField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertEquals(123, result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
        assertNotNull(result.getMessage());
    }

    @Test
    @DisplayName("Deve validar BigDecimal positivo com sucesso")
    void deveValidarBigDecimalPositivoComSucesso() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, new BigDecimal("123.45"), "testField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertNotNull(result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando valor é null para número positivo")
    void deveFalharQuandoValorENullParaNumeroPositivo() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, null, "testField");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando número é zero para número positivo")
    void deveFalharQuandoNumeroEZeroParaNumeroPositivo() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, 0, "testField");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando número é negativo para número positivo")
    void deveFalharQuandoNumeroENegativoParaNumeroPositivo() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, -123, "testField");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando valor não é número para número positivo")
    void deveFalharQuandoValorNaoENumeroParaNumeroPositivo() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, "not a number", "testField");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    // Testes para NUMBER_RANGE
    @Test
    @DisplayName("Deve validar número dentro do range com sucesso")
    void deveValidarNumeroDentroDoRangeComSucesso() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("min", 10);
        parameters.put("max", 100);
        
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_RANGE, 50, "testField", parameters, null);

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertEquals(50, result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar BigDecimal dentro do range com sucesso")
    void deveValidarBigDecimalDentroDoRangeComSucesso() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("min", new BigDecimal("10.5"));
        parameters.put("max", new BigDecimal("100.5"));
        
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_RANGE, new BigDecimal("50.25"), "testField", parameters, null);

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertNotNull(result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando número está abaixo do range mínimo")
    void deveFalharQuandoNumeroEstaAbaixoDoRangeMinimo() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("min", 10);
        parameters.put("max", 100);
        
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_RANGE, 5, "testField", parameters, null);

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando número está acima do range máximo")
    void deveFalharQuandoNumeroEstaAcimaDoRangeMaximo() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("min", 10);
        parameters.put("max", 100);
        
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_RANGE, 150, "testField", parameters, null);

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando valor é null para número range")
    void deveFalharQuandoValorENullParaNumeroRange() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("min", 10);
        parameters.put("max", 100);
        
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_RANGE, null, "testField", parameters, null);

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando parâmetros de range não são fornecidos")
    void deveFalharQuandoParametrosDeRangeNaoSaoFornecidos() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_RANGE, 50, "testField");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando valor não é número para número range")
    void deveFalharQuandoValorNaoENumeroParaNumeroRange() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("min", 10);
        parameters.put("max", 100);
        
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_RANGE, "not a number", "testField", parameters, null);

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    // Testes dos métodos da interface
    @Test
    @DisplayName("Deve validar número positivo via interface")
    void deveValidarNumeroPositivoViaInterface() {
        ValidationResult result = handler.validatePositive(123, "testField");

        assertTrue(result.isValid());
        assertEquals(123, result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar número em range via interface")
    void deveValidarNumeroEmRangeViaInterface() {
        ValidationResult result = handler.validateRange(50, "testField", 10, 100);

        assertTrue(result.isValid());
        assertEquals(50, result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando número está fora do range via interface")
    void deveFalharQuandoNumeroEstaForaDoRangeViaInterface() {
        ValidationResult result = handler.validateRange(5, "testField", 10, 100);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando número é negativo via interface")
    void deveFalharQuandoNumeroENegativoViaInterface() {
        ValidationResult result = handler.validatePositive(-123, "testField");

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar com diferentes tipos de números")
    void deveValidarComDiferentesTiposDeNumeros() {
        // Teste com Integer
        ValidationRequest request1 = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, Integer.valueOf(123), "testField");
        ValidationResult result1 = handler.handle(request1);
        assertTrue(result1.isValid());

        // Teste com Long
        ValidationRequest request2 = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, Long.valueOf(123), "testField");
        ValidationResult result2 = handler.handle(request2);
        assertTrue(result2.isValid());

        // Teste com Double
        ValidationRequest request3 = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, Double.valueOf(123.45), "testField");
        ValidationResult result3 = handler.handle(request3);
        assertTrue(result3.isValid());

        // Teste com Float
        ValidationRequest request4 = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, Float.valueOf(123.45f), "testField");
        ValidationResult result4 = handler.handle(request4);
        assertTrue(result4.isValid());
    }

    @Test
    @DisplayName("Deve validar com números decimais")
    void deveValidarComNumerosDecimais() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, new BigDecimal("123.456"), "testField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertNotNull(result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar com números muito grandes")
    void deveValidarComNumerosMuitoGrandes() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, new BigDecimal("999999999999999999999999.99"), "testField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertNotNull(result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar com números muito pequenos")
    void deveValidarComNumerosMuitoPequenos() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, new BigDecimal("0.000000001"), "testField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertNotNull(result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar com zero para número positivo")
    void deveFalharComZeroParaNumeroPositivo() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, BigDecimal.ZERO, "testField");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar com BigDecimal negativo")
    void deveFalharComBigDecimalNegativo() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, new BigDecimal("-123.45"), "testField");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    // Testes para métodos da interface que não estão cobertos
    @Test
    @DisplayName("Deve validar número mínimo via interface")
    void deveValidarNumeroMinimoViaInterface() {
        ValidationResult result = handler.validateMin(50, "testField", 10);

        assertTrue(result.isValid());
        assertEquals(50, result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando número é menor que mínimo via interface")
    void deveFalharQuandoNumeroEMenorQueMinimoViaInterface() {
        ValidationResult result = handler.validateMin(5, "testField", 10);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar número máximo via interface")
    void deveValidarNumeroMaximoViaInterface() {
        ValidationResult result = handler.validateMax(50, "testField", 100);

        assertTrue(result.isValid());
        assertEquals(50, result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando número é maior que máximo via interface")
    void deveFalharQuandoNumeroEMaiorQueMaximoViaInterface() {
        ValidationResult result = handler.validateMax(150, "testField", 100);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar precisão de BigDecimal via interface")
    void deveValidarPrecisaoDeBigDecimalViaInterface() {
        ValidationResult result = handler.validatePrecision(new BigDecimal("123.45"), "testField", 2);

        assertTrue(result.isValid());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando precisão excede limite via interface")
    void deveFalharQuandoPrecisaoExcedeLimiteViaInterface() {
        ValidationResult result = handler.validatePrecision(new BigDecimal("123.456"), "testField", 2);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar range de Integer via interface")
    void deveValidarRangeDeIntegerViaInterface() {
        ValidationResult result = handler.validateIntegerRange(50, "testField", 10, 100);

        assertTrue(result.isValid());
        assertEquals(50, result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando Integer está fora do range via interface")
    void deveFalharQuandoIntegerEstaForaDoRangeViaInterface() {
        ValidationResult result = handler.validateIntegerRange(150, "testField", 10, 100);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando min é maior que max no range")
    void deveFalharQuandoMinEMaiorQueMaxNoRange() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("min", 100);
        parameters.put("max", 10);
        
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_RANGE, 50, "testField", parameters, null);

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar número no limite mínimo do range")
    void deveValidarNumeroNoLimiteMinimoDoRange() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("min", 10);
        parameters.put("max", 100);
        
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_RANGE, 10, "testField", parameters, null);

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertEquals(10, result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar número no limite máximo do range")
    void deveValidarNumeroNoLimiteMaximoDoRange() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("min", 10);
        parameters.put("max", 100);
        
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_RANGE, 100, "testField", parameters, null);

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertEquals(100, result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando parâmetros de range são inválidos")
    void deveFalharQuandoParametrosDeRangeSaoInvalidos() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("min", "invalid");
        parameters.put("max", 100);
        
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_RANGE, 50, "testField", parameters, null);

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando tipo de validação não é suportado")
    void deveFalharQuandoTipoDeValidacaoNaoESuportado() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, 123, "testField");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }
}
