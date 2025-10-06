package br.com.guilhermedealmeidafreitas.creditos.service;

import org.springframework.data.domain.Sort;

/**
 * Interface principal para validação de campos de ordenação.
 * Agrega as interfaces específicas seguindo o Interface Segregation Principle (ISP).
 * 
 * Esta interface serve como um "facade" que combina as responsabilidades específicas,
 * mas os clientes podem optar por depender apenas das interfaces específicas.
 */
public interface SortFieldValidator extends FieldValidator, DirectionValidator {
    
    // Todos os métodos são herdados das interfaces específicas (FieldValidator e DirectionValidator).
    
    /**
     * Valida completamente os parâmetros de ordenação
     * @param sortBy Campo de ordenação
     * @param sortDirection Direção da ordenação
     * @return Objeto com os parâmetros validados
     */
    SortParams validateSortParams(String sortBy, String sortDirection);
    
    /**
     * Classe para encapsular parâmetros de ordenação validados
     */
    class SortParams {
        private final String field;
        private final Sort.Direction direction;
        
        public SortParams(String field, Sort.Direction direction) {
            this.field = field;
            this.direction = direction;
        }
        
        public String getField() {
            return field;
        }
        
        public Sort.Direction getDirection() {
            return direction;
        }
        
        public Sort getSort() {
            return Sort.by(direction, field);
        }
    }
}
