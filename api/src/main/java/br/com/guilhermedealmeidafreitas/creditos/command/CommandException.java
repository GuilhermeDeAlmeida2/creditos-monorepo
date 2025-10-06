package br.com.guilhermedealmeidafreitas.creditos.command;

/**
 * Exceção base para erros relacionados a comandos.
 * 
 * REFATORAÇÃO: Implementa Command Pattern para organizar
 * e padronizar operações de teste no sistema.
 */
public class CommandException extends Exception {
    
    private final String commandName;
    private final CommandStatus status;
    
    public CommandException(String message, String commandName) {
        super(message);
        this.commandName = commandName;
        this.status = null;
    }
    
    public CommandException(String message, String commandName, CommandStatus status) {
        super(message);
        this.commandName = commandName;
        this.status = status;
    }
    
    public CommandException(String message, String commandName, Throwable cause) {
        super(message, cause);
        this.commandName = commandName;
        this.status = null;
    }
    
    public CommandException(String message, String commandName, CommandStatus status, Throwable cause) {
        super(message, cause);
        this.commandName = commandName;
        this.status = status;
    }
    
    public String getCommandName() {
        return commandName;
    }
    
    public CommandStatus getStatus() {
        return status;
    }
    
    @Override
    public String getMessage() {
        String baseMessage = super.getMessage();
        if (commandName != null) {
            return String.format("[%s] %s", commandName, baseMessage);
        }
        return baseMessage;
    }
}
