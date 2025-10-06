package br.com.guilhermedealmeidafreitas.creditos.command;

/**
 * Exceção para erros de validação de comandos.
 * 
 * REFATORAÇÃO: Implementa Command Pattern para organizar
 * e padronizar operações de teste no sistema.
 */
public class CommandValidationException extends CommandException {
    
    private final String field;
    
    public CommandValidationException(String message, String commandName, String field) {
        super(message, commandName);
        this.field = field;
    }
    
    public CommandValidationException(String message, String commandName, String field, Throwable cause) {
        super(message, commandName, cause);
        this.field = field;
    }
    
    public String getField() {
        return field;
    }
    
    @Override
    public String getMessage() {
        String baseMessage = super.getMessage();
        if (field != null) {
            return String.format("%s (campo: %s)", baseMessage, field);
        }
        return baseMessage;
    }
}
