package br.com.guilhermedealmeidafreitas.creditos.exception;

/**
 * Implementação concreta simples da CreditoException
 * Usada para simplificar o sistema mantendo flexibilidade
 */
public class SimpleCreditoException extends CreditoException {
    
    private final int httpStatus;
    
    public SimpleCreditoException(String message, String errorCode, int httpStatus) {
        super(message, errorCode);
        this.httpStatus = httpStatus;
    }
    
    public SimpleCreditoException(String message, String errorCode, int httpStatus, Throwable cause) {
        super(message, errorCode, cause);
        this.httpStatus = httpStatus;
    }
    
    @Override
    public int getHttpStatus() {
        return httpStatus;
    }
    
    @Override
    public String getDetails() {
        return null; // Sem detalhes adicionais por padrão
    }
}
