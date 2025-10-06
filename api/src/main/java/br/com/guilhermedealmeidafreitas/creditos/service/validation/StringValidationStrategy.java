package br.com.guilhermedealmeidafreitas.creditos.service.validation;

import org.springframework.stereotype.Component;

/**
 * Estratégia de validação para campos String.
 * Valida se a string não é nula, não está vazia e não contém apenas espaços.
 */
@Component
public class StringValidationStrategy implements ValidationStrategy<String> {
    
    private static final String STRATEGY_NAME = "StringValidation";
    
    @Override
    public void validate(String input) {
        if (input == null) {
            throw new ValidationException("Campo não pode ser nulo", STRATEGY_NAME, "string");
        }
        
        if (input.trim().isEmpty()) {
            throw new ValidationException("Campo não pode ser vazio", STRATEGY_NAME, "string");
        }
    }
    
    @Override
    public boolean supports(Class<?> type) {
        return String.class.isAssignableFrom(type);
    }
    
    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }
    
    /**
     * Valida string com nome do campo personalizado.
     * 
     * @param input String a ser validada
     * @param fieldName Nome do campo para mensagens de erro
     * @throws ValidationException se a validação falhar
     */
    public void validate(String input, String fieldName) {
        if (input == null) {
            throw new ValidationException(fieldName + " não pode ser nulo", STRATEGY_NAME, fieldName);
        }
        
        if (input.trim().isEmpty()) {
            throw new ValidationException(fieldName + " não pode ser vazio", STRATEGY_NAME, fieldName);
        }
    }
    
    /**
     * Valida string opcional (pode ser nula ou vazia).
     * 
     * @param input String a ser validada
     * @return String normalizada (trim) ou null se vazia
     */
    public String validateOptional(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }
        return input.trim();
    }
}
