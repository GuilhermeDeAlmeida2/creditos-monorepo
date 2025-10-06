package br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers;

import br.com.guilhermedealmeidafreitas.creditos.validation.chain.AbstractValidationHandler;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationRequest;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationResult;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationType;
import org.springframework.stereotype.Component;

/**
 * Handler para validações de string no Chain of Responsibility.
 * Responsável por validar strings não vazias e opcionais.
 * 
 * REFATORAÇÃO: Implementa Chain of Responsibility Pattern para organizar
 * validações em uma cadeia flexível e extensível.
 */
@Component
public class StringValidationHandler extends AbstractValidationHandler {
    
    public StringValidationHandler() {
        super("StringValidationHandler", 100);
    }
    
    @Override
    public boolean canHandle(ValidationRequest request) {
        return request.getType() == ValidationType.STRING_NOT_EMPTY || 
               request.getType() == ValidationType.STRING_OPTIONAL;
    }
    
    @Override
    protected ValidationResult doHandle(ValidationRequest request) {
        Object value = request.getValue();
        String fieldName = request.getFieldName();
        
        // Validação de string não vazia
        if (request.getType() == ValidationType.STRING_NOT_EMPTY) {
            return validateStringNotEmpty(value, fieldName);
        }
        
        // Validação de string opcional
        if (request.getType() == ValidationType.STRING_OPTIONAL) {
            return validateStringOptional(value, fieldName);
        }
        
        // Se chegou aqui, não deveria acontecer
        return error("Tipo de validação não suportado: " + request.getType(), fieldName);
    }
    
    /**
     * Valida string não vazia.
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @return Resultado da validação
     */
    private ValidationResult validateStringNotEmpty(Object value, String fieldName) {
        // Verifica se o valor é nulo
        if (isNull(value)) {
            return error(String.format("Campo '%s' é obrigatório", fieldName), fieldName);
        }
        
        // Verifica se é uma string
        if (!(value instanceof String)) {
            return error(String.format("Campo '%s' deve ser uma string", fieldName), fieldName);
        }
        
        String stringValue = (String) value;
        
        // Verifica se a string não está vazia
        if (isNullOrEmpty(stringValue)) {
            return error(String.format("Campo '%s' não pode ser vazio", fieldName), fieldName);
        }
        
        // Verifica se a string não contém apenas espaços
        if (isNullOrBlank(stringValue)) {
            return error(String.format("Campo '%s' não pode conter apenas espaços", fieldName), fieldName);
        }
        
        // Validação bem-sucedida - retorna a string trimada
        String trimmedValue = stringValue.trim();
        return success(String.format("Campo '%s' validado com sucesso", fieldName), 
                      fieldName, trimmedValue);
    }
    
    /**
     * Valida string opcional.
     * 
     * @param value Valor a ser validado
     * @param fieldName Nome do campo
     * @return Resultado da validação
     */
    private ValidationResult validateStringOptional(Object value, String fieldName) {
        // Se o valor é nulo, é válido (opcional)
        if (isNull(value)) {
            return success(String.format("Campo '%s' é opcional e está nulo", fieldName), 
                          fieldName, null);
        }
        
        // Se não é uma string, retorna erro
        if (!(value instanceof String)) {
            return error(String.format("Campo '%s' deve ser uma string quando fornecido", fieldName), fieldName);
        }
        
        String stringValue = (String) value;
        
        // Se a string está vazia, retorna null (opcional)
        if (isNullOrEmpty(stringValue)) {
            return success(String.format("Campo '%s' é opcional e está vazio", fieldName), 
                          fieldName, null);
        }
        
        // Se a string contém apenas espaços, retorna null (opcional)
        if (isNullOrBlank(stringValue)) {
            return success(String.format("Campo '%s' é opcional e contém apenas espaços", fieldName), 
                          fieldName, null);
        }
        
        // Validação bem-sucedida - retorna a string trimada
        String trimmedValue = stringValue.trim();
        return success(String.format("Campo '%s' validado com sucesso", fieldName), 
                      fieldName, trimmedValue);
    }
}
