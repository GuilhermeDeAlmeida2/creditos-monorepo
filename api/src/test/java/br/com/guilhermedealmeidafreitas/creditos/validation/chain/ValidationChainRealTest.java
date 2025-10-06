package br.com.guilhermedealmeidafreitas.creditos.validation.chain;

import br.com.guilhermedealmeidafreitas.creditos.factory.PageableFactory;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers.NumberValidationHandler;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers.PageableValidationHandler;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers.StringValidationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários para ValidationChain usando implementações reais dos handlers.
 * Estes testes verificam o comportamento real da cadeia de validação.
 */
class ValidationChainRealTest {

    private ValidationChain validationChain;

    @BeforeEach
    void setUp() {
        // Cria a cadeia com handlers reais
        PageableFactory pageableFactory = new PageableFactory();
        validationChain = new ValidationChain(List.of(
            new StringValidationHandler(),
            new NumberValidationHandler(),
            new PageableValidationHandler(pageableFactory)
        ));
    }

    @Test
    void testValidateStringNotEmpty_WithValidString_ShouldReturnSuccess() {
        // Given
        String value = "test";
        String fieldName = "testField";

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

        // When
        ValidationResult result = validationChain.validateStringNotEmpty(value, fieldName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isFalse();
        assertThat(result.getFirstError()).contains("não pode ser vazio");
    }

    @Test
    void testValidateStringOptional_WithNullValue_ShouldReturnSuccess() {
        // Given
        String value = null;
        String fieldName = "testField";

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

        // When
        ValidationResult result = validationChain.validatePositiveNumber(value, fieldName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isFalse();
        assertThat(result.getFirstError()).contains("deve ser um número positivo");
    }

    @Test
    void testValidateNumberRange_WithValidNumber_ShouldReturnSuccess() {
        // Given
        int value = 5;
        String fieldName = "testField";
        int min = 1;
        int max = 10;

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

        // When
        ValidationResult result = validationChain.validateNumberRange(value, fieldName, min, max);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isFalse();
        assertThat(result.getFirstError()).contains("deve estar entre");
    }

    @Test
    void testValidateAndCreatePageable_WithValidParameters_ShouldReturnSuccess() {
        // Given
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String sortDirection = "ASC";

        // When
        ValidationResult result = validationChain.validateAndCreatePageable(page, size, sortBy, sortDirection);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isTrue();
        assertThat(result.getProcessedValue()).isNotNull();
    }

    @Test
    void testValidateAndCreatePageable_WithInvalidParameters_ShouldCorrectAndReturnSuccess() {
        // Given
        int page = -1;
        int size = 10;
        String sortBy = "id";
        String sortDirection = "ASC";

        // When
        ValidationResult result = validationChain.validateAndCreatePageable(page, size, sortBy, sortDirection);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isTrue();
        assertThat(result.getProcessedValue()).isNotNull();
        // Verifica se a página foi corrigida para 0
        Pageable pageable = (Pageable) result.getProcessedValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
    }

    @Test
    void testValidateSortField_WithValidField_ShouldReturnSuccess() {
        // Given
        String sortBy = "id";
        String fieldName = "sortBy";

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

        // When
        ValidationResult result = validationChain.validateSortField(sortBy, fieldName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isFalse();
        assertThat(result.getFirstError()).contains("não é válido");
    }

    @Test
    void testValidateSortDirection_WithValidDirection_ShouldReturnSuccess() {
        // Given
        String sortDirection = "ASC";
        String fieldName = "sortDirection";

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

        // When
        ValidationResult result = validationChain.validateSortDirection(sortDirection, fieldName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isFalse();
        assertThat(result.getFirstError()).contains("deve ser 'ASC' ou 'DESC'");
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
