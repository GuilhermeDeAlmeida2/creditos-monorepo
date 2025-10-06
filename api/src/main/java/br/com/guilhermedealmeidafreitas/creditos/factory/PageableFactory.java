package br.com.guilhermedealmeidafreitas.creditos.factory;

import br.com.guilhermedealmeidafreitas.creditos.constants.ValidationConstants;
import br.com.guilhermedealmeidafreitas.creditos.util.ValidationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * Factory para criação de objetos Pageable com validação e correção automática.
 * 
 * REFATORAÇÃO DRY: Centraliza a lógica complexa de criação de Pageable que estava
 * duplicada no PageableValidationHandler, eliminando duplicação de código e
 * melhorando a manutenibilidade.
 * 
 * @author Guilherme de Almeida Freitas
 */
@Component
public class PageableFactory {
    
    private final ValidationConstants validationConstants;
    
    public PageableFactory(ValidationConstants validationConstants) {
        this.validationConstants = validationConstants;
    }
    
    /**
     * Cria um objeto Pageable com validação e correção automática dos parâmetros.
     * 
     * @param page Página solicitada (será corrigida se negativa)
     * @param size Tamanho da página (será corrigido se inválido)
     * @param sortBy Campo de ordenação (será validado contra campos permitidos)
     * @param sortDirection Direção da ordenação (será validada)
     * @return Pageable validado e corrigido
     * @throws IllegalArgumentException se os parâmetros não puderem ser processados
     */
    public Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        // Validar e corrigir página
        int validPage = Math.max(0, page);
        
        // Validar e corrigir tamanho
        int validSize = validateAndCorrectSize(size);
        
        // Validar campo de ordenação
        String validSortBy = validateAndCorrectSortField(sortBy);
        
        // Validar direção de ordenação
        Sort.Direction validDirection = validateAndCorrectSortDirection(sortDirection);
        
        // Criar Sort e Pageable
        Sort sort = Sort.by(validDirection, validSortBy);
        return PageRequest.of(validPage, validSize, sort);
    }
    
    /**
     * Cria um Pageable a partir de parâmetros Object (útil para validações).
     * 
     * @param pageParam Parâmetro de página (pode ser Number ou String)
     * @param sizeParam Parâmetro de tamanho (pode ser Number ou String)
     * @param sortByParam Parâmetro de campo de ordenação (pode ser String)
     * @param sortDirectionParam Parâmetro de direção (pode ser String)
     * @return Pageable criado
     * @throws IllegalArgumentException se algum parâmetro for inválido
     */
    public Pageable createPageableFromObjects(Object pageParam, Object sizeParam, 
                                            Object sortByParam, Object sortDirectionParam) {
        // Processar página
        int page = 0;
        if (pageParam != null) {
            page = ValidationUtils.parseInteger(pageParam, "page");
        }
        
        // Processar tamanho
        int size = validationConstants.getDefaultPageSize();
        if (sizeParam != null) {
            size = ValidationUtils.parseInteger(sizeParam, "size");
        }
        
        // Processar campo de ordenação
        String sortBy = validationConstants.getDefaultSortField();
        if (sortByParam != null) {
            sortBy = ValidationUtils.parseString(sortByParam, "sortBy");
        }
        
        // Processar direção de ordenação
        String sortDirection = validationConstants.getDefaultSortDirection();
        if (sortDirectionParam != null) {
            sortDirection = ValidationUtils.parseString(sortDirectionParam, "sortDirection");
        }
        
        return createPageable(page, size, sortBy, sortDirection);
    }
    
    /**
     * Valida e corrige o tamanho da página.
     * 
     * @param size Tamanho original
     * @return Tamanho validado e corrigido
     */
    private int validateAndCorrectSize(int size) {
        if (size <= 0) {
            return validationConstants.getDefaultPageSize();
        }
        return Math.min(size, validationConstants.getMaxPageSize());
    }
    
    /**
     * Valida e corrige o campo de ordenação.
     * 
     * @param sortBy Campo original
     * @return Campo validado e corrigido
     */
    private String validateAndCorrectSortField(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return validationConstants.getDefaultSortField();
        }
        
        String trimmedSortBy = sortBy.trim();
        if (validationConstants.getValidSortFields().contains(trimmedSortBy)) {
            return trimmedSortBy;
        }
        
        return validationConstants.getDefaultSortField();
    }
    
    /**
     * Valida e corrige a direção de ordenação.
     * 
     * @param sortDirection Direção original
     * @return Direção validada e corrigida
     */
    private Sort.Direction validateAndCorrectSortDirection(String sortDirection) {
        if (sortDirection == null || sortDirection.trim().isEmpty()) {
            return Sort.Direction.ASC;
        }
        
        String trimmedDirection = sortDirection.trim().toUpperCase();
        if ("DESC".equals(trimmedDirection)) {
            return Sort.Direction.DESC;
        }
        
        return Sort.Direction.ASC;
    }
    
    /**
     * Cria um Pageable padrão com configurações padrão do sistema.
     * 
     * @return Pageable com configurações padrão
     */
    public Pageable createDefaultPageable() {
        return createPageable(0, validationConstants.getDefaultPageSize(), 
                            validationConstants.getDefaultSortField(), 
                            validationConstants.getDefaultSortDirection());
    }
    
    /**
     * Cria um Pageable apenas com paginação (sem ordenação).
     * 
     * @param page Página solicitada
     * @param size Tamanho da página
     * @return Pageable sem ordenação
     */
    public Pageable createPageableWithoutSort(int page, int size) {
        int validPage = Math.max(0, page);
        int validSize = validateAndCorrectSize(size);
        return PageRequest.of(validPage, validSize);
    }
}
