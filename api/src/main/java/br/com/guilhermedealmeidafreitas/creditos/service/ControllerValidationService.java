package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por centralizar todas as validações do controller
 * Segue o princípio Open/Closed permitindo extensão sem modificação
 */
@Service
public class ControllerValidationService {
    
    private final SortFieldValidator sortFieldValidator;
    
    /**
     * Construtor para injeção de dependências seguindo o Dependency Inversion Principle (DIP).
     * Torna as dependências explícitas e facilita testes unitários.
     */
    public ControllerValidationService(SortFieldValidator sortFieldValidator) {
        this.sortFieldValidator = sortFieldValidator;
    }
    
    // Limites configuráveis para validação
    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MIN_PAGE_NUMBER = 0;
    
    /**
     * Valida parâmetros de paginação e ordenação de forma integrada
     * @param page Número da página
     * @param size Tamanho da página
     * @param sortBy Campo de ordenação
     * @param sortDirection Direção da ordenação
     * @return Pageable validado e configurado
     */
    public Pageable validateAndCreatePageable(int page, int size, String sortBy, String sortDirection) {
        // Validar parâmetros de paginação
        int validatedPage = validatePageNumber(page);
        int validatedSize = validatePageSize(size);
        
        // Validar parâmetros de ordenação
        SortFieldValidator.SortParams sortParams = sortFieldValidator.validateSortParams(sortBy, sortDirection);
        
        // Criar Pageable com parâmetros validados
        return PageRequest.of(validatedPage, validatedSize, sortParams.getSort());
    }
    
    /**
     * Valida apenas parâmetros de paginação
     * @param page Número da página
     * @param size Tamanho da página
     * @return Pageable validado
     */
    public Pageable validatePaginationParams(int page, int size) {
        return PageRequest.of(validatePageNumber(page), validatePageSize(size));
    }
    
    /**
     * Valida número da página
     * @param page Número da página
     * @return Número da página validado
     */
    public int validatePageNumber(int page) {
        return Math.max(page, MIN_PAGE_NUMBER);
    }
    
    /**
     * Valida tamanho da página
     * @param size Tamanho da página
     * @return Tamanho da página validado
     */
    public int validatePageSize(int size) {
        if (size <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }
    
    /**
     * Valida campos de ordenação
     * @param sortBy Campo de ordenação
     * @param sortDirection Direção da ordenação
     * @return Parâmetros de ordenação validados
     */
    public SortFieldValidator.SortParams validateSortParams(String sortBy, String sortDirection) {
        return sortFieldValidator.validateSortParams(sortBy, sortDirection);
    }
    
    /**
     * Valida se um campo é válido para ordenação
     * @param field Campo a ser validado
     * @return true se o campo é válido
     */
    public boolean isValidSortField(String field) {
        return sortFieldValidator.isValidSortField(field);
    }
    
    /**
     * Valida string de entrada (não nula e não vazia)
     * @param input String a ser validada
     * @param fieldName Nome do campo para mensagens de erro
     * @return String validada (trimmed)
     * @throws IllegalArgumentException se a string for inválida
     */
    public String validateStringInput(String input, String fieldName) {
        if (input == null) {
            throw new ValidationException(fieldName + " não pode ser nulo", fieldName);
        }
        
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            throw new ValidationException(fieldName + " não pode ser vazio", fieldName);
        }
        
        return trimmed;
    }
    
    /**
     * Valida string de entrada opcional
     * @param input String a ser validada
     * @return String validada (trimmed) ou null se for null/vazia
     */
    public String validateOptionalStringInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        return input.trim();
    }
    
    /**
     * Valida número positivo
     * @param number Número a ser validado
     * @param fieldName Nome do campo para mensagens de erro
     * @return Número validado
     * @throws IllegalArgumentException se o número for inválido
     */
    public int validatePositiveNumber(int number, String fieldName) {
        if (number < 0) {
            throw new IllegalArgumentException(fieldName + " deve ser um número positivo");
        }
        return number;
    }
    
    /**
     * Valida número dentro de um range
     * @param number Número a ser validado
     * @param min Valor mínimo
     * @param max Valor máximo
     * @param fieldName Nome do campo para mensagens de erro
     * @return Número validado
     * @throws IllegalArgumentException se o número estiver fora do range
     */
    public int validateNumberInRange(int number, int min, int max, String fieldName) {
        if (number < min || number > max) {
            throw new IllegalArgumentException(
                String.format("%s deve estar entre %d e %d", fieldName, min, max)
            );
        }
        return number;
    }
}
