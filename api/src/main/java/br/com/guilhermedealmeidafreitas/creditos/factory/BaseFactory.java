package br.com.guilhermedealmeidafreitas.creditos.factory;

import java.util.Collections;
import java.util.Map;

/**
 * Classe base abstrata para implementar funcionalidades comuns do Improved Factory Pattern.
 * Fornece implementações padrão para métodos comuns.
 * 
 * REFATORAÇÃO: Implementa Improved Factory Pattern para organizar
 * e padronizar a criação de objetos complexos no sistema.
 * 
 * @param <T> Tipo do objeto que a factory cria
 */
public abstract class BaseFactory<T> implements AbstractFactory<T> {
    
    private final String factoryName;
    private final String description;
    private final Class<T> productType;
    
    /**
     * Construtor.
     * 
     * @param factoryName Nome da factory
     * @param description Descrição da factory
     * @param productType Tipo do produto que a factory cria
     */
    protected BaseFactory(String factoryName, String description, Class<T> productType) {
        this.factoryName = factoryName;
        this.description = description;
        this.productType = productType;
    }
    
    @Override
    public T create() {
        return create(Collections.emptyMap());
    }
    
    @Override
    public T create(Object parameter) {
        return create(Map.of("parameter", parameter));
    }
    
    @Override
    public boolean canCreate(Map<String, Object> parameters) {
        try {
            validateParameters(parameters);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    @Override
    public boolean canCreate(Object parameter) {
        try {
            validateParameter(parameter);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    @Override
    public Class<T> getProductType() {
        return productType;
    }
    
    @Override
    public String getFactoryName() {
        return factoryName;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public Map<String, String> getSupportedParameters() {
        return Collections.emptyMap();
    }
    
    @Override
    public void validateParameters(Map<String, Object> parameters) {
        // Implementação padrão - não valida nada
        // Subclasses podem sobrescrever para adicionar validações específicas
    }
    
    @Override
    public void validateParameter(Object parameter) {
        // Implementação padrão - não valida nada
        // Subclasses podem sobrescrever para adicionar validações específicas
    }
    
    /**
     * Método auxiliar para obter um parâmetro com valor padrão.
     * 
     * @param parameters Mapa de parâmetros
     * @param key Chave do parâmetro
     * @param defaultValue Valor padrão
     * @return Valor do parâmetro ou valor padrão
     */
    @SuppressWarnings("unchecked")
    protected <V> V getParameter(Map<String, Object> parameters, String key, V defaultValue) {
        Object value = parameters.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return (V) value;
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }
    
    /**
     * Método auxiliar para obter um parâmetro obrigatório.
     * 
     * @param parameters Mapa de parâmetros
     * @param key Chave do parâmetro
     * @return Valor do parâmetro
     * @throws IllegalArgumentException se o parâmetro não existir
     */
    @SuppressWarnings("unchecked")
    protected <V> V getRequiredParameter(Map<String, Object> parameters, String key) {
        Object value = parameters.get(key);
        if (value == null) {
            throw new IllegalArgumentException(String.format("Parâmetro obrigatório '%s' não fornecido", key));
        }
        try {
            return (V) value;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(String.format("Parâmetro '%s' tem tipo inválido", key));
        }
    }
    
    /**
     * Método auxiliar para verificar se um parâmetro existe.
     * 
     * @param parameters Mapa de parâmetros
     * @param key Chave do parâmetro
     * @return true se o parâmetro existe
     */
    protected boolean hasParameter(Map<String, Object> parameters, String key) {
        return parameters.containsKey(key);
    }
    
    @Override
    public String toString() {
        return String.format("%s{name='%s', description='%s', productType=%s}", 
                           getClass().getSimpleName(), factoryName, description, productType.getSimpleName());
    }
}
