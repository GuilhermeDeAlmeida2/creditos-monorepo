package br.com.guilhermedealmeidafreitas.creditos.validation.chain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para ValidationResult")
class ValidationResultTest {

    // Testes dos construtores
    @Test
    @DisplayName("Deve criar resultado de sucesso simples")
    void deveCriarResultadoDeSucessoSimples() {
        ValidationResult result = new ValidationResult(
            "Validação bem-sucedida", 
            null, 
            "valor123", 
            "TestHandler"
        );

        assertTrue(result.isValid());
        assertFalse(result.isInvalid());
        assertEquals("Validação bem-sucedida", result.getMessage());
        assertEquals("valor123", result.getProcessedValue());
        assertEquals("TestHandler", result.getHandlerName());
        assertNull(result.getFieldName());
        assertFalse(result.hasErrors());
        assertFalse(result.hasWarnings());
        assertTrue(result.getErrors().isEmpty());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    @DisplayName("Deve criar resultado de sucesso com campo")
    void deveCriarResultadoDeSucessoComCampo() {
        ValidationResult result = new ValidationResult(
            "Campo validado", 
            "nome", 
            "João Silva", 
            "StringHandler"
        );

        assertTrue(result.isValid());
        assertEquals("Campo validado", result.getMessage());
        assertEquals("nome", result.getFieldName());
        assertEquals("João Silva", result.getProcessedValue());
        assertEquals("StringHandler", result.getHandlerName());
        assertFalse(result.hasErrors());
        assertFalse(result.hasWarnings());
    }

    @Test
    @DisplayName("Deve criar resultado de falha simples")
    void deveCriarResultadoDeFalhaSimples() {
        ValidationResult result = new ValidationResult(
            "Campo obrigatório", 
            "email", 
            "RequiredHandler"
        );

        assertFalse(result.isValid());
        assertTrue(result.isInvalid());
        assertEquals("Campo obrigatório", result.getMessage());
        assertEquals("email", result.getFieldName());
        assertEquals("RequiredHandler", result.getHandlerName());
        assertNull(result.getProcessedValue());
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("Campo obrigatório", result.getErrors().get(0));
        assertEquals("Campo obrigatório", result.getFirstError());
    }

    @Test
    @DisplayName("Deve criar resultado de falha com múltiplos erros")
    void deveCriarResultadoDeFalhaComMultiplosErros() {
        List<String> errors = Arrays.asList("Erro 1", "Erro 2", "Erro 3");
        ValidationResult result = new ValidationResult(
            errors, 
            "senha", 
            "PasswordHandler"
        );

        assertFalse(result.isValid());
        assertEquals("Erro 1", result.getMessage()); // Primeiro erro como mensagem principal
        assertEquals("senha", result.getFieldName());
        assertEquals("PasswordHandler", result.getHandlerName());
        assertNull(result.getProcessedValue());
        assertTrue(result.hasErrors());
        assertEquals(3, result.getErrors().size());
        assertEquals("Erro 1", result.getFirstError());
        assertEquals(errors, result.getErrors());
    }

    @Test
    @DisplayName("Deve criar resultado completo com todos os parâmetros")
    void deveCriarResultadoCompletoComTodosOsParametros() {
        List<String> errors = Arrays.asList("Erro 1", "Erro 2");
        List<String> warnings = Arrays.asList("Aviso 1", "Aviso 2");
        
        ValidationResult result = new ValidationResult(
            false, 
            "Validação falhou", 
            "telefone", 
            errors, 
            warnings, 
            "123456789", 
            "PhoneHandler"
        );

        assertFalse(result.isValid());
        assertEquals("Validação falhou", result.getMessage());
        assertEquals("telefone", result.getFieldName());
        assertEquals("PhoneHandler", result.getHandlerName());
        assertEquals("123456789", result.getProcessedValue());
        assertTrue(result.hasErrors());
        assertTrue(result.hasWarnings());
        assertEquals(2, result.getErrors().size());
        assertEquals(2, result.getWarnings().size());
        assertEquals("Erro 1", result.getFirstError());
        assertEquals("Aviso 1", result.getFirstWarning());
    }

    @Test
    @DisplayName("Deve lidar com listas nulas no construtor completo")
    void deveLidarComListasNulasNoConstrutorCompleto() {
        ValidationResult result = new ValidationResult(
            true, 
            "Sucesso", 
            "campo", 
            null, 
            null, 
            "valor", 
            "Handler"
        );

        assertTrue(result.isValid());
        assertEquals("Sucesso", result.getMessage());
        assertEquals("campo", result.getFieldName());
        assertEquals("valor", result.getProcessedValue());
        assertEquals("Handler", result.getHandlerName());
        assertFalse(result.hasErrors());
        assertFalse(result.hasWarnings());
        assertTrue(result.getErrors().isEmpty());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    @DisplayName("Deve lidar com lista vazia de erros")
    void deveLidarComListaVaziaDeErros() {
        ValidationResult result = new ValidationResult(
            Collections.emptyList(), 
            "campo", 
            "Handler"
        );

        assertFalse(result.isValid());
        assertEquals("Validação falhou", result.getMessage()); // Mensagem padrão
        assertEquals("campo", result.getFieldName());
        assertEquals("Handler", result.getHandlerName());
        assertFalse(result.hasErrors());
        assertTrue(result.getErrors().isEmpty());
        assertNull(result.getFirstError());
    }

    // Testes do Builder
    @Test
    @DisplayName("Deve criar resultado de sucesso usando Builder")
    void deveCriarResultadoDeSucessoUsandoBuilder() {
        ValidationResult result = ValidationResult.Builder.success("Validação OK")
            .withFieldName("nome")
            .withProcessedValue("João")
            .withHandlerName("StringHandler")
            .build();

        assertTrue(result.isValid());
        assertEquals("Validação OK", result.getMessage());
        assertEquals("nome", result.getFieldName());
        assertEquals("João", result.getProcessedValue());
        assertEquals("StringHandler", result.getHandlerName());
        assertFalse(result.hasErrors());
        assertFalse(result.hasWarnings());
    }

    @Test
    @DisplayName("Deve criar resultado de erro usando Builder")
    void deveCriarResultadoDeErroUsandoBuilder() {
        ValidationResult result = ValidationResult.Builder.error("Campo inválido")
            .withFieldName("email")
            .withHandlerName("EmailHandler")
            .build();

        assertFalse(result.isValid());
        assertEquals("Campo inválido", result.getMessage());
        assertEquals("email", result.getFieldName());
        assertEquals("EmailHandler", result.getHandlerName());
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("Campo inválido", result.getErrors().get(0));
    }

    @Test
    @DisplayName("Deve criar resultado com múltiplos erros usando Builder")
    void deveCriarResultadoComMultiplosErrosUsandoBuilder() {
        ValidationResult result = new ValidationResult.Builder()
            .withValid(false)
            .withMessage("Múltiplos erros")
            .withFieldName("senha")
            .withHandlerName("PasswordHandler")
            .withError("Muito curta")
            .withError("Sem números")
            .withError("Sem caracteres especiais")
            .build();

        assertFalse(result.isValid());
        assertEquals("Múltiplos erros", result.getMessage());
        assertEquals("senha", result.getFieldName());
        assertEquals("PasswordHandler", result.getHandlerName());
        assertTrue(result.hasErrors());
        assertEquals(3, result.getErrors().size());
        assertTrue(result.getErrors().contains("Muito curta"));
        assertTrue(result.getErrors().contains("Sem números"));
        assertTrue(result.getErrors().contains("Sem caracteres especiais"));
    }

    @Test
    @DisplayName("Deve criar resultado com avisos usando Builder")
    void deveCriarResultadoComAvisosUsandoBuilder() {
        ValidationResult result = new ValidationResult.Builder()
            .withValid(true)
            .withMessage("Validação com avisos")
            .withFieldName("telefone")
            .withHandlerName("PhoneHandler")
            .withProcessedValue("123456789")
            .withWarning("Formato não padrão")
            .withWarning("Considerar internacionalização")
            .build();

        assertTrue(result.isValid());
        assertEquals("Validação com avisos", result.getMessage());
        assertEquals("telefone", result.getFieldName());
        assertEquals("123456789", result.getProcessedValue());
        assertEquals("PhoneHandler", result.getHandlerName());
        assertFalse(result.hasErrors());
        assertTrue(result.hasWarnings());
        assertEquals(2, result.getWarnings().size());
        assertTrue(result.getWarnings().contains("Formato não padrão"));
        assertTrue(result.getWarnings().contains("Considerar internacionalização"));
        assertEquals("Formato não padrão", result.getFirstWarning());
    }

    @Test
    @DisplayName("Deve ignorar erros e avisos vazios no Builder")
    void deveIgnorarErrosEAvisosVaziosNoBuilder() {
        ValidationResult result = new ValidationResult.Builder()
            .withValid(false)
            .withMessage("Erro principal")
            .withFieldName("campo")
            .withHandlerName("Handler")
            .withError("") // Erro vazio - deve ser ignorado
            .withError("   ") // Erro com espaços - deve ser ignorado
            .withError(null) // Erro nulo - deve ser ignorado
            .withError("Erro válido")
            .withWarning("") // Aviso vazio - deve ser ignorado
            .withWarning("Aviso válido")
            .build();

        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertTrue(result.hasWarnings());
        assertEquals(1, result.getErrors().size()); // Apenas "Erro válido" (mensagem principal não é adicionada automaticamente)
        assertEquals(1, result.getWarnings().size()); // Apenas "Aviso válido"
        assertTrue(result.getErrors().contains("Erro válido"));
        assertTrue(result.getWarnings().contains("Aviso válido"));
    }

    @Test
    @DisplayName("Deve definir listas de erros e avisos no Builder")
    void deveDefinirListasDeErrosEAvisosNoBuilder() {
        List<String> errors = Arrays.asList("Erro 1", "Erro 2");
        List<String> warnings = Arrays.asList("Aviso 1", "Aviso 2");
        
        ValidationResult result = new ValidationResult.Builder()
            .withValid(false)
            .withMessage("Erro principal")
            .withFieldName("campo")
            .withHandlerName("Handler")
            .withErrors(errors)
            .withWarnings(warnings)
            .build();

        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertTrue(result.hasWarnings());
        assertEquals(2, result.getErrors().size());
        assertEquals(2, result.getWarnings().size());
        assertEquals(errors, result.getErrors());
        assertEquals(warnings, result.getWarnings());
    }

    @Test
    @DisplayName("Deve lidar com listas nulas no Builder")
    void deveLidarComListasNulasNoBuilder() {
        ValidationResult result = new ValidationResult.Builder()
            .withValid(true)
            .withMessage("Sucesso")
            .withFieldName("campo")
            .withHandlerName("Handler")
            .withErrors(null)
            .withWarnings(null)
            .build();

        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
        assertFalse(result.hasWarnings());
        assertTrue(result.getErrors().isEmpty());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    @DisplayName("Deve configurar mensagem automaticamente para erros no Builder")
    void deveConfigurarMensagemAutomaticamenteParaErrosNoBuilder() {
        ValidationResult result = new ValidationResult.Builder()
            .withValid(false)
            .withFieldName("campo")
            .withHandlerName("Handler")
            .withError("Primeiro erro")
            .withError("Segundo erro")
            .build();

        assertFalse(result.isValid());
        assertEquals("Primeiro erro", result.getMessage()); // Mensagem automática do primeiro erro
        assertTrue(result.hasErrors());
        assertEquals(2, result.getErrors().size());
    }

    @Test
    @DisplayName("Deve adicionar mensagem principal aos erros quando necessário")
    void deveAdicionarMensagemPrincipalAosErrosQuandoNecessario() {
        ValidationResult result = new ValidationResult.Builder()
            .withValid(false)
            .withMessage("Erro principal")
            .withFieldName("campo")
            .withHandlerName("Handler")
            .build();

        assertFalse(result.isValid());
        assertEquals("Erro principal", result.getMessage());
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("Erro principal", result.getErrors().get(0));
    }

    // Testes de imutabilidade
    @Test
    @DisplayName("Deve retornar listas imutáveis")
    void deveRetornarListasImutaveis() {
        List<String> errors = Arrays.asList("Erro 1", "Erro 2");
        List<String> warnings = Arrays.asList("Aviso 1", "Aviso 2");
        
        ValidationResult result = new ValidationResult(
            false, 
            "Erro", 
            "campo", 
            errors, 
            warnings, 
            null, 
            "Handler"
        );

        assertThrows(UnsupportedOperationException.class, () -> {
            result.getErrors().add("Novo erro");
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            result.getWarnings().add("Novo aviso");
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            result.getErrors().remove(0);
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            result.getWarnings().clear();
        });
    }

    // Testes do toString
    @Test
    @DisplayName("Deve gerar toString corretamente")
    void deveGerarToStringCorretamente() {
        ValidationResult result = new ValidationResult(
            "Validação OK", 
            "nome", 
            "João", 
            "StringHandler"
        );

        String toString = result.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("valid=true"));
        assertTrue(toString.contains("fieldName='nome'"));
        assertTrue(toString.contains("message='Validação OK'"));
        assertTrue(toString.contains("handlerName='StringHandler'"));
    }

    // Testes de edge cases
    @Test
    @DisplayName("Deve lidar com valores nulos corretamente")
    void deveLidarComValoresNulosCorretamente() {
        ValidationResult result = new ValidationResult(
            (String) null, 
            null, 
            (String) null, 
            (String) null
        );

        assertTrue(result.isValid());
        assertNull(result.getMessage());
        assertNull(result.getProcessedValue());
        assertNull(result.getHandlerName());
        assertNull(result.getFieldName());
        assertFalse(result.hasErrors());
        assertFalse(result.hasWarnings());
    }

    @Test
    @DisplayName("Deve lidar com strings vazias corretamente")
    void deveLidarComStringsVaziasCorretamente() {
        ValidationResult result = new ValidationResult(
            "", 
            null, 
            "", 
            ""
        );

        assertTrue(result.isValid());
        assertEquals("", result.getMessage());
        assertEquals("", result.getProcessedValue());
        assertEquals("", result.getHandlerName());
        assertFalse(result.hasErrors());
        assertFalse(result.hasWarnings());
    }

    @Test
    @DisplayName("Deve retornar null para primeiro erro/aviso quando listas estão vazias")
    void deveRetornarNullParaPrimeiroErroAvisoQuandoListasEstaoVazias() {
        ValidationResult result = new ValidationResult(
            "Sucesso", 
            null, 
            "valor", 
            "Handler"
        );

        // Para resultado de sucesso, não há erros ou avisos, então getFirstError/getFirstWarning retornam null
        assertNull(result.getFirstError());
        assertNull(result.getFirstWarning());
    }
}
