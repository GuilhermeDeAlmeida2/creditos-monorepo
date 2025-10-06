package br.com.guilhermedealmeidafreitas.creditos.exception;

/**
 * Exceção lançada quando há erro na geração ou deleção de dados de teste.
 * Segue o LSP - pode ser substituída por CreditoException mantendo o comportamento.
 */
public class TestDataException extends CreditoException {
    
    private static final String ERROR_CODE = "TEST_DATA_ERROR";
    private static final int HTTP_STATUS = 500;
    
    private final String operation;
    
    public TestDataException(String message, String operation) {
        super(message, ERROR_CODE);
        this.operation = operation;
    }
    
    public TestDataException(String message, String operation, Throwable cause) {
        super(message, ERROR_CODE, cause);
        this.operation = operation;
    }
    
    @Override
    public int getHttpStatus() {
        return HTTP_STATUS;
    }
    
    public String getOperation() {
        return operation;
    }
    
    @Override
    public String getDetails() {
        return String.format("Erro durante operação de teste: %s", operation);
    }
}
