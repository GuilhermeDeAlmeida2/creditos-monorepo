package br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers;

import br.com.guilhermedealmeidafreitas.creditos.util.ValidationUtils;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.AbstractValidationHandler;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationRequest;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationResult;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationType;
import org.springframework.stereotype.Component;

/**
 * Handler para validações de números no Chain of Responsibility.
 * Responsável por validar números positivos e ranges.
 * 
 * REFATORAÇÃO: Implementa Chain of Responsibility Pattern para organizar
 * validações em uma cadeia flexível e extensível.
 */
@Component
public class NumberValidationHandler extends AbstractValidationHandler {
    
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
                return error(String.format("Campo '%s' deve ser um número positivo", fieldName), fieldName);
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
                return error("Parâmetro 'min' deve ser menor ou igual a 'max'", fieldName);
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
    
}
