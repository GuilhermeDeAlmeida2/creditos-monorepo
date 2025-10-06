package br.com.guilhermedealmeidafreitas.creditos.service;

/**
 * Interface específica para validação de campos de ordenação.
 * Segue o Interface Segregation Principle (ISP) - clientes que só precisam
 * validar campos não são forçados a implementar validação de direção.
 */
public interface FieldValidator {
    
    /**
     * Valida se um campo é válido para ordenação
     * @param field Campo a ser validado
     * @return true se o campo é válido para ordenação
     */
    boolean isValidSortField(String field);
    
    /**
     * Retorna o campo padrão caso o campo fornecido seja inválido
     * @param invalidField Campo inválido fornecido
     * @return Campo padrão válido
     */
    String getDefaultField(String invalidField);
}
