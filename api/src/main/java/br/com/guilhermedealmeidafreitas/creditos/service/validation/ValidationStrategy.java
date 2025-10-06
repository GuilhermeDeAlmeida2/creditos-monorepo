package br.com.guilhermedealmeidafreitas.creditos.service.validation;

/**
 * Interface base para o Strategy Pattern de validações.
 * Permite diferentes estratégias de validação para diferentes tipos de dados.
 * 
 * @param <T> Tipo de objeto a ser validado
 */
public interface ValidationStrategy<T> {
    
    /**
     * Executa a validação no objeto fornecido.
     * 
     * @param input Objeto a ser validado
     * @throws ValidationException se a validação falhar
     */
    void validate(T input);
    
    /**
     * Verifica se esta estratégia suporta o tipo de objeto fornecido.
     * 
     * @param type Classe do objeto a ser validado
     * @return true se esta estratégia pode validar o tipo fornecido
     */
    boolean supports(Class<?> type);
    
    /**
     * Retorna o nome da estratégia para logging e debugging.
     * 
     * @return Nome da estratégia
     */
    String getStrategyName();
}
