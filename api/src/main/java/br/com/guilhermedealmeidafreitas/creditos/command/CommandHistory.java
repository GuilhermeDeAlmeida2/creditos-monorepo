package br.com.guilhermedealmeidafreitas.creditos.command;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Gerenciador de histórico de comandos com suporte a undo/redo.
 * Implementa Command Pattern para operações de teste.
 * 
 * REFATORAÇÃO: Implementa Command Pattern para organizar
 * e padronizar operações de teste no sistema.
 */
public class CommandHistory {
    
    private final List<Command> executedCommands;
    private final List<Command> undoneCommands;
    private final int maxHistorySize;
    private final LocalDateTime createdAt;
    
    public CommandHistory() {
        this(100); // Tamanho padrão do histórico
    }
    
    public CommandHistory(int maxHistorySize) {
        this.executedCommands = new ArrayList<>();
        this.undoneCommands = new ArrayList<>();
        this.maxHistorySize = maxHistorySize;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Adiciona um comando ao histórico após execução bem-sucedida.
     * 
     * @param command Comando executado
     */
    public void addExecutedCommand(Command command) {
        if (command == null) {
            return;
        }
        
        executedCommands.add(command);
        undoneCommands.clear(); // Limpar comandos desfeitos quando um novo é executado
        
        // Manter o tamanho do histórico
        if (executedCommands.size() > maxHistorySize) {
            executedCommands.remove(0);
        }
    }
    
    /**
     * Move o último comando executado para a lista de comandos desfeitos.
     * 
     * @return Comando que foi desfeito, ou null se não houver comandos
     */
    public Command undoLastCommand() {
        if (executedCommands.isEmpty()) {
            return null;
        }
        
        Command lastCommand = executedCommands.remove(executedCommands.size() - 1);
        undoneCommands.add(lastCommand);
        return lastCommand;
    }
    
    /**
     * Move o último comando desfeito de volta para a lista de comandos executados.
     * 
     * @return Comando que foi refeito, ou null se não houver comandos desfeitos
     */
    public Command redoLastCommand() {
        if (undoneCommands.isEmpty()) {
            return null;
        }
        
        Command lastUndoneCommand = undoneCommands.remove(undoneCommands.size() - 1);
        executedCommands.add(lastUndoneCommand);
        return lastUndoneCommand;
    }
    
    /**
     * Verifica se é possível desfazer um comando.
     * 
     * @return true se é possível desfazer
     */
    public boolean canUndo() {
        return !executedCommands.isEmpty();
    }
    
    /**
     * Verifica se é possível refazer um comando.
     * 
     * @return true se é possível refazer
     */
    public boolean canRedo() {
        return !undoneCommands.isEmpty();
    }
    
    /**
     * Retorna o último comando executado.
     * 
     * @return Último comando executado, ou null se não houver
     */
    public Command getLastExecutedCommand() {
        if (executedCommands.isEmpty()) {
            return null;
        }
        return executedCommands.get(executedCommands.size() - 1);
    }
    
    /**
     * Retorna o último comando desfeito.
     * 
     * @return Último comando desfeito, ou null se não houver
     */
    public Command getLastUndoneCommand() {
        if (undoneCommands.isEmpty()) {
            return null;
        }
        return undoneCommands.get(undoneCommands.size() - 1);
    }
    
    /**
     * Retorna todos os comandos executados.
     * 
     * @return Lista de comandos executados
     */
    public List<Command> getExecutedCommands() {
        return new ArrayList<>(executedCommands);
    }
    
    /**
     * Retorna todos os comandos desfeitos.
     * 
     * @return Lista de comandos desfeitos
     */
    public List<Command> getUndoneCommands() {
        return new ArrayList<>(undoneCommands);
    }
    
    /**
     * Retorna o histórico completo (executados + desfeitos).
     * 
     * @return Lista de todos os comandos
     */
    public List<Command> getAllCommands() {
        List<Command> allCommands = new ArrayList<>();
        allCommands.addAll(executedCommands);
        allCommands.addAll(undoneCommands);
        return allCommands;
    }
    
    /**
     * Busca comandos por nome.
     * 
     * @param name Nome do comando
     * @return Lista de comandos com o nome especificado
     */
    public List<Command> findCommandsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return getAllCommands().stream()
            .filter(command -> name.equals(command.getName()))
            .collect(Collectors.toList());
    }
    
    /**
     * Busca comandos por tipo.
     * 
     * @param type Tipo do comando
     * @return Lista de comandos do tipo especificado
     */
    public List<Command> findCommandsByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return getAllCommands().stream()
            .filter(command -> type.equals(command.getType()))
            .collect(Collectors.toList());
    }
    
    /**
     * Busca comandos executados em um período específico.
     * 
     * @param start Data/hora de início
     * @param end Data/hora de fim
     * @return Lista de comandos executados no período
     */
    public List<Command> findCommandsByDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return new ArrayList<>();
        }
        
        return getAllCommands().stream()
            .filter(command -> {
                LocalDateTime executedAt = command.getLastExecutedAt();
                return executedAt != null && 
                       !executedAt.isBefore(start) && 
                       !executedAt.isAfter(end);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Retorna estatísticas do histórico.
     * 
     * @return Estatísticas do histórico
     */
    public HistoryStatistics getStatistics() {
        return new HistoryStatistics(this);
    }
    
    /**
     * Limpa todo o histórico.
     */
    public void clear() {
        executedCommands.clear();
        undoneCommands.clear();
    }
    
    /**
     * Limpa apenas os comandos desfeitos.
     */
    public void clearUndoneCommands() {
        undoneCommands.clear();
    }
    
    /**
     * Retorna o tamanho atual do histórico.
     * 
     * @return Número de comandos no histórico
     */
    public int size() {
        return executedCommands.size() + undoneCommands.size();
    }
    
    /**
     * Retorna o tamanho máximo do histórico.
     * 
     * @return Tamanho máximo do histórico
     */
    public int getMaxHistorySize() {
        return maxHistorySize;
    }
    
    /**
     * Retorna a data de criação do histórico.
     * 
     * @return Data de criação
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Classe para estatísticas do histórico.
     */
    public static class HistoryStatistics {
        private final int totalCommands;
        private final int executedCommands;
        private final int undoneCommands;
        private final Map<String, Integer> commandsByName;
        private final Map<String, Integer> commandsByType;
        private final long totalExecutionTime;
        private final double averageExecutionTime;
        private final LocalDateTime firstCommandTime;
        private final LocalDateTime lastCommandTime;
        
        public HistoryStatistics(CommandHistory history) {
            List<Command> allCommands = history.getAllCommands();
            List<Command> executed = history.getExecutedCommands();
            List<Command> undone = history.getUndoneCommands();
            
            this.totalCommands = allCommands.size();
            this.executedCommands = executed.size();
            this.undoneCommands = undone.size();
            
            // Contar comandos por nome
            this.commandsByName = allCommands.stream()
                .collect(Collectors.groupingBy(
                    Command::getName,
                    Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
            
            // Contar comandos por tipo
            this.commandsByType = allCommands.stream()
                .collect(Collectors.groupingBy(
                    Command::getType,
                    Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
            
            // Calcular tempos de execução
            this.totalExecutionTime = allCommands.stream()
                .mapToLong(Command::getActualExecutionTime)
                .sum();
            this.averageExecutionTime = totalCommands > 0 ? (double) totalExecutionTime / totalCommands : 0.0;
            
            // Encontrar primeiro e último comando
            this.firstCommandTime = allCommands.stream()
                .map(Command::getCreatedAt)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
            
            this.lastCommandTime = allCommands.stream()
                .map(Command::getLastExecutedAt)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        }
        
        // Getters
        public int getTotalCommands() { return totalCommands; }
        public int getExecutedCommands() { return executedCommands; }
        public int getUndoneCommands() { return undoneCommands; }
        public Map<String, Integer> getCommandsByName() { return new HashMap<>(commandsByName); }
        public Map<String, Integer> getCommandsByType() { return new HashMap<>(commandsByType); }
        public long getTotalExecutionTime() { return totalExecutionTime; }
        public double getAverageExecutionTime() { return averageExecutionTime; }
        public LocalDateTime getFirstCommandTime() { return firstCommandTime; }
        public LocalDateTime getLastCommandTime() { return lastCommandTime; }
        
        @Override
        public String toString() {
            return String.format("HistoryStatistics{total=%d, executed=%d, undone=%d, avgTime=%.2fms}", 
                               totalCommands, executedCommands, undoneCommands, averageExecutionTime);
        }
    }
}
