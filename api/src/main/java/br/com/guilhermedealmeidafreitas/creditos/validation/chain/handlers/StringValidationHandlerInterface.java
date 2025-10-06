package br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers;

import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationResult;

/**
 * Interface específica para handlers de validação de strings.
 * 
 * REFATORAÇÃO DRY: Define contratos específicos para validações de string,
 * melhorando a consistência e type safety dos handlers.
 * 
 * @author Guilherme de Almeida Freitas
 */
public interface StringValidationHandlerInterface {
    
    /**
     * Valida se uma string não está vazia.
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @return Resultado da validação
     */
    ValidationResult validateNotEmpty(String value, String fieldName);
    
    /**
     * Valida se uma string é opcional (pode ser nula ou vazia).
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @return Resultado da validação
     */
    ValidationResult validateOptional(String value, String fieldName);
    
    /**
     * Valida se uma string tem o tamanho correto.
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @param minLength Tamanho mínimo
     * @param maxLength Tamanho máximo
     * @return Resultado da validação
     */
    ValidationResult validateLength(String value, String fieldName, int minLength, int maxLength);
    
    /**
     * Valida se uma string corresponde a um padrão regex.
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @param pattern Padrão regex
     * @return Resultado da validação
     */
    ValidationResult validatePattern(String value, String fieldName, String pattern);
}
