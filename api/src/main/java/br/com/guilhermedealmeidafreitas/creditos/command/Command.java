package br.com.guilhermedealmeidafreitas.creditos.command;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Interface base para o Command Pattern.
 * Define operações comuns para todos os comandos do sistema.
 * 
 * REFATORAÇÃO: Implementa Command Pattern para organizar
 * e padronizar operações de teste no sistema.
 */
public interface Command {
    
    /**
     * Executa o comando.
     * 
     * @return Resultado da execução do comando
     * @throws CommandException se houver erro na execução
     */
    CommandResult execute() throws CommandException;
    
    /**
     * Desfaz o comando (undo).
     * 
     * @return Resultado da operação de undo
     * @throws CommandException se houver erro no undo
     */
    CommandResult undo() throws CommandException;
    
    /**
     * Verifica se o comando pode ser executado.
     * 
     * @return true se o comando pode ser executado
     */
    boolean canExecute();
    
    /**
     * Verifica se o comando pode ser desfeito.
     * 
     * @return true se o comando pode ser desfeito
     */
    boolean canUndo();
    
    /**
     * Retorna o nome do comando.
     * 
     * @return Nome do comando
     */
    String getName();
    
    /**
     * Retorna a descrição do comando.
     * 
     * @return Descrição do comando
     */
    String getDescription();
    
    /**
     * Retorna o tipo do comando.
     * 
     * @return Tipo do comando
     */
    String getType();
    
    /**
     * Retorna os parâmetros do comando.
     * 
     * @return Mapa com os parâmetros
     */
    Map<String, Object> getParameters();
    
    /**
     * Define os parâmetros do comando.
     * 
     * @param parameters Parâmetros do comando
     */
    void setParameters(Map<String, Object> parameters);
    
    /**
     * Retorna o timestamp de criação do comando.
     * 
     * @return Timestamp de criação
     */
    LocalDateTime getCreatedAt();
    
    /**
     * Retorna o timestamp da última execução.
     * 
     * @return Timestamp da última execução
     */
    LocalDateTime getLastExecutedAt();
    
    /**
     * Define o timestamp da última execução.
     * 
     * @param executedAt Timestamp da execução
     */
    void setLastExecutedAt(LocalDateTime executedAt);
    
    /**
     * Retorna o status atual do comando.
     * 
     * @return Status do comando
     */
    CommandStatus getStatus();
    
    /**
     * Define o status do comando.
     * 
     * @param status Status do comando
     */
    void setStatus(CommandStatus status);
    
    /**
     * Retorna o tempo estimado de execução em milissegundos.
     * 
     * @return Tempo estimado de execução
     */
    long getEstimatedExecutionTime();
    
    /**
     * Retorna o tempo real de execução em milissegundos.
     * 
     * @return Tempo real de execução
     */
    long getActualExecutionTime();
    
    /**
     * Define o tempo real de execução.
     * 
     * @param executionTime Tempo real de execução
     */
    void setActualExecutionTime(long executionTime);
    
    /**
     * Valida os parâmetros do comando.
     * 
     * @throws CommandValidationException se os parâmetros forem inválidos
     */
    void validate() throws CommandValidationException;
    
    /**
     * Retorna informações sobre o comando para logging.
     * 
     * @return Informações do comando
     */
    String getInfo();
}
