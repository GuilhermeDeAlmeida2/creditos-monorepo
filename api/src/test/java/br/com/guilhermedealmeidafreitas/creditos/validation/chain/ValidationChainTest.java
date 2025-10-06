package br.com.guilhermedealmeidafreitas.creditos.validation.chain;

import br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers.NumberValidationHandler;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers.PageableValidationHandler;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers.StringValidationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ValidationChainTest {

    @Mock
    private StringValidationHandler stringHandler;

    @Mock
    private NumberValidationHandler numberHandler;

    @Mock
    private PageableValidationHandler pageableHandler;

    private ValidationChain validationChain;

    @BeforeEach
    void setUp() {
        // Configura mocks
        when(stringHandler.getPriority()).thenReturn(100);
        when(numberHandler.getPriority()).thenReturn(200);
        when(pageableHandler.getPriority()).thenReturn(300);
        
        when(stringHandler.getHandlerName()).thenReturn("StringValidationHandler");
        when(numberHandler.getHandlerName()).thenReturn("NumberValidationHandler");
        when(pageableHandler.getHandlerName()).thenReturn("PageableValidationHandler");
        
        // Cria a cadeia com os handlers mockados
        validationChain = new ValidationChain(List.of(stringHandler, numberHandler, pageableHandler));
    }

    @Test
    void testValidateStringNotEmpty_WithValidString_ShouldReturnSuccess() {
        // Given
        String value = "test";
        String fieldName = "testField";
        ValidationResult expectedResult = new ValidationResult("Valid", fieldName, value, "StringValidationHandler");
        
        when(stringHandler.canHandle(any())).thenReturn(true);
        when(stringHandler.handle(any())).thenReturn(expectedResult);

        // When
        ValidationResult result = validationChain.validateStringNotEmpty(value, fieldName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isTrue();
        assertThat(result.getProcessedValue()).isEqualTo(value);
    }

    @Test
    void testValidateStringNotEmpty_WithEmptyString_ShouldReturnError() {
        // Given
        String value = "";
        String fieldName = "testField";
        ValidationResult expectedResult = new ValidationResult("Field is required", fieldName, "StringValidationHandler");
        
        when(stringHandler.canHandle(any())).thenReturn(true);
        when(stringHandler.handle(any())).thenReturn(expectedResult);

        // When
        ValidationResult result = validationChain.validateStringNotEmpty(value, fieldName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isFalse();
        assertThat(result.getFirstError()).isEqualTo("Field is required");
    }

    @Test
    void testValidateStringOptional_WithNullValue_ShouldReturnSuccess() {
        // Given
        String value = null;
        String fieldName = "testField";
        ValidationResult expectedResult = new ValidationResult("Optional field is null", fieldName, null, "StringValidationHandler");
        
        when(stringHandler.canHandle(any())).thenReturn(true);
        when(stringHandler.handle(any())).thenReturn(expectedResult);

        // When
        ValidationResult result = validationChain.validateStringOptional(value, fieldName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isTrue();
        assertThat(result.getProcessedValue()).isNull();
    }

    @Test
    void testValidatePositiveNumber_WithValidNumber_ShouldReturnSuccess() {
        // Given
        int value = 42;
        String fieldName = "testField";
        ValidationResult expectedResult = new ValidationResult("Valid", fieldName, value, "NumberValidationHandler");
        
        when(numberHandler.canHandle(any())).thenReturn(true);
        when(numberHandler.handle(any())).thenReturn(expectedResult);

        // When
        ValidationResult result = validationChain.validatePositiveNumber(value, fieldName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isTrue();
        assertThat(result.getProcessedValue()).isEqualTo(value);
    }

    @Test
    void testValidatePositiveNumber_WithNegativeNumber_ShouldReturnError() {
        // Given
        int value = -1;
        String fieldName = "testField";
        ValidationResult expectedResult = new ValidationResult("Number must be positive", fieldName, "NumberValidationHandler");
        
        when(numberHandler.canHandle(any())).thenReturn(true);
        when(numberHandler.handle(any())).thenReturn(expectedResult);

        // When
        ValidationResult result = validationChain.validatePositiveNumber(value, fieldName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isFalse();
        assertThat(result.getFirstError()).isEqualTo("Number must be positive");
    }

    @Test
    void testValidateNumberRange_WithValidNumber_ShouldReturnSuccess() {
        // Given
        int value = 5;
        String fieldName = "testField";
        int min = 1;
        int max = 10;
        ValidationResult expectedResult = new ValidationResult("Valid", fieldName, value, "NumberValidationHandler");
        
        when(numberHandler.canHandle(any())).thenReturn(true);
        when(numberHandler.handle(any())).thenReturn(expectedResult);

        // When
        ValidationResult result = validationChain.validateNumberRange(value, fieldName, min, max);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isTrue();
        assertThat(result.getProcessedValue()).isEqualTo(value);
    }

    @Test
    void testValidateNumberRange_WithNumberOutOfRange_ShouldReturnError() {
        // Given
        int value = 15;
        String fieldName = "testField";
        int min = 1;
        int max = 10;
        ValidationResult expectedResult = new ValidationResult("Number out of range", fieldName, "NumberValidationHandler");
        
        when(numberHandler.canHandle(any())).thenReturn(true);
        when(numberHandler.handle(any())).thenReturn(expectedResult);

        // When
        ValidationResult result = validationChain.validateNumberRange(value, fieldName, min, max);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isFalse();
        assertThat(result.getFirstError()).isEqualTo("Number out of range");
    }

    @Test
    void testValidateAndCreatePageable_WithValidParameters_ShouldReturnSuccess() {
        // Given
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String sortDirection = "ASC";
        ValidationResult expectedResult = new ValidationResult("Valid", "pageable", null, "PageableValidationHandler");
        
        when(pageableHandler.canHandle(any())).thenReturn(true);
        when(pageableHandler.handle(any())).thenReturn(expectedResult);

        // When
        ValidationResult result = validationChain.validateAndCreatePageable(page, size, sortBy, sortDirection);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void testValidateAndCreatePageable_WithInvalidParameters_ShouldReturnError() {
        // Given
        int page = -1;
        int size = 10;
        String sortBy = "id";
        String sortDirection = "ASC";
        ValidationResult expectedResult = new ValidationResult("Invalid page parameter", "pageable", "PageableValidationHandler");
        
        when(pageableHandler.canHandle(any())).thenReturn(true);
        when(pageableHandler.handle(any())).thenReturn(expectedResult);

        // When
        ValidationResult result = validationChain.validateAndCreatePageable(page, size, sortBy, sortDirection);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isFalse();
        assertThat(result.getFirstError()).isEqualTo("Invalid page parameter");
    }

    @Test
    void testValidateSortField_WithValidField_ShouldReturnSuccess() {
        // Given
        String sortBy = "id";
        String fieldName = "sortBy";
        ValidationResult expectedResult = new ValidationResult("Valid", fieldName, sortBy, "PageableValidationHandler");
        
        when(pageableHandler.canHandle(any())).thenReturn(true);
        when(pageableHandler.handle(any())).thenReturn(expectedResult);

        // When
        ValidationResult result = validationChain.validateSortField(sortBy, fieldName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isTrue();
        assertThat(result.getProcessedValue()).isEqualTo(sortBy);
    }

    @Test
    void testValidateSortField_WithInvalidField_ShouldReturnError() {
        // Given
        String sortBy = "invalidField";
        String fieldName = "sortBy";
        ValidationResult expectedResult = new ValidationResult("Invalid sort field", fieldName, "PageableValidationHandler");
        
        when(pageableHandler.canHandle(any())).thenReturn(true);
        when(pageableHandler.handle(any())).thenReturn(expectedResult);

        // When
        ValidationResult result = validationChain.validateSortField(sortBy, fieldName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isFalse();
        assertThat(result.getFirstError()).isEqualTo("Invalid sort field");
    }

    @Test
    void testValidateSortDirection_WithValidDirection_ShouldReturnSuccess() {
        // Given
        String sortDirection = "ASC";
        String fieldName = "sortDirection";
        ValidationResult expectedResult = new ValidationResult("Valid", fieldName, sortDirection, "PageableValidationHandler");
        
        when(pageableHandler.canHandle(any())).thenReturn(true);
        when(pageableHandler.handle(any())).thenReturn(expectedResult);

        // When
        ValidationResult result = validationChain.validateSortDirection(sortDirection, fieldName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isTrue();
        assertThat(result.getProcessedValue()).isEqualTo(sortDirection);
    }

    @Test
    void testValidateSortDirection_WithInvalidDirection_ShouldReturnError() {
        // Given
        String sortDirection = "INVALID";
        String fieldName = "sortDirection";
        ValidationResult expectedResult = new ValidationResult("Invalid sort direction", fieldName, "PageableValidationHandler");
        
        when(pageableHandler.canHandle(any())).thenReturn(true);
        when(pageableHandler.handle(any())).thenReturn(expectedResult);

        // When
        ValidationResult result = validationChain.validateSortDirection(sortDirection, fieldName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isFalse();
        assertThat(result.getFirstError()).isEqualTo("Invalid sort direction");
    }

    @Test
    void testGetRegisteredHandlers_ShouldReturnHandlersInPriorityOrder() {
        // When
        List<String> handlers = validationChain.getRegisteredHandlers();

        // Then
        assertThat(handlers).hasSize(3);
        assertThat(handlers.get(0)).contains("StringValidationHandler (prioridade: 100)");
        assertThat(handlers.get(1)).contains("NumberValidationHandler (prioridade: 200)");
        assertThat(handlers.get(2)).contains("PageableValidationHandler (prioridade: 300)");
    }

    @Test
    void testGetHandlerCount_ShouldReturnCorrectCount() {
        // When
        int count = validationChain.getHandlerCount();

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    void testHasHandlers_ShouldReturnTrue() {
        // When
        boolean hasHandlers = validationChain.hasHandlers();

        // Then
        assertThat(hasHandlers).isTrue();
    }

    @Test
    void testValidate_WithNoHandlers_ShouldReturnError() {
        // Given
        ValidationChain emptyChain = new ValidationChain(List.of());
        ValidationRequest request = new ValidationRequest(ValidationType.STRING_NOT_EMPTY, "test", "testField");

        // When
        ValidationResult result = emptyChain.validate(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isFalse();
        assertThat(result.getFirstError()).contains("Nenhum handler de validação disponível");
    }
}
