package br.com.guilhermedealmeidafreitas.creditos.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global de exceções que segue o Liskov Substitution Principle (LSP).
 * Qualquer subclasse de CreditoException pode ser tratada de forma consistente,
 * mantendo o comportamento esperado da classe base.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handler para todas as exceções específicas de crédito.
     * Segue o LSP - pode tratar qualquer subclasse de CreditoException
     * de forma consistente.
     */
    @ExceptionHandler(CreditoException.class)
    public ResponseEntity<Map<String, Object>> handleCreditoException(CreditoException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", Instant.now().toString());
        errorResponse.put("status", ex.getHttpStatus());
        errorResponse.put("error", ex.getClass().getSimpleName());
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("errorCode", ex.getErrorCode());
        
        // Adiciona detalhes se disponíveis
        String details = ex.getDetails();
        if (details != null) {
            errorResponse.put("details", details);
        }
        
        // Adiciona informações específicas baseadas no tipo de exceção
        addSpecificErrorInfo(ex, errorResponse);
        
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    /**
     * Adiciona informações específicas baseadas no tipo de exceção.
     * Demonstra o LSP - cada tipo de exceção pode ter informações específicas
     * mas mantém a interface comum.
     */
    private void addSpecificErrorInfo(CreditoException ex, Map<String, Object> errorResponse) {
        if (ex instanceof ValidationException) {
            ValidationException validationEx = (ValidationException) ex;
            if (validationEx.getFieldName() != null) {
                errorResponse.put("field", validationEx.getFieldName());
            }
        } else if (ex instanceof TestDataException) {
            TestDataException testDataEx = (TestDataException) ex;
            errorResponse.put("operation", testDataEx.getOperation());
        }
    }

    /**
     * Handler para IllegalArgumentException (usado nos serviços de validação).
     * Converte para ValidationException para manter consistência.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ValidationException validationEx = new ValidationException(ex.getMessage());
        return handleCreditoException(validationEx);
    }

    /**
     * Handler para exceções não mapeadas.
     * Converte para InternalServerException para manter consistência.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        InternalServerException serverEx = new InternalServerException(
            "Erro interno do servidor", ex);
        return handleCreditoException(serverEx);
    }
}
