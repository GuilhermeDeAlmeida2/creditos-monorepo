package br.com.guilhermedealmeidafreitas.creditos.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a classe CreditoExceptions.
 * 
 * Cobertura atual: 67.4%
 * Meta: > 80%
 */
class CreditoExceptionsTest {

    // Testes para método notFound
    @Test
    @DisplayName("Deve criar exceção para crédito não encontrado")
    void deveCriarExcecaoParaCreditoNaoEncontrado() {
        CreditoException exception = CreditoExceptions.notFound("12345", "NFS-e");
        
        assertNotNull(exception);
        assertEquals("Crédito não encontrado para NFS-e: 12345", exception.getMessage());
        assertEquals("CREDITO_NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getHttpStatus());
        assertInstanceOf(SimpleCreditoException.class, exception);
    }

    @Test
    @DisplayName("Deve criar exceção para crédito não encontrado com diferentes tipos")
    void deveCriarExcecaoParaCreditoNaoEncontradoComDiferentesTipos() {
        CreditoException exception1 = CreditoExceptions.notFound("67890", "Simples Nacional");
        CreditoException exception2 = CreditoExceptions.notFound("11111", "Alíquota");
        
        assertEquals("Crédito não encontrado para Simples Nacional: 67890", exception1.getMessage());
        assertEquals("Crédito não encontrado para Alíquota: 11111", exception2.getMessage());
        assertEquals("CREDITO_NOT_FOUND", exception1.getErrorCode());
        assertEquals("CREDITO_NOT_FOUND", exception2.getErrorCode());
        assertEquals(404, exception1.getHttpStatus());
        assertEquals(404, exception2.getHttpStatus());
    }

    // Testes para método validation
    @Test
    @DisplayName("Deve criar exceção de validação simples")
    void deveCriarExcecaoDeValidacaoSimples() {
        CreditoException exception = CreditoExceptions.validation("Campo obrigatório");
        
        assertNotNull(exception);
        assertEquals("Campo obrigatório", exception.getMessage());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        assertEquals(400, exception.getHttpStatus());
        assertInstanceOf(SimpleCreditoException.class, exception);
    }

    @Test
    @DisplayName("Deve criar exceção de validação com campo específico")
    void deveCriarExcecaoDeValidacaoComCampoEspecifico() {
        CreditoException exception = CreditoExceptions.validation("Campo obrigatório", "nome");
        
        assertNotNull(exception);
        assertEquals("Campo obrigatório (campo: nome)", exception.getMessage());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        assertEquals(400, exception.getHttpStatus());
        assertInstanceOf(SimpleCreditoException.class, exception);
    }

    @Test
    @DisplayName("Deve criar exceção de validação com diferentes campos")
    void deveCriarExcecaoDeValidacaoComDiferentesCampos() {
        CreditoException exception1 = CreditoExceptions.validation("Valor inválido", "idade");
        CreditoException exception2 = CreditoExceptions.validation("Formato incorreto", "email");
        
        assertEquals("Valor inválido (campo: idade)", exception1.getMessage());
        assertEquals("Formato incorreto (campo: email)", exception2.getMessage());
        assertEquals("VALIDATION_ERROR", exception1.getErrorCode());
        assertEquals("VALIDATION_ERROR", exception2.getErrorCode());
        assertEquals(400, exception1.getHttpStatus());
        assertEquals(400, exception2.getHttpStatus());
    }

    // Testes para método internalServer
    @Test
    @DisplayName("Deve criar exceção de erro interno do servidor simples")
    void deveCriarExcecaoDeErroInternoDoServidorSimples() {
        CreditoException exception = CreditoExceptions.internalServer("Erro interno");
        
        assertNotNull(exception);
        assertEquals("Erro interno", exception.getMessage());
        assertEquals("INTERNAL_SERVER_ERROR", exception.getErrorCode());
        assertEquals(500, exception.getHttpStatus());
        assertInstanceOf(SimpleCreditoException.class, exception);
    }

    @Test
    @DisplayName("Deve criar exceção de erro interno do servidor com causa")
    void deveCriarExcecaoDeErroInternoDoServidorComCausa() {
        RuntimeException cause = new RuntimeException("Causa original");
        CreditoException exception = CreditoExceptions.internalServer("Erro interno", cause);
        
        assertNotNull(exception);
        assertEquals("Erro interno", exception.getMessage());
        assertEquals("INTERNAL_SERVER_ERROR", exception.getErrorCode());
        assertEquals(500, exception.getHttpStatus());
        assertEquals(cause, exception.getCause());
        assertInstanceOf(SimpleCreditoException.class, exception);
    }

    @Test
    @DisplayName("Deve criar exceção de erro interno do servidor com diferentes causas")
    void deveCriarExcecaoDeErroInternoDoServidorComDiferentesCausas() {
        NullPointerException cause1 = new NullPointerException("NPE");
        IllegalArgumentException cause2 = new IllegalArgumentException("IAE");
        
        CreditoException exception1 = CreditoExceptions.internalServer("Erro NPE", cause1);
        CreditoException exception2 = CreditoExceptions.internalServer("Erro IAE", cause2);
        
        assertEquals("Erro NPE", exception1.getMessage());
        assertEquals("Erro IAE", exception2.getMessage());
        assertEquals(cause1, exception1.getCause());
        assertEquals(cause2, exception2.getCause());
        assertEquals("INTERNAL_SERVER_ERROR", exception1.getErrorCode());
        assertEquals("INTERNAL_SERVER_ERROR", exception2.getErrorCode());
        assertEquals(500, exception1.getHttpStatus());
        assertEquals(500, exception2.getHttpStatus());
    }

    // Testes para método notAvailable
    @Test
    @DisplayName("Deve criar exceção para funcionalidade não disponível")
    void deveCriarExcecaoParaFuncionalidadeNaoDisponivel() {
        CreditoException exception = CreditoExceptions.notAvailable("Funcionalidade em desenvolvimento");
        
        assertNotNull(exception);
        assertEquals("Funcionalidade em desenvolvimento", exception.getMessage());
        assertEquals("FEATURE_NOT_AVAILABLE", exception.getErrorCode());
        assertEquals(403, exception.getHttpStatus());
        assertInstanceOf(SimpleCreditoException.class, exception);
    }

    @Test
    @DisplayName("Deve criar exceção para diferentes funcionalidades não disponíveis")
    void deveCriarExcecaoParaDiferentesFuncionalidadesNaoDisponiveis() {
        CreditoException exception1 = CreditoExceptions.notAvailable("Relatório não implementado");
        CreditoException exception2 = CreditoExceptions.notAvailable("API em manutenção");
        
        assertEquals("Relatório não implementado", exception1.getMessage());
        assertEquals("API em manutenção", exception2.getMessage());
        assertEquals("FEATURE_NOT_AVAILABLE", exception1.getErrorCode());
        assertEquals("FEATURE_NOT_AVAILABLE", exception2.getErrorCode());
        assertEquals(403, exception1.getHttpStatus());
        assertEquals(403, exception2.getHttpStatus());
    }

    // Testes para método testDataError
    @Test
    @DisplayName("Deve criar exceção para erro de dados de teste")
    void deveCriarExcecaoParaErroDeDadosDeTeste() {
        CreditoException exception = CreditoExceptions.testDataError("Erro ao processar dados", "inserção");
        
        assertNotNull(exception);
        assertEquals("Erro ao processar dados (operação: inserção)", exception.getMessage());
        assertEquals("TEST_DATA_ERROR", exception.getErrorCode());
        assertEquals(500, exception.getHttpStatus());
        assertInstanceOf(SimpleCreditoException.class, exception);
    }

    @Test
    @DisplayName("Deve criar exceção para diferentes operações de dados de teste")
    void deveCriarExcecaoParaDiferentesOperacoesDeDadosDeTeste() {
        CreditoException exception1 = CreditoExceptions.testDataError("Falha na criação", "criação");
        CreditoException exception2 = CreditoExceptions.testDataError("Falha na limpeza", "limpeza");
        CreditoException exception3 = CreditoExceptions.testDataError("Falha na validação", "validação");
        
        assertEquals("Falha na criação (operação: criação)", exception1.getMessage());
        assertEquals("Falha na limpeza (operação: limpeza)", exception2.getMessage());
        assertEquals("Falha na validação (operação: validação)", exception3.getMessage());
        assertEquals("TEST_DATA_ERROR", exception1.getErrorCode());
        assertEquals("TEST_DATA_ERROR", exception2.getErrorCode());
        assertEquals("TEST_DATA_ERROR", exception3.getErrorCode());
        assertEquals(500, exception1.getHttpStatus());
        assertEquals(500, exception2.getHttpStatus());
        assertEquals(500, exception3.getHttpStatus());
    }

    // Testes para verificar propriedades herdadas
    @Test
    @DisplayName("Deve verificar propriedades da exceção base")
    void deveVerificarPropriedadesDaExcecaoBase() {
        CreditoException exception = CreditoExceptions.validation("Teste", "campo");
        
        // Verificar que é uma RuntimeException
        assertInstanceOf(RuntimeException.class, exception);
        
        // Verificar que tem getDetails() retornando null
        assertNull(exception.getDetails());
        
        // Verificar que é uma SimpleCreditoException
        assertInstanceOf(SimpleCreditoException.class, exception);
        SimpleCreditoException simpleException = (SimpleCreditoException) exception;
        assertEquals(400, simpleException.getHttpStatus());
    }

    // Testes para verificar formatação de mensagens
    @Test
    @DisplayName("Deve formatar mensagens corretamente")
    void deveFormatarMensagensCorretamente() {
        // Teste notFound
        CreditoException exception1 = CreditoExceptions.notFound("123", "Teste");
        assertEquals("Crédito não encontrado para Teste: 123", exception1.getMessage());
        
        // Teste validation com campo
        CreditoException exception2 = CreditoExceptions.validation("Mensagem", "campo");
        assertEquals("Mensagem (campo: campo)", exception2.getMessage());
        
        // Teste testDataError
        CreditoException exception3 = CreditoExceptions.testDataError("Mensagem", "op");
        assertEquals("Mensagem (operação: op)", exception3.getMessage());
    }

    // Testes para verificar códigos de erro únicos
    @Test
    @DisplayName("Deve usar códigos de erro únicos para cada tipo")
    void deveUsarCodigosDeErroUnicosParaCadaTipo() {
        assertEquals("CREDITO_NOT_FOUND", CreditoExceptions.notFound("1", "T").getErrorCode());
        assertEquals("VALIDATION_ERROR", CreditoExceptions.validation("msg").getErrorCode());
        assertEquals("VALIDATION_ERROR", CreditoExceptions.validation("msg", "f").getErrorCode());
        assertEquals("INTERNAL_SERVER_ERROR", CreditoExceptions.internalServer("msg").getErrorCode());
        assertEquals("INTERNAL_SERVER_ERROR", CreditoExceptions.internalServer("msg", new RuntimeException()).getErrorCode());
        assertEquals("FEATURE_NOT_AVAILABLE", CreditoExceptions.notAvailable("msg").getErrorCode());
        assertEquals("TEST_DATA_ERROR", CreditoExceptions.testDataError("msg", "op").getErrorCode());
    }

    // Testes para verificar status HTTP corretos
    @Test
    @DisplayName("Deve usar status HTTP corretos para cada tipo")
    void deveUsarStatusHttpCorretosParaCadaTipo() {
        assertEquals(404, CreditoExceptions.notFound("1", "T").getHttpStatus());
        assertEquals(400, CreditoExceptions.validation("msg").getHttpStatus());
        assertEquals(400, CreditoExceptions.validation("msg", "f").getHttpStatus());
        assertEquals(500, CreditoExceptions.internalServer("msg").getHttpStatus());
        assertEquals(500, CreditoExceptions.internalServer("msg", new RuntimeException()).getHttpStatus());
        assertEquals(403, CreditoExceptions.notAvailable("msg").getHttpStatus());
        assertEquals(500, CreditoExceptions.testDataError("msg", "op").getHttpStatus());
    }
}
