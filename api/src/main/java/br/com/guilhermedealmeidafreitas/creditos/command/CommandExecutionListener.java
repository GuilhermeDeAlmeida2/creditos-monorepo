package br.com.guilhermedealmeidafreitas.creditos.command;

/**
 * Interface para listeners de eventos de execução de comandos.
 * 
 * REFATORAÇÃO: Implementa Command Pattern para organizar
 * e padronizar operações de teste no sistema.
 */
public interface CommandExecutionListener {
    
    /**
     * Chamado quando a execução de um comando é iniciada.
     * 
     * @param command Comando sendo executado
     */
    void onExecutionStarted(Command command);
    
    /**
     * Chamado quando a execução de um comando é concluída com sucesso.
     * 
     * @param command Comando executado
     * @param result Resultado da execução
     */
    void onExecutionCompleted(Command command, CommandResult result);
    
    /**
     * Chamado quando a execução de um comando falha.
     * 
     * @param command Comando que falhou
     * @param error Erro que ocorreu
     */
    void onExecutionFailed(Command command, CommandException error);
    
    /**
     * Chamado quando a execução de um comando é cancelada.
     * 
     * @param command Comando cancelado
     */
    void onExecutionCancelled(Command command);
    
    /**
     * Chamado quando o undo de um comando é iniciado.
     * 
     * @param command Comando sendo desfeito
     */
    void onUndoStarted(Command command);
    
    /**
     * Chamado quando o undo de um comando é concluído com sucesso.
     * 
     * @param command Comando desfeito
     * @param result Resultado do undo
     */
    void onUndoCompleted(Command command, CommandResult result);
    
    /**
     * Chamado quando o undo de um comando falha.
     * 
     * @param command Comando que falhou no undo
     * @param error Erro que ocorreu
     */
    void onUndoFailed(Command command, CommandException error);
}
