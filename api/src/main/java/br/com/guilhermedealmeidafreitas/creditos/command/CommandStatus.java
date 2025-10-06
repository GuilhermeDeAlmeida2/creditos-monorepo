package br.com.guilhermedealmeidafreitas.creditos.command;

/**
 * Enum que representa o status de um comando.
 * 
 * REFATORAÇÃO: Implementa Command Pattern para organizar
 * e padronizar operações de teste no sistema.
 */
public enum CommandStatus {
    
    /**
     * Comando criado mas ainda não executado.
     */
    CREATED("Criado"),
    
    /**
     * Comando sendo executado.
     */
    EXECUTING("Executando"),
    
    /**
     * Comando executado com sucesso.
     */
    EXECUTED("Executado"),
    
    /**
     * Comando falhou na execução.
     */
    FAILED("Falhou"),
    
    /**
     * Comando sendo desfeito.
     */
    UNDOING("Desfazendo"),
    
    /**
     * Comando desfeito com sucesso.
     */
    UNDONE("Desfeito"),
    
    /**
     * Comando falhou ao ser desfeito.
     */
    UNDO_FAILED("Falha ao desfazer"),
    
    /**
     * Comando cancelado antes da execução.
     */
    CANCELLED("Cancelado");
    
    private final String description;
    
    CommandStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Verifica se o comando está em um estado de execução.
     * 
     * @return true se está executando
     */
    public boolean isExecuting() {
        return this == EXECUTING;
    }
    
    /**
     * Verifica se o comando foi executado com sucesso.
     * 
     * @return true se foi executado com sucesso
     */
    public boolean isExecuted() {
        return this == EXECUTED;
    }
    
    /**
     * Verifica se o comando falhou.
     * 
     * @return true se falhou
     */
    public boolean isFailed() {
        return this == FAILED || this == UNDO_FAILED;
    }
    
    /**
     * Verifica se o comando está sendo desfeito.
     * 
     * @return true se está sendo desfeito
     */
    public boolean isUndoing() {
        return this == UNDOING;
    }
    
    /**
     * Verifica se o comando foi desfeito.
     * 
     * @return true se foi desfeito
     */
    public boolean isUndone() {
        return this == UNDONE;
    }
    
    /**
     * Verifica se o comando foi cancelado.
     * 
     * @return true se foi cancelado
     */
    public boolean isCancelled() {
        return this == CANCELLED;
    }
    
    /**
     * Verifica se o comando está em um estado final.
     * 
     * @return true se está em estado final
     */
    public boolean isFinal() {
        return this == EXECUTED || this == FAILED || this == UNDONE || this == UNDO_FAILED || this == CANCELLED;
    }
    
    /**
     * Verifica se o comando pode ser executado.
     * 
     * @return true se pode ser executado
     */
    public boolean canExecute() {
        return this == CREATED || this == UNDONE;
    }
    
    /**
     * Verifica se o comando pode ser desfeito.
     * 
     * @return true se pode ser desfeito
     */
    public boolean canUndo() {
        return this == EXECUTED;
    }
    
    /**
     * Verifica se o comando pode ser cancelado.
     * 
     * @return true se pode ser cancelado
     */
    public boolean canCancel() {
        return this == CREATED || this == EXECUTING;
    }
}
