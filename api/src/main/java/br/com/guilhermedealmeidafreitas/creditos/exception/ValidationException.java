package br.com.guilhermedealmeidafreitas.creditos.exception;

/**
 * Exceção lançada quando há erro de validação de dados.
 * Segue o LSP - pode ser substituída por CreditoException mantendo o comportamento.
 */
public class ValidationException extends CreditoException {
    
    private static final String ERROR_CODE = "VALIDATION_ERROR";
    private static final int HTTP_STATUS = 400;
    
    private final String fieldName;
    
    public ValidationException(String message) {
        super(message, ERROR_CODE);
        this.fieldName = null;
    }
    
    public ValidationException(String message, String fieldName) {
        super(message, ERROR_CODE);
        this.fieldName = fieldName;
    }
    
    public ValidationException(String message, String fieldName, Throwable cause) {
        super(message, ERROR_CODE, cause);
        this.fieldName = fieldName;
    }
    
    @Override
    public int getHttpStatus() {
        return HTTP_STATUS;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    @Override
    public String getDetails() {
        if (fieldName != null) {
            return String.format("Campo '%s' contém dados inválidos.", fieldName);
        }
        return "Dados de entrada não passaram na validação.";
    }
}
