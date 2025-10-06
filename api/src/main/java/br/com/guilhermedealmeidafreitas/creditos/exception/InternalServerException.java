package br.com.guilhermedealmeidafreitas.creditos.exception;

/**
 * Exceção lançada quando há erro interno do servidor.
 * Segue o LSP - pode ser substituída por CreditoException mantendo o comportamento.
 */
public class InternalServerException extends CreditoException {
    
    private static final String ERROR_CODE = "INTERNAL_SERVER_ERROR";
    private static final int HTTP_STATUS = 500;
    
    public InternalServerException(String message) {
        super(message, ERROR_CODE);
    }
    
    public InternalServerException(String message, Throwable cause) {
        super(message, ERROR_CODE, cause);
    }
    
    @Override
    public int getHttpStatus() {
        return HTTP_STATUS;
    }
    
    @Override
    public String getDetails() {
        return "Erro interno do servidor. Tente novamente mais tarde.";
    }
}
