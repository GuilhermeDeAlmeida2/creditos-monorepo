package br.com.guilhermedealmeidafreitas.creditos.validation.chain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Gerenciador da cadeia de validação no Chain of Responsibility Pattern.
 * Organiza e executa os handlers de validação em ordem de prioridade.
 * 
 * REFATORAÇÃO: Implementa Chain of Responsibility Pattern para organizar
 * validações em uma cadeia flexível e extensível.
 */
@Component
public class ValidationChain {
    
    private final List<ValidationHandler> handlers;
    
    @Autowired
    public ValidationChain(List<ValidationHandler> handlers) {
        // Ordena os handlers por prioridade (menor número = maior prioridade)
        this.handlers = handlers.stream()
            .sorted(Comparator.comparingInt(ValidationHandler::getPriority))
            .toList();
        
        // Conecta os handlers em cadeia
        connectHandlers();
    }
    
    /**
     * Conecta os handlers em uma cadeia.
     */
    private void connectHandlers() {
        for (int i = 0; i < handlers.size() - 1; i++) {
            handlers.get(i).setNext(handlers.get(i + 1));
        }
    }
    
    /**
     * Executa a validação através da cadeia de handlers.
     * 
     * @param request Requisição de validação
     * @return Resultado da validação
     */
    public ValidationResult validate(ValidationRequest request) {
        if (handlers.isEmpty()) {
            return new ValidationResult(
                "Nenhum handler de validação disponível",
                request.getFieldName(),
                "ValidationChain"
            );
        }
        
        // Inicia a validação com o primeiro handler
        return handlers.get(0).handle(request);
    }
    
    /**
     * Valida uma string não vazia.
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @return Resultado da validação
     */
    public ValidationResult validateStringNotEmpty(Object value, String fieldName) {
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, value, fieldName
        );
        return validate(request);
    }
    
    /**
     * Valida uma string opcional.
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @return Resultado da validação
     */
    public ValidationResult validateStringOptional(Object value, String fieldName) {
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_OPTIONAL, value, fieldName
        );
        return validate(request);
    }
    
    /**
     * Valida um número positivo.
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @return Resultado da validação
     */
    public ValidationResult validatePositiveNumber(Object value, String fieldName) {
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_POSITIVE, value, fieldName
        );
        return validate(request);
    }
    
    /**
     * Valida um número em range.
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @param min Valor mínimo
     * @param max Valor máximo
     * @return Resultado da validação
     */
    public ValidationResult validateNumberRange(Object value, String fieldName, int min, int max) {
        ValidationRequest request = new ValidationRequest(
            ValidationType.NUMBER_RANGE, value, fieldName,
            Map.of("min", min, "max", max)
        );
        return validate(request);
    }
    
    /**
     * Valida parâmetros de paginação e cria Pageable.
     * 
     * @param page Número da página
     * @param size Tamanho da página
     * @param sortBy Campo de ordenação
     * @param sortDirection Direção de ordenação
     * @return Resultado da validação com Pageable criado
     */
    public ValidationResult validateAndCreatePageable(int page, int size, String sortBy, String sortDirection) {
        ValidationRequest request = new ValidationRequest(
            ValidationType.PAGEABLE, null, "pageable",
            Map.of("page", page, "size", size, "sortBy", sortBy, "sortDirection", sortDirection)
        );
        return validate(request);
    }
    
    /**
     * Valida um campo de ordenação.
     * 
     * @param sortBy Campo de ordenação
     * @param fieldName Nome do campo
     * @return Resultado da validação
     */
    public ValidationResult validateSortField(String sortBy, String fieldName) {
        ValidationRequest request = new ValidationRequest(
            ValidationType.SORT_FIELD, sortBy, fieldName
        );
        return validate(request);
    }
    
    /**
     * Valida uma direção de ordenação.
     * 
     * @param sortDirection Direção de ordenação
     * @param fieldName Nome do campo
     * @return Resultado da validação
     */
    public ValidationResult validateSortDirection(String sortDirection, String fieldName) {
        ValidationRequest request = new ValidationRequest(
            ValidationType.SORT_DIRECTION, sortDirection, fieldName
        );
        return validate(request);
    }
    
    /**
     * Retorna informações sobre os handlers registrados.
     * 
     * @return Lista com informações dos handlers
     */
    public List<String> getRegisteredHandlers() {
        return handlers.stream()
            .map(handler -> String.format("%s (prioridade: %d)", 
                                        handler.getHandlerName(), 
                                        handler.getPriority()))
            .toList();
    }
    
    /**
     * Retorna o número de handlers registrados.
     * 
     * @return Número de handlers
     */
    public int getHandlerCount() {
        return handlers.size();
    }
    
    /**
     * Verifica se há handlers registrados.
     * 
     * @return true se há handlers registrados
     */
    public boolean hasHandlers() {
        return !handlers.isEmpty();
    }
}
