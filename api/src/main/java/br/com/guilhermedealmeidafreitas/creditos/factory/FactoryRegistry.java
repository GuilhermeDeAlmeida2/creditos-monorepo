package br.com.guilhermedealmeidafreitas.creditos.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Registry para gerenciar todas as factories do sistema.
 * Implementa Improved Factory Pattern com registro centralizado.
 * 
 * REFATORAÇÃO: Implementa Improved Factory Pattern para organizar
 * e gerenciar todas as factories do sistema em um local centralizado.
 */
@Component
public class FactoryRegistry {
    
    private final List<AbstractFactory<?>> factories;
    
    @Autowired
    public FactoryRegistry(List<AbstractFactory<?>> factories) {
        this.factories = factories;
    }
    
    /**
     * Obtém uma factory pelo nome.
     * 
     * @param factoryName Nome da factory
     * @return Factory encontrada ou Optional.empty()
     */
    public Optional<AbstractFactory<?>> getFactory(String factoryName) {
        return factories.stream()
            .filter(factory -> factory.getFactoryName().equals(factoryName))
            .findFirst();
    }
    
    /**
     * Obtém uma factory pelo tipo de produto.
     * 
     * @param productType Tipo do produto
     * @return Factory encontrada ou Optional.empty()
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<AbstractFactory<T>> getFactory(Class<T> productType) {
        return factories.stream()
            .filter(factory -> factory.getProductType().equals(productType))
            .map(factory -> (AbstractFactory<T>) factory)
            .findFirst();
    }
    
    /**
     * Obtém uma factory específica pelo tipo.
     * 
     * @param factoryType Tipo da factory
     * @return Factory encontrada ou Optional.empty()
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractFactory<?>> Optional<T> getFactory(Class<T> factoryType, Class<?> productType) {
        return factories.stream()
            .filter(factory -> factoryType.isAssignableFrom(factory.getClass()))
            .filter(factory -> factory.getProductType().equals(productType))
            .map(factory -> (T) factory)
            .findFirst();
    }
    
    /**
     * Obtém todas as factories de um tipo específico.
     * 
     * @param productType Tipo do produto
     * @return Lista de factories
     */
    @SuppressWarnings("unchecked")
    public <T> List<AbstractFactory<T>> getFactories(Class<T> productType) {
        return factories.stream()
            .filter(factory -> factory.getProductType().equals(productType))
            .map(factory -> (AbstractFactory<T>) factory)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtém todas as factories registradas.
     * 
     * @return Lista de todas as factories
     */
    public List<AbstractFactory<?>> getAllFactories() {
        return List.copyOf(factories);
    }
    
    /**
     * Obtém informações sobre todas as factories registradas.
     * 
     * @return Mapa com informações das factories
     */
    public Map<String, String> getFactoryInfo() {
        return factories.stream()
            .collect(Collectors.toMap(
                AbstractFactory::getFactoryName,
                factory -> String.format("%s - %s", 
                                       factory.getProductType().getSimpleName(), 
                                       factory.getDescription())
            ));
    }
    
    /**
     * Obtém estatísticas das factories registradas.
     * 
     * @return Mapa com estatísticas
     */
    public Map<String, Object> getFactoryStatistics() {
        Map<Class<?>, Long> productTypeCount = factories.stream()
            .collect(Collectors.groupingBy(
                AbstractFactory::getProductType,
                Collectors.counting()
            ));
        
        return Map.of(
            "totalFactories", factories.size(),
            "uniqueProductTypes", productTypeCount.size(),
            "productTypeDistribution", productTypeCount
        );
    }
    
    /**
     * Verifica se uma factory está registrada.
     * 
     * @param factoryName Nome da factory
     * @return true se a factory está registrada
     */
    public boolean hasFactory(String factoryName) {
        return factories.stream()
            .anyMatch(factory -> factory.getFactoryName().equals(factoryName));
    }
    
    /**
     * Verifica se existe uma factory para um tipo de produto.
     * 
     * @param productType Tipo do produto
     * @return true se existe uma factory para o tipo
     */
    public boolean hasFactoryFor(Class<?> productType) {
        return factories.stream()
            .anyMatch(factory -> factory.getProductType().equals(productType));
    }
    
    /**
     * Obtém o número de factories registradas.
     * 
     * @return Número de factories
     */
    public int getFactoryCount() {
        return factories.size();
    }
    
    /**
     * Obtém o número de tipos de produto únicos.
     * 
     * @return Número de tipos únicos
     */
    public long getUniqueProductTypeCount() {
        return factories.stream()
            .map(AbstractFactory::getProductType)
            .distinct()
            .count();
    }
    
    /**
     * Lista todos os nomes de factories registradas.
     * 
     * @return Lista de nomes de factories
     */
    public List<String> getFactoryNames() {
        return factories.stream()
            .map(AbstractFactory::getFactoryName)
            .sorted()
            .collect(Collectors.toList());
    }
    
    /**
     * Lista todos os tipos de produto suportados.
     * 
     * @return Lista de tipos de produto
     */
    public List<Class<?>> getSupportedProductTypes() {
        return factories.stream()
            .map(AbstractFactory::getProductType)
            .distinct()
            .sorted((c1, c2) -> c1.getSimpleName().compareTo(c2.getSimpleName()))
            .collect(Collectors.toList());
    }
    
    /**
     * Obtém uma factory específica pelo nome e tipo.
     * 
     * @param factoryName Nome da factory
     * @param productType Tipo do produto
     * @return Factory encontrada ou Optional.empty()
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<AbstractFactory<T>> getFactory(String factoryName, Class<T> productType) {
        return factories.stream()
            .filter(factory -> factory.getFactoryName().equals(factoryName))
            .filter(factory -> factory.getProductType().equals(productType))
            .map(factory -> (AbstractFactory<T>) factory)
            .findFirst();
    }
    
    /**
     * Cria um objeto usando a factory apropriada.
     * 
     * @param productType Tipo do produto
     * @param parameters Parâmetros para criação
     * @return Objeto criado
     * @throws IllegalArgumentException se não encontrar factory apropriada
     */
    public <T> T create(Class<T> productType, Map<String, Object> parameters) {
        AbstractFactory<T> factory = getFactory(productType)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Nenhuma factory encontrada para o tipo: %s", productType.getSimpleName())
            ));
        
        return factory.create(parameters);
    }
    
    /**
     * Cria um objeto usando uma factory específica.
     * 
     * @param factoryName Nome da factory
     * @param parameters Parâmetros para criação
     * @return Objeto criado
     * @throws IllegalArgumentException se não encontrar a factory
     */
    public Object create(String factoryName, Map<String, Object> parameters) {
        AbstractFactory<?> factory = getFactory(factoryName)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Factory não encontrada: %s", factoryName)
            ));
        
        return factory.create(parameters);
    }
}
