package br.com.guilhermedealmeidafreitas.creditos.factory;

import br.com.guilhermedealmeidafreitas.creditos.exception.CreditoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreditoExceptionFactoryTest {

    private CreditoExceptionFactory factory;

    @BeforeEach
    void setUp() {
        factory = new CreditoExceptionFactory();
    }

    @Test
    void testCreate_WithValidParameters_ShouldCreateException() {
        // Given
        Map<String, Object> parameters = Map.of(
            CreditoExceptionFactory.PARAM_MESSAGE, "Test message",
            CreditoExceptionFactory.PARAM_TYPE, CreditoExceptionFactory.TYPE_VALIDATION
        );

        // When
        CreditoException exception = factory.create(parameters);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Test message");
        assertThat(exception.getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(exception.getHttpStatus()).isEqualTo(400);
    }

    @Test
    void testCreate_WithMissingMessage_ShouldThrowException() {
        // Given
        Map<String, Object> parameters = Map.of(
            CreditoExceptionFactory.PARAM_TYPE, CreditoExceptionFactory.TYPE_VALIDATION
        );

        // When & Then
        assertThatThrownBy(() -> factory.create(parameters))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Parâmetro 'message' é obrigatório");
    }

    @Test
    void testCreate_WithEmptyMessage_ShouldThrowException() {
        // Given
        Map<String, Object> parameters = Map.of(
            CreditoExceptionFactory.PARAM_MESSAGE, "   ",
            CreditoExceptionFactory.PARAM_TYPE, CreditoExceptionFactory.TYPE_VALIDATION
        );

        // When & Then
        assertThatThrownBy(() -> factory.create(parameters))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Mensagem não pode ser vazia");
    }

    @Test
    void testCreate_WithNullParameters_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> factory.create((Map<String, Object>) null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Parâmetros não podem ser nulos");
    }

    @Test
    void testCreateNotFound_ShouldCreateNotFoundException() {
        // When
        CreditoException exception = factory.createNotFound("123456", "ISS");

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).contains("Crédito não encontrado para ISS: 123456");
        assertThat(exception.getErrorCode()).isEqualTo("CREDITO_NOT_FOUND");
        assertThat(exception.getHttpStatus()).isEqualTo(404);
    }

    @Test
    void testCreateValidation_ShouldCreateValidationException() {
        // When
        CreditoException exception = factory.createValidation("Invalid field");

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Invalid field");
        assertThat(exception.getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(exception.getHttpStatus()).isEqualTo(400);
    }

    @Test
    void testCreateValidation_WithField_ShouldCreateValidationExceptionWithField() {
        // When
        CreditoException exception = factory.createValidation("Invalid value", "fieldName");

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Invalid value (campo: fieldName)");
        assertThat(exception.getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(exception.getHttpStatus()).isEqualTo(400);
    }

    @Test
    void testCreateInternalServer_ShouldCreateInternalServerException() {
        // When
        CreditoException exception = factory.createInternalServer("Internal error");

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Internal error");
        assertThat(exception.getErrorCode()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(exception.getHttpStatus()).isEqualTo(500);
    }

    @Test
    void testCreateInternalServer_WithCause_ShouldCreateInternalServerExceptionWithCause() {
        // Given
        RuntimeException cause = new RuntimeException("Root cause");

        // When
        CreditoException exception = factory.createInternalServer("Internal error", cause);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Internal error");
        assertThat(exception.getErrorCode()).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(exception.getHttpStatus()).isEqualTo(500);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void testCreateFeatureNotAvailable_ShouldCreateFeatureNotAvailableException() {
        // When
        CreditoException exception = factory.createFeatureNotAvailable("Feature not available");

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Feature not available");
        assertThat(exception.getErrorCode()).isEqualTo("FEATURE_NOT_AVAILABLE");
        assertThat(exception.getHttpStatus()).isEqualTo(403);
    }

    @Test
    void testCreateTestDataError_ShouldCreateTestDataErrorException() {
        // When
        CreditoException exception = factory.createTestDataError("Test data error", "generate");

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Test data error (operação: generate)");
        assertThat(exception.getErrorCode()).isEqualTo("TEST_DATA_ERROR");
        assertThat(exception.getHttpStatus()).isEqualTo(500);
    }

    @Test
    void testCreate_WithBusinessRuleType_ShouldCreateBusinessRuleException() {
        // Given
        Map<String, Object> parameters = Map.of(
            CreditoExceptionFactory.PARAM_MESSAGE, "Business rule violated",
            CreditoExceptionFactory.PARAM_TYPE, CreditoExceptionFactory.TYPE_BUSINESS_RULE
        );

        // When
        CreditoException exception = factory.create(parameters);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Business rule violated");
        assertThat(exception.getErrorCode()).isEqualTo("BUSINESS_RULE_VIOLATION");
        assertThat(exception.getHttpStatus()).isEqualTo(422);
    }

    @Test
    void testCreate_WithAuthorizationType_ShouldCreateAuthorizationException() {
        // Given
        Map<String, Object> parameters = Map.of(
            CreditoExceptionFactory.PARAM_MESSAGE, "Unauthorized access",
            CreditoExceptionFactory.PARAM_TYPE, CreditoExceptionFactory.TYPE_AUTHORIZATION
        );

        // When
        CreditoException exception = factory.create(parameters);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Unauthorized access");
        assertThat(exception.getErrorCode()).isEqualTo("AUTHORIZATION_ERROR");
        assertThat(exception.getHttpStatus()).isEqualTo(401);
    }

    @Test
    void testCreate_WithRateLimitType_ShouldCreateRateLimitException() {
        // Given
        Map<String, Object> parameters = Map.of(
            CreditoExceptionFactory.PARAM_MESSAGE, "Rate limit exceeded",
            CreditoExceptionFactory.PARAM_TYPE, CreditoExceptionFactory.TYPE_RATE_LIMIT
        );

        // When
        CreditoException exception = factory.create(parameters);

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Rate limit exceeded");
        assertThat(exception.getErrorCode()).isEqualTo("RATE_LIMIT_EXCEEDED");
        assertThat(exception.getHttpStatus()).isEqualTo(429);
    }

    @Test
    void testGetFactoryName_ShouldReturnCorrectName() {
        // When
        String name = factory.getFactoryName();

        // Then
        assertThat(name).isEqualTo("CreditoExceptionFactory");
    }

    @Test
    void testGetDescription_ShouldReturnCorrectDescription() {
        // When
        String description = factory.getDescription();

        // Then
        assertThat(description).isEqualTo("Factory para criação de exceções do sistema de créditos");
    }

    @Test
    void testGetProductType_ShouldReturnCorrectType() {
        // When
        Class<?> productType = factory.getProductType();

        // Then
        assertThat(productType).isEqualTo(CreditoException.class);
    }

    @Test
    void testGetSupportedParameters_ShouldReturnAllParameters() {
        // When
        Map<String, String> parameters = factory.getSupportedParameters();

        // Then
        assertThat(parameters).isNotEmpty();
        assertThat(parameters).containsKey(CreditoExceptionFactory.PARAM_MESSAGE);
        assertThat(parameters).containsKey(CreditoExceptionFactory.PARAM_TYPE);
        assertThat(parameters).containsKey(CreditoExceptionFactory.PARAM_FIELD);
        assertThat(parameters).containsKey(CreditoExceptionFactory.PARAM_OPERATION);
        assertThat(parameters).containsKey(CreditoExceptionFactory.PARAM_CAUSE);
    }

    @Test
    void testCanCreate_WithValidParameters_ShouldReturnTrue() {
        // Given
        Map<String, Object> parameters = Map.of(
            CreditoExceptionFactory.PARAM_MESSAGE, "Test message"
        );

        // When
        boolean canCreate = factory.canCreate(parameters);

        // Then
        assertThat(canCreate).isTrue();
    }

    @Test
    void testCanCreate_WithInvalidParameters_ShouldReturnFalse() {
        // Given
        Map<String, Object> parameters = Map.of(
            CreditoExceptionFactory.PARAM_TYPE, CreditoExceptionFactory.TYPE_VALIDATION
        );

        // When
        boolean canCreate = factory.canCreate(parameters);

        // Then
        assertThat(canCreate).isFalse();
    }
}
