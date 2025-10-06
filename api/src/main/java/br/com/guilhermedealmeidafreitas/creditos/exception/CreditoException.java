package br.com.guilhermedealmeidafreitas.creditos.exception;

/**
 * Exceção base para todas as exceções relacionadas a créditos.
 * Segue o Liskov Substitution Principle (LSP), permitindo que subclasses
 * sejam substituídas pela classe base mantendo o comportamento esperado.
 */
public abstract class CreditoException extends RuntimeException {
    
    private final String errorCode;
    
    protected CreditoException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    protected CreditoException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Método abstrato que deve ser implementado pelas subclasses
     * para definir o status HTTP apropriado.
     * @return O status HTTP correspondente ao tipo de exceção
     */
    public abstract int getHttpStatus();
    
    /**
     * Método que pode ser sobrescrito pelas subclasses para
     * fornecer detalhes adicionais do erro.
     * @return Detalhes adicionais do erro
     */
    public String getDetails() {
        return null;
    }
}
