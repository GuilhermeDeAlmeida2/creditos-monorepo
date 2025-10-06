package br.com.guilhermedealmeidafreitas.creditos.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementação configurável do SortFieldValidator que segue o princípio Open/Closed
 * Permite adicionar novos campos de ordenação sem modificar o código existente
 */
@Service
public class ConfigurableSortFieldValidator implements FieldValidator, DirectionValidator {
    
    // Campos válidos para ordenação - podem ser configurados via properties
    private final Set<String> validSortFields;
    private final String defaultSortField;
    private final Sort.Direction defaultDirection;
    
    public ConfigurableSortFieldValidator() {
        // Campos padrão - podem ser externalizados para application.yml
        this.validSortFields = new HashSet<>(Arrays.asList(
            "id", "numeroCredito", "numeroNfse", "dataConstituicao", 
            "valorIssqn", "tipoCredito", "simplesNacional", "aliquota", 
            "valorFaturado", "valorDeducao", "baseCalculo"
        ));
        this.defaultSortField = "dataConstituicao";
        this.defaultDirection = Sort.Direction.DESC;
    }
    
    /**
     * Construtor que permite configurar os campos válidos
     * @param validFields Conjunto de campos válidos
     * @param defaultField Campo padrão
     * @param defaultDirection Direção padrão
     */
    public ConfigurableSortFieldValidator(Set<String> validFields, String defaultField, Sort.Direction defaultDirection) {
        this.validSortFields = new HashSet<>(validFields);
        this.defaultSortField = defaultField;
        this.defaultDirection = defaultDirection;
    }
    
    @Override
    public boolean isValidSortField(String field) {
        if (field == null || field.trim().isEmpty()) {
            return false;
        }
        return validSortFields.contains(field.trim());
    }
    
    @Override
    public String getDefaultField(String invalidField) {
        return defaultSortField;
    }
    
    @Override
    public Sort.Direction validateSortDirection(String direction) {
        if (direction == null || direction.trim().isEmpty()) {
            return defaultDirection;
        }
        
        String normalizedDirection = direction.trim().toLowerCase();
        if ("asc".equals(normalizedDirection)) {
            return Sort.Direction.ASC;
        } else if ("desc".equals(normalizedDirection)) {
            return Sort.Direction.DESC;
        }
        
        return defaultDirection;
    }
    
    @Override
    public Sort.Direction getDefaultDirection() {
        return defaultDirection;
    }
    
    // Método validateSortParams removido - agora está apenas na interface principal SortFieldValidator
    
    /**
     * Adiciona um novo campo válido para ordenação
     * Permite extensão sem modificar o código existente (OCP)
     * @param field Campo a ser adicionado
     */
    public void addValidSortField(String field) {
        if (field != null && !field.trim().isEmpty()) {
            validSortFields.add(field.trim());
        }
    }
    
    /**
     * Remove um campo válido para ordenação
     * @param field Campo a ser removido
     */
    public void removeValidSortField(String field) {
        if (field != null) {
            validSortFields.remove(field);
        }
    }
    
    /**
     * Retorna uma cópia dos campos válidos
     * @return Set com os campos válidos
     */
    public Set<String> getValidSortFields() {
        return new HashSet<>(validSortFields);
    }
    
    /**
     * Verifica se um campo específico está configurado como válido
     * @param field Campo a ser verificado
     * @return true se o campo está configurado
     */
    public boolean isFieldConfigured(String field) {
        return validSortFields.contains(field);
    }
}
