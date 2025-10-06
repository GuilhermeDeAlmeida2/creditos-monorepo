package br.com.guilhermedealmeidafreitas.creditos.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Configurações de validação externalizadas do application.yml.
 * 
 * REFATORAÇÃO DRY: Centraliza todas as configurações de validação que estavam
 * hardcoded no código, permitindo maior flexibilidade e configurabilidade
 * do sistema sem necessidade de recompilação.
 * 
 * @author Guilherme de Almeida Freitas
 */
@Component
@ConfigurationProperties(prefix = "app.validation")
public class ValidationConfig {
    
    /**
     * Configurações de paginação.
     */
    private Pagination pagination = new Pagination();
    
    /**
     * Configurações de strings.
     */
    private StringValidation stringValidation = new StringValidation();
    
    /**
     * Configurações de números.
     */
    private NumberValidation numberValidation = new NumberValidation();
    
    /**
     * Configurações de campos de ordenação.
     */
    private SortFields sortFields = new SortFields();
    
    // Getters e Setters
    public Pagination getPagination() {
        return pagination;
    }
    
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
    
    public StringValidation getStringValidation() {
        return stringValidation;
    }
    
    public void setStringValidation(StringValidation stringValidation) {
        this.stringValidation = stringValidation;
    }
    
    public NumberValidation getNumberValidation() {
        return numberValidation;
    }
    
    public void setNumberValidation(NumberValidation numberValidation) {
        this.numberValidation = numberValidation;
    }
    
    public SortFields getSortFields() {
        return sortFields;
    }
    
    public void setSortFields(SortFields sortFields) {
        this.sortFields = sortFields;
    }
    
    /**
     * Configurações de paginação.
     */
    public static class Pagination {
        private int defaultPageSize = 10;
        private int maxPageSize = 100;
        private int minPageSize = 1;
        private int defaultPage = 0;
        
        // Getters e Setters
        public int getDefaultPageSize() {
            return defaultPageSize;
        }
        
        public void setDefaultPageSize(int defaultPageSize) {
            this.defaultPageSize = defaultPageSize;
        }
        
        public int getMaxPageSize() {
            return maxPageSize;
        }
        
        public void setMaxPageSize(int maxPageSize) {
            this.maxPageSize = maxPageSize;
        }
        
        public int getMinPageSize() {
            return minPageSize;
        }
        
        public void setMinPageSize(int minPageSize) {
            this.minPageSize = minPageSize;
        }
        
        public int getDefaultPage() {
            return defaultPage;
        }
        
        public void setDefaultPage(int defaultPage) {
            this.defaultPage = defaultPage;
        }
    }
    
    /**
     * Configurações de validação de strings.
     */
    public static class StringValidation {
        private int defaultMinLength = 1;
        private int defaultMaxLength = 255;
        private boolean trimEnabled = true;
        private boolean allowEmpty = false;
        
        // Getters e Setters
        public int getDefaultMinLength() {
            return defaultMinLength;
        }
        
        public void setDefaultMinLength(int defaultMinLength) {
            this.defaultMinLength = defaultMinLength;
        }
        
        public int getDefaultMaxLength() {
            return defaultMaxLength;
        }
        
        public void setDefaultMaxLength(int defaultMaxLength) {
            this.defaultMaxLength = defaultMaxLength;
        }
        
        public boolean isTrimEnabled() {
            return trimEnabled;
        }
        
        public void setTrimEnabled(boolean trimEnabled) {
            this.trimEnabled = trimEnabled;
        }
        
        public boolean isAllowEmpty() {
            return allowEmpty;
        }
        
        public void setAllowEmpty(boolean allowEmpty) {
            this.allowEmpty = allowEmpty;
        }
    }
    
    /**
     * Configurações de validação de números.
     */
    public static class NumberValidation {
        private boolean allowNegative = false;
        private boolean allowZero = true;
        private int defaultPrecision = 2;
        private int maxPrecision = 10;
        
        // Getters e Setters
        public boolean isAllowNegative() {
            return allowNegative;
        }
        
        public void setAllowNegative(boolean allowNegative) {
            this.allowNegative = allowNegative;
        }
        
        public boolean isAllowZero() {
            return allowZero;
        }
        
        public void setAllowZero(boolean allowZero) {
            this.allowZero = allowZero;
        }
        
        public int getDefaultPrecision() {
            return defaultPrecision;
        }
        
        public void setDefaultPrecision(int defaultPrecision) {
            this.defaultPrecision = defaultPrecision;
        }
        
        public int getMaxPrecision() {
            return maxPrecision;
        }
        
        public void setMaxPrecision(int maxPrecision) {
            this.maxPrecision = maxPrecision;
        }
    }
    
    /**
     * Configurações de campos de ordenação.
     */
    public static class SortFields {
        private Set<String> validSortFields = Set.of(
            "id", "numeroCredito", "numeroNfse", "dataConstituicao", 
            "valorIssqn", "tipoCredito", "simplesNacional", "aliquota", 
            "valorFaturado", "valorDeducao", "baseCalculo"
        );
        private String defaultSortField = "id";
        private String defaultSortDirection = "ASC";
        
        // Getters e Setters
        public Set<String> getValidSortFields() {
            return validSortFields;
        }
        
        public void setValidSortFields(Set<String> validSortFields) {
            this.validSortFields = validSortFields;
        }
        
        public String getDefaultSortField() {
            return defaultSortField;
        }
        
        public void setDefaultSortField(String defaultSortField) {
            this.defaultSortField = defaultSortField;
        }
        
        public String getDefaultSortDirection() {
            return defaultSortDirection;
        }
        
        public void setDefaultSortDirection(String defaultSortDirection) {
            this.defaultSortDirection = defaultSortDirection;
        }
    }
}
