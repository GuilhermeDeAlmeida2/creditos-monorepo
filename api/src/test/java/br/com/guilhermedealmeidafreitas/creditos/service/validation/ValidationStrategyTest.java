package br.com.guilhermedealmeidafreitas.creditos.service.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class ValidationStrategyTest {

    private StringValidationStrategy stringValidationStrategy;
    private NumberValidationStrategy numberValidationStrategy;
    private PageableValidationStrategy pageableValidationStrategy;
    private ValidationContext validationContext;

    @BeforeEach
    void setUp() {
        stringValidationStrategy = new StringValidationStrategy();
        numberValidationStrategy = new NumberValidationStrategy();
        pageableValidationStrategy = new PageableValidationStrategy();
        validationContext = new ValidationContext(
            java.util.List.of(stringValidationStrategy, numberValidationStrategy, pageableValidationStrategy),
            stringValidationStrategy,
            numberValidationStrategy,
            pageableValidationStrategy
        );
    }

    @Test
    void testStringValidationStrategy_ValidInput() {
        // Given
        String validInput = "test string";

        // When & Then
        assertThat(stringValidationStrategy.supports(String.class)).isTrue();
        assertThat(stringValidationStrategy.getStrategyName()).isEqualTo("StringValidation");
        
        // Should not throw exception
        stringValidationStrategy.validate(validInput);
    }

    @Test
    void testStringValidationStrategy_NullInput() {
        // Given
        String nullInput = null;

        // When & Then
        assertThatThrownBy(() -> stringValidationStrategy.validate(nullInput))
            .isInstanceOf(ValidationException.class)
            .hasMessage("Campo não pode ser nulo");
    }

    @Test
    void testStringValidationStrategy_EmptyInput() {
        // Given
        String emptyInput = "   ";

        // When & Then
        assertThatThrownBy(() -> stringValidationStrategy.validate(emptyInput))
            .isInstanceOf(ValidationException.class)
            .hasMessage("Campo não pode ser vazio");
    }

    @Test
    void testStringValidationStrategy_WithFieldName() {
        // Given
        String nullInput = null;
        String fieldName = "numeroCredito";

        // When & Then
        assertThatThrownBy(() -> stringValidationStrategy.validate(nullInput, fieldName))
            .isInstanceOf(ValidationException.class)
            .hasMessage("numeroCredito não pode ser nulo");
    }

    @Test
    void testStringValidationStrategy_OptionalValidation() {
        // Given
        String nullInput = null;
        String emptyInput = "   ";
        String validInput = "  test  ";

        // When & Then
        assertThat(stringValidationStrategy.validateOptional(nullInput)).isNull();
        assertThat(stringValidationStrategy.validateOptional(emptyInput)).isNull();
        assertThat(stringValidationStrategy.validateOptional(validInput)).isEqualTo("test");
    }

    @Test
    void testNumberValidationStrategy_ValidInput() {
        // Given
        Integer validNumber = 42;

        // When & Then
        assertThat(numberValidationStrategy.supports(Integer.class)).isTrue();
        assertThat(numberValidationStrategy.getStrategyName()).isEqualTo("NumberValidation");
        
        // Should not throw exception
        numberValidationStrategy.validate(validNumber);
    }

    @Test
    void testNumberValidationStrategy_NullInput() {
        // Given
        Integer nullNumber = null;

        // When & Then
        assertThatThrownBy(() -> numberValidationStrategy.validate(nullNumber))
            .isInstanceOf(ValidationException.class)
            .hasMessage("Número não pode ser nulo");
    }

    @Test
    void testNumberValidationStrategy_PositiveValidation() {
        // Given
        Integer negativeNumber = -5;
        String fieldName = "page";

        // When & Then
        assertThatThrownBy(() -> numberValidationStrategy.validatePositive(negativeNumber, fieldName))
            .isInstanceOf(ValidationException.class)
            .hasMessage("page deve ser um número positivo");
    }

    @Test
    void testNumberValidationStrategy_RangeValidation() {
        // Given
        Integer outOfRangeNumber = 150;
        String fieldName = "size";

        // When & Then
        assertThatThrownBy(() -> numberValidationStrategy.validateRange(outOfRangeNumber, 1, 100, fieldName))
            .isInstanceOf(ValidationException.class)
            .hasMessage("size deve estar entre 1 e 100");
    }

    @Test
    void testPageableValidationStrategy_ValidInput() {
        // Given
        Pageable validPageable = PageRequest.of(0, 10);

        // When & Then
        assertThat(pageableValidationStrategy.supports(Pageable.class)).isTrue();
        assertThat(pageableValidationStrategy.getStrategyName()).isEqualTo("PageableValidation");
        
        // Should not throw exception
        pageableValidationStrategy.validate(validPageable);
    }

    @Test
    void testPageableValidationStrategy_InvalidPageSize() {
        // Given
        Pageable invalidPageable = PageRequest.of(0, 150);

        // When & Then
        assertThatThrownBy(() -> pageableValidationStrategy.validate(invalidPageable))
            .isInstanceOf(ValidationException.class)
            .hasMessage("Tamanho da página não pode ser maior que 100");
    }

    @Test
    void testPageableValidationStrategy_ValidateAndCreate() {
        // Given
        int page = 0;
        int size = 10;
        String sortBy = "dataConstituicao";
        String sortDirection = "desc";

        // When
        Pageable result = pageableValidationStrategy.validateAndCreatePageable(page, size, sortBy, sortDirection);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(10);
    }

    @Test
    void testValidationContext_StringValidation() {
        // Given
        String validInput = "test";
        String nullInput = null;

        // When & Then
        validationContext.validate(validInput); // Should not throw
        
        assertThatThrownBy(() -> validationContext.validate(nullInput))
            .isInstanceOf(ValidationException.class);
    }

    @Test
    void testValidationContext_ConvenienceMethods() {
        // Given
        String validString = "test";
        String fieldName = "numeroCredito";
        Integer validNumber = 5;

        // When & Then
        validationContext.validateString(validString, fieldName); // Should not throw
        validationContext.validatePositiveNumber(validNumber, fieldName); // Should not throw
        
        assertThat(validationContext.validateOptionalString("  test  ")).isEqualTo("test");
        assertThat(validationContext.validateOptionalString(null)).isNull();
    }

    @Test
    void testValidationContext_RegisteredStrategies() {
        // When
        var strategies = validationContext.getRegisteredStrategies();

        // Then
        assertThat(strategies).isNotEmpty();
        assertThat(strategies).containsKey("String");
        assertThat(strategies).containsKey("Number");
        assertThat(strategies).containsKey("Pageable");
    }
}
