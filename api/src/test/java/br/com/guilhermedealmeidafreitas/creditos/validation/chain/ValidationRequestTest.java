package br.com.guilhermedealmeidafreitas.creditos.validation.chain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a classe ValidationRequest.
 * 
 * Cobertura atual: 54.7%
 * Meta: > 80%
 */
class ValidationRequestTest {

    // Testes básicos para construtores
    @Test
    @DisplayName("Deve criar ValidationRequest com construtor principal")
    void deveCriarValidationRequestComConstrutorPrincipal() {
        ValidationType type = ValidationType.STRING_NOT_EMPTY;
        Object value = "teste";
        String fieldName = "nome";
        Map<String, Object> parameters = Map.of("maxLength", 100);
        ValidationContext context = ValidationContext.API_REST;

        ValidationRequest request = new ValidationRequest(type, value, fieldName, parameters, context);

        assertEquals(type, request.getType());
        assertEquals(value, request.getValue());
        assertEquals(fieldName, request.getFieldName());
        assertEquals(parameters, request.getParameters());
        assertEquals(context, request.getContext());
    }

    @Test
    @DisplayName("Deve criar ValidationRequest com construtor simplificado")
    void deveCriarValidationRequestComConstrutorSimplificado() {
        ValidationType type = ValidationType.STRING_NOT_EMPTY;
        Object value = "teste";
        String fieldName = "nome";

        ValidationRequest request = new ValidationRequest(type, value, fieldName);

        assertEquals(type, request.getType());
        assertEquals(value, request.getValue());
        assertEquals(fieldName, request.getFieldName());
        assertEquals(Map.of(), request.getParameters());
        assertEquals(ValidationContext.DEFAULT, request.getContext());
    }

    @Test
    @DisplayName("Deve criar ValidationRequest com construtor com parâmetros")
    void deveCriarValidationRequestComConstrutorComParametros() {
        ValidationType type = ValidationType.STRING_NOT_EMPTY;
        Object value = "teste";
        String fieldName = "nome";
        Map<String, Object> parameters = Map.of("maxLength", 100);

        ValidationRequest request = new ValidationRequest(type, value, fieldName, parameters);

        assertEquals(type, request.getType());
        assertEquals(value, request.getValue());
        assertEquals(fieldName, request.getFieldName());
        assertEquals(parameters, request.getParameters());
        assertEquals(ValidationContext.DEFAULT, request.getContext());
    }

    // Testes para tratamento de parâmetros nulos
    @Test
    @DisplayName("Deve tratar parâmetros nulos no construtor principal")
    void deveTratarParametrosNulosNoConstrutorPrincipal() {
        ValidationType type = ValidationType.STRING_NOT_EMPTY;
        Object value = "teste";
        String fieldName = "nome";

        ValidationRequest request = new ValidationRequest(type, value, fieldName, null, ValidationContext.DEFAULT);

        assertEquals(type, request.getType());
        assertEquals(value, request.getValue());
        assertEquals(fieldName, request.getFieldName());
        assertEquals(Map.of(), request.getParameters());
        assertEquals(ValidationContext.DEFAULT, request.getContext());
    }

    // Testes para getters
    @Test
    @DisplayName("Deve retornar valores corretos dos getters")
    void deveRetornarValoresCorretosDosGetters() {
        ValidationType type = ValidationType.NUMBER_POSITIVE;
        Object value = 42;
        String fieldName = "idade";
        Map<String, Object> parameters = Map.of("min", 0, "max", 120);
        ValidationContext context = ValidationContext.USER_INPUT;

        ValidationRequest request = new ValidationRequest(type, value, fieldName, parameters, context);

        assertEquals(type, request.getType());
        assertEquals(value, request.getValue());
        assertEquals(fieldName, request.getFieldName());
        assertEquals(parameters, request.getParameters());
        assertEquals(context, request.getContext());
    }

    // Testes para métodos de parâmetros
    @Test
    @DisplayName("Deve obter parâmetro específico")
    void deveObterParametroEspecifico() {
        Map<String, Object> parameters = Map.of("maxLength", 100, "minLength", 5);
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, "teste", "nome", parameters
        );

        assertEquals(100, request.getParameter("maxLength"));
        assertEquals(5, request.getParameter("minLength"));
        assertNull(request.getParameter("inexistente"));
    }

    @Test
    @DisplayName("Deve obter parâmetro com valor padrão")
    void deveObterParametroComValorPadrao() {
        Map<String, Object> parameters = Map.of("maxLength", 100);
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, "teste", "nome", parameters
        );

        assertEquals(100, request.getParameter("maxLength", 50));
        assertEquals(50, request.getParameter("inexistente", 50));
        assertEquals("padrao", request.getParameter("inexistente", "padrao"));
    }

    @Test
    @DisplayName("Deve verificar se parâmetro existe")
    void deveVerificarSeParametroExiste() {
        Map<String, Object> parameters = Map.of("maxLength", 100, "minLength", 5);
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, "teste", "nome", parameters
        );

        assertTrue(request.hasParameter("maxLength"));
        assertTrue(request.hasParameter("minLength"));
        assertFalse(request.hasParameter("inexistente"));
    }

    // Testes para toString
    @Test
    @DisplayName("Deve retornar toString correto")
    void deveRetornarToStringCorreto() {
        ValidationType type = ValidationType.STRING_NOT_EMPTY;
        Object value = "teste";
        String fieldName = "nome";
        Map<String, Object> parameters = Map.of("maxLength", 100);

        ValidationRequest request = new ValidationRequest(type, value, fieldName, parameters);

        String result = request.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("ValidationRequest"));
        assertTrue(result.contains("type=" + type));
        assertTrue(result.contains("fieldName='nome'"));
        assertTrue(result.contains("value=teste"));
        assertTrue(result.contains("parameters="));
    }

    // Testes com diferentes tipos de valores
    @Test
    @DisplayName("Deve funcionar com valor nulo")
    void deveFuncionarComValorNulo() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, null, "nome"
        );

        assertNull(request.getValue());
        assertEquals("nome", request.getFieldName());
    }

    @Test
    @DisplayName("Deve funcionar com diferentes tipos de objetos")
    void deveFuncionarComDiferentesTiposDeObjetos() {
        // String
        ValidationRequest request1 = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, "texto", "campo1"
        );
        assertEquals("texto", request1.getValue());

        // Integer
        ValidationRequest request2 = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, 42, "campo2"
        );
        assertEquals(42, request2.getValue());

        // Double
        ValidationRequest request3 = new ValidationRequest(
            ValidationType.NUMBER_RANGE, 3.14, "campo3"
        );
        assertEquals(3.14, request3.getValue());
    }

    // Testes com diferentes contextos
    @Test
    @DisplayName("Deve funcionar com diferentes contextos")
    void deveFuncionarComDiferentesContextos() {
        ValidationRequest request1 = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, "teste", "nome", 
            Map.of(), ValidationContext.API_REST
        );
        assertEquals(ValidationContext.API_REST, request1.getContext());

        ValidationRequest request2 = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, "teste", "nome", 
            Map.of(), ValidationContext.USER_INPUT
        );
        assertEquals(ValidationContext.USER_INPUT, request2.getContext());
    }

    // Testes com parâmetros vazios
    @Test
    @DisplayName("Deve funcionar com parâmetros vazios")
    void deveFuncionarComParametrosVazios() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, "teste", "nome", Map.of()
        );

        assertTrue(request.getParameters().isEmpty());
        assertFalse(request.hasParameter("qualquer"));
        assertNull(request.getParameter("qualquer"));
        assertEquals("padrao", request.getParameter("qualquer", "padrao"));
    }
}
