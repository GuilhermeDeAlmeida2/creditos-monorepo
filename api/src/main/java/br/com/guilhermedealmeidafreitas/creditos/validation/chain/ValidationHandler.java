package br.com.guilhermedealmeidafreitas.creditos.validation.chain;

/**
 * Interface base para o Chain of Responsibility Pattern de validação.
 * Cada handler é responsável por um tipo específico de validação.
 * 
 * REFATORAÇÃO: Implementa Chain of Responsibility Pattern para organizar
 * validações em uma cadeia flexível e extensível.
 */
public interface ValidationHandler {
    
    /**
     * Define o próximo handler na cadeia.
     * 
     * @param nextHandler Próximo handler na cadeia
     * @return O handler atual para permitir encadeamento fluente
     */
    ValidationHandler setNext(ValidationHandler nextHandler);
    
    /**
     * Processa a validação. Se não conseguir processar, passa para o próximo handler.
     * 
     * @param request Requisição de validação contendo os dados a serem validados
     * @return Resultado da validação
     */
    ValidationResult handle(ValidationRequest request);
    
    /**
     * Verifica se este handler pode processar o tipo de validação solicitado.
     * 
     * @param request Requisição de validação
     * @return true se este handler pode processar a requisição
     */
    boolean canHandle(ValidationRequest request);
    
    /**
     * Retorna o nome do handler para logging e debugging.
     * 
     * @return Nome do handler
     */
    String getHandlerName();
    
    /**
     * Retorna a ordem de prioridade do handler (menor número = maior prioridade).
     * 
     * @return Ordem de prioridade
     */
    int getPriority();
}
