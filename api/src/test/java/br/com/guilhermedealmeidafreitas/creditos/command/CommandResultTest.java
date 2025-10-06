package br.com.guilhermedealmeidafreitas.creditos.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para CommandResult.
 * 
 * Este teste cobre todos os cenários do resultado de comando,
 * incluindo criação, métodos estáticos, getters e conversões de tipo.
 */
@ExtendWith(MockitoExtension.class)
class CommandResultTest {

    @Test
    @DisplayName("Deve criar resultado de sucesso com construtor básico")
    void deveCriarResultadoDeSucessoComConstrutorBasico() {
        // Given
        String message = "Operação executada com sucesso";
        Object data = "dados";
        long executionTime = 1000L;

        // When
        CommandResult result = new CommandResult(message, data, executionTime);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertFalse(result.isError());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
        assertEquals(executionTime, result.getExecutionTimeMs());
        assertNull(result.getError());
        assertNull(result.getMetadata());
        assertNotNull(result.getExecutedAt());
    }

    @Test
    @DisplayName("Deve criar resultado de sucesso com metadados")
    void deveCriarResultadoDeSucessoComMetadados() {
        // Given
        String message = "Operação executada com sucesso";
        Object data = "dados";
        long executionTime = 1000L;
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("count", 10);
        metadata.put("type", "test");

        // When
        CommandResult result = new CommandResult(message, data, executionTime, metadata);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertFalse(result.isError());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
        assertEquals(executionTime, result.getExecutionTimeMs());
        assertEquals(metadata, result.getMetadata());
        assertNull(result.getError());
        assertNotNull(result.getExecutedAt());
    }

    @Test
    @DisplayName("Deve criar resultado de erro")
    void deveCriarResultadoDeErro() {
        // Given
        String message = "Erro na operação";
        RuntimeException error = new RuntimeException("Erro interno");
        long executionTime = 500L;

        // When
        CommandResult result = new CommandResult(message, error, executionTime);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.isError());
        assertEquals(message, result.getMessage());
        assertNull(result.getData());
        assertEquals(executionTime, result.getExecutionTimeMs());
        assertEquals(error, result.getError());
        assertNull(result.getMetadata());
        assertNotNull(result.getExecutedAt());
    }

    @Test
    @DisplayName("Deve criar resultado com construtor completo")
    void deveCriarResultadoComConstrutorCompleto() {
        // Given
        boolean success = true;
        String message = "Operação completa";
        Object data = "dados";
        LocalDateTime executedAt = LocalDateTime.now();
        long executionTime = 750L;
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("status", "completed");
        Throwable error = null;

        // When
        CommandResult result = new CommandResult(success, message, data, executedAt, executionTime, metadata, error);

        // Then
        assertNotNull(result);
        assertEquals(success, result.isSuccess());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
        assertEquals(executedAt, result.getExecutedAt());
        assertEquals(executionTime, result.getExecutionTimeMs());
        assertEquals(metadata, result.getMetadata());
        assertEquals(error, result.getError());
    }

    @Test
    @DisplayName("Deve criar resultado de sucesso usando método estático")
    void deveCriarResultadoDeSucessoUsandoMetodoEstatico() {
        // Given
        String message = "Sucesso estático";
        Object data = "dados estáticos";
        long executionTime = 2000L;

        // When
        CommandResult result = CommandResult.success(message, data, executionTime);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertFalse(result.isError());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
        assertEquals(executionTime, result.getExecutionTimeMs());
        assertNull(result.getError());
        assertNull(result.getMetadata());
    }

    @Test
    @DisplayName("Deve criar resultado de sucesso com metadados usando método estático")
    void deveCriarResultadoDeSucessoComMetadadosUsandoMetodoEstatico() {
        // Given
        String message = "Sucesso com metadados";
        Object data = "dados";
        long executionTime = 1500L;
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("processed", true);
        metadata.put("items", 5);

        // When
        CommandResult result = CommandResult.success(message, data, executionTime, metadata);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
        assertEquals(executionTime, result.getExecutionTimeMs());
        assertEquals(metadata, result.getMetadata());
        assertNull(result.getError());
    }

    @Test
    @DisplayName("Deve criar resultado de erro usando método estático")
    void deveCriarResultadoDeErroUsandoMetodoEstatico() {
        // Given
        String message = "Erro estático";
        IllegalArgumentException error = new IllegalArgumentException("Parâmetro inválido");
        long executionTime = 300L;

        // When
        CommandResult result = CommandResult.error(message, error, executionTime);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.isError());
        assertEquals(message, result.getMessage());
        assertNull(result.getData());
        assertEquals(executionTime, result.getExecutionTimeMs());
        assertEquals(error, result.getError());
        assertNull(result.getMetadata());
    }

    @Test
    @DisplayName("Deve retornar dados como tipo específico")
    void deveRetornarDadosComoTipoEspecifico() {
        // Given
        String message = "Teste de tipo";
        String data = "dados string";
        long executionTime = 100L;
        CommandResult result = new CommandResult(message, data, executionTime);

        // When
        String typedData = result.getDataAs(String.class);

        // Then
        assertEquals(data, typedData);
    }

    @Test
    @DisplayName("Deve retornar null para dados nulos")
    void deveRetornarNullParaDadosNulos() {
        // Given
        String message = "Teste de dados nulos";
        Object data = null;
        long executionTime = 100L;
        CommandResult result = new CommandResult(message, data, executionTime);

        // When
        String typedData = result.getDataAs(String.class);

        // Then
        assertNull(typedData);
    }

    @Test
    @DisplayName("Deve lançar ClassCastException para tipo incorreto")
    void deveLancarClassCastExceptionParaTipoIncorreto() {
        // Given
        String message = "Teste de tipo incorreto";
        String data = "dados string";
        long executionTime = 100L;
        CommandResult result = new CommandResult(message, data, executionTime);

        // When & Then
        ClassCastException exception = assertThrows(
            ClassCastException.class,
            () -> result.getDataAs(Integer.class)
        );
        
        assertTrue(exception.getMessage().contains("Data is not of type Integer"));
    }

    @Test
    @DisplayName("Deve retornar metadado específico")
    void deveRetornarMetadadoEspecifico() {
        // Given
        String message = "Teste de metadados";
        Object data = "dados";
        long executionTime = 100L;
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("count", 10);
        metadata.put("type", "test");
        CommandResult result = new CommandResult(message, data, executionTime, metadata);

        // When
        Object count = result.getMetadata("count");
        Object type = result.getMetadata("type");
        Object nonExistent = result.getMetadata("nonExistent");

        // Then
        assertEquals(10, count);
        assertEquals("test", type);
        assertNull(nonExistent);
    }

    @Test
    @DisplayName("Deve retornar null para metadados nulos")
    void deveRetornarNullParaMetadadosNulos() {
        // Given
        String message = "Teste sem metadados";
        Object data = "dados";
        long executionTime = 100L;
        CommandResult result = new CommandResult(message, data, executionTime);

        // When
        Object metadata = result.getMetadata("anyKey");

        // Then
        assertNull(metadata);
    }

    @Test
    @DisplayName("Deve retornar metadado como tipo específico")
    void deveRetornarMetadadoComoTipoEspecifico() {
        // Given
        String message = "Teste de tipo de metadado";
        Object data = "dados";
        long executionTime = 100L;
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("count", 10);
        metadata.put("name", "teste");
        CommandResult result = new CommandResult(message, data, executionTime, metadata);

        // When
        Integer count = result.getMetadataAs("count", Integer.class);
        String name = result.getMetadataAs("name", String.class);

        // Then
        assertEquals(Integer.valueOf(10), count);
        assertEquals("teste", name);
    }

    @Test
    @DisplayName("Deve retornar null para metadado nulo")
    void deveRetornarNullParaMetadadoNulo() {
        // Given
        String message = "Teste de metadado nulo";
        Object data = "dados";
        long executionTime = 100L;
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("count", null);
        CommandResult result = new CommandResult(message, data, executionTime, metadata);

        // When
        Integer count = result.getMetadataAs("count", Integer.class);
        Integer nonExistent = result.getMetadataAs("nonExistent", Integer.class);

        // Then
        assertNull(count);
        assertNull(nonExistent);
    }

    @Test
    @DisplayName("Deve lançar ClassCastException para tipo de metadado incorreto")
    void deveLancarClassCastExceptionParaTipoDeMetadadoIncorreto() {
        // Given
        String message = "Teste de tipo de metadado incorreto";
        Object data = "dados";
        long executionTime = 100L;
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("count", "dez"); // String em vez de Integer
        CommandResult result = new CommandResult(message, data, executionTime, metadata);

        // When & Then
        ClassCastException exception = assertThrows(
            ClassCastException.class,
            () -> result.getMetadataAs("count", Integer.class)
        );
        
        assertTrue(exception.getMessage().contains("Metadata 'count' is not of type Integer"));
    }

    @Test
    @DisplayName("Deve criar resultado de erro com dados nulos")
    void deveCriarResultadoDeErroComDadosNulos() {
        // Given
        String message = "Erro com dados nulos";
        RuntimeException error = new RuntimeException("Erro");
        long executionTime = 100L;

        // When
        CommandResult result = new CommandResult(message, error, executionTime);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.isError());
        assertEquals(message, result.getMessage());
        assertNull(result.getData());
        assertEquals(error, result.getError());
        assertEquals(executionTime, result.getExecutionTimeMs());
    }

    @Test
    @DisplayName("Deve criar resultado de sucesso com dados nulos")
    void deveCriarResultadoDeSucessoComDadosNulos() {
        // Given
        String message = "Sucesso com dados nulos";
        Object data = null;
        long executionTime = 100L;

        // When
        CommandResult result = new CommandResult(message, data, executionTime);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertFalse(result.isError());
        assertEquals(message, result.getMessage());
        assertNull(result.getData());
        assertEquals(executionTime, result.getExecutionTimeMs());
        assertNull(result.getError());
    }

    @Test
    @DisplayName("Deve criar resultado com metadados vazios")
    void deveCriarResultadoComMetadadosVazios() {
        // Given
        String message = "Sucesso com metadados vazios";
        Object data = "dados";
        long executionTime = 100L;
        Map<String, Object> metadata = new HashMap<>(); // Vazio

        // When
        CommandResult result = new CommandResult(message, data, executionTime, metadata);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
        assertEquals(metadata, result.getMetadata());
        assertTrue(metadata.isEmpty());
    }

    @Test
    @DisplayName("Deve criar resultado com tempo de execução zero")
    void deveCriarResultadoComTempoDeExecucaoZero() {
        // Given
        String message = "Execução instantânea";
        Object data = "dados";
        long executionTime = 0L;

        // When
        CommandResult result = new CommandResult(message, data, executionTime);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
        assertEquals(0L, result.getExecutionTimeMs());
    }

    @Test
    @DisplayName("Deve criar resultado com tempo de execução negativo")
    void deveCriarResultadoComTempoDeExecucaoNegativo() {
        // Given
        String message = "Execução com tempo negativo";
        Object data = "dados";
        long executionTime = -100L;

        // When
        CommandResult result = new CommandResult(message, data, executionTime);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
        assertEquals(-100L, result.getExecutionTimeMs());
    }

    @Test
    @DisplayName("Deve criar resultado com mensagem nula")
    void deveCriarResultadoComMensagemNula() {
        // Given
        String message = null;
        Object data = "dados";
        long executionTime = 100L;

        // When
        CommandResult result = new CommandResult(message, data, executionTime);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNull(result.getMessage());
        assertEquals(data, result.getData());
        assertEquals(executionTime, result.getExecutionTimeMs());
    }

    @Test
    @DisplayName("Deve criar resultado com timestamp de execução específico")
    void deveCriarResultadoComTimestampDeExecucaoEspecifico() {
        // Given
        boolean success = true;
        String message = "Execução com timestamp";
        Object data = "dados";
        LocalDateTime executedAt = LocalDateTime.of(2023, 12, 25, 10, 30, 0);
        long executionTime = 100L;
        Map<String, Object> metadata = null;
        Throwable error = null;

        // When
        CommandResult result = new CommandResult(success, message, data, executedAt, executionTime, metadata, error);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
        assertEquals(executedAt, result.getExecutedAt());
        assertEquals(executionTime, result.getExecutionTimeMs());
        assertNull(result.getMetadata());
        assertNull(result.getError());
    }

    @Test
    @DisplayName("Deve criar resultado de erro com construtor completo")
    void deveCriarResultadoDeErroComConstrutorCompleto() {
        // Given
        boolean success = false;
        String message = "Erro completo";
        Object data = null;
        LocalDateTime executedAt = LocalDateTime.now();
        long executionTime = 200L;
        Map<String, Object> metadata = null;
        RuntimeException error = new RuntimeException("Erro interno");

        // When
        CommandResult result = new CommandResult(success, message, data, executedAt, executionTime, metadata, error);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.isError());
        assertEquals(message, result.getMessage());
        assertNull(result.getData());
        assertEquals(executedAt, result.getExecutedAt());
        assertEquals(executionTime, result.getExecutionTimeMs());
        assertNull(result.getMetadata());
        assertEquals(error, result.getError());
    }
}