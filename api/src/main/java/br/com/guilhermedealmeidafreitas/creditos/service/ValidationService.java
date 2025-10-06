package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.exception.CreditoExceptions;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Serviço único de validação - simples mas extensível
 * Mantém DRY (sem duplicação) e SOLID (responsabilidade única)
 */
@Service
public class ValidationService {
    
    // Constantes centralizadas (DRY)
    private static final Set<String> VALID_SORT_FIELDS = Set.of(
        "id", "numeroCredito", "numeroNfse", "dataConstituicao", 
        "valorIssqn", "tipoCredito", "simplesNacional", "aliquota", 
        "valorFaturado", "valorDeducao", "baseCalculo"
    );
    private static final int MAX_PAGE_SIZE = 100;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_SORT_FIELD = "dataConstituicao";
    private static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.DESC;
    
    /**
     * Valida e cria Pageable - método único e simples
     */
    public Pageable validateAndCreatePageable(int page, int size, String sortBy, String sortDirection) {
        int validatedPage = Math.max(page, 0);
        int validatedSize = size <= 0 ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        String validatedSortBy = isValidSortField(sortBy) ? sortBy : DEFAULT_SORT_FIELD;
        Sort.Direction validatedDirection = validateSortDirection(sortDirection);
        
        return PageRequest.of(validatedPage, validatedSize, Sort.by(validatedDirection, validatedSortBy));
    }
    
    /**
     * Valida string de entrada
     */
    public String validateStringInput(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            throw CreditoExceptions.validation(fieldName + " não pode ser nulo ou vazio");
        }
        return input.trim();
    }
    
    /**
     * Valida número positivo
     */
    public int validatePositiveNumber(int number, String fieldName) {
        if (number < 0) {
            throw CreditoExceptions.validation(fieldName + " deve ser um número positivo");
        }
        return number;
    }
    
    /**
     * Valida número dentro de um range
     */
    public int validateNumberInRange(int number, int min, int max, String fieldName) {
        if (number < min || number > max) {
            throw CreditoExceptions.validation(
                String.format("%s deve estar entre %d e %d", fieldName, min, max));
        }
        return number;
    }
    
    /**
     * Valida string de entrada opcional
     */
    public String validateOptionalStringInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        return input.trim();
    }
    
    // Métodos privados simples
    private boolean isValidSortField(String field) {
        return field != null && VALID_SORT_FIELDS.contains(field.trim());
    }
    
    private Sort.Direction validateSortDirection(String direction) {
        if (direction == null) return DEFAULT_SORT_DIRECTION;
        
        String normalized = direction.trim().toLowerCase();
        return "asc".equals(normalized) ? Sort.Direction.ASC : Sort.Direction.DESC;
    }
    
    /**
     * Retorna a lista de campos válidos para ordenação
     */
    public Set<String> getValidSortFields() {
        return VALID_SORT_FIELDS;
    }
    
}
