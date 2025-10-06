package br.com.guilhermedealmeidafreitas.creditos.command;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Resultado da execução de um comando.
 * Contém informações sobre o sucesso/falha e dados retornados.
 * 
 * REFATORAÇÃO: Implementa Command Pattern para organizar
 * e padronizar operações de teste no sistema.
 */
public class CommandResult {
    
    private final boolean success;
    private final String message;
    private final Object data;
    private final LocalDateTime executedAt;
    private final long executionTimeMs;
    private final Map<String, Object> metadata;
    private final Throwable error;
    
    /**
     * Construtor para resultado de sucesso.
     * 
     * @param message Mensagem de sucesso
     * @param data Dados retornados
     * @param executionTimeMs Tempo de execução em milissegundos
     */
    public CommandResult(String message, Object data, long executionTimeMs) {
        this(true, message, data, LocalDateTime.now(), executionTimeMs, null, null);
    }
    
    /**
     * Construtor para resultado de sucesso com metadados.
     * 
     * @param message Mensagem de sucesso
     * @param data Dados retornados
     * @param executionTimeMs Tempo de execução em milissegundos
     * @param metadata Metadados adicionais
     */
    public CommandResult(String message, Object data, long executionTimeMs, Map<String, Object> metadata) {
        this(true, message, data, LocalDateTime.now(), executionTimeMs, metadata, null);
    }
    
    /**
     * Construtor para resultado de erro.
     * 
     * @param message Mensagem de erro
     * @param error Exceção que causou o erro
     * @param executionTimeMs Tempo de execução em milissegundos
     */
    public CommandResult(String message, Throwable error, long executionTimeMs) {
        this(false, message, null, LocalDateTime.now(), executionTimeMs, null, error);
    }
    
    /**
     * Construtor completo.
     * 
     * @param success Se a execução foi bem-sucedida
     * @param message Mensagem do resultado
     * @param data Dados retornados
     * @param executedAt Timestamp da execução
     * @param executionTimeMs Tempo de execução em milissegundos
     * @param metadata Metadados adicionais
     * @param error Exceção (se houver)
     */
    public CommandResult(boolean success, String message, Object data, LocalDateTime executedAt, 
                        long executionTimeMs, Map<String, Object> metadata, Throwable error) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.executedAt = executedAt;
        this.executionTimeMs = executionTimeMs;
        this.metadata = metadata;
        this.error = error;
    }
    
    /**
     * Cria um resultado de sucesso.
     * 
     * @param message Mensagem de sucesso
     * @param data Dados retornados
     * @param executionTimeMs Tempo de execução
     * @return Resultado de sucesso
     */
    public static CommandResult success(String message, Object data, long executionTimeMs) {
        return new CommandResult(message, data, executionTimeMs);
    }
    
    /**
     * Cria um resultado de sucesso com metadados.
     * 
     * @param message Mensagem de sucesso
     * @param data Dados retornados
     * @param executionTimeMs Tempo de execução
     * @param metadata Metadados adicionais
     * @return Resultado de sucesso
     */
    public static CommandResult success(String message, Object data, long executionTimeMs, Map<String, Object> metadata) {
        return new CommandResult(message, data, executionTimeMs, metadata);
    }
    
    /**
     * Cria um resultado de erro.
     * 
     * @param message Mensagem de erro
     * @param error Exceção que causou o erro
     * @param executionTimeMs Tempo de execução
     * @return Resultado de erro
     */
    public static CommandResult error(String message, Throwable error, long executionTimeMs) {
        return new CommandResult(message, error, executionTimeMs);
    }
    
    // Getters
    public boolean isSuccess() {
        return success;
    }
    
    public boolean isError() {
        return !success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Object getData() {
        return data;
    }
    
    public LocalDateTime getExecutedAt() {
        return executedAt;
    }
    
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public Throwable getError() {
        return error;
    }
    
    /**
     * Retorna os dados como um tipo específico.
     * 
     * @param <T> Tipo esperado
     * @param clazz Classe do tipo esperado
     * @return Dados convertidos para o tipo especificado
     * @throws ClassCastException se os dados não forem do tipo esperado
     */
    @SuppressWarnings("unchecked")
    public <T> T getDataAs(Class<T> clazz) {
        if (data == null) {
            return null;
        }
        if (clazz.isInstance(data)) {
            return (T) data;
        }
        throw new ClassCastException("Data is not of type " + clazz.getSimpleName());
    }
    
    /**
     * Retorna um metadado específico.
     * 
     * @param key Chave do metadado
     * @return Valor do metadado
     */
    public Object getMetadata(String key) {
        return metadata != null ? metadata.get(key) : null;
    }
    
    /**
     * Retorna um metadado específico como um tipo.
     * 
     * @param <T> Tipo esperado
     * @param key Chave do metadado
     * @param clazz Classe do tipo esperado
     * @return Valor do metadado convertido
     */
    @SuppressWarnings("unchecked")
    public <T> T getMetadataAs(String key, Class<T> clazz) {
        Object value = getMetadata(key);
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return (T) value;
        }
        throw new ClassCastException("Metadata '" + key + "' is not of type " + clazz.getSimpleName());
    }
    
    @Override
    public String toString() {
        return String.format("CommandResult{success=%s, message='%s', executedAt=%s, executionTimeMs=%d}", 
                           success, message, executedAt, executionTimeMs);
    }
}
