package br.com.guilhermedealmeidafreitas.creditos.validation.chain;

/**
 * Enum que define os tipos de validação disponíveis no Chain of Responsibility.
 * 
 * REFATORAÇÃO: Implementa Chain of Responsibility Pattern para organizar
 * validações em uma cadeia flexível e extensível.
 */
public enum ValidationType {
    
    /**
     * Validação de string não nula e não vazia.
     */
    STRING_NOT_EMPTY("String não pode ser nula ou vazia"),
    
    /**
     * Validação de string opcional (pode ser nula ou vazia).
     */
    STRING_OPTIONAL("String opcional"),
    
    /**
     * Validação de número positivo.
     */
    NUMBER_POSITIVE("Número deve ser positivo"),
    
    /**
     * Validação de número dentro de um range.
     */
    NUMBER_RANGE("Número deve estar dentro do range especificado"),
    
    /**
     * Validação de parâmetros de paginação.
     */
    PAGEABLE("Parâmetros de paginação inválidos"),
    
    /**
     * Validação de campo de ordenação.
     */
    SORT_FIELD("Campo de ordenação inválido"),
    
    /**
     * Validação de direção de ordenação.
     */
    SORT_DIRECTION("Direção de ordenação inválida"),
    
    /**
     * Validação de formato de data.
     */
    DATE_FORMAT("Formato de data inválido"),
    
    /**
     * Validação de formato de número decimal.
     */
    DECIMAL_FORMAT("Formato de número decimal inválido"),
    
    /**
     * Validação de tamanho de string.
     */
    STRING_LENGTH("Tamanho da string inválido"),
    
    /**
     * Validação de padrão regex.
     */
    REGEX_PATTERN("String não corresponde ao padrão esperado"),
    
    /**
     * Validação de valor em lista de valores permitidos.
     */
    ENUM_VALUE("Valor não está na lista de valores permitidos"),
    
    /**
     * Validação de objeto não nulo.
     */
    NOT_NULL("Objeto não pode ser nulo"),
    
    /**
     * Validação de coleção não vazia.
     */
    COLLECTION_NOT_EMPTY("Coleção não pode ser vazia"),
    
    /**
     * Validação customizada.
     */
    CUSTOM("Validação customizada");
    
    private final String description;
    
    ValidationType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return String.format("%s: %s", name(), description);
    }
}
