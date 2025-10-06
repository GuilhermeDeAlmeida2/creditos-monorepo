package br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers;

import br.com.guilhermedealmeidafreitas.creditos.validation.chain.AbstractValidationHandler;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationRequest;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationResult;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Handler para validações de paginação no Chain of Responsibility.
 * Responsável por validar parâmetros de paginação e criar Pageable.
 * 
 * REFATORAÇÃO: Implementa Chain of Responsibility Pattern para organizar
 * validações em uma cadeia flexível e extensível.
 */
@Component
public class PageableValidationHandler extends AbstractValidationHandler {
    
    // Campos válidos para ordenação
    private static final Set<String> VALID_SORT_FIELDS = Set.of(
        "id", "numeroCredito", "numeroNfse", "dataConstituicao", 
        "valorIssqn", "tipoCredito", "simplesNacional", "aliquota", 
        "valorFaturado", "valorDeducao", "baseCalculo"
    );
    
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
            if (pageParam instanceof Number) {
                page = ((Number) pageParam).intValue();
            } else if (pageParam instanceof String) {
                try {
                    page = Integer.parseInt((String) pageParam);
                } catch (NumberFormatException e) {
                    return error("Parâmetro 'page' deve ser um número inteiro", "page");
                }
            } else {
                return error("Parâmetro 'page' deve ser um número", "page");
            }
            
            if (page < 0) {
                return error("Parâmetro 'page' deve ser maior ou igual a 0", "page");
            }
        }
        
        // Valida tamanho
        int size = 10; // Valor padrão
        if (sizeParam != null) {
            if (sizeParam instanceof Number) {
                size = ((Number) sizeParam).intValue();
            } else if (sizeParam instanceof String) {
                try {
                    size = Integer.parseInt((String) sizeParam);
                } catch (NumberFormatException e) {
                    return error("Parâmetro 'size' deve ser um número inteiro", "size");
                }
            } else {
                return error("Parâmetro 'size' deve ser um número", "size");
            }
            
            if (size <= 0) {
                return error("Parâmetro 'size' deve ser maior que 0", "size");
            }
            
            if (size > 100) {
                return error("Parâmetro 'size' deve ser menor ou igual a 100", "size");
            }
        }
        
        // Valida campo de ordenação
        String sortBy = "id"; // Valor padrão
        if (sortByParam != null) {
            if (!(sortByParam instanceof String)) {
                return error("Parâmetro 'sortBy' deve ser uma string", "sortBy");
            }
            
            sortBy = ((String) sortByParam).trim();
            if (!VALID_SORT_FIELDS.contains(sortBy)) {
                return error(String.format("Campo de ordenação '%s' não é válido. Campos válidos: %s", 
                                         sortBy, VALID_SORT_FIELDS), "sortBy");
            }
        }
        
        // Valida direção de ordenação
        Sort.Direction sortDirection = Sort.Direction.ASC; // Valor padrão
        if (sortDirectionParam != null) {
            if (!(sortDirectionParam instanceof String)) {
                return error("Parâmetro 'sortDirection' deve ser uma string", "sortDirection");
            }
            
            String directionStr = ((String) sortDirectionParam).trim().toUpperCase();
            if (!"ASC".equals(directionStr) && !"DESC".equals(directionStr)) {
                return error("Parâmetro 'sortDirection' deve ser 'ASC' ou 'DESC'", "sortDirection");
            }
            
            sortDirection = "ASC".equals(directionStr) ? Sort.Direction.ASC : Sort.Direction.DESC;
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
        if (!VALID_SORT_FIELDS.contains(sortField)) {
            return error(String.format("Campo de ordenação '%s' não é válido. Campos válidos: %s", 
                                     sortField, VALID_SORT_FIELDS), fieldName);
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
