package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.constants.ValidationConstants;
import br.com.guilhermedealmeidafreitas.creditos.exception.CreditoExceptions;
import br.com.guilhermedealmeidafreitas.creditos.service.validation.ValidationContext;
import br.com.guilhermedealmeidafreitas.creditos.service.validation.ValidationException;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationChain;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Serviço de validação refatorado para usar Chain of Responsibility Pattern.
 * Mantém compatibilidade com código existente enquanto usa a nova arquitetura.
 * 
 * REFATORAÇÃO: Agora usa ValidationChain que implementa Chain of Responsibility
 * para organizar validações em uma cadeia flexível e extensível.
 */
@Service
public class ValidationService {
    
    private final ValidationContext validationContext;
    private final ValidationChain validationChain;
    private final ValidationConstants validationConstants;
    
    @Autowired
    public ValidationService(ValidationContext validationContext, ValidationChain validationChain, ValidationConstants validationConstants) {
        this.validationContext = validationContext;
        this.validationChain = validationChain;
        this.validationConstants = validationConstants;
    }
    
    /**
     * Valida e cria Pageable usando Chain of Responsibility Pattern.
     * REFATORAÇÃO: Delega para ValidationChain que usa PageableValidationHandler.
     */
    public Pageable validateAndCreatePageable(int page, int size, String sortBy, String sortDirection) {
        ValidationResult result = validationChain.validateAndCreatePageable(page, size, sortBy, sortDirection);
        
        if (result.isInvalid()) {
            throw CreditoExceptions.validation(result.getFirstError());
        }
        
        return (Pageable) result.getProcessedValue();
    }
    
    /**
     * Valida string de entrada usando Chain of Responsibility Pattern.
     * REFATORAÇÃO: Delega para ValidationChain que usa StringValidationHandler.
     */
    public String validateStringInput(String input, String fieldName) {
        ValidationResult result = validationChain.validateStringNotEmpty(input, fieldName);
        
        if (result.isInvalid()) {
            throw CreditoExceptions.validation(result.getFirstError());
        }
        
        return (String) result.getProcessedValue();
    }
    
    /**
     * Valida número positivo usando Chain of Responsibility Pattern.
     * REFATORAÇÃO: Delega para ValidationChain que usa NumberValidationHandler.
     */
    public int validatePositiveNumber(int number, String fieldName) {
        ValidationResult result = validationChain.validatePositiveNumber(number, fieldName);
        
        if (result.isInvalid()) {
            throw CreditoExceptions.validation(result.getFirstError());
        }
        
        return ((Number) result.getProcessedValue()).intValue();
    }
    
    /**
     * Valida número dentro de um range usando Chain of Responsibility Pattern.
     * REFATORAÇÃO: Delega para ValidationChain que usa NumberValidationHandler.
     */
    public int validateNumberInRange(int number, int min, int max, String fieldName) {
        ValidationResult result = validationChain.validateNumberRange(number, fieldName, min, max);
        
        if (result.isInvalid()) {
            throw CreditoExceptions.validation(result.getFirstError());
        }
        
        return ((Number) result.getProcessedValue()).intValue();
    }
    
    /**
     * Valida string de entrada opcional usando Chain of Responsibility Pattern.
     * REFATORAÇÃO: Delega para ValidationChain que usa StringValidationHandler.
     */
    public String validateOptionalStringInput(String input) {
        ValidationResult result = validationChain.validateStringOptional(input, "optionalString");
        
        if (result.isInvalid()) {
            throw CreditoExceptions.validation(result.getFirstError());
        }
        
        return (String) result.getProcessedValue();
    }
    
    
    /**
     * Retorna a lista de campos válidos para ordenação
     */
    public Set<String> getValidSortFields() {
        return validationConstants.getValidSortFields();
    }
    
    // ===== MÉTODOS DE CONVENIÊNCIA PARA ACESSO DIRETO À VALIDATIONCHAIN =====
    
    /**
     * Valida um objeto genérico usando a cadeia de validação.
     * 
     * @param input Objeto a ser validado
     * @throws ValidationException se a validação falhar
     */
    public void validate(Object input) {
        validationContext.validate(input);
    }
    
    /**
     * Retorna informações sobre os handlers registrados na cadeia.
     * 
     * @return Lista com informações dos handlers
     */
    public java.util.List<String> getRegisteredHandlers() {
        return validationChain.getRegisteredHandlers();
    }
    
    /**
     * Valida um Pageable existente.
     * 
     * @param pageable Pageable a ser validado
     * @throws ValidationException se a validação falhar
     */
    public void validatePageable(Pageable pageable) {
        validationContext.validatePageable(pageable);
    }
    
    /**
     * Retorna informações sobre as estratégias registradas (compatibilidade).
     * 
     * @return Mapa com informações das estratégias
     */
    public java.util.Map<String, String> getRegisteredStrategies() {
        return validationContext.getRegisteredStrategies();
    }
    
    /**
     * Retorna o número de handlers registrados na cadeia.
     * 
     * @return Número de handlers
     */
    public int getHandlerCount() {
        return validationChain.getHandlerCount();
    }
    
    /**
     * Verifica se há handlers registrados na cadeia.
     * 
     * @return true se há handlers registrados
     */
    public boolean hasHandlers() {
        return validationChain.hasHandlers();
    }
    
}
