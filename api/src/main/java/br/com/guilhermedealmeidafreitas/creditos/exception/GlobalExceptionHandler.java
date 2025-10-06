package br.com.guilhermedealmeidafreitas.creditos.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global de exceções simplificado
 * Segue o princípio KISS mantendo funcionalidade completa
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handler para todas as exceções específicas de crédito.
     * Simples e direto - sem complexidade desnecessária.
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
        
        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    /**
     * Handler para IllegalArgumentException (usado nos serviços de validação).
     * Converte para CreditoException para manter consistência.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        CreditoException creditoEx = CreditoExceptions.validation(ex.getMessage());
        return handleCreditoException(creditoEx);
    }

    /**
     * Handler para exceções não mapeadas.
     * Converte para CreditoException para manter consistência.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        CreditoException serverEx = CreditoExceptions.internalServer(
            "Erro interno do servidor", ex);
        return handleCreditoException(serverEx);
    }
}
