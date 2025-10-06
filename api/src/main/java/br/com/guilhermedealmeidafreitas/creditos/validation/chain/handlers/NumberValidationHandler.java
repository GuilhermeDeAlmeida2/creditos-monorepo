package br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers;

import br.com.guilhermedealmeidafreitas.creditos.constants.ErrorMessages;
import br.com.guilhermedealmeidafreitas.creditos.util.ValidationUtils;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.AbstractValidationHandler;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationRequest;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationResult;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Handler para validações de números no Chain of Responsibility.
 * Responsável por validar números positivos e ranges.
 * 
 * REFATORAÇÃO: Implementa Chain of Responsibility Pattern para organizar
 * validações em uma cadeia flexível e extensível.
 */
@Component
public class NumberValidationHandler extends AbstractValidationHandler implements NumberValidationHandlerInterface {
    
    public NumberValidationHandler() {
        super("NumberValidationHandler", 200);
    }
    
    @Override
    public boolean canHandle(ValidationRequest request) {
        return request.getType() == ValidationType.NUMBER_POSITIVE || 
               request.getType() == ValidationType.NUMBER_RANGE;
    }
    
    @Override
    protected ValidationResult doHandle(ValidationRequest request) {
        Object value = request.getValue();
        String fieldName = request.getFieldName();
        
        // Validação de número positivo
        if (request.getType() == ValidationType.NUMBER_POSITIVE) {
            return validatePositiveNumber(value, fieldName);
        }
        
        // Validação de número em range
        if (request.getType() == ValidationType.NUMBER_RANGE) {
            return validateNumberRange(value, fieldName, request);
        }
        
        // Se chegou aqui, não deveria acontecer
        return error("Tipo de validação não suportado: " + request.getType(), fieldName);
    }
    
    /**
     * Valida número positivo.
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @return Resultado da validação
     */
    private ValidationResult validatePositiveNumber(Object value, String fieldName) {
        // Verifica se o valor é nulo
        if (ValidationUtils.isNull(value)) {
            return error(String.format("Campo '%s' é obrigatório", fieldName), fieldName);
        }
        
        // Converte para número usando ValidationUtils
        try {
            Number number = ValidationUtils.parseNumber(value, fieldName);
            
            // Verifica se é positivo
            if (number.doubleValue() <= 0) {
                return error(ErrorMessages.numberMustBePositive(fieldName), fieldName);
            }
            
            // Validação bem-sucedida
            return success(String.format("Campo '%s' validado com sucesso", fieldName), 
                          fieldName, number);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage(), fieldName);
        }
    }
    
    /**
     * Valida número em range.
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @param request Requisição de validação
     * @return Resultado da validação
     */
    private ValidationResult validateNumberRange(Object value, String fieldName, ValidationRequest request) {
        // Verifica se o valor é nulo
        if (ValidationUtils.isNull(value)) {
            return error(String.format("Campo '%s' é obrigatório", fieldName), fieldName);
        }
        
        try {
            // Converte para número usando ValidationUtils
            Number number = ValidationUtils.parseNumber(value, fieldName);
            
            // Obtém os parâmetros de range
            Object minParam = request.getParameter("min");
            Object maxParam = request.getParameter("max");
            
            if (minParam == null || maxParam == null) {
                return error(String.format("Parâmetros 'min' e 'max' são obrigatórios para validação de range do campo '%s'", fieldName), fieldName);
            }
            
            // Converte os parâmetros para números usando ValidationUtils
            Number min = ValidationUtils.parseNumber(minParam, "min");
            Number max = ValidationUtils.parseNumber(maxParam, "max");
            
            // Verifica se min <= max
            if (min.doubleValue() > max.doubleValue()) {
                return error(ErrorMessages.MIN_MUST_BE_LESS_OR_EQUAL_MAX, fieldName);
            }
            
            // Verifica se o número está no range
            double numberValue = number.doubleValue();
            double minValue = min.doubleValue();
            double maxValue = max.doubleValue();
            
            if (numberValue < minValue || numberValue > maxValue) {
                return error(String.format("Campo '%s' deve estar entre %s e %s", fieldName, min, max), fieldName);
            }
            
            // Validação bem-sucedida
            return success(String.format("Campo '%s' validado com sucesso", fieldName), 
                          fieldName, number);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage(), fieldName);
        }
    }
    
    // ==================== IMPLEMENTAÇÃO DA INTERFACE ====================
    
    @Override
    public ValidationResult validatePositive(Number value, String fieldName) {
        return validatePositiveNumber(value, fieldName);
    }
    
    @Override
    public ValidationResult validateRange(Number value, String fieldName, Number min, Number max) {
        ValidationRequest request = new ValidationRequest(ValidationType.NUMBER_RANGE, value, fieldName, 
            Map.of("min", min, "max", max));
        return validateNumberRange(value, fieldName, request);
    }
    
    @Override
    public ValidationResult validateMin(Number value, String fieldName, Number min) {
        if (ValidationUtils.isNull(value)) {
            return error(ErrorMessages.format("Campo '%s' é obrigatório", fieldName), fieldName);
        }
        
        try {
            Number number = ValidationUtils.parseNumber(value, fieldName);
            
            if (number.doubleValue() < min.doubleValue()) {
                return error(ErrorMessages.format("Campo '%s' deve ser maior ou igual a %s", fieldName, min), fieldName);
            }
            
            return success(ErrorMessages.format("Campo '%s' validado com sucesso", fieldName), fieldName, number);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage(), fieldName);
        }
    }
    
    @Override
    public ValidationResult validateMax(Number value, String fieldName, Number max) {
        if (ValidationUtils.isNull(value)) {
            return error(ErrorMessages.format("Campo '%s' é obrigatório", fieldName), fieldName);
        }
        
        try {
            Number number = ValidationUtils.parseNumber(value, fieldName);
            
            if (number.doubleValue() > max.doubleValue()) {
                return error(ErrorMessages.format("Campo '%s' deve ser menor ou igual a %s", fieldName, max), fieldName);
            }
            
            return success(ErrorMessages.format("Campo '%s' validado com sucesso", fieldName), fieldName, number);
        } catch (IllegalArgumentException e) {
            return error(e.getMessage(), fieldName);
        }
    }
    
    @Override
    public ValidationResult validatePrecision(BigDecimal value, String fieldName, int precision) {
        if (ValidationUtils.isNull(value)) {
            return error(ErrorMessages.format("Campo '%s' é obrigatório", fieldName), fieldName);
        }
        
        int actualPrecision = value.scale();
        if (actualPrecision > precision) {
            return error(ErrorMessages.format("Campo '%s' deve ter no máximo %d casas decimais", fieldName, precision), fieldName);
        }
        
        return success(ErrorMessages.format("Campo '%s' validado com sucesso", fieldName), fieldName, value);
    }
    
    @Override
    public ValidationResult validateIntegerRange(Integer value, String fieldName, int min, int max) {
        if (ValidationUtils.isNull(value)) {
            return error(ErrorMessages.format("Campo '%s' é obrigatório", fieldName), fieldName);
        }
        
        if (value < min || value > max) {
            return error(ErrorMessages.format("Campo '%s' deve estar entre %d e %d", fieldName, min, max), fieldName);
        }
        
        return success(ErrorMessages.format("Campo '%s' validado com sucesso", fieldName), fieldName, value);
    }
    
}
