package br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers;

import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationResult;
import org.springframework.data.domain.Pageable;

/**
 * Interface específica para handlers de validação de paginação.
 * 
 * REFATORAÇÃO DRY: Define contratos específicos para validações de paginação,
 * melhorando a consistência e type safety dos handlers.
 * 
 * @author Guilherme de Almeida Freitas
 */
public interface PageableValidationHandlerInterface {
    
    /**
     * Valida parâmetros de paginação e cria um Pageable.
     * 
     * @param page Página solicitada
     * @param size Tamanho da página
     * @param sortBy Campo de ordenação
     * @param sortDirection Direção da ordenação
     * @return Resultado da validação com Pageable criado
     */
    ValidationResult validatePageable(int page, int size, String sortBy, String sortDirection);
    
    /**
     * Valida parâmetros de paginação a partir de objetos.
     * 
     * @param pageParam Parâmetro de página
     * @param sizeParam Parâmetro de tamanho
     * @param sortByParam Parâmetro de campo de ordenação
     * @param sortDirectionParam Parâmetro de direção de ordenação
     * @return Resultado da validação com Pageable criado
     */
    ValidationResult validatePageableFromObjects(Object pageParam, Object sizeParam, 
                                               Object sortByParam, Object sortDirectionParam);
    
    /**
     * Valida campo de ordenação.
     * 
     * @param sortField Campo de ordenação
     * @param fieldName Nome do campo
     * @return Resultado da validação
     */
    ValidationResult validateSortField(String sortField, String fieldName);
    
    /**
     * Valida direção de ordenação.
     * 
     * @param sortDirection Direção de ordenação
     * @param fieldName Nome do campo
     * @return Resultado da validação
     */
    ValidationResult validateSortDirection(String sortDirection, String fieldName);
    
    /**
     * Cria um Pageable padrão.
     * 
     * @return Resultado da validação com Pageable padrão
     */
    ValidationResult createDefaultPageable();
    
    /**
     * Valida se um Pageable é válido.
     * 
     * @param pageable Pageable a ser validado
     * @param fieldName Nome do campo
     * @return Resultado da validação
     */
    ValidationResult validatePageableObject(Pageable pageable, String fieldName);
}
