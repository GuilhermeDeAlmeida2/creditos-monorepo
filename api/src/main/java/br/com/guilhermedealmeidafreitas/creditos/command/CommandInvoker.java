package br.com.guilhermedealmeidafreitas.creditos.command;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Invocador de comandos que gerencia a execução de comandos.
 * Implementa Command Pattern para operações de teste.
 * 
 * REFATORAÇÃO: Implementa Command Pattern para organizar
 * e padronizar operações de teste no sistema.
 */
@Component
public class CommandInvoker {
    
    private final ExecutorService executorService;
    private final List<Command> commandHistory;
    private final List<CommandExecutionListener> listeners;
    
    public CommandInvoker() {
        this.executorService = Executors.newFixedThreadPool(5);
        this.commandHistory = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }
    
    /**
     * Executa um comando de forma síncrona.
     * 
     * @param command Comando a ser executado
     * @return Resultado da execução
     * @throws CommandException se houver erro na execução
     */
    public CommandResult execute(Command command) throws CommandException {
        if (command == null) {
            throw new IllegalArgumentException("Comando não pode ser nulo");
        }
        
        // Notificar início da execução
        notifyExecutionStarted(command);
        
        try {
            // Executar o comando
            CommandResult result = command.execute();
            
            // Adicionar ao histórico
            commandHistory.add(command);
            
            // Notificar sucesso
            notifyExecutionCompleted(command, result);
            
            return result;
        } catch (CommandException e) {
            // Notificar erro
            notifyExecutionFailed(command, e);
            throw e;
        }
    }
    
    /**
     * Executa um comando de forma assíncrona.
     * 
     * @param command Comando a ser executado
     * @return Future com o resultado da execução
     */
    public Future<CommandResult> executeAsync(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Comando não pode ser nulo");
        }
        
        return executorService.submit(() -> {
            try {
                return execute(command);
            } catch (CommandException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    /**
     * Executa um comando usando CompletableFuture.
     * 
     * @param command Comando a ser executado
     * @return CompletableFuture com o resultado da execução
     */
    public CompletableFuture<CommandResult> executeCompletable(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Comando não pode ser nulo");
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                return execute(command);
            } catch (CommandException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }
    
    /**
     * Desfaz o último comando executado.
     * 
     * @return Resultado da operação de undo
     * @throws CommandException se houver erro no undo
     */
    public CommandResult undoLast() throws CommandException {
        if (commandHistory.isEmpty()) {
            throw new CommandException("Nenhum comando no histórico para desfazer", "CommandInvoker");
        }
        
        Command lastCommand = commandHistory.get(commandHistory.size() - 1);
        return undo(lastCommand);
    }
    
    /**
     * Desfaz um comando específico.
     * 
     * @param command Comando a ser desfeito
     * @return Resultado da operação de undo
     * @throws CommandException se houver erro no undo
     */
    public CommandResult undo(Command command) throws CommandException {
        if (command == null) {
            throw new IllegalArgumentException("Comando não pode ser nulo");
        }
        
        if (!command.canUndo()) {
            throw new CommandException("Comando não pode ser desfeito", command.getName(), command.getStatus());
        }
        
        // Notificar início do undo
        notifyUndoStarted(command);
        
        try {
            // Desfazer o comando
            CommandResult result = command.undo();
            
            // Notificar sucesso do undo
            notifyUndoCompleted(command, result);
            
            return result;
        } catch (CommandException e) {
            // Notificar erro do undo
            notifyUndoFailed(command, e);
            throw e;
        }
    }
    
    /**
     * Cancela um comando se ainda não foi executado.
     * 
     * @param command Comando a ser cancelado
     * @return true se o comando foi cancelado
     */
    public boolean cancel(Command command) {
        if (command == null) {
            return false;
        }
        
        if (!command.getStatus().canCancel()) {
            return false;
        }
        
        command.setStatus(CommandStatus.CANCELLED);
        notifyExecutionCancelled(command);
        return true;
    }
    
    /**
     * Retorna o histórico de comandos executados.
     * 
     * @return Lista de comandos executados
     */
    public List<Command> getCommandHistory() {
        return new ArrayList<>(commandHistory);
    }
    
    /**
     * Limpa o histórico de comandos.
     */
    public void clearHistory() {
        commandHistory.clear();
    }
    
    /**
     * Retorna estatísticas dos comandos executados.
     * 
     * @return Estatísticas dos comandos
     */
    public CommandStatistics getStatistics() {
        return new CommandStatistics(commandHistory);
    }
    
    /**
     * Adiciona um listener para eventos de execução de comandos.
     * 
     * @param listener Listener a ser adicionado
     */
    public void addListener(CommandExecutionListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove um listener.
     * 
     * @param listener Listener a ser removido
     */
    public void removeListener(CommandExecutionListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Fecha o executor service.
     */
    public void shutdown() {
        executorService.shutdown();
    }
    
    // Métodos de notificação
    private void notifyExecutionStarted(Command command) {
        for (CommandExecutionListener listener : listeners) {
            try {
                listener.onExecutionStarted(command);
            } catch (Exception e) {
                // Log error but don't fail the execution
                System.err.println("Erro ao notificar listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyExecutionCompleted(Command command, CommandResult result) {
        for (CommandExecutionListener listener : listeners) {
            try {
                listener.onExecutionCompleted(command, result);
            } catch (Exception e) {
                System.err.println("Erro ao notificar listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyExecutionFailed(Command command, CommandException error) {
        for (CommandExecutionListener listener : listeners) {
            try {
                listener.onExecutionFailed(command, error);
            } catch (Exception e) {
                System.err.println("Erro ao notificar listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyExecutionCancelled(Command command) {
        for (CommandExecutionListener listener : listeners) {
            try {
                listener.onExecutionCancelled(command);
            } catch (Exception e) {
                System.err.println("Erro ao notificar listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyUndoStarted(Command command) {
        for (CommandExecutionListener listener : listeners) {
            try {
                listener.onUndoStarted(command);
            } catch (Exception e) {
                System.err.println("Erro ao notificar listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyUndoCompleted(Command command, CommandResult result) {
        for (CommandExecutionListener listener : listeners) {
            try {
                listener.onUndoCompleted(command, result);
            } catch (Exception e) {
                System.err.println("Erro ao notificar listener: " + e.getMessage());
            }
        }
    }
    
    private void notifyUndoFailed(Command command, CommandException error) {
        for (CommandExecutionListener listener : listeners) {
            try {
                listener.onUndoFailed(command, error);
            } catch (Exception e) {
                System.err.println("Erro ao notificar listener: " + e.getMessage());
            }
        }
    }
    
    /**
     * Classe para estatísticas dos comandos.
     */
    public static class CommandStatistics {
        private final int totalCommands;
        private final int successfulCommands;
        private final int failedCommands;
        private final int cancelledCommands;
        private final long totalExecutionTime;
        private final double averageExecutionTime;
        
        public CommandStatistics(List<Command> commands) {
            this.totalCommands = commands.size();
            this.successfulCommands = (int) commands.stream().filter(c -> c.getStatus().isExecuted()).count();
            this.failedCommands = (int) commands.stream().filter(c -> c.getStatus().isFailed()).count();
            this.cancelledCommands = (int) commands.stream().filter(c -> c.getStatus().isCancelled()).count();
            this.totalExecutionTime = commands.stream().mapToLong(Command::getActualExecutionTime).sum();
            this.averageExecutionTime = totalCommands > 0 ? (double) totalExecutionTime / totalCommands : 0.0;
        }
        
        // Getters
        public int getTotalCommands() { return totalCommands; }
        public int getSuccessfulCommands() { return successfulCommands; }
        public int getFailedCommands() { return failedCommands; }
        public int getCancelledCommands() { return cancelledCommands; }
        public long getTotalExecutionTime() { return totalExecutionTime; }
        public double getAverageExecutionTime() { return averageExecutionTime; }
        
        @Override
        public String toString() {
            return String.format("CommandStatistics{total=%d, successful=%d, failed=%d, cancelled=%d, avgTime=%.2fms}", 
                               totalCommands, successfulCommands, failedCommands, cancelledCommands, averageExecutionTime);
        }
    }
}
