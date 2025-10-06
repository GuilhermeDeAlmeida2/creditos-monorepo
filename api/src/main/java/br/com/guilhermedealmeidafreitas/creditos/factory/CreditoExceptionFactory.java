package br.com.guilhermedealmeidafreitas.creditos.factory;

import br.com.guilhermedealmeidafreitas.creditos.exception.CreditoException;
import br.com.guilhermedealmeidafreitas.creditos.exception.SimpleCreditoException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Factory melhorada para criação de exceções do sistema de créditos.
 * Implementa Improved Factory Pattern com suporte a diferentes tipos de exceções.
 * 
 * REFATORAÇÃO: Implementa Improved Factory Pattern para organizar
 * e padronizar a criação de exceções no sistema.
 */
@Component
public class CreditoExceptionFactory extends BaseFactory<CreditoException> {
    
    // Tipos de exceção suportados
    public static final String TYPE_NOT_FOUND = "NOT_FOUND";
    public static final String TYPE_VALIDATION = "VALIDATION";
    public static final String TYPE_INTERNAL_SERVER = "INTERNAL_SERVER";
    public static final String TYPE_FEATURE_NOT_AVAILABLE = "FEATURE_NOT_AVAILABLE";
    public static final String TYPE_TEST_DATA_ERROR = "TEST_DATA_ERROR";
    public static final String TYPE_BUSINESS_RULE = "BUSINESS_RULE";
    public static final String TYPE_AUTHORIZATION = "AUTHORIZATION";
    public static final String TYPE_RATE_LIMIT = "RATE_LIMIT";
    
    // Parâmetros suportados
    public static final String PARAM_MESSAGE = "message";
    public static final String PARAM_TYPE = "type";
    public static final String PARAM_FIELD = "field";
    public static final String PARAM_OPERATION = "operation";
    public static final String PARAM_CAUSE = "cause";
    public static final String PARAM_NUMERO = "numero";
    public static final String PARAM_TIPO_CREDITO = "tipoCredito";
    public static final String PARAM_STATUS_CODE = "statusCode";
    public static final String PARAM_ERROR_CODE = "errorCode";
    
    public CreditoExceptionFactory() {
        super("CreditoExceptionFactory", 
              "Factory para criação de exceções do sistema de créditos", 
              CreditoException.class);
    }
    
    @Override
    public CreditoException create(Map<String, Object> parameters) {
        validateParameters(parameters);
        
        String type = getParameter(parameters, PARAM_TYPE, TYPE_VALIDATION);
        String message = getRequiredParameter(parameters, PARAM_MESSAGE);
        
        return createExceptionByType(type, message, parameters);
    }
    
    @Override
    public Map<String, String> getSupportedParameters() {
        return Map.of(
            PARAM_MESSAGE, "Mensagem da exceção (obrigatório)",
            PARAM_TYPE, "Tipo da exceção (opcional, padrão: VALIDATION)",
            PARAM_FIELD, "Campo relacionado à exceção (opcional)",
            PARAM_OPERATION, "Operação que causou a exceção (opcional)",
            PARAM_CAUSE, "Causa da exceção (opcional)",
            PARAM_NUMERO, "Número do crédito (opcional)",
            PARAM_TIPO_CREDITO, "Tipo do crédito (opcional)",
            PARAM_STATUS_CODE, "Código de status HTTP (opcional)",
            PARAM_ERROR_CODE, "Código de erro personalizado (opcional)"
        );
    }
    
    @Override
    public void validateParameters(Map<String, Object> parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("Parâmetros não podem ser nulos");
        }
        
        if (!hasParameter(parameters, PARAM_MESSAGE)) {
            throw new IllegalArgumentException("Parâmetro 'message' é obrigatório");
        }
        
        String message = getParameter(parameters, PARAM_MESSAGE, "");
        if (message.trim().isEmpty()) {
            throw new IllegalArgumentException("Mensagem não pode ser vazia");
        }
    }
    
    /**
     * Cria exceção baseada no tipo especificado.
     * 
     * @param type Tipo da exceção
     * @param message Mensagem da exceção
     * @param parameters Parâmetros adicionais
     * @return Exceção criada
     */
    private CreditoException createExceptionByType(String type, String message, Map<String, Object> parameters) {
        return switch (type.toUpperCase()) {
            case TYPE_NOT_FOUND -> createNotFoundException(message, parameters);
            case TYPE_VALIDATION -> createValidationException(message, parameters);
            case TYPE_INTERNAL_SERVER -> createInternalServerException(message, parameters);
            case TYPE_FEATURE_NOT_AVAILABLE -> createFeatureNotAvailableException(message, parameters);
            case TYPE_TEST_DATA_ERROR -> createTestDataErrorException(message, parameters);
            case TYPE_BUSINESS_RULE -> createBusinessRuleException(message, parameters);
            case TYPE_AUTHORIZATION -> createAuthorizationException(message, parameters);
            case TYPE_RATE_LIMIT -> createRateLimitException(message, parameters);
            default -> createValidationException(message, parameters);
        };
    }
    
    /**
     * Cria exceção de crédito não encontrado.
     */
    private CreditoException createNotFoundException(String message, Map<String, Object> parameters) {
        String numero = getParameter(parameters, PARAM_NUMERO, "");
        String tipoCredito = getParameter(parameters, PARAM_TIPO_CREDITO, "");
        
        if (!numero.isEmpty() && !tipoCredito.isEmpty()) {
            message = String.format("Crédito não encontrado para %s: %s", tipoCredito, numero);
        }
        
        return new SimpleCreditoException(message, "CREDITO_NOT_FOUND", 404);
    }
    
    /**
     * Cria exceção de validação.
     */
    private CreditoException createValidationException(String message, Map<String, Object> parameters) {
        String field = getParameter(parameters, PARAM_FIELD, "");
        
        if (!field.isEmpty()) {
            message = String.format("%s (campo: %s)", message, field);
        }
        
        String errorCode = getParameter(parameters, PARAM_ERROR_CODE, "VALIDATION_ERROR");
        Integer statusCode = getParameter(parameters, PARAM_STATUS_CODE, 400);
        
        return new SimpleCreditoException(message, errorCode, statusCode);
    }
    
    /**
     * Cria exceção de erro interno do servidor.
     */
    private CreditoException createInternalServerException(String message, Map<String, Object> parameters) {
        Throwable cause = getParameter(parameters, PARAM_CAUSE, null);
        
        if (cause != null) {
            return new SimpleCreditoException(message, "INTERNAL_SERVER_ERROR", 500, cause);
        }
        
        return new SimpleCreditoException(message, "INTERNAL_SERVER_ERROR", 500);
    }
    
    /**
     * Cria exceção de funcionalidade não disponível.
     */
    private CreditoException createFeatureNotAvailableException(String message, Map<String, Object> parameters) {
        return new SimpleCreditoException(message, "FEATURE_NOT_AVAILABLE", 403);
    }
    
    /**
     * Cria exceção de erro de dados de teste.
     */
    private CreditoException createTestDataErrorException(String message, Map<String, Object> parameters) {
        String operation = getParameter(parameters, PARAM_OPERATION, "");
        
        if (!operation.isEmpty()) {
            message = String.format("%s (operação: %s)", message, operation);
        }
        
        return new SimpleCreditoException(message, "TEST_DATA_ERROR", 500);
    }
    
    /**
     * Cria exceção de regra de negócio.
     */
    private CreditoException createBusinessRuleException(String message, Map<String, Object> parameters) {
        String errorCode = getParameter(parameters, PARAM_ERROR_CODE, "BUSINESS_RULE_VIOLATION");
        Integer statusCode = getParameter(parameters, PARAM_STATUS_CODE, 422);
        
        return new SimpleCreditoException(message, errorCode, statusCode);
    }
    
    /**
     * Cria exceção de autorização.
     */
    private CreditoException createAuthorizationException(String message, Map<String, Object> parameters) {
        return new SimpleCreditoException(message, "AUTHORIZATION_ERROR", 401);
    }
    
    /**
     * Cria exceção de limite de taxa.
     */
    private CreditoException createRateLimitException(String message, Map<String, Object> parameters) {
        return new SimpleCreditoException(message, "RATE_LIMIT_EXCEEDED", 429);
    }
    
    // ===== MÉTODOS DE CONVENIÊNCIA =====
    
    /**
     * Cria exceção de crédito não encontrado.
     */
    public CreditoException createNotFound(String numero, String tipoCredito) {
        return create(Map.of(
            PARAM_TYPE, TYPE_NOT_FOUND,
            PARAM_MESSAGE, "Crédito não encontrado",
            PARAM_NUMERO, numero,
            PARAM_TIPO_CREDITO, tipoCredito
        ));
    }
    
    /**
     * Cria exceção de validação.
     */
    public CreditoException createValidation(String message) {
        return create(Map.of(
            PARAM_TYPE, TYPE_VALIDATION,
            PARAM_MESSAGE, message
        ));
    }
    
    /**
     * Cria exceção de validação com campo.
     */
    public CreditoException createValidation(String message, String field) {
        return create(Map.of(
            PARAM_TYPE, TYPE_VALIDATION,
            PARAM_MESSAGE, message,
            PARAM_FIELD, field
        ));
    }
    
    /**
     * Cria exceção de erro interno.
     */
    public CreditoException createInternalServer(String message) {
        return create(Map.of(
            PARAM_TYPE, TYPE_INTERNAL_SERVER,
            PARAM_MESSAGE, message
        ));
    }
    
    /**
     * Cria exceção de erro interno com causa.
     */
    public CreditoException createInternalServer(String message, Throwable cause) {
        return create(Map.of(
            PARAM_TYPE, TYPE_INTERNAL_SERVER,
            PARAM_MESSAGE, message,
            PARAM_CAUSE, cause
        ));
    }
    
    /**
     * Cria exceção de funcionalidade não disponível.
     */
    public CreditoException createFeatureNotAvailable(String message) {
        return create(Map.of(
            PARAM_TYPE, TYPE_FEATURE_NOT_AVAILABLE,
            PARAM_MESSAGE, message
        ));
    }
    
    /**
     * Cria exceção de erro de dados de teste.
     */
    public CreditoException createTestDataError(String message, String operation) {
        return create(Map.of(
            PARAM_TYPE, TYPE_TEST_DATA_ERROR,
            PARAM_MESSAGE, message,
            PARAM_OPERATION, operation
        ));
    }
}
