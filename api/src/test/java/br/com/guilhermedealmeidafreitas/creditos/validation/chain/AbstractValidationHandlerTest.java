package br.com.guilhermedealmeidafreitas.creditos.validation.chain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes para a classe AbstractValidationHandler.
 * 
 * Cobertura atual: 44.4%
 * Meta: > 80%
 */
class AbstractValidationHandlerTest {

    /**
     * Implementação concreta para testar AbstractValidationHandler.
     */
    private static class TestValidationHandler extends AbstractValidationHandler {
        
        private final boolean canHandleResult;
        private final ValidationResult handleResult;
        private final RuntimeException exceptionToThrow;
        
        private TestValidationHandler(String handlerName, int priority, 
                                    boolean canHandleResult, ValidationResult handleResult, 
                                    RuntimeException exceptionToThrow) {
            super(handlerName, priority);
            this.canHandleResult = canHandleResult;
            this.handleResult = handleResult;
            this.exceptionToThrow = exceptionToThrow;
        }
        
        public static TestValidationHandler createWithResult(String handlerName, int priority, 
                                                           boolean canHandleResult, ValidationResult handleResult) {
            return new TestValidationHandler(handlerName, priority, canHandleResult, handleResult, null);
        }
        
        public static TestValidationHandler createWithException(String handlerName, int priority, 
                                                              boolean canHandleResult, RuntimeException exceptionToThrow) {
            return new TestValidationHandler(handlerName, priority, canHandleResult, null, exceptionToThrow);
        }
        
        public static TestValidationHandler createBasic(String handlerName, int priority, boolean canHandleResult) {
            return new TestValidationHandler(handlerName, priority, canHandleResult, null, null);
        }
        
        @Override
        public boolean canHandle(ValidationRequest request) {
            return canHandleResult;
        }
        
        @Override
        protected ValidationResult doHandle(ValidationRequest request) {
            if (exceptionToThrow != null) {
                throw exceptionToThrow;
            }
            return handleResult;
        }
    }

    // Testes para construtor e getters básicos
    @Test
    @DisplayName("Deve criar handler com nome e prioridade corretos")
    void deveCriarHandlerComNomeEPrioridadeCorretos() {
        TestValidationHandler handler = TestValidationHandler.createBasic("TestHandler", 10, true);
        
        assertEquals("TestHandler", handler.getHandlerName());
        assertEquals(10, handler.getPriority());
    }

    @Test
    @DisplayName("Deve configurar próximo handler")
    void deveConfigurarProximoHandler() {
        TestValidationHandler handler1 = TestValidationHandler.createBasic("Handler1", 1, true);
        TestValidationHandler handler2 = TestValidationHandler.createBasic("Handler2", 2, true);
        
        ValidationHandler result = handler1.setNext(handler2);
        
        assertSame(handler1, result);
        assertSame(handler2, handler1.getNextHandler());
    }

    // Testes para método handle - sucesso
    @Test
    @DisplayName("Deve processar requisição com sucesso quando pode handle")
    void deveProcessarRequisicaoComSucessoQuandoPodeHandle() {
        ValidationResult expectedResult = ValidationResult.Builder.success("Sucesso")
            .withFieldName("campo")
            .build();
        
        TestValidationHandler handler = TestValidationHandler.createWithResult("TestHandler", 1, true, expectedResult);
        ValidationRequest request = new ValidationRequest(ValidationType.STRING_NOT_EMPTY, "valor", "campo");
        
        ValidationResult result = handler.handle(request);
        
        assertSame(expectedResult, result);
    }

    @Test
    @DisplayName("Deve processar requisição com falha quando pode handle")
    void deveProcessarRequisicaoComFalhaQuandoPodeHandle() {
        ValidationResult expectedResult = ValidationResult.Builder.error("Erro")
            .withFieldName("campo")
            .build();
        
        TestValidationHandler handler = TestValidationHandler.createWithResult("TestHandler", 1, true, expectedResult);
        ValidationRequest request = new ValidationRequest(ValidationType.STRING_NOT_EMPTY, "valor", "campo");
        
        ValidationResult result = handler.handle(request);
        
        assertSame(expectedResult, result);
    }

    // Testes para método handle - exceção
    @Test
    @DisplayName("Deve capturar exceção e retornar erro quando doHandle lança exceção")
    void deveCapturarExcecaoERetornarErroQuandoDoHandleLancaExcecao() {
        RuntimeException exception = new RuntimeException("Erro de teste");
        TestValidationHandler handler = TestValidationHandler.createWithException("TestHandler", 1, true, exception);
        ValidationRequest request = new ValidationRequest(ValidationType.STRING_NOT_EMPTY, "valor", "campo");
        
        ValidationResult result = handler.handle(request);
        
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("Erro durante validação"));
        assertTrue(result.getMessage().contains("Erro de teste"));
        assertEquals("campo", result.getFieldName());
        assertEquals("TestHandler", result.getHandlerName());
    }

    // Testes para método handle - delegação
    @Test
    @DisplayName("Deve delegar para próximo handler quando não pode handle")
    void deveDelegarParaProximoHandlerQuandoNaoPodeHandle() {
        ValidationResult nextResult = ValidationResult.Builder.success("Próximo handler")
            .withFieldName("campo")
            .build();
        
        TestValidationHandler handler1 = TestValidationHandler.createBasic("Handler1", 1, false);
        TestValidationHandler handler2 = TestValidationHandler.createWithResult("Handler2", 2, true, nextResult);
        
        handler1.setNext(handler2);
        ValidationRequest request = new ValidationRequest(ValidationType.STRING_NOT_EMPTY, "valor", "campo");
        
        ValidationResult result = handler1.handle(request);
        
        assertSame(nextResult, result);
    }

    @Test
    @DisplayName("Deve retornar erro quando não pode handle e não há próximo handler")
    void deveRetornarErroQuandoNaoPodeHandleENaoHaProximoHandler() {
        TestValidationHandler handler = TestValidationHandler.createBasic("TestHandler", 1, false);
        ValidationRequest request = new ValidationRequest(ValidationType.STRING_NOT_EMPTY, "valor", "campo");
        
        ValidationResult result = handler.handle(request);
        
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("Nenhum handler disponível"));
        assertTrue(result.getMessage().contains("STRING_NOT_EMPTY"));
        assertEquals("campo", result.getFieldName());
        assertEquals("ChainOfResponsibility", result.getHandlerName());
    }

    // Testes para métodos auxiliares de resultado
    @Test
    @DisplayName("Deve criar resultado de sucesso com método auxiliar")
    void deveCriarResultadoDeSucessoComMetodoAuxiliar() {
        TestValidationHandler handler = TestValidationHandler.createBasic("TestHandler", 1, true);
        
        ValidationResult result = handler.success("Sucesso", "valor processado");
        
        assertTrue(result.isValid());
        assertEquals("Sucesso", result.getMessage());
        assertEquals("valor processado", result.getProcessedValue());
        assertEquals("TestHandler", result.getHandlerName());
    }

    @Test
    @DisplayName("Deve criar resultado de sucesso com campo usando método auxiliar")
    void deveCriarResultadoDeSucessoComCampoUsandoMetodoAuxiliar() {
        TestValidationHandler handler = TestValidationHandler.createBasic("TestHandler", 1, true);
        
        ValidationResult result = handler.success("Sucesso", "campo", "valor processado");
        
        assertTrue(result.isValid());
        assertEquals("Sucesso", result.getMessage());
        assertEquals("campo", result.getFieldName());
        assertEquals("valor processado", result.getProcessedValue());
        assertEquals("TestHandler", result.getHandlerName());
    }

    @Test
    @DisplayName("Deve criar resultado de erro com método auxiliar")
    void deveCriarResultadoDeErroComMetodoAuxiliar() {
        TestValidationHandler handler = TestValidationHandler.createBasic("TestHandler", 1, true);
        
        ValidationResult result = handler.error("Erro", "campo");
        
        assertFalse(result.isValid());
        assertEquals("Erro", result.getMessage());
        assertEquals("campo", result.getFieldName());
        assertEquals("TestHandler", result.getHandlerName());
    }

    @Test
    @DisplayName("Deve criar resultado de erro com múltiplas mensagens usando método auxiliar")
    void deveCriarResultadoDeErroComMultiplasMensagensUsandoMetodoAuxiliar() {
        TestValidationHandler handler = TestValidationHandler.createBasic("TestHandler", 1, true);
        List<String> errors = Arrays.asList("Erro 1", "Erro 2");
        
        ValidationResult result = handler.error(errors, "campo");
        
        assertFalse(result.isValid());
        assertEquals(errors, result.getErrors());
        assertEquals("campo", result.getFieldName());
        assertEquals("TestHandler", result.getHandlerName());
    }

    // Testes para métodos auxiliares de verificação
    @Test
    @DisplayName("Deve verificar se valor é nulo")
    void deveVerificarSeValorENulo() {
        TestValidationHandler handler = TestValidationHandler.createBasic("TestHandler", 1, true);
        
        assertTrue(handler.isNull(null));
        assertFalse(handler.isNull("valor"));
        assertFalse(handler.isNull(42));
    }

    @Test
    @DisplayName("Deve verificar se string é nula ou vazia")
    void deveVerificarSeStringENulaOuVazia() {
        TestValidationHandler handler = TestValidationHandler.createBasic("TestHandler", 1, true);
        
        assertTrue(handler.isNullOrEmpty(null));
        assertTrue(handler.isNullOrEmpty(""));
        assertTrue(handler.isNullOrEmpty("   "));
        assertFalse(handler.isNullOrEmpty("valor"));
        assertFalse(handler.isNullOrEmpty("  valor  "));
    }

    @Test
    @DisplayName("Deve verificar se string é nula ou blank")
    void deveVerificarSeStringENulaOuBlank() {
        TestValidationHandler handler = TestValidationHandler.createBasic("TestHandler", 1, true);
        
        assertTrue(handler.isNullOrBlank(null));
        assertTrue(handler.isNullOrBlank(""));
        assertTrue(handler.isNullOrBlank("   "));
        assertFalse(handler.isNullOrBlank("valor"));
        assertFalse(handler.isNullOrBlank("  valor  "));
    }

    // Testes para cadeia de handlers
    @Test
    @DisplayName("Deve funcionar com cadeia de handlers")
    void deveFuncionarComCadeiaDeHandlers() {
        ValidationResult result1 = ValidationResult.Builder.success("Handler 1")
            .withFieldName("campo")
            .build();
        ValidationResult result2 = ValidationResult.Builder.success("Handler 2")
            .withFieldName("campo")
            .build();
        
        TestValidationHandler handler1 = TestValidationHandler.createBasic("Handler1", 1, false);
        TestValidationHandler handler2 = TestValidationHandler.createBasic("Handler2", 2, false);
        TestValidationHandler handler3 = TestValidationHandler.createWithResult("Handler3", 3, true, result2);
        
        handler1.setNext(handler2);
        handler2.setNext(handler3);
        
        ValidationRequest request = new ValidationRequest(ValidationType.STRING_NOT_EMPTY, "valor", "campo");
        ValidationResult result = handler1.handle(request);
        
        assertSame(result2, result);
    }

    @Test
    @DisplayName("Deve obter próximo handler corretamente")
    void deveObterProximoHandlerCorretamente() {
        TestValidationHandler handler1 = TestValidationHandler.createBasic("Handler1", 1, true);
        TestValidationHandler handler2 = TestValidationHandler.createBasic("Handler2", 2, true);
        
        assertNull(handler1.getNextHandler());
        
        handler1.setNext(handler2);
        assertSame(handler2, handler1.getNextHandler());
    }
}
