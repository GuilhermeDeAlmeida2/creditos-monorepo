package br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers;

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
public class PageableValidationHandler extends AbstractValidationHandler {
    
    private final PageableFactory pageableFactory;
    
    public PageableValidationHandler(PageableFactory pageableFactory) {
        super("PageableValidationHandler", 300);
        this.pageableFactory = pageableFactory;
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
        return error("Tipo de validação não suportado: " + request.getType(), request.getFieldName());
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
            return success("Parâmetros de paginação validados com sucesso", 
                          "pageable", pageable);
            
        } catch (IllegalArgumentException e) {
            return error("Erro na validação de parâmetros de paginação: " + e.getMessage(), "pageable");
        }
    }
    
    /**
     * Valida parâmetro de página.
     */
    private ValidationResult validatePageParameter(Object pageParam) {
        if (pageParam == null) {
            return success("Página não especificada, usando padrão", "page", 0);
        }
        
        try {
            int page = ValidationUtils.parseInteger(pageParam, "page");
            if (page < 0) {
                return success("Página negativa corrigida para 0", "page", 0);
            }
            return success("Página validada com sucesso", "page", page);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage(), "page");
        }
    }
    
    /**
     * Valida parâmetro de tamanho.
     */
    private ValidationResult validateSizeParameter(Object sizeParam) {
        if (sizeParam == null) {
            return success("Tamanho não especificado, usando padrão", "size", ValidationConstants.DEFAULT_PAGE_SIZE);
        }
        
        try {
            int size = ValidationUtils.parseInteger(sizeParam, "size");
            if (size <= 0) {
                return success("Tamanho inválido corrigido para padrão", "size", ValidationConstants.DEFAULT_PAGE_SIZE);
            }
            if (size > ValidationConstants.MAX_PAGE_SIZE) {
                return success("Tamanho excedeu limite, corrigido para máximo", "size", ValidationConstants.MAX_PAGE_SIZE);
            }
            return success("Tamanho validado com sucesso", "size", size);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage(), "size");
        }
    }
    
    /**
     * Valida parâmetro de campo de ordenação.
     */
    private ValidationResult validateSortByParameter(Object sortByParam) {
        if (sortByParam == null) {
            return success("Campo de ordenação não especificado, usando padrão", "sortBy", ValidationConstants.DEFAULT_SORT_FIELD);
        }
        
        try {
            String sortBy = ValidationUtils.parseString(sortByParam, "sortBy");
            if (!ValidationConstants.VALID_SORT_FIELDS.contains(sortBy)) {
                return error(String.format("Campo de ordenação '%s' não é válido. Campos válidos: %s", 
                                         sortBy, ValidationConstants.VALID_SORT_FIELDS), "sortBy");
            }
            return success("Campo de ordenação validado com sucesso", "sortBy", sortBy);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage(), "sortBy");
        }
    }
    
    /**
     * Valida parâmetro de direção de ordenação.
     */
    private ValidationResult validateSortDirectionParameter(Object sortDirectionParam) {
        if (sortDirectionParam == null) {
            return success("Direção de ordenação não especificada, usando padrão", "sortDirection", ValidationConstants.DEFAULT_SORT_DIRECTION);
        }
        
        try {
            String directionStr = ValidationUtils.parseString(sortDirectionParam, "sortDirection").toUpperCase();
            if (!"ASC".equals(directionStr) && !"DESC".equals(directionStr)) {
                return error("Parâmetro 'sortDirection' deve ser 'ASC' ou 'DESC'", "sortDirection");
            }
            return success("Direção de ordenação validada com sucesso", "sortDirection", directionStr);
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
            return error("Campo de ordenação deve ser uma string", fieldName);
        }
        
        String sortField = ((String) value).trim();
        if (!ValidationConstants.VALID_SORT_FIELDS.contains(sortField)) {
            return error(String.format("Campo de ordenação '%s' não é válido. Campos válidos: %s", 
                                     sortField, ValidationConstants.VALID_SORT_FIELDS), fieldName);
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
            return error("Direção de ordenação deve ser uma string", fieldName);
        }
        
        String direction = ((String) value).trim().toUpperCase();
        if (!"ASC".equals(direction) && !"DESC".equals(direction)) {
            return error("Direção de ordenação deve ser 'ASC' ou 'DESC'", fieldName);
        }
        
        return success("Direção de ordenação validada com sucesso", fieldName, direction);
    }
}
