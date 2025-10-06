package br.com.guilhermedealmeidafreitas.creditos.constants;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para ErrorMessages.
 * 
 * @author Guilherme de Almeida Freitas
 */
@DisplayName("ErrorMessages Tests")
class ErrorMessagesTest {
    
    @Test
    @DisplayName("Deve formatar mensagem de string vazia corretamente")
    void shouldFormatStringCannotBeEmptyCorrectly() {
        // Given
        String fieldName = "nome";
        
        // When
        String message = ErrorMessages.stringCannotBeEmpty(fieldName);
        
        // Then
        assertEquals("Campo 'nome' não pode ser vazio", message);
    }
    
    @Test
    @DisplayName("Deve formatar mensagem de string deve ser string corretamente")
    void shouldFormatStringMustBeStringCorrectly() {
        // Given
        String fieldName = "codigo";
        
        // When
        String message = ErrorMessages.stringMustBeString(fieldName);
        
        // Then
        assertEquals("Campo 'codigo' deve ser uma string", message);
    }
    
    @Test
    @DisplayName("Deve formatar mensagem de número positivo corretamente")
    void shouldFormatNumberMustBePositiveCorrectly() {
        // Given
        String fieldName = "valor";
        
        // When
        String message = ErrorMessages.numberMustBePositive(fieldName);
        
        // Then
        assertEquals("Campo 'valor' deve ser um número positivo", message);
    }
    
    @Test
    @DisplayName("Deve formatar mensagem de campo de ordenação inválido corretamente")
    void shouldFormatInvalidSortFieldCorrectly() {
        // Given
        String sortField = "campoInvalido";
        String validFields = "[id, nome, data]";
        
        // When
        String message = ErrorMessages.invalidSortField(sortField, validFields);
        
        // Then
        assertEquals("Campo de ordenação 'campoInvalido' não é válido. Campos válidos: [id, nome, data]", message);
    }
    
    @Test
    @DisplayName("Deve formatar mensagem de tipo de validação não suportado corretamente")
    void shouldFormatValidationTypeNotSupportedCorrectly() {
        // Given
        String validationType = "INVALID_TYPE";
        
        // When
        String message = ErrorMessages.validationTypeNotSupported(validationType);
        
        // Then
        assertEquals("Tipo de validação não suportado: INVALID_TYPE", message);
    }
    
    @Test
    @DisplayName("Deve formatar mensagem de erro de validação de paginação corretamente")
    void shouldFormatPageableValidationErrorCorrectly() {
        // Given
        String errorMessage = "Parâmetro inválido";
        
        // When
        String message = ErrorMessages.pageableValidationError(errorMessage);
        
        // Then
        assertEquals("Erro na validação de parâmetros de paginação: Parâmetro inválido", message);
    }
    
    @Test
    @DisplayName("Deve formatar mensagem genérica com múltiplos argumentos")
    void shouldFormatGenericMessageWithMultipleArguments() {
        // Given
        String template = "Campo '%s' deve estar entre %d e %d";
        String fieldName = "idade";
        int min = 18;
        int max = 65;
        
        // When
        String message = ErrorMessages.format(template, fieldName, min, max);
        
        // Then
        assertEquals("Campo 'idade' deve estar entre 18 e 65", message);
    }
    
    @Test
    @DisplayName("Deve formatar mensagem com argumento único")
    void shouldFormatMessageWithSingleArgument() {
        // Given
        String template = "Campo '%s' é obrigatório";
        String fieldName = "email";
        
        // When
        String message = ErrorMessages.format(template, fieldName);
        
        // Then
        assertEquals("Campo 'email' é obrigatório", message);
    }
    
    @Test
    @DisplayName("Deve retornar template sem formatação quando não há argumentos")
    void shouldReturnTemplateWithoutFormattingWhenNoArguments() {
        // Given
        String template = "Mensagem simples";
        
        // When
        String message = ErrorMessages.format(template);
        
        // Then
        assertEquals("Mensagem simples", message);
    }
    
    @Test
    @DisplayName("Deve verificar constantes de mensagens de erro")
    void shouldVerifyErrorMessageConstants() {
        // Then
        assertNotNull(ErrorMessages.STRING_CANNOT_BE_EMPTY);
        assertNotNull(ErrorMessages.STRING_MUST_BE_STRING);
        assertNotNull(ErrorMessages.NUMBER_MUST_BE_POSITIVE);
        assertNotNull(ErrorMessages.INVALID_SORT_FIELD);
        assertNotNull(ErrorMessages.INVALID_SORT_DIRECTION);
        assertNotNull(ErrorMessages.PAGEABLE_VALIDATED_SUCCESS);
        assertNotNull(ErrorMessages.VALIDATION_TYPE_NOT_SUPPORTED);
        
        // Verifica se as constantes contêm placeholders apropriados
        assertTrue(ErrorMessages.STRING_CANNOT_BE_EMPTY.contains("%s"));
        assertTrue(ErrorMessages.STRING_MUST_BE_STRING.contains("%s"));
        assertTrue(ErrorMessages.NUMBER_MUST_BE_POSITIVE.contains("%s"));
        assertTrue(ErrorMessages.INVALID_SORT_FIELD.contains("%s"));
    }
    
    @Test
    @DisplayName("Deve verificar constantes de mensagens de sucesso")
    void shouldVerifySuccessMessageConstants() {
        // Then
        assertNotNull(ErrorMessages.PAGE_NOT_SPECIFIED);
        assertNotNull(ErrorMessages.PAGE_NEGATIVE_CORRECTED);
        assertNotNull(ErrorMessages.PAGE_VALIDATED_SUCCESS);
        assertNotNull(ErrorMessages.SIZE_NOT_SPECIFIED);
        assertNotNull(ErrorMessages.SIZE_INVALID_CORRECTED);
        assertNotNull(ErrorMessages.SIZE_EXCEEDED_LIMIT);
        assertNotNull(ErrorMessages.SIZE_VALIDATED_SUCCESS);
        assertNotNull(ErrorMessages.SORT_FIELD_NOT_SPECIFIED);
        assertNotNull(ErrorMessages.SORT_FIELD_VALIDATED_SUCCESS);
        assertNotNull(ErrorMessages.SORT_DIRECTION_NOT_SPECIFIED);
        assertNotNull(ErrorMessages.SORT_DIRECTION_VALIDATED_SUCCESS);
        assertNotNull(ErrorMessages.PAGEABLE_VALIDATED_SUCCESS);
    }
    
    @Test
    @DisplayName("Deve verificar constantes de mensagens genéricas")
    void shouldVerifyGenericMessageConstants() {
        // Then
        assertNotNull(ErrorMessages.VALIDATION_TYPE_NOT_SUPPORTED);
        assertNotNull(ErrorMessages.FIELD_IS_OPTIONAL);
        assertNotNull(ErrorMessages.MIN_MUST_BE_LESS_OR_EQUAL_MAX);
        assertNotNull(ErrorMessages.SORT_DIRECTION_MUST_BE_ASC_OR_DESC);
        assertNotNull(ErrorMessages.SORT_FIELD_MUST_BE_STRING);
        assertNotNull(ErrorMessages.SORT_DIRECTION_MUST_BE_STRING);
    }
}
