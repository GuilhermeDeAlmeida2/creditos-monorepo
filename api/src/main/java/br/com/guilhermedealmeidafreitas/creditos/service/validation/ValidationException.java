package br.com.guilhermedealmeidafreitas.creditos.service.validation;

import br.com.guilhermedealmeidafreitas.creditos.exception.CreditoException;

/**
 * Exceção específica para falhas de validação no Strategy Pattern.
 * Estende CreditoException para manter compatibilidade com o sistema de exceções existente.
 */
public class ValidationException extends CreditoException {
    
    private final String strategyName;
    private final String fieldName;
    
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
        this.strategyName = null;
        this.fieldName = null;
    }
    
    public ValidationException(String message, String fieldName) {
        super(message, "VALIDATION_ERROR");
        this.strategyName = null;
        this.fieldName = fieldName;
    }
    
    public ValidationException(String message, String strategyName, String fieldName) {
        super(message, "VALIDATION_ERROR");
        this.strategyName = strategyName;
        this.fieldName = fieldName;
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, "VALIDATION_ERROR", cause);
        this.strategyName = null;
        this.fieldName = null;
    }
    
    public String getStrategyName() {
        return strategyName;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    @Override
    public int getHttpStatus() {
        return 400; // Bad Request para erros de validação
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ValidationException");
        if (strategyName != null) {
            sb.append(" [Strategy: ").append(strategyName).append("]");
        }
        if (fieldName != null) {
            sb.append(" [Field: ").append(fieldName).append("]");
        }
        sb.append(": ").append(getMessage());
        return sb.toString();
    }
}
