package br.com.guilhermedealmeidafreitas.creditos.service.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para ValidationException.
 * 
 * Este teste cobre todos os cenários da exceção de validação,
 * incluindo construtores, métodos e formatação de mensagens.
 */
@ExtendWith(MockitoExtension.class)
class ValidationExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com apenas mensagem")
    void deveCriarExcecaoComApenasMensagem() {
        // Given
        String message = "Erro de validação";

        // When
        ValidationException exception = new ValidationException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        assertNull(exception.getStrategyName());
        assertNull(exception.getFieldName());
        assertNull(exception.getCause());
        assertEquals(400, exception.getHttpStatus());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem e nome do campo")
    void deveCriarExcecaoComMensagemENomeDoCampo() {
        // Given
        String message = "Campo obrigatório";
        String fieldName = "nome";

        // When
        ValidationException exception = new ValidationException(message, fieldName);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        assertNull(exception.getStrategyName());
        assertEquals(fieldName, exception.getFieldName());
        assertNull(exception.getCause());
        assertEquals(400, exception.getHttpStatus());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem, nome da estratégia e nome do campo")
    void deveCriarExcecaoComMensagemNomeDaEstrategiaENomeDoCampo() {
        // Given
        String message = "Valor inválido";
        String strategyName = "StringValidationStrategy";
        String fieldName = "email";

        // When
        ValidationException exception = new ValidationException(message, strategyName, fieldName);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        assertEquals(strategyName, exception.getStrategyName());
        assertEquals(fieldName, exception.getFieldName());
        assertNull(exception.getCause());
        assertEquals(400, exception.getHttpStatus());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem e causa")
    void deveCriarExcecaoComMensagemECausa() {
        // Given
        String message = "Erro de validação";
        Throwable cause = new IllegalArgumentException("Erro interno");

        // When
        ValidationException exception = new ValidationException(message, cause);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        assertNull(exception.getStrategyName());
        assertNull(exception.getFieldName());
        assertEquals(cause, exception.getCause());
        assertEquals(400, exception.getHttpStatus());
    }

    @Test
    @DisplayName("Deve retornar nome da estratégia corretamente")
    void deveRetornarNomeDaEstrategiaCorretamente() {
        // Given
        String message = "Validação falhou";
        String strategyName = "NumberValidationStrategy";
        String fieldName = "valor";

        // When
        ValidationException exception = new ValidationException(message, strategyName, fieldName);

        // Then
        assertEquals(strategyName, exception.getStrategyName());
    }

    @Test
    @DisplayName("Deve retornar nome do campo corretamente")
    void deveRetornarNomeDoCampoCorretamente() {
        // Given
        String message = "Campo obrigatório";
        String fieldName = "telefone";

        // When
        ValidationException exception = new ValidationException(message, fieldName);

        // Then
        assertEquals(fieldName, exception.getFieldName());
    }

    @Test
    @DisplayName("Deve retornar status HTTP 400 para erros de validação")
    void deveRetornarStatusHttp400ParaErrosDeValidacao() {
        // Given
        String message = "Erro de validação";

        // When
        ValidationException exception = new ValidationException(message);

        // Then
        assertEquals(400, exception.getHttpStatus());
    }

    @Test
    @DisplayName("Deve formatar toString corretamente sem estratégia nem campo")
    void deveFormatarToStringCorretamenteSemEstrategiaNemCampo() {
        // Given
        String message = "Erro de validação";
        ValidationException exception = new ValidationException(message);

        // When
        String result = exception.toString();

        // Then
        String expected = "ValidationException: " + message;
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Deve formatar toString corretamente apenas com estratégia")
    void deveFormatarToStringCorretamenteApenasComEstrategia() {
        // Given
        String message = "Validação falhou";
        String strategyName = "StringValidationStrategy";
        String fieldName = null;
        ValidationException exception = new ValidationException(message, strategyName, fieldName);

        // When
        String result = exception.toString();

        // Then
        String expected = "ValidationException [Strategy: " + strategyName + "]: " + message;
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Deve formatar toString corretamente apenas com campo")
    void deveFormatarToStringCorretamenteApenasComCampo() {
        // Given
        String message = "Campo obrigatório";
        String strategyName = null;
        String fieldName = "nome";
        ValidationException exception = new ValidationException(message, strategyName, fieldName);

        // When
        String result = exception.toString();

        // Then
        String expected = "ValidationException [Field: " + fieldName + "]: " + message;
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Deve formatar toString corretamente com estratégia e campo")
    void deveFormatarToStringCorretamenteComEstrategiaECampo() {
        // Given
        String message = "Valor inválido";
        String strategyName = "NumberValidationStrategy";
        String fieldName = "idade";
        ValidationException exception = new ValidationException(message, strategyName, fieldName);

        // When
        String result = exception.toString();

        // Then
        String expected = "ValidationException [Strategy: " + strategyName + "] [Field: " + fieldName + "]: " + message;
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Deve permitir mensagem nula")
    void devePermitirMensagemNula() {
        // Given
        String message = null;

        // When
        ValidationException exception = new ValidationException(message);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        assertNull(exception.getStrategyName());
        assertNull(exception.getFieldName());
        assertEquals(400, exception.getHttpStatus());
    }

    @Test
    @DisplayName("Deve permitir campo nulo")
    void devePermitirCampoNulo() {
        // Given
        String message = "Erro de validação";
        String fieldName = null;

        // When
        ValidationException exception = new ValidationException(message, fieldName);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        assertNull(exception.getStrategyName());
        assertNull(exception.getFieldName());
        assertEquals(400, exception.getHttpStatus());
    }

    @Test
    @DisplayName("Deve permitir estratégia nula")
    void devePermitirEstrategiaNula() {
        // Given
        String message = "Validação falhou";
        String strategyName = null;
        String fieldName = "email";

        // When
        ValidationException exception = new ValidationException(message, strategyName, fieldName);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        assertNull(exception.getStrategyName());
        assertEquals(fieldName, exception.getFieldName());
        assertEquals(400, exception.getHttpStatus());
    }

    @Test
    @DisplayName("Deve permitir causa nula")
    void devePermitirCausaNula() {
        // Given
        String message = "Erro de validação";
        Throwable cause = null;

        // When
        ValidationException exception = new ValidationException(message, cause);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        assertNull(exception.getStrategyName());
        assertNull(exception.getFieldName());
        assertNull(exception.getCause());
        assertEquals(400, exception.getHttpStatus());
    }

    @Test
    @DisplayName("Deve herdar de CreditoException")
    void deveHerdarDeCreditoException() {
        // Given
        String message = "Erro de validação";

        // When
        ValidationException exception = new ValidationException(message);

        // Then
        assertTrue(exception instanceof br.com.guilhermedealmeidafreitas.creditos.exception.CreditoException);
    }

    @Test
    @DisplayName("Deve formatar toString com caracteres especiais no nome da estratégia")
    void deveFormatarToStringComCaracteresEspeciaisNoNomeDaEstrategia() {
        // Given
        String message = "Validação falhou";
        String strategyName = "String_Validation-Strategy@123";
        String fieldName = "teste";
        ValidationException exception = new ValidationException(message, strategyName, fieldName);

        // When
        String result = exception.toString();

        // Then
        String expected = "ValidationException [Strategy: " + strategyName + "] [Field: " + fieldName + "]: " + message;
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Deve formatar toString com caracteres especiais no nome do campo")
    void deveFormatarToStringComCaracteresEspeciaisNoNomeDoCampo() {
        // Given
        String message = "Campo obrigatório";
        String strategyName = "StringValidationStrategy";
        String fieldName = "campo-com_especiais@123";
        ValidationException exception = new ValidationException(message, strategyName, fieldName);

        // When
        String result = exception.toString();

        // Then
        String expected = "ValidationException [Strategy: " + strategyName + "] [Field: " + fieldName + "]: " + message;
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Deve manter causa quando fornecida no construtor")
    void deveManterCausaQuandoFornecidaNoConstrutor() {
        // Given
        String message = "Erro de validação";
        RuntimeException cause = new RuntimeException("Erro interno");

        // When
        ValidationException exception = new ValidationException(message, cause);

        // Then
        assertEquals(cause, exception.getCause());
        assertEquals("Erro interno", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("Deve criar exceção com todos os parâmetros nulos exceto mensagem")
    void deveCriarExcecaoComTodosOsParametrosNulosExcetoMensagem() {
        // Given
        String message = "Erro de validação";
        String strategyName = null;
        String fieldName = null;

        // When
        ValidationException exception = new ValidationException(message, strategyName, fieldName);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        assertNull(exception.getStrategyName());
        assertNull(exception.getFieldName());
        assertNull(exception.getCause());
        assertEquals(400, exception.getHttpStatus());
        
        String result = exception.toString();
        String expected = "ValidationException: " + message;
        assertEquals(expected, result);
    }
}
