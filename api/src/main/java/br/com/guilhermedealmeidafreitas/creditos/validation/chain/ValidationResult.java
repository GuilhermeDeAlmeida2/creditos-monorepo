package br.com.guilhermedealmeidafreitas.creditos.validation.chain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa o resultado de uma validação no Chain of Responsibility.
 * Contém informações sobre sucesso/falha e detalhes da validação.
 * 
 * REFATORAÇÃO: Implementa Chain of Responsibility Pattern para organizar
 * validações em uma cadeia flexível e extensível.
 */
public class ValidationResult {
    
    private final boolean valid;
    private final String message;
    private final String fieldName;
    private final List<String> errors;
    private final List<String> warnings;
    private final Object processedValue;
    private final String handlerName;
    
    /**
     * Construtor para resultado de sucesso.
     * 
     * @param message Mensagem de sucesso
     * @param processedValue Valor processado/validado
     * @param handlerName Nome do handler que processou
     */
    public ValidationResult(String message, Object processedValue, String handlerName) {
        this.valid = true;
        this.message = message;
        this.fieldName = null;
        this.errors = Collections.emptyList();
        this.warnings = Collections.emptyList();
        this.processedValue = processedValue;
        this.handlerName = handlerName;
    }
    
    /**
     * Construtor para resultado de sucesso com campo.
     * 
     * @param message Mensagem de sucesso
     * @param fieldName Nome do campo validado
     * @param processedValue Valor processado/validado
     * @param handlerName Nome do handler que processou
     */
    public ValidationResult(String message, String fieldName, Object processedValue, String handlerName) {
        this.valid = true;
        this.message = message;
        this.fieldName = fieldName;
        this.errors = Collections.emptyList();
        this.warnings = Collections.emptyList();
        this.processedValue = processedValue;
        this.handlerName = handlerName;
    }
    
    /**
     * Construtor para resultado de falha.
     * 
     * @param message Mensagem de erro
     * @param fieldName Nome do campo com erro
     * @param handlerName Nome do handler que detectou o erro
     */
    public ValidationResult(String message, String fieldName, String handlerName) {
        this.valid = false;
        this.message = message;
        this.fieldName = fieldName;
        this.errors = List.of(message);
        this.warnings = Collections.emptyList();
        this.processedValue = null;
        this.handlerName = handlerName;
    }
    
    /**
     * Construtor para resultado de falha com múltiplos erros.
     * 
     * @param errors Lista de erros
     * @param fieldName Nome do campo com erro
     * @param handlerName Nome do handler que detectou os erros
     */
    public ValidationResult(List<String> errors, String fieldName, String handlerName) {
        this.valid = false;
        this.message = errors.isEmpty() ? "Validação falhou" : errors.get(0);
        this.fieldName = fieldName;
        this.errors = new ArrayList<>(errors);
        this.warnings = Collections.emptyList();
        this.processedValue = null;
        this.handlerName = handlerName;
    }
    
    /**
     * Construtor completo para casos avançados.
     * 
     * @param valid Se a validação foi bem-sucedida
     * @param message Mensagem principal
     * @param fieldName Nome do campo
     * @param errors Lista de erros
     * @param warnings Lista de avisos
     * @param processedValue Valor processado
     * @param handlerName Nome do handler
     */
    public ValidationResult(boolean valid, String message, String fieldName, 
                          List<String> errors, List<String> warnings, 
                          Object processedValue, String handlerName) {
        this.valid = valid;
        this.message = message;
        this.fieldName = fieldName;
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
        this.warnings = warnings != null ? new ArrayList<>(warnings) : new ArrayList<>();
        this.processedValue = processedValue;
        this.handlerName = handlerName;
    }
    
    // Getters
    public boolean isValid() {
        return valid;
    }
    
    public boolean isInvalid() {
        return !valid;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
    
    public List<String> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }
    
    public Object getProcessedValue() {
        return processedValue;
    }
    
    public String getHandlerName() {
        return handlerName;
    }
    
    /**
     * Verifica se há erros.
     * 
     * @return true se há erros
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Verifica se há avisos.
     * 
     * @return true se há avisos
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    /**
     * Obtém o primeiro erro.
     * 
     * @return Primeiro erro ou null se não houver erros
     */
    public String getFirstError() {
        return errors.isEmpty() ? null : errors.get(0);
    }
    
    /**
     * Obtém o primeiro aviso.
     * 
     * @return Primeiro aviso ou null se não houver avisos
     */
    public String getFirstWarning() {
        return warnings.isEmpty() ? null : warnings.get(0);
    }
    
    @Override
    public String toString() {
        return String.format("ValidationResult{valid=%s, fieldName='%s', message='%s', handlerName='%s'}", 
                           valid, fieldName, message, handlerName);
    }
}
