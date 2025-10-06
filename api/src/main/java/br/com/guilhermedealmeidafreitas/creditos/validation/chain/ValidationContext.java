package br.com.guilhermedealmeidafreitas.creditos.validation.chain;

/**
 * Enum que define o contexto de validação no Chain of Responsibility.
 * Diferentes contextos podem ter regras de validação diferentes.
 * 
 * REFATORAÇÃO: Implementa Chain of Responsibility Pattern para organizar
 * validações em uma cadeia flexível e extensível.
 */
public enum ValidationContext {
    
    /**
     * Contexto padrão para validações gerais.
     */
    DEFAULT("Validação padrão"),
    
    /**
     * Contexto para validações de API REST.
     */
    API_REST("Validação de API REST"),
    
    /**
     * Contexto para validações de dados de entrada do usuário.
     */
    USER_INPUT("Validação de entrada do usuário"),
    
    /**
     * Contexto para validações de dados de teste.
     */
    TEST_DATA("Validação de dados de teste"),
    
    /**
     * Contexto para validações de dados de produção.
     */
    PRODUCTION("Validação de dados de produção"),
    
    /**
     * Contexto para validações de dados de desenvolvimento.
     */
    DEVELOPMENT("Validação de dados de desenvolvimento"),
    
    /**
     * Contexto para validações de paginação.
     */
    PAGINATION("Validação de paginação"),
    
    /**
     * Contexto para validações de ordenação.
     */
    SORTING("Validação de ordenação"),
    
    /**
     * Contexto para validações de filtros.
     */
    FILTERING("Validação de filtros"),
    
    /**
     * Contexto para validações de dados fiscais.
     */
    FISCAL("Validação de dados fiscais"),
    
    /**
     * Contexto para validações de dados de crédito.
     */
    CREDIT("Validação de dados de crédito"),
    
    /**
     * Contexto para validações de dados de NFS-e.
     */
    NFSE("Validação de dados de NFS-e"),
    
    /**
     * Contexto para validações de dados de Simples Nacional.
     */
    SIMPLES_NACIONAL("Validação de dados do Simples Nacional"),
    
    /**
     * Contexto para validações de dados de alíquota.
     */
    ALIQUOTA("Validação de dados de alíquota"),
    
    /**
     * Contexto para validações de dados de valores monetários.
     */
    MONETARY("Validação de dados monetários");
    
    private final String description;
    
    ValidationContext(String description) {
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
