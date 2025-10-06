package br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers;

import br.com.guilhermedealmeidafreitas.creditos.constants.ValidationConstants;
import br.com.guilhermedealmeidafreitas.creditos.util.ValidationUtils;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.AbstractValidationHandler;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationRequest;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationResult;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    
    public PageableValidationHandler() {
        super("PageableValidationHandler", 300);
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
     * Valida parâmetros de paginação e cria Pageable.
     * 
     * @param request Requisição de validação
     * @return Resultado da validação com Pageable criado
     */
    private ValidationResult validatePageable(ValidationRequest request) {
        // Obtém os parâmetros
        Object pageParam = request.getParameter("page");
        Object sizeParam = request.getParameter("size");
        Object sortByParam = request.getParameter("sortBy");
        Object sortDirectionParam = request.getParameter("sortDirection");
        
        // Valida página
        int page = 0;
        if (pageParam != null) {
            try {
                page = ValidationUtils.parseInteger(pageParam, "page");
                // Corrige página negativa para 0
                page = Math.max(0, page);
            } catch (IllegalArgumentException e) {
                return error(e.getMessage(), "page");
            }
        }
        
        // Valida tamanho
        int size = ValidationConstants.DEFAULT_PAGE_SIZE; // Valor padrão
        if (sizeParam != null) {
            try {
                size = ValidationUtils.parseInteger(sizeParam, "size");
                // Corrige tamanho inválido para valores padrão
                if (size <= 0) {
                    size = ValidationConstants.DEFAULT_PAGE_SIZE; // Tamanho padrão
                } else if (size > ValidationConstants.MAX_PAGE_SIZE) {
                    size = ValidationConstants.MAX_PAGE_SIZE; // Limite máximo
                }
            } catch (IllegalArgumentException e) {
                return error(e.getMessage(), "size");
            }
        }
        
        // Valida campo de ordenação
        String sortBy = ValidationConstants.DEFAULT_SORT_FIELD; // Valor padrão
        if (sortByParam != null) {
            try {
                sortBy = ValidationUtils.parseString(sortByParam, "sortBy");
                if (!ValidationConstants.VALID_SORT_FIELDS.contains(sortBy)) {
                    return error(String.format("Campo de ordenação '%s' não é válido. Campos válidos: %s", 
                                             sortBy, ValidationConstants.VALID_SORT_FIELDS), "sortBy");
                }
            } catch (IllegalArgumentException e) {
                return error(e.getMessage(), "sortBy");
            }
        }
        
        // Valida direção de ordenação
        Sort.Direction sortDirection = Sort.Direction.ASC; // Valor padrão
        if (sortDirectionParam != null) {
            try {
                String directionStr = ValidationUtils.parseString(sortDirectionParam, "sortDirection").toUpperCase();
                if (!"ASC".equals(directionStr) && !"DESC".equals(directionStr)) {
                    return error("Parâmetro 'sortDirection' deve ser 'ASC' ou 'DESC'", "sortDirection");
                }
                sortDirection = "ASC".equals(directionStr) ? Sort.Direction.ASC : Sort.Direction.DESC;
            } catch (IllegalArgumentException e) {
                return error(e.getMessage(), "sortDirection");
            }
        }
        
        // Cria o Pageable
        Sort sort = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Validação bem-sucedida
        return success("Parâmetros de paginação validados com sucesso", 
                      "pageable", pageable);
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
