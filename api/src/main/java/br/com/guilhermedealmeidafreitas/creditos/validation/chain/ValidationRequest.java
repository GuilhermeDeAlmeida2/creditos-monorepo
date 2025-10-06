package br.com.guilhermedealmeidafreitas.creditos.validation.chain;

import java.util.Map;

/**
 * Representa uma requisição de validação no Chain of Responsibility.
 * Contém todos os dados necessários para a validação.
 * 
 * REFATORAÇÃO: Implementa Chain of Responsibility Pattern para organizar
 * validações em uma cadeia flexível e extensível.
 */
public class ValidationRequest {
    
    private final ValidationType type;
    private final Object value;
    private final String fieldName;
    private final Map<String, Object> parameters;
    private final ValidationContext context;
    
    /**
     * Construtor principal.
     * 
     * @param type Tipo de validação
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @param parameters Parâmetros adicionais para validação
     * @param context Contexto da validação
     */
    public ValidationRequest(ValidationType type, Object value, String fieldName, 
                           Map<String, Object> parameters, ValidationContext context) {
        this.type = type;
        this.value = value;
        this.fieldName = fieldName;
        this.parameters = parameters != null ? Map.copyOf(parameters) : Map.of();
        this.context = context;
    }
    
    /**
     * Construtor simplificado para validações básicas.
     * 
     * @param type Tipo de validação
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     */
    public ValidationRequest(ValidationType type, Object value, String fieldName) {
        this(type, value, fieldName, Map.of(), ValidationContext.DEFAULT);
    }
    
    /**
     * Construtor para validações com parâmetros.
     * 
     * @param type Tipo de validação
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @param parameters Parâmetros adicionais
     */
    public ValidationRequest(ValidationType type, Object value, String fieldName, 
                           Map<String, Object> parameters) {
        this(type, value, fieldName, parameters, ValidationContext.DEFAULT);
    }
    
    // Getters
    public ValidationType getType() {
        return type;
    }
    
    public Object getValue() {
        return value;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public ValidationContext getContext() {
        return context;
    }
    
    /**
     * Obtém um parâmetro específico.
     * 
     * @param key Chave do parâmetro
     * @return Valor do parâmetro ou null se não existir
     */
    public Object getParameter(String key) {
        return parameters.get(key);
    }
    
    /**
     * Obtém um parâmetro específico com valor padrão.
     * 
     * @param key Chave do parâmetro
     * @param defaultValue Valor padrão se o parâmetro não existir
     * @return Valor do parâmetro ou valor padrão
     */
    public Object getParameter(String key, Object defaultValue) {
        return parameters.getOrDefault(key, defaultValue);
    }
    
    /**
     * Verifica se um parâmetro existe.
     * 
     * @param key Chave do parâmetro
     * @return true se o parâmetro existe
     */
    public boolean hasParameter(String key) {
        return parameters.containsKey(key);
    }
    
    @Override
    public String toString() {
        return String.format("ValidationRequest{type=%s, fieldName='%s', value=%s, parameters=%s}", 
                           type, fieldName, value, parameters);
    }
}
