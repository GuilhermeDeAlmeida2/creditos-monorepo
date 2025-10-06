package br.com.guilhermedealmeidafreitas.creditos.exception;

/**
 * Factory simples para criar exceções específicas
 * Mantém DRY (evita repetição de códigos de status)
 */
public class CreditoExceptions {
    
    /**
     * Cria exceção para crédito não encontrado
     */
    public static CreditoException notFound(String numero, String tipo) {
        return new SimpleCreditoException(
            String.format("Crédito não encontrado para %s: %s", tipo, numero),
            "CREDITO_NOT_FOUND", 404
        );
    }
    
    /**
     * Cria exceção de validação
     */
    public static CreditoException validation(String message) {
        return new SimpleCreditoException(message, "VALIDATION_ERROR", 400);
    }
    
    /**
     * Cria exceção de validação com campo específico
     */
    public static CreditoException validation(String message, String field) {
        return new SimpleCreditoException(
            String.format("%s (campo: %s)", message, field), "VALIDATION_ERROR", 400);
    }
    
    /**
     * Cria exceção de erro interno do servidor
     */
    public static CreditoException internalServer(String message) {
        return new SimpleCreditoException(message, "INTERNAL_SERVER_ERROR", 500);
    }
    
    /**
     * Cria exceção de erro interno do servidor com causa
     */
    public static CreditoException internalServer(String message, Throwable cause) {
        return new SimpleCreditoException(message, "INTERNAL_SERVER_ERROR", 500, cause);
    }
    
    /**
     * Cria exceção para funcionalidade não disponível
     */
    public static CreditoException notAvailable(String message) {
        return new SimpleCreditoException(message, "FEATURE_NOT_AVAILABLE", 403);
    }
    
    /**
     * Cria exceção para erro de dados de teste
     */
    public static CreditoException testDataError(String message, String operation) {
        return new SimpleCreditoException(
            String.format("%s (operação: %s)", message, operation), 
            "TEST_DATA_ERROR", 500);
    }
}
