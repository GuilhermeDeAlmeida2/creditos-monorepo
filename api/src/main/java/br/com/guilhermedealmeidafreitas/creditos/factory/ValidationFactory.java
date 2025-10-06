package br.com.guilhermedealmeidafreitas.creditos.factory;

import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationChain;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Factory para criação de objetos de validação.
 * Implementa Improved Factory Pattern para diferentes contextos de validação.
 * 
 * REFATORAÇÃO: Implementa Improved Factory Pattern para organizar
 * e padronizar a criação de objetos de validação no sistema.
 */
@Component
public class ValidationFactory extends BaseFactory<ValidationResult> {
    
    // Tipos de validação suportados
    public static final String TYPE_STRING_NOT_EMPTY = "STRING_NOT_EMPTY";
    public static final String TYPE_STRING_OPTIONAL = "STRING_OPTIONAL";
    public static final String TYPE_NUMBER_POSITIVE = "NUMBER_POSITIVE";
    public static final String TYPE_NUMBER_RANGE = "NUMBER_RANGE";
    public static final String TYPE_PAGEABLE = "PAGEABLE";
    public static final String TYPE_SORT_FIELD = "SORT_FIELD";
    public static final String TYPE_SORT_DIRECTION = "SORT_DIRECTION";
    
    // Parâmetros suportados
    public static final String PARAM_VALUE = "value";
    public static final String PARAM_FIELD_NAME = "fieldName";
    public static final String PARAM_MIN = "min";
    public static final String PARAM_MAX = "max";
    public static final String PARAM_PAGE = "page";
    public static final String PARAM_SIZE = "size";
    public static final String PARAM_SORT_BY = "sortBy";
    public static final String PARAM_SORT_DIRECTION = "sortDirection";
    
    private final ValidationChain validationChain;
    
    @Autowired
    public ValidationFactory(ValidationChain validationChain) {
        super("ValidationFactory", 
              "Factory para criação de objetos de validação", 
              ValidationResult.class);
        this.validationChain = validationChain;
    }
    
    @Override
    public ValidationResult create(Map<String, Object> parameters) {
        validateParameters(parameters);
        
        String type = getParameter(parameters, "type", TYPE_STRING_NOT_EMPTY);
        
        return createValidationByType(type, parameters);
    }
    
    @Override
    public Map<String, String> getSupportedParameters() {
        return Map.of(
            "type", "Tipo de validação (obrigatório)",
            PARAM_VALUE, "Valor a ser validado (obrigatório)",
            PARAM_FIELD_NAME, "Nome do campo (obrigatório)",
            PARAM_MIN, "Valor mínimo para validação de range (opcional)",
            PARAM_MAX, "Valor máximo para validação de range (opcional)",
            PARAM_PAGE, "Número da página para validação de paginação (opcional)",
            PARAM_SIZE, "Tamanho da página para validação de paginação (opcional)",
            PARAM_SORT_BY, "Campo de ordenação (opcional)",
            PARAM_SORT_DIRECTION, "Direção de ordenação (opcional)"
        );
    }
    
    @Override
    public void validateParameters(Map<String, Object> parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("Parâmetros não podem ser nulos");
        }
        
        if (!hasParameter(parameters, "type")) {
            throw new IllegalArgumentException("Parâmetro 'type' é obrigatório");
        }
        
        if (!hasParameter(parameters, PARAM_VALUE)) {
            throw new IllegalArgumentException("Parâmetro 'value' é obrigatório");
        }
        
        if (!hasParameter(parameters, PARAM_FIELD_NAME)) {
            throw new IllegalArgumentException("Parâmetro 'fieldName' é obrigatório");
        }
    }
    
    /**
     * Cria validação baseada no tipo especificado.
     * 
     * @param type Tipo de validação
     * @param parameters Parâmetros para validação
     * @return Resultado da validação
     */
    private ValidationResult createValidationByType(String type, Map<String, Object> parameters) {
        Object value = getRequiredParameter(parameters, PARAM_VALUE);
        String fieldName = getRequiredParameter(parameters, PARAM_FIELD_NAME);
        
        return switch (type.toUpperCase()) {
            case TYPE_STRING_NOT_EMPTY -> validationChain.validateStringNotEmpty(value, fieldName);
            case TYPE_STRING_OPTIONAL -> validationChain.validateStringOptional(value, fieldName);
            case TYPE_NUMBER_POSITIVE -> validationChain.validatePositiveNumber(value, fieldName);
            case TYPE_NUMBER_RANGE -> {
                Integer min = getRequiredParameter(parameters, PARAM_MIN);
                Integer max = getRequiredParameter(parameters, PARAM_MAX);
                yield validationChain.validateNumberRange(value, fieldName, min, max);
            }
            case TYPE_PAGEABLE -> {
                Integer page = getRequiredParameter(parameters, PARAM_PAGE);
                Integer size = getRequiredParameter(parameters, PARAM_SIZE);
                String sortBy = getParameter(parameters, PARAM_SORT_BY, "id");
                String sortDirection = getParameter(parameters, PARAM_SORT_DIRECTION, "ASC");
                yield validationChain.validateAndCreatePageable(page, size, sortBy, sortDirection);
            }
            case TYPE_SORT_FIELD -> validationChain.validateSortField((String) value, fieldName);
            case TYPE_SORT_DIRECTION -> validationChain.validateSortDirection((String) value, fieldName);
            default -> validationChain.validateStringNotEmpty(value, fieldName);
        };
    }
    
    // ===== MÉTODOS DE CONVENIÊNCIA =====
    
    /**
     * Cria validação de string não vazia.
     */
    public ValidationResult createStringNotEmptyValidation(Object value, String fieldName) {
        return create(Map.of(
            "type", TYPE_STRING_NOT_EMPTY,
            PARAM_VALUE, value,
            PARAM_FIELD_NAME, fieldName
        ));
    }
    
    /**
     * Cria validação de string opcional.
     */
    public ValidationResult createStringOptionalValidation(Object value, String fieldName) {
        return create(Map.of(
            "type", TYPE_STRING_OPTIONAL,
            PARAM_VALUE, value,
            PARAM_FIELD_NAME, fieldName
        ));
    }
    
    /**
     * Cria validação de número positivo.
     */
    public ValidationResult createPositiveNumberValidation(Object value, String fieldName) {
        return create(Map.of(
            "type", TYPE_NUMBER_POSITIVE,
            PARAM_VALUE, value,
            PARAM_FIELD_NAME, fieldName
        ));
    }
    
    /**
     * Cria validação de número em range.
     */
    public ValidationResult createNumberRangeValidation(Object value, String fieldName, int min, int max) {
        return create(Map.of(
            "type", TYPE_NUMBER_RANGE,
            PARAM_VALUE, value,
            PARAM_FIELD_NAME, fieldName,
            PARAM_MIN, min,
            PARAM_MAX, max
        ));
    }
    
    /**
     * Cria validação de parâmetros de paginação.
     */
    public ValidationResult createPageableValidation(int page, int size, String sortBy, String sortDirection) {
        return create(Map.of(
            "type", TYPE_PAGEABLE,
            PARAM_VALUE, null,
            PARAM_FIELD_NAME, "pageable",
            PARAM_PAGE, page,
            PARAM_SIZE, size,
            PARAM_SORT_BY, sortBy,
            PARAM_SORT_DIRECTION, sortDirection
        ));
    }
    
    /**
     * Cria validação de campo de ordenação.
     */
    public ValidationResult createSortFieldValidation(String sortBy, String fieldName) {
        return create(Map.of(
            "type", TYPE_SORT_FIELD,
            PARAM_VALUE, sortBy,
            PARAM_FIELD_NAME, fieldName
        ));
    }
    
    /**
     * Cria validação de direção de ordenação.
     */
    public ValidationResult createSortDirectionValidation(String sortDirection, String fieldName) {
        return create(Map.of(
            "type", TYPE_SORT_DIRECTION,
            PARAM_VALUE, sortDirection,
            PARAM_FIELD_NAME, fieldName
        ));
    }
    
    /**
     * Cria Pageable a partir de parâmetros validados.
     * 
     * @param page Número da página
     * @param size Tamanho da página
     * @param sortBy Campo de ordenação
     * @param sortDirection Direção de ordenação
     * @return Pageable criado
     */
    public Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        ValidationResult result = createPageableValidation(page, size, sortBy, sortDirection);
        
        if (result.isInvalid()) {
            throw new IllegalArgumentException("Parâmetros de paginação inválidos: " + result.getFirstError());
        }
        
        return (Pageable) result.getProcessedValue();
    }
}
