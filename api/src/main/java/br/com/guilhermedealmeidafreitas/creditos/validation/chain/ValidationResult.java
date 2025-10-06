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
    
    /**
     * Builder para criar ValidationResult de forma fluente.
     * 
     * REFATORAÇÃO DRY: Implementa Builder Pattern para simplificar a criação
     * de ValidationResult e eliminar duplicações nos construtores.
     */
    public static class Builder {
        private boolean valid = true;
        private String message;
        private String fieldName;
        private List<String> errors = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();
        private Object processedValue;
        private String handlerName;
        
        /**
         * Cria um novo builder.
         */
        public Builder() {}
        
        /**
         * Cria um builder para resultado de sucesso.
         * 
         * @param message Mensagem de sucesso
         * @return Builder configurado para sucesso
         */
        public static Builder success(String message) {
            return new Builder()
                .withValid(true)
                .withMessage(message);
        }
        
        /**
         * Cria um builder para resultado de erro.
         * 
         * @param message Mensagem de erro
         * @return Builder configurado para erro
         */
        public static Builder error(String message) {
            return new Builder()
                .withValid(false)
                .withMessage(message)
                .withError(message);
        }
        
        /**
         * Define se a validação é válida.
         * 
         * @param valid true se válida, false caso contrário
         * @return Builder para encadeamento
         */
        public Builder withValid(boolean valid) {
            this.valid = valid;
            return this;
        }
        
        /**
         * Define a mensagem principal.
         * 
         * @param message Mensagem principal
         * @return Builder para encadeamento
         */
        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }
        
        /**
         * Define o nome do campo.
         * 
         * @param fieldName Nome do campo
         * @return Builder para encadeamento
         */
        public Builder withFieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }
        
        /**
         * Adiciona um erro à lista de erros.
         * 
         * @param error Mensagem de erro
         * @return Builder para encadeamento
         */
        public Builder withError(String error) {
            if (error != null && !error.trim().isEmpty()) {
                this.errors.add(error);
            }
            return this;
        }
        
        /**
         * Define a lista de erros.
         * 
         * @param errors Lista de erros
         * @return Builder para encadeamento
         */
        public Builder withErrors(List<String> errors) {
            this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
            return this;
        }
        
        /**
         * Adiciona um aviso à lista de avisos.
         * 
         * @param warning Mensagem de aviso
         * @return Builder para encadeamento
         */
        public Builder withWarning(String warning) {
            if (warning != null && !warning.trim().isEmpty()) {
                this.warnings.add(warning);
            }
            return this;
        }
        
        /**
         * Define a lista de avisos.
         * 
         * @param warnings Lista de avisos
         * @return Builder para encadeamento
         */
        public Builder withWarnings(List<String> warnings) {
            this.warnings = warnings != null ? new ArrayList<>(warnings) : new ArrayList<>();
            return this;
        }
        
        /**
         * Define o valor processado.
         * 
         * @param processedValue Valor processado/validado
         * @return Builder para encadeamento
         */
        public Builder withProcessedValue(Object processedValue) {
            this.processedValue = processedValue;
            return this;
        }
        
        /**
         * Define o nome do handler.
         * 
         * @param handlerName Nome do handler
         * @return Builder para encadeamento
         */
        public Builder withHandlerName(String handlerName) {
            this.handlerName = handlerName;
            return this;
        }
        
        /**
         * Constrói o ValidationResult.
         * 
         * @return ValidationResult configurado
         */
        public ValidationResult build() {
            // Se é um erro mas não há mensagem principal, usa o primeiro erro
            if (!valid && message == null && !errors.isEmpty()) {
                message = errors.get(0);
            }
            
            // Se é um erro mas não há erros na lista e há mensagem, adiciona a mensagem principal
            if (!valid && errors.isEmpty() && message != null) {
                errors.add(message);
            }
            
            return new ValidationResult(
                valid,
                message,
                fieldName,
                errors,
                warnings,
                processedValue,
                handlerName
            );
        }
    }
}
