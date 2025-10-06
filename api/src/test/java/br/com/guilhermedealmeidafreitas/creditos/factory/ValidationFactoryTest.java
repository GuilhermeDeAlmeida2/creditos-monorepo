package br.com.guilhermedealmeidafreitas.creditos.factory;

import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationChain;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ValidationFactory.
 * 
 * Este teste cobre todos os cenários da factory de validação,
 * incluindo criação de diferentes tipos de validação e métodos de conveniência.
 */
@ExtendWith(MockitoExtension.class)
class ValidationFactoryTest {

    @Mock
    private ValidationChain validationChain;

    private ValidationFactory validationFactory;

    @BeforeEach
    void setUp() {
        validationFactory = new ValidationFactory(validationChain);
    }

    @Test
    @DisplayName("Deve criar validação de string não vazia")
    void deveCriarValidacaoDeStringNaoVazia() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", ValidationFactory.TYPE_STRING_NOT_EMPTY);
        parameters.put(ValidationFactory.PARAM_VALUE, "teste");
        parameters.put(ValidationFactory.PARAM_FIELD_NAME, "nome");

        ValidationResult expectedResult = ValidationResult.Builder.success("Validação OK")
                .withFieldName("nome")
                .withProcessedValue("teste")
                .withHandlerName("StringValidationHandler")
                .build();
        when(validationChain.validateStringNotEmpty("teste", "nome")).thenReturn(expectedResult);

        // When
        ValidationResult result = validationFactory.create(parameters);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(validationChain).validateStringNotEmpty("teste", "nome");
    }

    @Test
    @DisplayName("Deve criar validação de string opcional")
    void deveCriarValidacaoDeStringOpcional() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", ValidationFactory.TYPE_STRING_OPTIONAL);
        parameters.put(ValidationFactory.PARAM_VALUE, "teste");
        parameters.put(ValidationFactory.PARAM_FIELD_NAME, "nome");

        ValidationResult expectedResult = ValidationResult.Builder.success("Validação OK")
                .withFieldName("nome")
                .withProcessedValue("teste")
                .withHandlerName("StringValidationHandler")
                .build();
        when(validationChain.validateStringOptional("teste", "nome")).thenReturn(expectedResult);

        // When
        ValidationResult result = validationFactory.create(parameters);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(validationChain).validateStringOptional("teste", "nome");
    }

    @Test
    @DisplayName("Deve criar validação de número positivo")
    void deveCriarValidacaoDeNumeroPositivo() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", ValidationFactory.TYPE_NUMBER_POSITIVE);
        parameters.put(ValidationFactory.PARAM_VALUE, 100);
        parameters.put(ValidationFactory.PARAM_FIELD_NAME, "valor");

        ValidationResult expectedResult = ValidationResult.Builder.success("Validação OK")
                .withFieldName("valor")
                .withProcessedValue(100)
                .withHandlerName("NumberValidationHandler")
                .build();
        when(validationChain.validatePositiveNumber(100, "valor")).thenReturn(expectedResult);

        // When
        ValidationResult result = validationFactory.create(parameters);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(validationChain).validatePositiveNumber(100, "valor");
    }

    @Test
    @DisplayName("Deve criar validação de número em range")
    void deveCriarValidacaoDeNumeroEmRange() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", ValidationFactory.TYPE_NUMBER_RANGE);
        parameters.put(ValidationFactory.PARAM_VALUE, 50);
        parameters.put(ValidationFactory.PARAM_FIELD_NAME, "valor");
        parameters.put(ValidationFactory.PARAM_MIN, 10);
        parameters.put(ValidationFactory.PARAM_MAX, 100);

        ValidationResult expectedResult = ValidationResult.Builder.success("Validação OK")
                .withFieldName("valor")
                .withProcessedValue(50)
                .withHandlerName("NumberValidationHandler")
                .build();
        when(validationChain.validateNumberRange(50, "valor", 10, 100)).thenReturn(expectedResult);

        // When
        ValidationResult result = validationFactory.create(parameters);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(validationChain).validateNumberRange(50, "valor", 10, 100);
    }

    @Test
    @DisplayName("Deve criar validação de campo de ordenação")
    void deveCriarValidacaoDeCampoDeOrdenacao() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", ValidationFactory.TYPE_SORT_FIELD);
        parameters.put(ValidationFactory.PARAM_VALUE, "nome");
        parameters.put(ValidationFactory.PARAM_FIELD_NAME, "sortBy");

        ValidationResult expectedResult = ValidationResult.Builder.success("Validação OK")
                .withFieldName("sortBy")
                .withProcessedValue("nome")
                .withHandlerName("SortValidationHandler")
                .build();
        when(validationChain.validateSortField("nome", "sortBy")).thenReturn(expectedResult);

        // When
        ValidationResult result = validationFactory.create(parameters);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(validationChain).validateSortField("nome", "sortBy");
    }

    @Test
    @DisplayName("Deve criar validação de direção de ordenação")
    void deveCriarValidacaoDeDirecaoDeOrdenacao() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", ValidationFactory.TYPE_SORT_DIRECTION);
        parameters.put(ValidationFactory.PARAM_VALUE, "ASC");
        parameters.put(ValidationFactory.PARAM_FIELD_NAME, "sortDirection");

        ValidationResult expectedResult = ValidationResult.Builder.success("Validação OK")
                .withFieldName("sortDirection")
                .withProcessedValue("ASC")
                .withHandlerName("SortValidationHandler")
                .build();
        when(validationChain.validateSortDirection("ASC", "sortDirection")).thenReturn(expectedResult);

        // When
        ValidationResult result = validationFactory.create(parameters);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(validationChain).validateSortDirection("ASC", "sortDirection");
    }

    @Test
    @DisplayName("Deve aceitar tipo de validação em minúsculas")
    void deveAceitarTipoDeValidacaoEmMinusculas() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "string_not_empty");
        parameters.put(ValidationFactory.PARAM_VALUE, "teste");
        parameters.put(ValidationFactory.PARAM_FIELD_NAME, "nome");

        ValidationResult expectedResult = ValidationResult.Builder.success("Validação OK")
                .withFieldName("nome")
                .withProcessedValue("teste")
                .withHandlerName("StringValidationHandler")
                .build();
        when(validationChain.validateStringNotEmpty("teste", "nome")).thenReturn(expectedResult);

        // When
        ValidationResult result = validationFactory.create(parameters);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(validationChain).validateStringNotEmpty("teste", "nome");
    }

    @Test
    @DisplayName("Deve aceitar tipo de validação com case misto")
    void deveAceitarTipoDeValidacaoComCaseMisto() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "String_Not_Empty");
        parameters.put(ValidationFactory.PARAM_VALUE, "teste");
        parameters.put(ValidationFactory.PARAM_FIELD_NAME, "nome");

        ValidationResult expectedResult = ValidationResult.Builder.success("Validação OK")
                .withFieldName("nome")
                .withProcessedValue("teste")
                .withHandlerName("StringValidationHandler")
                .build();
        when(validationChain.validateStringNotEmpty("teste", "nome")).thenReturn(expectedResult);

        // When
        ValidationResult result = validationFactory.create(parameters);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(validationChain).validateStringNotEmpty("teste", "nome");
    }

    @Test
    @DisplayName("Deve usar validação de string não vazia como padrão para tipo desconhecido")
    void deveUsarValidacaoDeStringNaoVaziaComoPadraoParaTipoDesconhecido() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "UNKNOWN_TYPE");
        parameters.put(ValidationFactory.PARAM_VALUE, "teste");
        parameters.put(ValidationFactory.PARAM_FIELD_NAME, "nome");

        ValidationResult expectedResult = ValidationResult.Builder.success("Validação OK")
                .withFieldName("nome")
                .withProcessedValue("teste")
                .withHandlerName("StringValidationHandler")
                .build();
        when(validationChain.validateStringNotEmpty("teste", "nome")).thenReturn(expectedResult);

        // When
        ValidationResult result = validationFactory.create(parameters);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(validationChain).validateStringNotEmpty("teste", "nome");
    }

    @Test
    @DisplayName("Deve lançar exceção para parâmetros nulos")
    void deveLancarExcecaoParaParametrosNulos() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validationFactory.create(null)
        );
        
        assertEquals("Parâmetros não podem ser nulos", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando parâmetro type não está presente")
    void deveLancarExcecaoQuandoParametroTypeNaoEstaPresente() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ValidationFactory.PARAM_VALUE, "teste");
        parameters.put(ValidationFactory.PARAM_FIELD_NAME, "nome");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validationFactory.create(parameters)
        );
        
        assertEquals("Parâmetro 'type' é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando parâmetro value não está presente")
    void deveLancarExcecaoQuandoParametroValueNaoEstaPresente() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", ValidationFactory.TYPE_STRING_NOT_EMPTY);
        parameters.put(ValidationFactory.PARAM_FIELD_NAME, "nome");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validationFactory.create(parameters)
        );
        
        assertEquals("Parâmetro 'value' é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando parâmetro fieldName não está presente")
    void deveLancarExcecaoQuandoParametroFieldNameNaoEstaPresente() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", ValidationFactory.TYPE_STRING_NOT_EMPTY);
        parameters.put(ValidationFactory.PARAM_VALUE, "teste");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validationFactory.create(parameters)
        );
        
        assertEquals("Parâmetro 'fieldName' é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar parâmetros suportados")
    void deveRetornarParametrosSuportados() {
        // When
        Map<String, String> supportedParameters = validationFactory.getSupportedParameters();

        // Then
        assertNotNull(supportedParameters);
        assertTrue(supportedParameters.containsKey("type"));
        assertTrue(supportedParameters.containsKey(ValidationFactory.PARAM_VALUE));
        assertTrue(supportedParameters.containsKey(ValidationFactory.PARAM_FIELD_NAME));
        assertTrue(supportedParameters.containsKey(ValidationFactory.PARAM_MIN));
        assertTrue(supportedParameters.containsKey(ValidationFactory.PARAM_MAX));
        assertTrue(supportedParameters.containsKey(ValidationFactory.PARAM_PAGE));
        assertTrue(supportedParameters.containsKey(ValidationFactory.PARAM_SIZE));
        assertTrue(supportedParameters.containsKey(ValidationFactory.PARAM_SORT_BY));
        assertTrue(supportedParameters.containsKey(ValidationFactory.PARAM_SORT_DIRECTION));
    }

    @Test
    @DisplayName("Deve criar validação de string não vazia usando método de conveniência")
    void deveCriarValidacaoDeStringNaoVaziaUsandoMetodoDeConveniencia() {
        // Given
        String value = "teste";
        String fieldName = "nome";

        ValidationResult expectedResult = ValidationResult.Builder.success("Validação OK")
                .withFieldName(fieldName)
                .withProcessedValue(value)
                .withHandlerName("StringValidationHandler")
                .build();
        when(validationChain.validateStringNotEmpty(value, fieldName)).thenReturn(expectedResult);

        // When
        ValidationResult result = validationFactory.createStringNotEmptyValidation(value, fieldName);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(validationChain).validateStringNotEmpty(value, fieldName);
    }

    @Test
    @DisplayName("Deve criar validação de string opcional usando método de conveniência")
    void deveCriarValidacaoDeStringOpcionalUsandoMetodoDeConveniencia() {
        // Given
        String value = "teste";
        String fieldName = "nome";

        ValidationResult expectedResult = ValidationResult.Builder.success("Validação OK")
                .withFieldName(fieldName)
                .withProcessedValue(value)
                .withHandlerName("StringValidationHandler")
                .build();
        when(validationChain.validateStringOptional(value, fieldName)).thenReturn(expectedResult);

        // When
        ValidationResult result = validationFactory.createStringOptionalValidation(value, fieldName);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(validationChain).validateStringOptional(value, fieldName);
    }

    @Test
    @DisplayName("Deve criar validação de número positivo usando método de conveniência")
    void deveCriarValidacaoDeNumeroPositivoUsandoMetodoDeConveniencia() {
        // Given
        Integer value = 100;
        String fieldName = "valor";

        ValidationResult expectedResult = ValidationResult.Builder.success("Validação OK")
                .withFieldName(fieldName)
                .withProcessedValue(value)
                .withHandlerName("NumberValidationHandler")
                .build();
        when(validationChain.validatePositiveNumber(value, fieldName)).thenReturn(expectedResult);

        // When
        ValidationResult result = validationFactory.createPositiveNumberValidation(value, fieldName);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(validationChain).validatePositiveNumber(value, fieldName);
    }

    @Test
    @DisplayName("Deve criar validação de número em range usando método de conveniência")
    void deveCriarValidacaoDeNumeroEmRangeUsandoMetodoDeConveniencia() {
        // Given
        Integer value = 50;
        String fieldName = "valor";
        int min = 10;
        int max = 100;

        ValidationResult expectedResult = ValidationResult.Builder.success("Validação OK")
                .withFieldName(fieldName)
                .withProcessedValue(value)
                .withHandlerName("NumberValidationHandler")
                .build();
        when(validationChain.validateNumberRange(value, fieldName, min, max)).thenReturn(expectedResult);

        // When
        ValidationResult result = validationFactory.createNumberRangeValidation(value, fieldName, min, max);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(validationChain).validateNumberRange(value, fieldName, min, max);
    }

    @Test
    @DisplayName("Deve criar validação de campo de ordenação usando método de conveniência")
    void deveCriarValidacaoDeCampoDeOrdenacaoUsandoMetodoDeConveniencia() {
        // Given
        String sortBy = "nome";
        String fieldName = "sortBy";

        ValidationResult expectedResult = ValidationResult.Builder.success("Validação OK")
                .withFieldName(fieldName)
                .withProcessedValue(sortBy)
                .withHandlerName("SortValidationHandler")
                .build();
        when(validationChain.validateSortField(sortBy, fieldName)).thenReturn(expectedResult);

        // When
        ValidationResult result = validationFactory.createSortFieldValidation(sortBy, fieldName);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(validationChain).validateSortField(sortBy, fieldName);
    }

    @Test
    @DisplayName("Deve criar validação de direção de ordenação usando método de conveniência")
    void deveCriarValidacaoDeDirecaoDeOrdenacaoUsandoMetodoDeConveniencia() {
        // Given
        String sortDirection = "DESC";
        String fieldName = "sortDirection";

        ValidationResult expectedResult = ValidationResult.Builder.success("Validação OK")
                .withFieldName(fieldName)
                .withProcessedValue(sortDirection)
                .withHandlerName("SortValidationHandler")
                .build();
        when(validationChain.validateSortDirection(sortDirection, fieldName)).thenReturn(expectedResult);

        // When
        ValidationResult result = validationFactory.createSortDirectionValidation(sortDirection, fieldName);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(validationChain).validateSortDirection(sortDirection, fieldName);
    }
}