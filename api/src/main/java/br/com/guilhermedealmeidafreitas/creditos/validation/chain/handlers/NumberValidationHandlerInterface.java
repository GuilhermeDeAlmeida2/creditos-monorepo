package br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers;

import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationResult;

import java.math.BigDecimal;

/**
 * Interface específica para handlers de validação de números.
 * 
 * REFATORAÇÃO DRY: Define contratos específicos para validações de números,
 * melhorando a consistência e type safety dos handlers.
 * 
 * @author Guilherme de Almeida Freitas
 */
public interface NumberValidationHandlerInterface {
    
    /**
     * Valida se um número é positivo.
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @return Resultado da validação
     */
    ValidationResult validatePositive(Number value, String fieldName);
    
    /**
     * Valida se um número está dentro de um range.
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @param min Valor mínimo
     * @param max Valor máximo
     * @return Resultado da validação
     */
    ValidationResult validateRange(Number value, String fieldName, Number min, Number max);
    
    /**
     * Valida se um número é maior que um valor mínimo.
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @param min Valor mínimo
     * @return Resultado da validação
     */
    ValidationResult validateMin(Number value, String fieldName, Number min);
    
    /**
     * Valida se um número é menor que um valor máximo.
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @param max Valor máximo
     * @return Resultado da validação
     */
    ValidationResult validateMax(Number value, String fieldName, Number max);
    
    /**
     * Valida se um número decimal tem a precisão correta.
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @param precision Precisão (número de casas decimais)
     * @return Resultado da validação
     */
    ValidationResult validatePrecision(BigDecimal value, String fieldName, int precision);
    
    /**
     * Valida se um número inteiro está dentro de um range.
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @param min Valor mínimo
     * @param max Valor máximo
     * @return Resultado da validação
     */
    ValidationResult validateIntegerRange(Integer value, String fieldName, int min, int max);
}
