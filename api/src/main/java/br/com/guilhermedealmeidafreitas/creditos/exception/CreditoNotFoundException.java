package br.com.guilhermedealmeidafreitas.creditos.exception;

/**
 * Exceção lançada quando um crédito não é encontrado.
 * Segue o LSP - pode ser substituída por CreditoException mantendo o comportamento.
 */
public class CreditoNotFoundException extends CreditoException {
    
    private static final String ERROR_CODE = "CREDITO_NOT_FOUND";
    private static final int HTTP_STATUS = 404;
    
    public CreditoNotFoundException(String message) {
        super(message, ERROR_CODE);
    }
    
    public CreditoNotFoundException(String message, Throwable cause) {
        super(message, ERROR_CODE, cause);
    }
    
    public CreditoNotFoundException(String numeroCredito, String tipoBusca) {
        super(String.format("Crédito não encontrado para %s: %s", tipoBusca, numeroCredito), ERROR_CODE);
    }
    
    @Override
    public int getHttpStatus() {
        return HTTP_STATUS;
    }
    
    @Override
    public String getDetails() {
        return "Verifique se o número informado está correto e se o crédito existe no sistema.";
    }
}
