package br.com.guilhermedealmeidafreitas.creditos.factory;

import java.util.Map;

/**
 * Interface base para o Improved Factory Pattern.
 * Define operações comuns para todas as factories do sistema.
 * 
 * REFATORAÇÃO: Implementa Improved Factory Pattern para organizar
 * e padronizar a criação de objetos complexos no sistema.
 * 
 * @param <T> Tipo do objeto que a factory cria
 */
public interface AbstractFactory<T> {
    
    /**
     * Cria um objeto do tipo T.
     * 
     * @return Objeto criado
     */
    T create();
    
    /**
     * Cria um objeto do tipo T com parâmetros específicos.
     * 
     * @param parameters Parâmetros para criação do objeto
     * @return Objeto criado
     */
    T create(Map<String, Object> parameters);
    
    /**
     * Cria um objeto do tipo T com um parâmetro específico.
     * 
     * @param parameter Parâmetro para criação do objeto
     * @return Objeto criado
     */
    T create(Object parameter);
    
    /**
     * Verifica se a factory pode criar um objeto com os parâmetros fornecidos.
     * 
     * @param parameters Parâmetros para verificação
     * @return true se pode criar o objeto
     */
    boolean canCreate(Map<String, Object> parameters);
    
    /**
     * Verifica se a factory pode criar um objeto com o parâmetro fornecido.
     * 
     * @param parameter Parâmetro para verificação
     * @return true se pode criar o objeto
     */
    boolean canCreate(Object parameter);
    
    /**
     * Retorna o tipo de objeto que esta factory cria.
     * 
     * @return Classe do tipo T
     */
    Class<T> getProductType();
    
    /**
     * Retorna o nome da factory.
     * 
     * @return Nome da factory
     */
    String getFactoryName();
    
    /**
     * Retorna uma descrição da factory.
     * 
     * @return Descrição da factory
     */
    String getDescription();
    
    /**
     * Retorna informações sobre os parâmetros suportados.
     * 
     * @return Mapa com informações dos parâmetros
     */
    Map<String, String> getSupportedParameters();
    
    /**
     * Valida os parâmetros fornecidos.
     * 
     * @param parameters Parâmetros para validação
     * @throws IllegalArgumentException se os parâmetros forem inválidos
     */
    void validateParameters(Map<String, Object> parameters);
    
    /**
     * Valida um parâmetro específico.
     * 
     * @param parameter Parâmetro para validação
     * @throws IllegalArgumentException se o parâmetro for inválido
     */
    void validateParameter(Object parameter);
}
