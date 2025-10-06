package br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers;

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
        if (isNull(value)) {
            return error(String.format("Campo '%s' é obrigatório", fieldName), fieldName);
        }
        
        // Converte para número
        Number number = convertToNumber(value, fieldName);
        if (number == null) {
            return error(String.format("Campo '%s' deve ser um número", fieldName), fieldName);
        }
        
        // Verifica se é positivo
        if (number.doubleValue() <= 0) {
            return error(String.format("Campo '%s' deve ser um número positivo", fieldName), fieldName);
        }
        
        // Validação bem-sucedida
        return success(String.format("Campo '%s' validado com sucesso", fieldName), 
                      fieldName, number);
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
        if (isNull(value)) {
            return error(String.format("Campo '%s' é obrigatório", fieldName), fieldName);
        }
        
        // Converte para número
        Number number = convertToNumber(value, fieldName);
        if (number == null) {
            return error(String.format("Campo '%s' deve ser um número", fieldName), fieldName);
        }
        
        // Obtém os parâmetros de range
        Object minParam = request.getParameter("min");
        Object maxParam = request.getParameter("max");
        
        if (minParam == null || maxParam == null) {
            return error(String.format("Parâmetros 'min' e 'max' são obrigatórios para validação de range do campo '%s'", fieldName), fieldName);
        }
        
        // Converte os parâmetros para números
        Number min = convertToNumber(minParam, "min");
        Number max = convertToNumber(maxParam, "max");
        
        if (min == null || max == null) {
            return error("Parâmetros 'min' e 'max' devem ser números", fieldName);
        }
        
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
    }
    
    /**
     * Converte um objeto para Number.
     * 
     * @param value Valor a ser convertido
     * @param fieldName Nome do campo (para mensagens de erro)
     * @return Number ou null se não conseguir converter
     */
    private Number convertToNumber(Object value, String fieldName) {
        if (value instanceof Number) {
            return (Number) value;
        }
        
        if (value instanceof String) {
            try {
                String stringValue = ((String) value).trim();
                if (stringValue.contains(".")) {
                    return Double.parseDouble(stringValue);
                } else {
                    return Integer.parseInt(stringValue);
                }
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }
}
