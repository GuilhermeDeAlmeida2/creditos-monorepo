package br.com.guilhermedealmeidafreitas.creditos.service;

import org.springframework.data.domain.Sort;

/**
 * Interface específica para validação de direção de ordenação.
 * Segue o Interface Segregation Principle (ISP) - clientes que só precisam
 * validar direção não são forçados a implementar validação de campos.
 */
public interface DirectionValidator {
    
    /**
     * Valida e normaliza a direção da ordenação
     * @param direction Direção fornecida (asc/desc)
     * @return Direção validada e normalizada
     */
    Sort.Direction validateSortDirection(String direction);
    
    /**
     * Retorna a direção padrão caso a direção fornecida seja inválida
     * @return Direção padrão
     */
    Sort.Direction getDefaultDirection();
}
