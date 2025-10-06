package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.exception.CreditoExceptions;
import br.com.guilhermedealmeidafreitas.creditos.service.validation.ValidationContext;
import br.com.guilhermedealmeidafreitas.creditos.service.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Serviço de validação refatorado para usar Strategy Pattern.
 * Mantém compatibilidade com código existente enquanto usa a nova arquitetura.
 * 
 * REFATORAÇÃO: Agora delega validações para ValidationContext que gerencia
 * diferentes estratégias de validação seguindo o Strategy Pattern.
 */
@Service
public class ValidationService {
    
    private final ValidationContext validationContext;
    
    // Constantes mantidas para compatibilidade
    private static final Set<String> VALID_SORT_FIELDS = Set.of(
        "id", "numeroCredito", "numeroNfse", "dataConstituicao", 
        "valorIssqn", "tipoCredito", "simplesNacional", "aliquota", 
        "valorFaturado", "valorDeducao", "baseCalculo"
    );
    
    @Autowired
    public ValidationService(ValidationContext validationContext) {
        this.validationContext = validationContext;
    }
    
    /**
     * Valida e cria Pageable usando Strategy Pattern.
     * REFATORAÇÃO: Delega para ValidationContext que usa PageableValidationStrategy.
     */
    public Pageable validateAndCreatePageable(int page, int size, String sortBy, String sortDirection) {
        try {
            return validationContext.validateAndCreatePageable(page, size, sortBy, sortDirection);
        } catch (ValidationException e) {
            // Converte ValidationException para CreditoException para manter compatibilidade
            throw CreditoExceptions.validation(e.getMessage());
        }
    }
    
    /**
     * Valida string de entrada usando Strategy Pattern.
     * REFATORAÇÃO: Delega para ValidationContext que usa StringValidationStrategy.
     */
    public String validateStringInput(String input, String fieldName) {
        try {
            validationContext.validateString(input, fieldName);
            return input.trim();
        } catch (ValidationException e) {
            // Converte ValidationException para CreditoException para manter compatibilidade
            throw CreditoExceptions.validation(e.getMessage());
        }
    }
    
    /**
     * Valida número positivo usando Strategy Pattern.
     * REFATORAÇÃO: Delega para ValidationContext que usa NumberValidationStrategy.
     */
    public int validatePositiveNumber(int number, String fieldName) {
        try {
            validationContext.validatePositiveNumber(number, fieldName);
            return number;
        } catch (ValidationException e) {
            // Converte ValidationException para CreditoException para manter compatibilidade
            throw CreditoExceptions.validation(e.getMessage());
        }
    }
    
    /**
     * Valida número dentro de um range usando Strategy Pattern.
     * REFATORAÇÃO: Delega para ValidationContext que usa NumberValidationStrategy.
     */
    public int validateNumberInRange(int number, int min, int max, String fieldName) {
        try {
            validationContext.validateNumberRange(number, min, max, fieldName);
            return number;
        } catch (ValidationException e) {
            // Converte ValidationException para CreditoException para manter compatibilidade
            throw CreditoExceptions.validation(e.getMessage());
        }
    }
    
    /**
     * Valida string de entrada opcional usando Strategy Pattern.
     * REFATORAÇÃO: Delega para ValidationContext que usa StringValidationStrategy.
     */
    public String validateOptionalStringInput(String input) {
        return validationContext.validateOptionalString(input);
    }
    
    
    /**
     * Retorna a lista de campos válidos para ordenação
     */
    public Set<String> getValidSortFields() {
        return VALID_SORT_FIELDS;
    }
    
    // ===== MÉTODOS DE CONVENIÊNCIA PARA ACESSO DIRETO AO VALIDATIONCONTEXT =====
    
    /**
     * Valida um objeto genérico usando a estratégia apropriada.
     * 
     * @param input Objeto a ser validado
     * @throws ValidationException se a validação falhar
     */
    public void validate(Object input) {
        validationContext.validate(input);
    }
    
    /**
     * Retorna informações sobre as estratégias registradas.
     * 
     * @return Mapa com informações das estratégias
     */
    public java.util.Map<String, String> getRegisteredStrategies() {
        return validationContext.getRegisteredStrategies();
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
    
}
