package br.com.guilhermedealmeidafreitas.creditos.validation.chain;

/**
 * Classe base abstrata para handlers de validação no Chain of Responsibility.
 * Implementa a lógica comum da cadeia e fornece métodos auxiliares.
 * 
 * REFATORAÇÃO: Implementa Chain of Responsibility Pattern para organizar
 * validações em uma cadeia flexível e extensível.
 */
public abstract class AbstractValidationHandler implements ValidationHandler {
    
    private ValidationHandler nextHandler;
    private final String handlerName;
    private final int priority;
    
    /**
     * Construtor.
     * 
     * @param handlerName Nome do handler
     * @param priority Prioridade do handler (menor número = maior prioridade)
     */
    protected AbstractValidationHandler(String handlerName, int priority) {
        this.handlerName = handlerName;
        this.priority = priority;
    }
    
    @Override
    public ValidationHandler setNext(ValidationHandler nextHandler) {
        this.nextHandler = nextHandler;
        return this;
    }
    
    @Override
    public ValidationResult handle(ValidationRequest request) {
        // Verifica se este handler pode processar a requisição
        if (canHandle(request)) {
            try {
                // Processa a validação
                ValidationResult result = doHandle(request);
                
                // Se a validação foi bem-sucedida, retorna o resultado
                if (result.isValid()) {
                    return result;
                }
                
                // Se a validação falhou, retorna o erro
                return result;
                
            } catch (Exception e) {
                // Se houve uma exceção durante o processamento, retorna erro
                return new ValidationResult(
                    String.format("Erro durante validação: %s", e.getMessage()),
                    request.getFieldName(),
                    getHandlerName()
                );
            }
        }
        
        // Se este handler não pode processar, passa para o próximo
        if (nextHandler != null) {
            return nextHandler.handle(request);
        }
        
        // Se não há próximo handler, retorna erro
        return new ValidationResult(
            String.format("Nenhum handler disponível para processar validação do tipo: %s", 
                         request.getType()),
            request.getFieldName(),
            "ChainOfResponsibility"
        );
    }
    
    @Override
    public String getHandlerName() {
        return handlerName;
    }
    
    @Override
    public int getPriority() {
        return priority;
    }
    
    /**
     * Método abstrato que deve ser implementado pelas subclasses
     * para realizar a validação específica.
     * 
     * @param request Requisição de validação
     * @return Resultado da validação
     */
    protected abstract ValidationResult doHandle(ValidationRequest request);
    
    /**
     * Método auxiliar para criar resultado de sucesso.
     * 
     * @param message Mensagem de sucesso
     * @param processedValue Valor processado
     * @return Resultado de sucesso
     */
    protected ValidationResult success(String message, Object processedValue) {
        return new ValidationResult(message, processedValue, getHandlerName());
    }
    
    /**
     * Método auxiliar para criar resultado de sucesso com campo.
     * 
     * @param message Mensagem de sucesso
     * @param fieldName Nome do campo
     * @param processedValue Valor processado
     * @return Resultado de sucesso
     */
    protected ValidationResult success(String message, String fieldName, Object processedValue) {
        return new ValidationResult(message, fieldName, processedValue, getHandlerName());
    }
    
    /**
     * Método auxiliar para criar resultado de erro.
     * 
     * @param message Mensagem de erro
     * @param fieldName Nome do campo
     * @return Resultado de erro
     */
    protected ValidationResult error(String message, String fieldName) {
        return new ValidationResult(message, fieldName, getHandlerName());
    }
    
    /**
     * Método auxiliar para criar resultado de erro com múltiplas mensagens.
     * 
     * @param errors Lista de erros
     * @param fieldName Nome do campo
     * @return Resultado de erro
     */
    protected ValidationResult error(java.util.List<String> errors, String fieldName) {
        return new ValidationResult(errors, fieldName, getHandlerName());
    }
    
    /**
     * Verifica se o valor é nulo.
     * 
     * @param value Valor a ser verificado
     * @return true se o valor for nulo
     */
    protected boolean isNull(Object value) {
        return value == null;
    }
    
    /**
     * Verifica se a string é nula ou vazia.
     * 
     * @param value String a ser verificada
     * @return true se a string for nula ou vazia
     */
    protected boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    /**
     * Verifica se a string é nula, vazia ou contém apenas espaços.
     * 
     * @param value String a ser verificada
     * @return true se a string for nula, vazia ou contém apenas espaços
     */
    protected boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    /**
     * Obtém o próximo handler na cadeia.
     * 
     * @return Próximo handler ou null se não houver
     */
    protected ValidationHandler getNextHandler() {
        return nextHandler;
    }
}
