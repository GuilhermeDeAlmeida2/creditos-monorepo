package br.com.guilhermedealmeidafreitas.creditos.command;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe base abstrata para implementar funcionalidades comuns do Command Pattern.
 * Fornece implementações padrão para métodos comuns.
 * 
 * REFATORAÇÃO: Implementa Command Pattern para organizar
 * e padronizar operações de teste no sistema.
 */
public abstract class BaseCommand implements Command {
    
    private final String name;
    private final String description;
    private final String type;
    private final LocalDateTime createdAt;
    
    private Map<String, Object> parameters;
    private LocalDateTime lastExecutedAt;
    private CommandStatus status;
    private long actualExecutionTime;
    
    /**
     * Construtor.
     * 
     * @param name Nome do comando
     * @param description Descrição do comando
     * @param type Tipo do comando
     */
    protected BaseCommand(String name, String description, String type) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.parameters = new HashMap<>();
        this.status = CommandStatus.CREATED;
        this.actualExecutionTime = 0;
    }
    
    @Override
    public CommandResult execute() throws CommandException {
        if (!canExecute()) {
            throw new CommandException("Comando não pode ser executado no estado atual", getName(), getStatus());
        }
        
        long startTime = System.currentTimeMillis();
        setStatus(CommandStatus.EXECUTING);
        
        try {
            validate();
            CommandResult result = doExecute();
            setStatus(CommandStatus.EXECUTED);
            setLastExecutedAt(LocalDateTime.now());
            setActualExecutionTime(System.currentTimeMillis() - startTime);
            return result;
        } catch (CommandValidationException e) {
            setStatus(CommandStatus.FAILED);
            setActualExecutionTime(System.currentTimeMillis() - startTime);
            throw e;
        } catch (Exception e) {
            setStatus(CommandStatus.FAILED);
            setActualExecutionTime(System.currentTimeMillis() - startTime);
            throw new CommandException("Erro na execução do comando", getName(), getStatus(), e);
        }
    }
    
    @Override
    public CommandResult undo() throws CommandException {
        if (!canUndo()) {
            throw new CommandException("Comando não pode ser desfeito no estado atual", getName(), getStatus());
        }
        
        long startTime = System.currentTimeMillis();
        setStatus(CommandStatus.UNDOING);
        
        try {
            CommandResult result = doUndo();
            setStatus(CommandStatus.UNDONE);
            setActualExecutionTime(System.currentTimeMillis() - startTime);
            return result;
        } catch (Exception e) {
            setStatus(CommandStatus.UNDO_FAILED);
            setActualExecutionTime(System.currentTimeMillis() - startTime);
            throw new CommandException("Erro ao desfazer o comando", getName(), getStatus(), e);
        }
    }
    
    @Override
    public boolean canExecute() {
        return getStatus().canExecute();
    }
    
    @Override
    public boolean canUndo() {
        return getStatus().canUndo();
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public String getType() {
        return type;
    }
    
    @Override
    public Map<String, Object> getParameters() {
        return new HashMap<>(parameters);
    }
    
    @Override
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
    }
    
    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    @Override
    public LocalDateTime getLastExecutedAt() {
        return lastExecutedAt;
    }
    
    @Override
    public void setLastExecutedAt(LocalDateTime executedAt) {
        this.lastExecutedAt = executedAt;
    }
    
    @Override
    public CommandStatus getStatus() {
        return status;
    }
    
    @Override
    public void setStatus(CommandStatus status) {
        this.status = status;
    }
    
    @Override
    public long getActualExecutionTime() {
        return actualExecutionTime;
    }
    
    @Override
    public void setActualExecutionTime(long executionTime) {
        this.actualExecutionTime = executionTime;
    }
    
    @Override
    public void validate() throws CommandValidationException {
        // Implementação padrão - não valida nada
        // Subclasses podem sobrescrever para adicionar validações específicas
    }
    
    @Override
    public String getInfo() {
        return String.format("Command{name='%s', type='%s', status=%s, createdAt=%s, lastExecutedAt=%s, executionTime=%dms}", 
                           name, type, status, createdAt, lastExecutedAt, actualExecutionTime);
    }
    
    /**
     * Método auxiliar para obter um parâmetro com valor padrão.
     * 
     * @param key Chave do parâmetro
     * @param defaultValue Valor padrão
     * @return Valor do parâmetro ou valor padrão
     */
    @SuppressWarnings("unchecked")
    protected <T> T getParameter(String key, T defaultValue) {
        Object value = parameters.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return (T) value;
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }
    
    /**
     * Método auxiliar para obter um parâmetro obrigatório.
     * 
     * @param key Chave do parâmetro
     * @return Valor do parâmetro
     * @throws CommandValidationException se o parâmetro não existir
     */
    @SuppressWarnings("unchecked")
    protected <T> T getRequiredParameter(String key) throws CommandValidationException {
        Object value = parameters.get(key);
        if (value == null) {
            throw new CommandValidationException("Parâmetro obrigatório não fornecido", getName(), key);
        }
        try {
            return (T) value;
        } catch (ClassCastException e) {
            throw new CommandValidationException("Parâmetro tem tipo inválido", getName(), key, e);
        }
    }
    
    /**
     * Método auxiliar para verificar se um parâmetro existe.
     * 
     * @param key Chave do parâmetro
     * @return true se o parâmetro existe
     */
    protected boolean hasParameter(String key) {
        return parameters.containsKey(key);
    }
    
    /**
     * Método auxiliar para definir um parâmetro.
     * 
     * @param key Chave do parâmetro
     * @param value Valor do parâmetro
     */
    protected void setParameter(String key, Object value) {
        parameters.put(key, value);
    }
    
    /**
     * Método abstrato que deve ser implementado pelas subclasses para executar o comando.
     * 
     * @return Resultado da execução
     * @throws Exception se houver erro na execução
     */
    protected abstract CommandResult doExecute() throws Exception;
    
    /**
     * Método abstrato que deve ser implementado pelas subclasses para desfazer o comando.
     * 
     * @return Resultado da operação de undo
     * @throws Exception se houver erro no undo
     */
    protected abstract CommandResult doUndo() throws Exception;
    
    /**
     * Retorna o tempo estimado de execução em milissegundos.
     * Implementação padrão retorna 1000ms (1 segundo).
     * Subclasses podem sobrescrever para fornecer estimativas mais precisas.
     * 
     * @return Tempo estimado de execução
     */
    @Override
    public long getEstimatedExecutionTime() {
        return 1000; // 1 segundo por padrão
    }
    
    @Override
    public String toString() {
        return getInfo();
    }
}
