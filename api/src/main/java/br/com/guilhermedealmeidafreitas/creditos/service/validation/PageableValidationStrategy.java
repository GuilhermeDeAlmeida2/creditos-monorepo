package br.com.guilhermedealmeidafreitas.creditos.service.validation;

import br.com.guilhermedealmeidafreitas.creditos.constants.ValidationConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Estratégia de validação para objetos Pageable.
 * Valida parâmetros de paginação e ordenação.
 */
@Component
public class PageableValidationStrategy implements ValidationStrategy<Pageable> {
    
    private static final String STRATEGY_NAME = "PageableValidation";
    private static final int MAX_PAGE_SIZE = ValidationConstants.MAX_PAGE_SIZE;
    private static final int DEFAULT_PAGE_SIZE = ValidationConstants.DEFAULT_PAGE_SIZE;
    private static final String DEFAULT_SORT_FIELD = "dataConstituicao";
    private static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.DESC;
    
    @Override
    public void validate(Pageable input) {
        if (input == null) {
            throw new ValidationException("Pageable não pode ser nulo", STRATEGY_NAME, "pageable");
        }
        
        // Validar número da página
        if (input.getPageNumber() < 0) {
            throw new ValidationException("Número da página deve ser maior ou igual a 0", STRATEGY_NAME, "page");
        }
        
        // Validar tamanho da página
        if (input.getPageSize() <= 0) {
            throw new ValidationException("Tamanho da página deve ser maior que 0", STRATEGY_NAME, "size");
        }
        
        if (input.getPageSize() > MAX_PAGE_SIZE) {
            throw new ValidationException(
                String.format("Tamanho da página não pode ser maior que %d", MAX_PAGE_SIZE), 
                STRATEGY_NAME, 
                "size"
            );
        }
        
        // Validar campos de ordenação
        validateSortFields(input.getSort());
    }
    
    @Override
    public boolean supports(Class<?> type) {
        return Pageable.class.isAssignableFrom(type);
    }
    
    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }
    
    /**
     * Valida e cria um Pageable com parâmetros fornecidos.
     * Aplica validações e valores padrão quando necessário.
     * 
     * @param page Número da página
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDirection Direção da ordenação
     * @return Pageable validado e normalizado
     */
    public Pageable validateAndCreatePageable(int page, int size, String sortBy, String sortDirection) {
        // Validar e normalizar parâmetros
        int validatedPage = Math.max(page, 0);
        int validatedSize = size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        String validatedSortBy = isValidSortField(sortBy) ? sortBy : DEFAULT_SORT_FIELD;
        Sort.Direction validatedDirection = validateSortDirection(sortDirection);
        
        return PageRequest.of(validatedPage, validatedSize, Sort.by(validatedDirection, validatedSortBy));
    }
    
    /**
     * Valida campos de ordenação.
     * 
     * @param sort Objeto Sort a ser validado
     * @throws ValidationException se algum campo de ordenação for inválido
     */
    private void validateSortFields(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return; // Ordenação nula ou não especificada é válida
        }
        
        for (Sort.Order order : sort) {
            String property = order.getProperty();
            if (!isValidSortField(property)) {
                throw new ValidationException(
                    String.format("Campo '%s' não é válido para ordenação. Campos válidos: %s", 
                        property, ValidationConstants.VALID_SORT_FIELDS), 
                    STRATEGY_NAME, 
                    "sortBy"
                );
            }
        }
    }
    
    /**
     * Verifica se um campo é válido para ordenação.
     * 
     * @param field Campo a ser verificado
     * @return true se o campo é válido para ordenação
     */
    private boolean isValidSortField(String field) {
        return field != null && ValidationConstants.VALID_SORT_FIELDS.contains(field.trim());
    }
    
    /**
     * Valida e normaliza a direção da ordenação.
     * 
     * @param direction Direção fornecida
     * @return Direção validada e normalizada
     */
    private Sort.Direction validateSortDirection(String direction) {
        if (direction == null) {
            return DEFAULT_SORT_DIRECTION;
        }
        
        String normalized = direction.trim().toLowerCase();
        return "asc".equals(normalized) ? Sort.Direction.ASC : Sort.Direction.DESC;
    }
    
    /**
     * Retorna a lista de campos válidos para ordenação.
     * 
     * @return Set com os campos válidos
     */
    public Set<String> getValidSortFields() {
        return ValidationConstants.VALID_SORT_FIELDS;
    }
    
    /**
     * Retorna o tamanho máximo permitido para páginas.
     * 
     * @return Tamanho máximo da página
     */
    public int getMaxPageSize() {
        return MAX_PAGE_SIZE;
    }
    
    /**
     * Retorna o tamanho padrão da página.
     * 
     * @return Tamanho padrão da página
     */
    public int getDefaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }
}
