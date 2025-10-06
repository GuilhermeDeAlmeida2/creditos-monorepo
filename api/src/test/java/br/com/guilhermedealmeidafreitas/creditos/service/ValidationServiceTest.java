package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.constants.ValidationConstants;
import br.com.guilhermedealmeidafreitas.creditos.exception.CreditoExceptions;
import br.com.guilhermedealmeidafreitas.creditos.service.validation.ValidationContext;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ValidationService.
 * 
 * Este teste cobre todos os cenários do serviço de validação,
 * incluindo validações de entrada, criação de Pageable e métodos de conveniência.
 */
@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {

    @Mock
    private ValidationContext validationContext;

    @Mock
    private ValidationChain validationChain;

    @Mock
    private ValidationConstants validationConstants;

    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService(validationContext, validationChain, validationConstants);
    }

    @Test
    @DisplayName("Deve validar e criar Pageable com sucesso")
    void deveValidarECriarPageableComSucesso() {
        // Given
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String sortDirection = "ASC";
        
        Pageable expectedPageable = PageRequest.of(page, size, Sort.by("id"));
        ValidationResult successResult = ValidationResult.Builder.success("Validação OK")
                .withProcessedValue(expectedPageable)
                .build();
        
        when(validationChain.validateAndCreatePageable(page, size, sortBy, sortDirection))
                .thenReturn(successResult);

        // When
        Pageable result = validationService.validateAndCreatePageable(page, size, sortBy, sortDirection);

        // Then
        assertNotNull(result);
        assertEquals(expectedPageable, result);
        verify(validationChain).validateAndCreatePageable(page, size, sortBy, sortDirection);
    }

    @Test
    @DisplayName("Deve lançar exceção quando validação de Pageable falha")
    void deveLancarExcecaoQuandoValidacaoDePageableFalha() {
        // Given
        int page = -1;
        int size = 10;
        String sortBy = "id";
        String sortDirection = "ASC";
        
        ValidationResult errorResult = ValidationResult.Builder.error("Página deve ser maior ou igual a 0")
                .withFieldName("page")
                .build();
        
        when(validationChain.validateAndCreatePageable(page, size, sortBy, sortDirection))
                .thenReturn(errorResult);

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> validationService.validateAndCreatePageable(page, size, sortBy, sortDirection)
        );
        
        verify(validationChain).validateAndCreatePageable(page, size, sortBy, sortDirection);
    }

    @Test
    @DisplayName("Deve validar string de entrada com sucesso")
    void deveValidarStringDeEntradaComSucesso() {
        // Given
        String input = "teste";
        String fieldName = "nome";
        
        ValidationResult successResult = ValidationResult.Builder.success("Validação OK")
                .withProcessedValue(input)
                .build();
        
        when(validationChain.validateStringNotEmpty(input, fieldName))
                .thenReturn(successResult);

        // When
        String result = validationService.validateStringInput(input, fieldName);

        // Then
        assertEquals(input, result);
        verify(validationChain).validateStringNotEmpty(input, fieldName);
    }

    @Test
    @DisplayName("Deve lançar exceção quando validação de string falha")
    void deveLancarExcecaoQuandoValidacaoDeStringFalha() {
        // Given
        String input = "";
        String fieldName = "nome";
        
        ValidationResult errorResult = ValidationResult.Builder.error("Campo não pode ser vazio")
                .withFieldName(fieldName)
                .build();
        
        when(validationChain.validateStringNotEmpty(input, fieldName))
                .thenReturn(errorResult);

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> validationService.validateStringInput(input, fieldName)
        );
        
        verify(validationChain).validateStringNotEmpty(input, fieldName);
    }

    @Test
    @DisplayName("Deve validar número positivo com sucesso")
    void deveValidarNumeroPositivoComSucesso() {
        // Given
        int number = 100;
        String fieldName = "valor";
        
        ValidationResult successResult = ValidationResult.Builder.success("Validação OK")
                .withProcessedValue(number)
                .build();
        
        when(validationChain.validatePositiveNumber(number, fieldName))
                .thenReturn(successResult);

        // When
        int result = validationService.validatePositiveNumber(number, fieldName);

        // Then
        assertEquals(number, result);
        verify(validationChain).validatePositiveNumber(number, fieldName);
    }

    @Test
    @DisplayName("Deve lançar exceção quando validação de número positivo falha")
    void deveLancarExcecaoQuandoValidacaoDeNumeroPositivoFalha() {
        // Given
        int number = -5;
        String fieldName = "valor";
        
        ValidationResult errorResult = ValidationResult.Builder.error("Número deve ser positivo")
                .withFieldName(fieldName)
                .build();
        
        when(validationChain.validatePositiveNumber(number, fieldName))
                .thenReturn(errorResult);

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> validationService.validatePositiveNumber(number, fieldName)
        );
        
        verify(validationChain).validatePositiveNumber(number, fieldName);
    }

    @Test
    @DisplayName("Deve validar número em range com sucesso")
    void deveValidarNumeroEmRangeComSucesso() {
        // Given
        int number = 50;
        int min = 10;
        int max = 100;
        String fieldName = "valor";
        
        ValidationResult successResult = ValidationResult.Builder.success("Validação OK")
                .withProcessedValue(number)
                .build();
        
        when(validationChain.validateNumberRange(number, fieldName, min, max))
                .thenReturn(successResult);

        // When
        int result = validationService.validateNumberInRange(number, min, max, fieldName);

        // Then
        assertEquals(number, result);
        verify(validationChain).validateNumberRange(number, fieldName, min, max);
    }

    @Test
    @DisplayName("Deve lançar exceção quando validação de número em range falha")
    void deveLancarExcecaoQuandoValidacaoDeNumeroEmRangeFalha() {
        // Given
        int number = 150;
        int min = 10;
        int max = 100;
        String fieldName = "valor";
        
        ValidationResult errorResult = ValidationResult.Builder.error("Número deve estar entre 10 e 100")
                .withFieldName(fieldName)
                .build();
        
        when(validationChain.validateNumberRange(number, fieldName, min, max))
                .thenReturn(errorResult);

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> validationService.validateNumberInRange(number, min, max, fieldName)
        );
        
        verify(validationChain).validateNumberRange(number, fieldName, min, max);
    }

    @Test
    @DisplayName("Deve validar string opcional com sucesso")
    void deveValidarStringOpcionalComSucesso() {
        // Given
        String input = "teste";
        
        ValidationResult successResult = ValidationResult.Builder.success("Validação OK")
                .withProcessedValue(input)
                .build();
        
        when(validationChain.validateStringOptional(input, "optionalString"))
                .thenReturn(successResult);

        // When
        String result = validationService.validateOptionalStringInput(input);

        // Then
        assertEquals(input, result);
        verify(validationChain).validateStringOptional(input, "optionalString");
    }

    @Test
    @DisplayName("Deve validar string opcional nula com sucesso")
    void deveValidarStringOpcionalNulaComSucesso() {
        // Given
        String input = null;
        
        ValidationResult successResult = ValidationResult.Builder.success("Validação OK")
                .withProcessedValue(input)
                .build();
        
        when(validationChain.validateStringOptional(input, "optionalString"))
                .thenReturn(successResult);

        // When
        String result = validationService.validateOptionalStringInput(input);

        // Then
        assertNull(result);
        verify(validationChain).validateStringOptional(input, "optionalString");
    }

    @Test
    @DisplayName("Deve lançar exceção quando validação de string opcional falha")
    void deveLancarExcecaoQuandoValidacaoDeStringOpcionalFalha() {
        // Given
        String input = "   ";
        
        ValidationResult errorResult = ValidationResult.Builder.error("String não pode conter apenas espaços")
                .build();
        
        when(validationChain.validateStringOptional(input, "optionalString"))
                .thenReturn(errorResult);

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> validationService.validateOptionalStringInput(input)
        );
        
        verify(validationChain).validateStringOptional(input, "optionalString");
    }

    @Test
    @DisplayName("Deve retornar campos válidos para ordenação")
    void deveRetornarCamposValidosParaOrdenacao() {
        // Given
        Set<String> expectedFields = new HashSet<>(Arrays.asList("id", "nome", "data"));
        when(validationConstants.getValidSortFields()).thenReturn(expectedFields);

        // When
        Set<String> result = validationService.getValidSortFields();

        // Then
        assertEquals(expectedFields, result);
        verify(validationConstants).getValidSortFields();
    }

    @Test
    @DisplayName("Deve validar objeto genérico usando ValidationContext")
    void deveValidarObjetoGenericoUsandoValidationContext() {
        // Given
        Object input = "teste";
        doNothing().when(validationContext).validate(input);

        // When
        assertDoesNotThrow(() -> validationService.validate(input));

        // Then
        verify(validationContext).validate(input);
    }

    @Test
    @DisplayName("Deve retornar handlers registrados")
    void deveRetornarHandlersRegistrados() {
        // Given
        List<String> expectedHandlers = Arrays.asList("StringValidationHandler", "NumberValidationHandler");
        when(validationChain.getRegisteredHandlers()).thenReturn(expectedHandlers);

        // When
        List<String> result = validationService.getRegisteredHandlers();

        // Then
        assertEquals(expectedHandlers, result);
        verify(validationChain).getRegisteredHandlers();
    }

    @Test
    @DisplayName("Deve validar Pageable existente")
    void deveValidarPageableExistente() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        doNothing().when(validationContext).validatePageable(pageable);

        // When
        assertDoesNotThrow(() -> validationService.validatePageable(pageable));

        // Then
        verify(validationContext).validatePageable(pageable);
    }

    @Test
    @DisplayName("Deve retornar estratégias registradas")
    void deveRetornarEstrategiasRegistradas() {
        // Given
        Map<String, String> expectedStrategies = new HashMap<>();
        expectedStrategies.put("StringValidationStrategy", "Valida strings");
        expectedStrategies.put("NumberValidationStrategy", "Valida números");
        when(validationContext.getRegisteredStrategies()).thenReturn(expectedStrategies);

        // When
        Map<String, String> result = validationService.getRegisteredStrategies();

        // Then
        assertEquals(expectedStrategies, result);
        verify(validationContext).getRegisteredStrategies();
    }

    @Test
    @DisplayName("Deve retornar número de handlers registrados")
    void deveRetornarNumeroDeHandlersRegistrados() {
        // Given
        int expectedCount = 5;
        when(validationChain.getHandlerCount()).thenReturn(expectedCount);

        // When
        int result = validationService.getHandlerCount();

        // Then
        assertEquals(expectedCount, result);
        verify(validationChain).getHandlerCount();
    }

    @Test
    @DisplayName("Deve verificar se há handlers registrados")
    void deveVerificarSeHaHandlersRegistrados() {
        // Given
        when(validationChain.hasHandlers()).thenReturn(true);

        // When
        boolean result = validationService.hasHandlers();

        // Then
        assertTrue(result);
        verify(validationChain).hasHandlers();
    }

    @Test
    @DisplayName("Deve retornar false quando não há handlers registrados")
    void deveRetornarFalseQuandoNaoHaHandlersRegistrados() {
        // Given
        when(validationChain.hasHandlers()).thenReturn(false);

        // When
        boolean result = validationService.hasHandlers();

        // Then
        assertFalse(result);
        verify(validationChain).hasHandlers();
    }

    @Test
    @DisplayName("Deve converter número processado corretamente")
    void deveConverterNumeroProcessadoCorretamente() {
        // Given
        Integer number = 100;
        String fieldName = "valor";
        
        ValidationResult successResult = ValidationResult.Builder.success("Validação OK")
                .withProcessedValue(number)
                .build();
        
        when(validationChain.validatePositiveNumber(number, fieldName))
                .thenReturn(successResult);

        // When
        int result = validationService.validatePositiveNumber(number, fieldName);

        // Then
        assertEquals(100, result);
        verify(validationChain).validatePositiveNumber(number, fieldName);
    }

    @Test
    @DisplayName("Deve converter número em range processado corretamente")
    void deveConverterNumeroEmRangeProcessadoCorretamente() {
        // Given
        Integer number = 75;
        int min = 10;
        int max = 100;
        String fieldName = "valor";
        
        ValidationResult successResult = ValidationResult.Builder.success("Validação OK")
                .withProcessedValue(number)
                .build();
        
        when(validationChain.validateNumberRange(number, fieldName, min, max))
                .thenReturn(successResult);

        // When
        int result = validationService.validateNumberInRange(number, min, max, fieldName);

        // Then
        assertEquals(75, result);
        verify(validationChain).validateNumberRange(number, fieldName, min, max);
    }

    @Test
    @DisplayName("Deve retornar campos válidos vazios quando não há campos")
    void deveRetornarCamposValidosVaziosQuandoNaoHaCampos() {
        // Given
        Set<String> emptyFields = new HashSet<>();
        when(validationConstants.getValidSortFields()).thenReturn(emptyFields);

        // When
        Set<String> result = validationService.getValidSortFields();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(validationConstants).getValidSortFields();
    }

    @Test
    @DisplayName("Deve retornar lista vazia de handlers quando não há handlers")
    void deveRetornarListaVaziaDeHandlersQuandoNaoHaHandlers() {
        // Given
        List<String> emptyHandlers = new ArrayList<>();
        when(validationChain.getRegisteredHandlers()).thenReturn(emptyHandlers);

        // When
        List<String> result = validationService.getRegisteredHandlers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(validationChain).getRegisteredHandlers();
    }

    @Test
    @DisplayName("Deve retornar mapa vazio de estratégias quando não há estratégias")
    void deveRetornarMapaVazioDeEstrategiasQuandoNaoHaEstrategias() {
        // Given
        Map<String, String> emptyStrategies = new HashMap<>();
        when(validationContext.getRegisteredStrategies()).thenReturn(emptyStrategies);

        // When
        Map<String, String> result = validationService.getRegisteredStrategies();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(validationContext).getRegisteredStrategies();
    }
}
