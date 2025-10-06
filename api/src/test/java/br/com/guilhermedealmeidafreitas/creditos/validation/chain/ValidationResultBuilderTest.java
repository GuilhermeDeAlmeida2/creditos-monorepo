package br.com.guilhermedealmeidafreitas.creditos.validation.chain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationResultBuilderTest {

    @Test
    @DisplayName("Builder.success deve criar resultado de sucesso")
    void builderSuccessDeveCriarResultadoDeSucesso() {
        // Act
        ValidationResult result = ValidationResult.Builder.success("Validação bem-sucedida")
            .withFieldName("testField")
            .withProcessedValue("processedValue")
            .withHandlerName("TestHandler")
            .build();

        // Assert
        assertTrue(result.isValid());
        assertEquals("Validação bem-sucedida", result.getMessage());
        assertEquals("testField", result.getFieldName());
        assertEquals("processedValue", result.getProcessedValue());
        assertEquals("TestHandler", result.getHandlerName());
        assertFalse(result.hasErrors());
        assertFalse(result.hasWarnings());
    }

    @Test
    @DisplayName("Builder.error deve criar resultado de erro")
    void builderErrorDeveCriarResultadoDeErro() {
        // Act
        ValidationResult result = ValidationResult.Builder.error("Erro de validação")
            .withFieldName("testField")
            .withHandlerName("TestHandler")
            .build();

        // Assert
        assertFalse(result.isValid());
        assertEquals("Erro de validação", result.getMessage());
        assertEquals("testField", result.getFieldName());
        assertEquals("TestHandler", result.getHandlerName());
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("Erro de validação", result.getFirstError());
        assertFalse(result.hasWarnings());
    }

    @Test
    @DisplayName("Builder deve permitir adicionar múltiplos erros")
    void builderDevePermitirAdicionarMultiplosErros() {
        // Act
        ValidationResult result = ValidationResult.Builder.error("Primeiro erro")
            .withError("Segundo erro")
            .withError("Terceiro erro")
            .withFieldName("testField")
            .withHandlerName("TestHandler")
            .build();

        // Assert
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals(3, result.getErrors().size());
        assertEquals("Primeiro erro", result.getFirstError());
        assertTrue(result.getErrors().contains("Segundo erro"));
        assertTrue(result.getErrors().contains("Terceiro erro"));
    }

    @Test
    @DisplayName("Builder deve permitir adicionar avisos")
    void builderDevePermitirAdicionarAvisos() {
        // Act
        ValidationResult result = ValidationResult.Builder.success("Validação com avisos")
            .withWarning("Primeiro aviso")
            .withWarning("Segundo aviso")
            .withFieldName("testField")
            .withHandlerName("TestHandler")
            .build();

        // Assert
        assertTrue(result.isValid());
        assertTrue(result.hasWarnings());
        assertEquals(2, result.getWarnings().size());
        assertEquals("Primeiro aviso", result.getFirstWarning());
        assertTrue(result.getWarnings().contains("Segundo aviso"));
    }

    @Test
    @DisplayName("Builder deve permitir definir lista de erros")
    void builderDevePermitirDefinirListaDeErros() {
        // Arrange
        List<String> errors = Arrays.asList("Erro 1", "Erro 2", "Erro 3");

        // Act
        ValidationResult result = ValidationResult.Builder.error("Erro principal")
            .withErrors(errors)
            .withFieldName("testField")
            .withHandlerName("TestHandler")
            .build();

        // Assert
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals(3, result.getErrors().size());
        assertTrue(result.getErrors().containsAll(errors));
    }

    @Test
    @DisplayName("Builder deve permitir definir lista de avisos")
    void builderDevePermitirDefinirListaDeAvisos() {
        // Arrange
        List<String> warnings = Arrays.asList("Aviso 1", "Aviso 2");

        // Act
        ValidationResult result = ValidationResult.Builder.success("Validação com avisos")
            .withWarnings(warnings)
            .withFieldName("testField")
            .withHandlerName("TestHandler")
            .build();

        // Assert
        assertTrue(result.isValid());
        assertTrue(result.hasWarnings());
        assertEquals(2, result.getWarnings().size());
        assertTrue(result.getWarnings().containsAll(warnings));
    }

    @Test
    @DisplayName("Builder deve ignorar erros nulos ou vazios")
    void builderDeveIgnorarErrosNulosOuVazios() {
        // Act
        ValidationResult result = ValidationResult.Builder.error("Erro válido")
            .withError(null)
            .withError("")
            .withError("   ")
            .withError("Erro válido 2")
            .withFieldName("testField")
            .withHandlerName("TestHandler")
            .build();

        // Assert
        assertFalse(result.isValid());
        assertEquals(2, result.getErrors().size());
        assertTrue(result.getErrors().contains("Erro válido"));
        assertTrue(result.getErrors().contains("Erro válido 2"));
    }

    @Test
    @DisplayName("Builder deve ignorar avisos nulos ou vazios")
    void builderDeveIgnorarAvisosNulosOuVazios() {
        // Act
        ValidationResult result = ValidationResult.Builder.success("Sucesso com avisos")
            .withWarning(null)
            .withWarning("")
            .withWarning("   ")
            .withWarning("Aviso válido")
            .withFieldName("testField")
            .withHandlerName("TestHandler")
            .build();

        // Assert
        assertTrue(result.isValid());
        assertEquals(1, result.getWarnings().size());
        assertTrue(result.getWarnings().contains("Aviso válido"));
    }

    @Test
    @DisplayName("Builder deve usar primeiro erro como mensagem principal quando não especificada")
    void builderDeveUsarPrimeiroErroComoMensagemPrincipalQuandoNaoEspecificada() {
        // Act
        ValidationResult result = new ValidationResult.Builder()
            .withValid(false)
            .withError("Primeiro erro")
            .withError("Segundo erro")
            .withFieldName("testField")
            .withHandlerName("TestHandler")
            .build();

        // Assert
        assertFalse(result.isValid());
        assertEquals("Primeiro erro", result.getMessage());
        assertEquals(2, result.getErrors().size());
    }

    @Test
    @DisplayName("Builder deve adicionar mensagem principal aos erros se não estiver na lista")
    void builderDeveAdicionarMensagemPrincipalAosErrosSeNaoEstiverNaLista() {
        // Act
        ValidationResult result = ValidationResult.Builder.error("Mensagem principal")
            .withError("Erro adicional")
            .withFieldName("testField")
            .withHandlerName("TestHandler")
            .build();

        // Assert
        assertFalse(result.isValid());
        assertEquals("Mensagem principal", result.getMessage());
        assertEquals(2, result.getErrors().size());
        assertTrue(result.getErrors().contains("Mensagem principal"));
        assertTrue(result.getErrors().contains("Erro adicional"));
    }

    @Test
    @DisplayName("Builder deve permitir criar resultado complexo com todos os campos")
    void builderDevePermitirCriarResultadoComplexoComTodosOsCampos() {
        // Arrange
        List<String> errors = Arrays.asList("Erro 1", "Erro 2");
        List<String> warnings = Arrays.asList("Aviso 1", "Aviso 2");

        // Act
        ValidationResult result = new ValidationResult.Builder()
            .withValid(false)
            .withMessage("Mensagem principal")
            .withFieldName("campoTeste")
            .withErrors(errors)
            .withWarnings(warnings)
            .withProcessedValue("valorProcessado")
            .withHandlerName("HandlerTeste")
            .build();

        // Assert
        assertFalse(result.isValid());
        assertEquals("Mensagem principal", result.getMessage());
        assertEquals("campoTeste", result.getFieldName());
        assertEquals("valorProcessado", result.getProcessedValue());
        assertEquals("HandlerTeste", result.getHandlerName());
        assertTrue(result.hasErrors());
        assertTrue(result.hasWarnings());
        assertEquals(2, result.getErrors().size());
        assertEquals(2, result.getWarnings().size());
    }

    @Test
    @DisplayName("Builder deve permitir encadeamento fluente")
    void builderDevePermitirEncadeamentoFluente() {
        // Act
        ValidationResult result = ValidationResult.Builder.success("Sucesso")
            .withFieldName("field1")
            .withProcessedValue("value1")
            .withWarning("warning1")
            .withHandlerName("handler1")
            .build();

        // Assert
        assertTrue(result.isValid());
        assertEquals("Sucesso", result.getMessage());
        assertEquals("field1", result.getFieldName());
        assertEquals("value1", result.getProcessedValue());
        assertEquals("handler1", result.getHandlerName());
        assertTrue(result.hasWarnings());
        assertEquals("warning1", result.getFirstWarning());
    }

    @Test
    @DisplayName("Builder deve criar resultado válido por padrão")
    void builderDeveCriarResultadoValidoPorPadrao() {
        // Act
        ValidationResult result = new ValidationResult.Builder()
            .withMessage("Mensagem")
            .build();

        // Assert
        assertTrue(result.isValid());
        assertEquals("Mensagem", result.getMessage());
        assertFalse(result.hasErrors());
        assertFalse(result.hasWarnings());
    }
}
