package br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers;

import br.com.guilhermedealmeidafreitas.creditos.constants.ErrorMessages;
import br.com.guilhermedealmeidafreitas.creditos.constants.ValidationConstants;
import br.com.guilhermedealmeidafreitas.creditos.factory.PageableFactory;
import br.com.guilhermedealmeidafreitas.creditos.util.ValidationUtils;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.AbstractValidationHandler;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationRequest;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationResult;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationType;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Handler para validações de paginação no Chain of Responsibility.
 * Responsável por validar parâmetros de paginação e criar Pageable.
 * 
 * REFATORAÇÃO: Implementa Chain of Responsibility Pattern para organizar
 * validações em uma cadeia flexível e extensível.
 */
@Component
public class PageableValidationHandler extends AbstractValidationHandler implements PageableValidationHandlerInterface {
    
    private final PageableFactory pageableFactory;
    private final ValidationConstants validationConstants;
    
    public PageableValidationHandler(PageableFactory pageableFactory, ValidationConstants validationConstants) {
        super("PageableValidationHandler", 300);
        this.pageableFactory = pageableFactory;
        this.validationConstants = validationConstants;
    }
    
    @Override
    public boolean canHandle(ValidationRequest request) {
        return request.getType() == ValidationType.PAGEABLE ||
               request.getType() == ValidationType.SORT_FIELD ||
               request.getType() == ValidationType.SORT_DIRECTION;
    }
    
    @Override
    protected ValidationResult doHandle(ValidationRequest request) {
        // Validação de parâmetros de paginação
        if (request.getType() == ValidationType.PAGEABLE) {
            return validatePageable(request);
        }
        
        // Validação de campo de ordenação
        if (request.getType() == ValidationType.SORT_FIELD) {
            return validateSortField(request);
        }
        
        // Validação de direção de ordenação
        if (request.getType() == ValidationType.SORT_DIRECTION) {
            return validateSortDirection(request);
        }
        
        // Se chegou aqui, não deveria acontecer
        return error(ErrorMessages.validationTypeNotSupported(request.getType().toString()), request.getFieldName());
    }
    
    /**
     * Valida parâmetros de paginação e cria Pageable usando a PageableFactory.
     * 
     * REFATORAÇÃO DRY: Simplifica a lógica de validação delegando a criação
     * do Pageable para a PageableFactory, eliminando duplicação de código.
     * 
     * @param request Requisição de validação
     * @return Resultado da validação com Pageable criado
     */
    private ValidationResult validatePageable(ValidationRequest request) {
        try {
            // Obtém os parâmetros
            Object pageParam = request.getParameter("page");
            Object sizeParam = request.getParameter("size");
            Object sortByParam = request.getParameter("sortBy");
            Object sortDirectionParam = request.getParameter("sortDirection");
            
            // Valida parâmetros individuais antes de criar o Pageable
            ValidationResult pageValidation = validatePageParameter(pageParam);
            if (!pageValidation.isValid()) {
                return pageValidation;
            }
            
            ValidationResult sizeValidation = validateSizeParameter(sizeParam);
            if (!sizeValidation.isValid()) {
                return sizeValidation;
            }
            
            ValidationResult sortByValidation = validateSortByParameter(sortByParam);
            if (!sortByValidation.isValid()) {
                return sortByValidation;
            }
            
            ValidationResult sortDirectionValidation = validateSortDirectionParameter(sortDirectionParam);
            if (!sortDirectionValidation.isValid()) {
                return sortDirectionValidation;
            }
            
            // Cria o Pageable usando a factory
            Pageable pageable = pageableFactory.createPageableFromObjects(
                pageParam, sizeParam, sortByParam, sortDirectionParam);
            
            // Validação bem-sucedida
            return success(ErrorMessages.PAGEABLE_VALIDATED_SUCCESS, 
                          "pageable", pageable);
            
        } catch (IllegalArgumentException e) {
            return error(ErrorMessages.pageableValidationError(e.getMessage()), "pageable");
        }
    }
    
    /**
     * Valida parâmetro de página.
     */
    private ValidationResult validatePageParameter(Object pageParam) {
        if (pageParam == null) {
            return success(ErrorMessages.PAGE_NOT_SPECIFIED, "page", 0);
        }
        
        try {
            int page = ValidationUtils.parseInteger(pageParam, "page");
            if (page < 0) {
                return success(ErrorMessages.PAGE_NEGATIVE_CORRECTED, "page", 0);
            }
            return success(ErrorMessages.PAGE_VALIDATED_SUCCESS, "page", page);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage(), "page");
        }
    }
    
    /**
     * Valida parâmetro de tamanho.
     */
    private ValidationResult validateSizeParameter(Object sizeParam) {
        if (sizeParam == null) {
            return success(ErrorMessages.SIZE_NOT_SPECIFIED, "size", validationConstants.getDefaultPageSize());
        }
        
        try {
            int size = ValidationUtils.parseInteger(sizeParam, "size");
            if (size <= 0) {
                return success(ErrorMessages.SIZE_INVALID_CORRECTED, "size", validationConstants.getDefaultPageSize());
            }
            if (size > validationConstants.getMaxPageSize()) {
                return success(ErrorMessages.SIZE_EXCEEDED_LIMIT, "size", validationConstants.getMaxPageSize());
            }
            return success(ErrorMessages.SIZE_VALIDATED_SUCCESS, "size", size);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage(), "size");
        }
    }
    
    /**
     * Valida parâmetro de campo de ordenação.
     */
    private ValidationResult validateSortByParameter(Object sortByParam) {
        if (sortByParam == null) {
            return success(ErrorMessages.SORT_FIELD_NOT_SPECIFIED, "sortBy", validationConstants.getDefaultSortField());
        }
        
        try {
            String sortBy = ValidationUtils.parseString(sortByParam, "sortBy");
            if (!validationConstants.getValidSortFields().contains(sortBy)) {
                return error(ErrorMessages.invalidSortField(sortBy, validationConstants.getValidSortFields().toString()), "sortBy");
            }
            return success(ErrorMessages.SORT_FIELD_VALIDATED_SUCCESS, "sortBy", sortBy);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage(), "sortBy");
        }
    }
    
    /**
     * Valida parâmetro de direção de ordenação.
     */
    private ValidationResult validateSortDirectionParameter(Object sortDirectionParam) {
        if (sortDirectionParam == null) {
            return success(ErrorMessages.SORT_DIRECTION_NOT_SPECIFIED, "sortDirection", validationConstants.getDefaultSortDirection());
        }
        
        try {
            String directionStr = ValidationUtils.parseString(sortDirectionParam, "sortDirection").toUpperCase();
            if (!"ASC".equals(directionStr) && !"DESC".equals(directionStr)) {
                return error(ErrorMessages.SORT_DIRECTION_MUST_BE_ASC_OR_DESC, "sortDirection");
            }
            return success(ErrorMessages.SORT_DIRECTION_VALIDATED_SUCCESS, "sortDirection", directionStr);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage(), "sortDirection");
        }
    }
    
    /**
     * Valida campo de ordenação.
     * 
     * @param request Requisição de validação
     * @return Resultado da validação
     */
    private ValidationResult validateSortField(ValidationRequest request) {
        Object value = request.getValue();
        String fieldName = request.getFieldName();
        
        if (isNull(value)) {
            return success("Campo de ordenação é opcional", fieldName, "id");
        }
        
        if (!(value instanceof String)) {
            return error(ErrorMessages.SORT_FIELD_MUST_BE_STRING, fieldName);
        }
        
        String sortField = ((String) value).trim();
        if (!validationConstants.getValidSortFields().contains(sortField)) {
            return error(ErrorMessages.invalidSortField(sortField, validationConstants.getValidSortFields().toString()), fieldName);
        }
        
        return success("Campo de ordenação validado com sucesso", fieldName, sortField);
    }
    
    /**
     * Valida direção de ordenação.
     * 
     * @param request Requisição de validação
     * @return Resultado da validação
     */
    private ValidationResult validateSortDirection(ValidationRequest request) {
        Object value = request.getValue();
        String fieldName = request.getFieldName();
        
        if (isNull(value)) {
            return success("Direção de ordenação é opcional", fieldName, "ASC");
        }
        
        if (!(value instanceof String)) {
            return error(ErrorMessages.SORT_DIRECTION_MUST_BE_STRING, fieldName);
        }
        
        String direction = ((String) value).trim().toUpperCase();
        if (!"ASC".equals(direction) && !"DESC".equals(direction)) {
            return error(ErrorMessages.INVALID_SORT_DIRECTION, fieldName);
        }
        
        return success("Direção de ordenação validada com sucesso", fieldName, direction);
    }
    
    // ==================== IMPLEMENTAÇÃO DA INTERFACE ====================
    
    @Override
    public ValidationResult validatePageable(int page, int size, String sortBy, String sortDirection) {
        try {
            Pageable pageable = pageableFactory.createPageable(page, size, sortBy, sortDirection);
            return success(ErrorMessages.PAGEABLE_VALIDATED_SUCCESS, "pageable", pageable);
        } catch (IllegalArgumentException e) {
            return error(ErrorMessages.pageableValidationError(e.getMessage()), "pageable");
        }
    }
    
    @Override
    public ValidationResult validatePageableFromObjects(Object pageParam, Object sizeParam, 
                                                      Object sortByParam, Object sortDirectionParam) {
        try {
            Pageable pageable = pageableFactory.createPageableFromObjects(
                pageParam, sizeParam, sortByParam, sortDirectionParam);
            return success(ErrorMessages.PAGEABLE_VALIDATED_SUCCESS, "pageable", pageable);
        } catch (IllegalArgumentException e) {
            return error(ErrorMessages.pageableValidationError(e.getMessage()), "pageable");
        }
    }
    
    @Override
    public ValidationResult validateSortField(String sortField, String fieldName) {
        if (ValidationUtils.isNull(sortField)) {
            return success(ErrorMessages.format(ErrorMessages.FIELD_IS_OPTIONAL, fieldName), fieldName, validationConstants.getDefaultSortField());
        }
        
        String trimmedSortField = sortField.trim();
        if (!validationConstants.getValidSortFields().contains(trimmedSortField)) {
            return error(ErrorMessages.invalidSortField(trimmedSortField, validationConstants.getValidSortFields().toString()), fieldName);
        }
        
        return success(ErrorMessages.SORT_FIELD_VALIDATED_SUCCESS, fieldName, trimmedSortField);
    }
    
    @Override
    public ValidationResult validateSortDirection(String sortDirection, String fieldName) {
        if (ValidationUtils.isNull(sortDirection)) {
            return success(ErrorMessages.format(ErrorMessages.FIELD_IS_OPTIONAL, fieldName), fieldName, validationConstants.getDefaultSortDirection());
        }
        
        String trimmedDirection = sortDirection.trim().toUpperCase();
        if (!"ASC".equals(trimmedDirection) && !"DESC".equals(trimmedDirection)) {
            return error(ErrorMessages.INVALID_SORT_DIRECTION, fieldName);
        }
        
        return success(ErrorMessages.SORT_DIRECTION_VALIDATED_SUCCESS, fieldName, trimmedDirection);
    }
    
    @Override
    public ValidationResult createDefaultPageable() {
        try {
            Pageable pageable = pageableFactory.createDefaultPageable();
            return success(ErrorMessages.PAGEABLE_VALIDATED_SUCCESS, "pageable", pageable);
        } catch (IllegalArgumentException e) {
            return error(ErrorMessages.pageableValidationError(e.getMessage()), "pageable");
        }
    }
    
    @Override
    public ValidationResult validatePageableObject(Pageable pageable, String fieldName) {
        if (ValidationUtils.isNull(pageable)) {
            return error(ErrorMessages.format("Campo '%s' é obrigatório", fieldName), fieldName);
        }
        
        // Validações básicas do Pageable
        if (pageable.getPageNumber() < 0) {
            return error(ErrorMessages.format("Página do campo '%s' não pode ser negativa", fieldName), fieldName);
        }
        
        if (pageable.getPageSize() <= 0) {
            return error(ErrorMessages.format("Tamanho da página do campo '%s' deve ser positivo", fieldName), fieldName);
        }
        
        if (pageable.getPageSize() > validationConstants.getMaxPageSize()) {
            return error(ErrorMessages.format("Tamanho da página do campo '%s' excede o limite máximo de %d", fieldName, validationConstants.getMaxPageSize()), fieldName);
        }
        
        return success(ErrorMessages.PAGEABLE_VALIDATED_SUCCESS, fieldName, pageable);
    }
}
