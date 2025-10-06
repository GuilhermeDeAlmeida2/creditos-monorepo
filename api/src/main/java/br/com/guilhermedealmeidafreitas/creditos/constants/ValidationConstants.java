package br.com.guilhermedealmeidafreitas.creditos.constants;

import br.com.guilhermedealmeidafreitas.creditos.config.ValidationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Constantes centralizadas para validações do sistema.
 * 
 * REFATORAÇÃO DRY: Centraliza todas as constantes de validação que estavam
 * duplicadas em múltiplos arquivos, eliminando violações do princípio DRY.
 * 
 * REFATORAÇÃO: Agora usa configurações externalizadas do ValidationConfig
 * para maior flexibilidade e configurabilidade.
 * 
 * @author Guilherme de Almeida Freitas
 */
@Component
public class ValidationConstants {
    
    private final ValidationConfig validationConfig;
    
    @Autowired
    public ValidationConstants(ValidationConfig validationConfig) {
        this.validationConfig = validationConfig;
    }
    
    /**
     * Campos válidos para ordenação em consultas paginadas.
     * Centraliza a definição que estava duplicada em 3 locais diferentes.
     */
    public Set<String> getValidSortFields() {
        return validationConfig.getSortFields().getValidSortFields();
    }
    
    /**
     * Tamanho padrão da página para paginação.
     */
    public int getDefaultPageSize() {
        return validationConfig.getPagination().getDefaultPageSize();
    }
    
    /**
     * Tamanho máximo permitido para páginas.
     */
    public int getMaxPageSize() {
        return validationConfig.getPagination().getMaxPageSize();
    }
    
    /**
     * Tamanho mínimo permitido para páginas.
     */
    public int getMinPageSize() {
        return validationConfig.getPagination().getMinPageSize();
    }
    
    /**
     * Página padrão para paginação.
     */
    public int getDefaultPage() {
        return validationConfig.getPagination().getDefaultPage();
    }
    
    /**
     * Campo padrão para ordenação.
     */
    public String getDefaultSortField() {
        return validationConfig.getSortFields().getDefaultSortField();
    }
    
    /**
     * Direção padrão para ordenação.
     */
    public String getDefaultSortDirection() {
        return validationConfig.getSortFields().getDefaultSortDirection();
    }
    
    /**
     * Comprimento mínimo padrão para strings.
     */
    public int getDefaultMinLength() {
        return validationConfig.getStringValidation().getDefaultMinLength();
    }
    
    /**
     * Comprimento máximo padrão para strings.
     */
    public int getDefaultMaxLength() {
        return validationConfig.getStringValidation().getDefaultMaxLength();
    }
    
    /**
     * Se o trim está habilitado para strings.
     */
    public boolean isTrimEnabled() {
        return validationConfig.getStringValidation().isTrimEnabled();
    }
    
    /**
     * Se strings vazias são permitidas.
     */
    public boolean isAllowEmpty() {
        return validationConfig.getStringValidation().isAllowEmpty();
    }
    
    /**
     * Se números negativos são permitidos.
     */
    public boolean isAllowNegative() {
        return validationConfig.getNumberValidation().isAllowNegative();
    }
    
    /**
     * Se zero é permitido em números.
     */
    public boolean isAllowZero() {
        return validationConfig.getNumberValidation().isAllowZero();
    }
    
    /**
     * Precisão padrão para números decimais.
     */
    public int getDefaultPrecision() {
        return validationConfig.getNumberValidation().getDefaultPrecision();
    }
    
    /**
     * Precisão máxima para números decimais.
     */
    public int getMaxPrecision() {
        return validationConfig.getNumberValidation().getMaxPrecision();
    }
}
