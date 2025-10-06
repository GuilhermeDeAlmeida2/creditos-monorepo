package br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers;

import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationRequest;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationResult;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para StringValidationHandler")
class StringValidationHandlerTest {

    private StringValidationHandler handler;

    @BeforeEach
    void setUp() {
        handler = new StringValidationHandler();
    }

    @Test
    @DisplayName("Deve criar handler com sucesso")
    void deveCriarHandlerComSucesso() {
        assertNotNull(handler);
        assertTrue(handler instanceof StringValidationHandler);
    }

    @Test
    @DisplayName("Deve aceitar tipos STRING_NOT_EMPTY e STRING_OPTIONAL")
    void deveAceitarTiposStringNotEmptyEStringOptional() {
        ValidationRequest requestNotEmpty = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, "test", "testField");

        ValidationRequest requestOptional = new ValidationRequest(
            ValidationType.STRING_OPTIONAL, "test", "testField");

        assertTrue(handler.canHandle(requestNotEmpty));
        assertTrue(handler.canHandle(requestOptional));
    }

    @Test
    @DisplayName("Não deve aceitar outros tipos de validação")
    void naoDeveAceitarOutrosTiposDeValidacao() {
        ValidationRequest requestNumber = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, 123, "testField");

        ValidationRequest requestPageable = new ValidationRequest(
            ValidationType.PAGEABLE, "test", "testField");

        assertFalse(handler.canHandle(requestNumber));
        assertFalse(handler.canHandle(requestPageable));
    }

    // Testes para STRING_NOT_EMPTY
    @Test
    @DisplayName("Deve validar string não vazia com sucesso")
    void deveValidarStringNaoVaziaComSucesso() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, "test value", "testField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertEquals("test value", result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
        assertTrue(result.getMessage().contains("validado com sucesso"));
    }

    @Test
    @DisplayName("Deve falhar quando valor é null para string não vazia")
    void deveFalharQuandoValorENullParaStringNaoVazia() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, null, "testField");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("obrigatório"));
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando string está vazia")
    void deveFalharQuandoStringEstaVazia() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, "", "testField");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("não pode ser vazio"));
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando string contém apenas espaços")
    void deveFalharQuandoStringContemApenasEspacos() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, "   ", "testField");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve aceitar string com espaços nas bordas para string não vazia")
    void deveAceitarStringComEspacosNasBordasParaStringNaoVazia() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, "  test value  ", "testField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertEquals("test value", result.getProcessedValue());
    }

    @Test
    @DisplayName("Deve falhar quando valor não é string para string não vazia")
    void deveFalharQuandoValorNaoEStringParaStringNaoVazia() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, 123, "testField");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("deve ser uma string"));
        assertEquals("testField", result.getFieldName());
    }

    // Testes para STRING_OPTIONAL
    @Test
    @DisplayName("Deve aceitar null para string opcional")
    void deveAceitarNullParaStringOpcional() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_OPTIONAL, null, "testField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertNull(result.getProcessedValue());
        assertTrue(result.getMessage().contains("opcional e está nulo"));
    }

    @Test
    @DisplayName("Deve aceitar string vazia para string opcional")
    void deveAceitarStringVaziaParaStringOpcional() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_OPTIONAL, "", "testField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertNull(result.getProcessedValue());
        assertTrue(result.getMessage().contains("opcional"));
    }

    @Test
    @DisplayName("Deve aceitar string com apenas espaços para string opcional")
    void deveAceitarStringComApenasEspacosParaStringOpcional() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_OPTIONAL, "   ", "testField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertNotNull(result.getMessage());
    }

    @Test
    @DisplayName("Deve validar string com conteúdo para string opcional")
    void deveValidarStringComConteudoParaStringOpcional() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_OPTIONAL, "  test value  ", "testField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertEquals("test value", result.getProcessedValue());
        assertTrue(result.getMessage().contains("validado com sucesso"));
    }

    // Testes dos métodos da interface
    @Test
    @DisplayName("Deve validar string não vazia via interface")
    void deveValidarStringNaoVaziaViaInterface() {
        ValidationResult result = handler.validateNotEmpty("test", "testField");

        assertTrue(result.isValid());
        assertEquals("test", result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar string opcional via interface")
    void deveValidarStringOpcionalViaInterface() {
        ValidationResult result = handler.validateOptional("test", "testField");

        assertTrue(result.isValid());
        assertEquals("test", result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar comprimento da string com sucesso")
    void deveValidarComprimentoDaStringComSucesso() {
        ValidationResult result = handler.validateLength("test", "testField", 2, 10);

        assertTrue(result.isValid());
        assertEquals("test", result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando string é muito curta")
    void deveFalharQuandoStringEMuitoCurta() {
        ValidationResult result = handler.validateLength("a", "testField", 2, 10);

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("pelo menos 2 caracteres"));
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando string é muito longa")
    void deveFalharQuandoStringEMuitoLonga() {
        ValidationResult result = handler.validateLength("very long string", "testField", 2, 5);

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("no máximo 5 caracteres"));
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando string é null para validação de comprimento")
    void deveFalharQuandoStringENullParaValidacaoDeComprimento() {
        ValidationResult result = handler.validateLength(null, "testField", 2, 10);

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("obrigatório"));
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar padrão com sucesso")
    void deveValidarPadraoComSucesso() {
        ValidationResult result = handler.validatePattern("123-456", "testField", "\\d{3}-\\d{3}");

        assertTrue(result.isValid());
        assertEquals("123-456", result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando string não corresponde ao padrão")
    void deveFalharQuandoStringNaoCorrespondeAoPadrao() {
        ValidationResult result = handler.validatePattern("abc-123", "testField", "\\d{3}-\\d{3}");

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("não corresponde ao padrão esperado"));
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando string é null para validação de padrão")
    void deveFalharQuandoStringENullParaValidacaoDePadrao() {
        ValidationResult result = handler.validatePattern(null, "testField", "\\d{3}-\\d{3}");

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("obrigatório"));
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve trimar espaços na validação de comprimento")
    void deveTrimarEspacosNaValidacaoDeComprimento() {
        ValidationResult result = handler.validateLength("  test  ", "testField", 4, 10);

        assertTrue(result.isValid());
        assertEquals("test", result.getProcessedValue());
    }

    @Test
    @DisplayName("Deve trimar espaços na validação de padrão")
    void deveTrimarEspacosNaValidacaoDePadrao() {
        ValidationResult result = handler.validatePattern("  123-456  ", "testField", "\\d{3}-\\d{3}");

        assertTrue(result.isValid());
        assertEquals("123-456", result.getProcessedValue());
    }

    @Test
    @DisplayName("Deve validar com diferentes tipos de objetos")
    void deveValidarComDiferentesTiposDeObjetos() {
        // Teste com StringBuilder
        ValidationRequest request1 = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, new StringBuilder("test"), "testField");

        ValidationResult result1 = handler.handle(request1);
        assertNotNull(result1);

        // Teste com StringBuffer
        ValidationRequest request2 = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, new StringBuffer("test"), "testField");

        ValidationResult result2 = handler.handle(request2);
        assertNotNull(result2);
    }

    @Test
    @DisplayName("Deve validar com caracteres especiais")
    void deveValidarComCaracteresEspeciais() {
        String specialString = "test@#$%^&*()_+-=[]{}|;':\",./<>?";
        
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, specialString, "testField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertEquals(specialString, result.getProcessedValue());
    }

    @Test
    @DisplayName("Deve validar com strings unicode")
    void deveValidarComStringsUnicode() {
        String unicodeString = "testáção中文한국어";
        
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, unicodeString, "testField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertEquals(unicodeString, result.getProcessedValue());
    }

    // Testes adicionais para aumentar cobertura
    @Test
    @DisplayName("Deve falhar quando tipo de validação não é suportado")
    void deveFalharQuandoTipoDeValidacaoNaoESuportado() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, "test", "testField");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar string opcional com valor null via interface")
    void deveValidarStringOpcionalComValorNullViaInterface() {
        ValidationResult result = handler.validateOptional(null, "testField");

        assertTrue(result.isValid());
        assertNull(result.getProcessedValue());
        assertTrue(result.getMessage().contains("opcional e está nulo"));
    }

    @Test
    @DisplayName("Deve validar string opcional com string vazia via interface")
    void deveValidarStringOpcionalComStringVaziaViaInterface() {
        ValidationResult result = handler.validateOptional("", "testField");

        assertTrue(result.isValid());
        assertNull(result.getProcessedValue());
        assertTrue(result.getMessage().contains("opcional"));
    }

    @Test
    @DisplayName("Deve validar string opcional com apenas espaços via interface")
    void deveValidarStringOpcionalComApenasEspacosViaInterface() {
        ValidationResult result = handler.validateOptional("   ", "testField");

        assertTrue(result.isValid());
        assertNull(result.getProcessedValue());
        assertNotNull(result.getMessage());
    }

    @Test
    @DisplayName("Deve validar string não vazia com valor null via interface")
    void deveValidarStringNaoVaziaComValorNullViaInterface() {
        ValidationResult result = handler.validateNotEmpty(null, "testField");

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("obrigatório"));
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar string não vazia com string vazia via interface")
    void deveValidarStringNaoVaziaComStringVaziaViaInterface() {
        ValidationResult result = handler.validateNotEmpty("", "testField");

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("não pode ser vazio"));
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar string não vazia com apenas espaços via interface")
    void deveValidarStringNaoVaziaComApenasEspacosViaInterface() {
        ValidationResult result = handler.validateNotEmpty("   ", "testField");

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar comprimento com string no limite mínimo")
    void deveValidarComprimentoComStringNoLimiteMinimo() {
        ValidationResult result = handler.validateLength("ab", "testField", 2, 10);

        assertTrue(result.isValid());
        assertEquals("ab", result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar comprimento com string no limite máximo")
    void deveValidarComprimentoComStringNoLimiteMaximo() {
        ValidationResult result = handler.validateLength("abcdefghij", "testField", 2, 10);

        assertTrue(result.isValid());
        assertEquals("abcdefghij", result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar comprimento com string exatamente no limite")
    void deveValidarComprimentoComStringExatamenteNoLimite() {
        ValidationResult result = handler.validateLength("abc", "testField", 3, 3);

        assertTrue(result.isValid());
        assertEquals("abc", result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar padrão com string que corresponde exatamente")
    void deveValidarPadraoComStringQueCorrespondeExatamente() {
        ValidationResult result = handler.validatePattern("123-456", "testField", "\\d{3}-\\d{3}");

        assertTrue(result.isValid());
        assertEquals("123-456", result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar padrão com string que corresponde parcialmente")
    void deveValidarPadraoComStringQueCorrespondeParcialmente() {
        ValidationResult result = handler.validatePattern("abc-123", "testField", "\\d{3}-\\d{3}");

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("não corresponde ao padrão esperado"));
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar padrão com string vazia")
    void deveValidarPadraoComStringVazia() {
        ValidationResult result = handler.validatePattern("", "testField", "\\d{3}-\\d{3}");

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("não corresponde ao padrão esperado"));
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar padrão com string contendo apenas espaços")
    void deveValidarPadraoComStringContendoApenasEspacos() {
        ValidationResult result = handler.validatePattern("   ", "testField", "\\d{3}-\\d{3}");

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("não corresponde ao padrão esperado"));
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar padrão com regex complexa")
    void deveValidarPadraoComRegexComplexa() {
        ValidationResult result = handler.validatePattern("user@domain.com", "testField", "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

        assertTrue(result.isValid());
        assertEquals("user@domain.com", result.getProcessedValue());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar com padrão de email inválido")
    void deveFalharComPadraoDeEmailInvalido() {
        ValidationResult result = handler.validatePattern("invalid-email", "testField", "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("não corresponde ao padrão esperado"));
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar com diferentes tipos de objetos convertíveis para string")
    void deveValidarComDiferentesTiposDeObjetosConvertiveisParaString() {
        // Teste com Integer
        ValidationRequest request1 = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, 123, "testField");
        ValidationResult result1 = handler.handle(request1);
        assertNotNull(result1);

        // Teste com Boolean
        ValidationRequest request2 = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, true, "testField");
        ValidationResult result2 = handler.handle(request2);
        assertNotNull(result2);

        // Teste com Character
        ValidationRequest request3 = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, 'A', "testField");
        ValidationResult result3 = handler.handle(request3);
        assertNotNull(result3);
    }

    @Test
    @DisplayName("Deve validar com strings contendo caracteres especiais e números")
    void deveValidarComStringsContendoCaracteresEspeciaisENumeros() {
        String specialString = "test123@#$%^&*()_+-=[]{}|;':\",./<>?";
        
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, specialString, "testField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertEquals(specialString, result.getProcessedValue());
    }

    @Test
    @DisplayName("Deve validar com strings contendo quebras de linha")
    void deveValidarComStringsContendoQuebrasDeLinha() {
        String multilineString = "linha1\nlinha2\r\nlinha3";
        
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, multilineString, "testField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertEquals(multilineString, result.getProcessedValue());
    }

    @Test
    @DisplayName("Deve validar com strings contendo tabs")
    void deveValidarComStringsContendoTabs() {
        String tabString = "coluna1\tcoluna2\tcoluna3";
        
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, tabString, "testField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertEquals(tabString, result.getProcessedValue());
    }
}