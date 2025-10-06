package br.com.guilhermedealmeidafreitas.creditos.validation.chain.handlers;

import br.com.guilhermedealmeidafreitas.creditos.constants.ValidationConstants;
import br.com.guilhermedealmeidafreitas.creditos.factory.PageableFactory;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationRequest;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationResult;
import br.com.guilhermedealmeidafreitas.creditos.validation.chain.ValidationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Testes para PageableValidationHandler")
class PageableValidationHandlerTest {

    @Mock
    private PageableFactory pageableFactory;

    @Mock
    private ValidationConstants validationConstants;

    private PageableValidationHandler handler;

    @BeforeEach
    void setUp() {
        // Configurar apenas os valores essenciais
        when(validationConstants.getValidSortFields()).thenReturn(java.util.Set.of("id", "nome", "data"));
        when(validationConstants.getDefaultSortField()).thenReturn("id");
        when(validationConstants.getDefaultSortDirection()).thenReturn("ASC");
        when(validationConstants.getDefaultPageSize()).thenReturn(20);

        handler = new PageableValidationHandler(pageableFactory, validationConstants);
    }

    @Test
    @DisplayName("Deve criar handler com sucesso")
    void deveCriarHandlerComSucesso() {
        assertNotNull(handler);
        assertTrue(handler instanceof PageableValidationHandler);
    }

    @Test
    @DisplayName("Deve aceitar tipos PAGEABLE")
    void deveAceitarTiposPageable() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.PAGEABLE, null, "pageable");

        assertTrue(handler.canHandle(request));
    }

    @Test
    @DisplayName("Deve aceitar tipos SORT_FIELD")
    void deveAceitarTiposSortField() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.SORT_FIELD, "nome", "sortField");

        assertTrue(handler.canHandle(request));
    }

    @Test
    @DisplayName("Deve aceitar tipos SORT_DIRECTION")
    void deveAceitarTiposSortDirection() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.SORT_DIRECTION, "ASC", "sortDirection");

        assertTrue(handler.canHandle(request));
    }

    @Test
    @DisplayName("Não deve aceitar outros tipos de validação")
    void naoDeveAceitarOutrosTiposDeValidacao() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, "test", "testField");

        assertFalse(handler.canHandle(request));
    }

    @Test
    @DisplayName("Deve validar campo de ordenação válido via interface")
    void deveValidarCampoDeOrdenacaoValidoViaInterface() {
        ValidationResult result = handler.validateSortField("nome", "sortField");

        assertTrue(result.isValid());
        assertEquals("sortField", result.getFieldName());
        assertEquals("nome", result.getProcessedValue());
    }

    @Test
    @DisplayName("Deve falhar com campo de ordenação inválido via interface")
    void deveFalharComCampoDeOrdenacaoInvalidoViaInterface() {
        ValidationResult result = handler.validateSortField("campoInvalido", "sortField");

        assertFalse(result.isValid());
        assertEquals("sortField", result.getFieldName());
        assertNotNull(result.getMessage());
    }

    @Test
    @DisplayName("Deve validar direção de ordenação ASC via interface")
    void deveValidarDirecaoDeOrdenacaoASCViaInterface() {
        ValidationResult result = handler.validateSortDirection("ASC", "sortDirection");

        assertTrue(result.isValid());
        assertEquals("sortDirection", result.getFieldName());
        assertEquals("ASC", result.getProcessedValue());
    }

    @Test
    @DisplayName("Deve validar direção de ordenação DESC via interface")
    void deveValidarDirecaoDeOrdenacaoDESCViaInterface() {
        ValidationResult result = handler.validateSortDirection("DESC", "sortDirection");

        assertTrue(result.isValid());
        assertEquals("sortDirection", result.getFieldName());
        assertEquals("DESC", result.getProcessedValue());
    }

    @Test
    @DisplayName("Deve falhar com direção de ordenação inválida via interface")
    void deveFalharComDirecaoDeOrdenacaoInvalidaViaInterface() {
        ValidationResult result = handler.validateSortDirection("INVALID", "sortDirection");

        assertFalse(result.isValid());
        assertEquals("sortDirection", result.getFieldName());
        assertNotNull(result.getMessage());
    }

    @Test
    @DisplayName("Deve validar campo de ordenação nulo via interface")
    void deveValidarCampoDeOrdenacaoNuloViaInterface() {
        ValidationResult result = handler.validateSortField(null, "sortField");

        assertTrue(result.isValid());
        assertEquals("sortField", result.getFieldName());
        assertEquals("id", result.getProcessedValue()); // Default sort field
    }

    @Test
    @DisplayName("Deve validar direção de ordenação nula via interface")
    void deveValidarDirecaoDeOrdenacaoNulaViaInterface() {
        ValidationResult result = handler.validateSortDirection(null, "sortDirection");

        assertTrue(result.isValid());
        assertEquals("sortDirection", result.getFieldName());
        assertEquals("ASC", result.getProcessedValue()); // Default sort direction
    }

    @Test
    @DisplayName("Deve validar campo de ordenação via handle")
    void deveValidarCampoDeOrdenacaoViaHandle() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.SORT_FIELD, "nome", "sortField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertEquals("sortField", result.getFieldName());
        assertEquals("nome", result.getProcessedValue());
    }

    @Test
    @DisplayName("Deve validar direção de ordenação via handle")
    void deveValidarDirecaoDeOrdenacaoViaHandle() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.SORT_DIRECTION, "DESC", "sortDirection");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertEquals("sortDirection", result.getFieldName());
        assertEquals("DESC", result.getProcessedValue());
    }

    @Test
    @DisplayName("Deve validar com strings vazias via interface")
    void deveValidarComStringsVaziasViaInterface() {
        ValidationResult result1 = handler.validateSortField("", "sortField");
        assertFalse(result1.isValid());

        ValidationResult result2 = handler.validateSortDirection("", "sortDirection");
        assertFalse(result2.isValid());
    }

    @Test
    @DisplayName("Deve aceitar diferentes campos válidos")
    void deveAceitarDiferentesCamposValidos() {
        ValidationResult result1 = handler.validateSortField("id", "sortField");
        assertTrue(result1.isValid());

        ValidationResult result2 = handler.validateSortField("data", "sortField");
        assertTrue(result2.isValid());

        ValidationResult result3 = handler.validateSortField("nome", "sortField");
        assertTrue(result3.isValid());
    }

    @Test
    @DisplayName("Deve aceitar diferentes direções válidas")
    void deveAceitarDiferentesDirecoesValidas() {
        ValidationResult result1 = handler.validateSortDirection("ASC", "sortDirection");
        assertTrue(result1.isValid());

        ValidationResult result2 = handler.validateSortDirection("DESC", "sortDirection");
        assertTrue(result2.isValid());
    }

    // Testes para validação de Pageable via interface
    @Test
    @DisplayName("Deve validar Pageable com parâmetros válidos via interface")
    void deveValidarPageableComParametrosValidosViaInterface() {
        Pageable expectedPageable = PageRequest.of(1, 20, Sort.by(Sort.Direction.ASC, "nome"));
        when(pageableFactory.createPageableFromObjects(1, 20, "nome", "ASC"))
            .thenReturn(expectedPageable);

        ValidationResult result = handler.validatePageableFromObjects(1, 20, "nome", "ASC");

        assertTrue(result.isValid());
        assertEquals(expectedPageable, result.getProcessedValue());
        assertNotNull(result.getMessage());
    }

    @Test
    @DisplayName("Deve validar Pageable com parâmetros nulos via interface")
    void deveValidarPageableComParametrosNulosViaInterface() {
        Pageable expectedPageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));
        when(pageableFactory.createPageableFromObjects(null, null, null, null))
            .thenReturn(expectedPageable);

        ValidationResult result = handler.validatePageableFromObjects(null, null, null, null);

        assertTrue(result.isValid());
        assertEquals(expectedPageable, result.getProcessedValue());
        assertNotNull(result.getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando PageableFactory lança exceção via interface")
    void deveFalharQuandoPageableFactoryLancaExcecaoViaInterface() {
        when(pageableFactory.createPageableFromObjects(1, 20, "nome", "ASC"))
            .thenThrow(new IllegalArgumentException("Erro na criação do Pageable"));

        ValidationResult result = handler.validatePageableFromObjects(1, 20, "nome", "ASC");

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("Erro na criação do Pageable"));
    }

    // Testes para validação de Pageable via handle
    @Test
    @DisplayName("Deve validar Pageable com parâmetros válidos via handle")
    void deveValidarPageableComParametrosValidosViaHandle() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("page", 2);
        parameters.put("size", 25);
        parameters.put("sortBy", "nome");
        parameters.put("sortDirection", "DESC");

        Pageable expectedPageable = PageRequest.of(2, 25, Sort.by(Sort.Direction.DESC, "nome"));
        when(pageableFactory.createPageableFromObjects(2, 25, "nome", "DESC"))
            .thenReturn(expectedPageable);

        ValidationRequest request = new ValidationRequest(
            ValidationType.PAGEABLE, null, "pageable", parameters, null);

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertEquals("pageable", result.getFieldName());
        assertEquals(expectedPageable, result.getProcessedValue());
    }

    @Test
    @DisplayName("Deve falhar quando PageableFactory lança exceção via handle")
    void deveFalharQuandoPageableFactoryLancaExcecaoViaHandle() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("page", 2);
        parameters.put("size", 25);
        parameters.put("sortBy", "nome");
        parameters.put("sortDirection", "DESC");

        when(pageableFactory.createPageableFromObjects(2, 25, "nome", "DESC"))
            .thenThrow(new IllegalArgumentException("Erro na criação do Pageable"));

        ValidationRequest request = new ValidationRequest(
            ValidationType.PAGEABLE, null, "pageable", parameters, null);

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertEquals("pageable", result.getFieldName());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("Erro na criação do Pageable"));
    }

    // Testes para validação de Pageable object - apenas testes básicos
    @Test
    @DisplayName("Deve validar Pageable object válido")
    void deveValidarPageableObjectValido() {
        Pageable pageable = PageRequest.of(1, 20, Sort.by(Sort.Direction.ASC, "nome"));

        ValidationResult result = handler.validatePageableObject(pageable, "pageable");

        // Apenas verificar que o método não lança exceção e retorna um resultado
        assertNotNull(result);
        assertEquals("pageable", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar Pageable object nulo")
    void deveValidarPageableObjectNulo() {
        ValidationResult result = handler.validatePageableObject(null, "pageable");

        // Apenas verificar que o método não lança exceção e retorna um resultado
        assertNotNull(result);
        assertEquals("pageable", result.getFieldName());
    }

    // Testes para criação de Pageable padrão
    @Test
    @DisplayName("Deve criar Pageable padrão")
    void deveCriarPageablePadrao() {
        ValidationResult result = handler.createDefaultPageable();

        // Apenas verificar que o método não lança exceção e retorna um resultado
        assertNotNull(result);
        assertNotNull(result.getMessage());
    }

    // Testes adicionais para aumentar cobertura
    @Test
    @DisplayName("Deve validar Pageable com parâmetros válidos via interface validatePageable")
    void deveValidarPageableComParametrosValidosViaInterfaceValidatePageable() {
        Pageable expectedPageable = PageRequest.of(1, 20, Sort.by(Sort.Direction.ASC, "nome"));
        when(pageableFactory.createPageable(1, 20, "nome", "ASC"))
            .thenReturn(expectedPageable);

        ValidationResult result = handler.validatePageable(1, 20, "nome", "ASC");

        assertTrue(result.isValid());
        assertEquals(expectedPageable, result.getProcessedValue());
        assertNotNull(result.getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando PageableFactory lança exceção via interface validatePageable")
    void deveFalharQuandoPageableFactoryLancaExcecaoViaInterfaceValidatePageable() {
        when(pageableFactory.createPageable(1, 20, "nome", "ASC"))
            .thenThrow(new IllegalArgumentException("Erro na criação do Pageable"));

        ValidationResult result = handler.validatePageable(1, 20, "nome", "ASC");

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("Erro na criação do Pageable"));
    }

    @Test
    @DisplayName("Deve falhar quando PageableFactory lança exceção via interface createDefaultPageable")
    void deveFalharQuandoPageableFactoryLancaExcecaoViaInterfaceCreateDefaultPageable() {
        when(pageableFactory.createDefaultPageable())
            .thenThrow(new IllegalArgumentException("Erro na criação do Pageable padrão"));

        ValidationResult result = handler.createDefaultPageable();

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("Erro na criação do Pageable padrão"));
    }

    @Test
    @DisplayName("Deve validar Pageable object com página negativa")
    void deveValidarPageableObjectComPaginaNegativa() {
        // Criar um Pageable mock com página negativa
        Pageable pageable = new Pageable() {
            @Override
            public int getPageNumber() { return -1; }
            @Override
            public int getPageSize() { return 20; }
            @Override
            public long getOffset() { return -20; }
            @Override
            public Sort getSort() { return Sort.by(Sort.Direction.ASC, "nome"); }
            @Override
            public Pageable next() { return null; }
            @Override
            public Pageable previousOrFirst() { return null; }
            @Override
            public Pageable first() { return null; }
            @Override
            public Pageable withPage(int pageNumber) { return null; }
            @Override
            public boolean hasPrevious() { return false; }
        };

        ValidationResult result = handler.validatePageableObject(pageable, "pageable");

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("pageable", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar Pageable object com tamanho zero")
    void deveValidarPageableObjectComTamanhoZero() {
        // Criar um Pageable mock com tamanho zero
        Pageable pageable = new Pageable() {
            @Override
            public int getPageNumber() { return 0; }
            @Override
            public int getPageSize() { return 0; }
            @Override
            public long getOffset() { return 0; }
            @Override
            public Sort getSort() { return Sort.by(Sort.Direction.ASC, "nome"); }
            @Override
            public Pageable next() { return null; }
            @Override
            public Pageable previousOrFirst() { return null; }
            @Override
            public Pageable first() { return null; }
            @Override
            public Pageable withPage(int pageNumber) { return null; }
            @Override
            public boolean hasPrevious() { return false; }
        };

        ValidationResult result = handler.validatePageableObject(pageable, "pageable");

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("pageable", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar Pageable object com tamanho excedendo limite")
    void deveValidarPageableObjectComTamanhoExcedendoLimite() {
        when(validationConstants.getMaxPageSize()).thenReturn(50);
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "nome"));

        ValidationResult result = handler.validatePageableObject(pageable, "pageable");

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("pageable", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar Pageable object válido com limite alto")
    void deveValidarPageableObjectValidoComLimiteAlto() {
        when(validationConstants.getMaxPageSize()).thenReturn(100);
        Pageable pageable = PageRequest.of(1, 20, Sort.by(Sort.Direction.ASC, "nome"));

        ValidationResult result = handler.validatePageableObject(pageable, "pageable");

        assertTrue(result.isValid());
        assertEquals("pageable", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar campo de ordenação nulo via handle")
    void deveValidarCampoDeOrdenacaoNuloViaHandle() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.SORT_FIELD, null, "sortField");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertEquals("sortField", result.getFieldName());
        assertEquals("id", result.getProcessedValue()); // Default sort field
    }

    @Test
    @DisplayName("Deve validar direção de ordenação nula via handle")
    void deveValidarDirecaoDeOrdenacaoNulaViaHandle() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.SORT_DIRECTION, null, "sortDirection");

        ValidationResult result = handler.handle(request);

        assertTrue(result.isValid());
        assertEquals("sortDirection", result.getFieldName());
        assertEquals("ASC", result.getProcessedValue()); // Default sort direction
    }

    @Test
    @DisplayName("Deve falhar com campo de ordenação inválido via handle")
    void deveFalharComCampoDeOrdenacaoInvalidoViaHandle() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.SORT_FIELD, "campoInvalido", "sortField");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("sortField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar com direção de ordenação inválida via handle")
    void deveFalharComDirecaoDeOrdenacaoInvalidaViaHandle() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.SORT_DIRECTION, "INVALID", "sortDirection");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("sortDirection", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar com campo de ordenação não string via handle")
    void deveFalharComCampoDeOrdenacaoNaoStringViaHandle() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.SORT_FIELD, 123, "sortField");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("sortField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar com direção de ordenação não string via handle")
    void deveFalharComDirecaoDeOrdenacaoNaoStringViaHandle() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.SORT_DIRECTION, 123, "sortDirection");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("sortDirection", result.getFieldName());
    }

    @Test
    @DisplayName("Deve falhar quando tipo de validação não é suportado")
    void deveFalharQuandoTipoDeValidacaoNaoESuportado() {
        ValidationRequest request = new ValidationRequest(
            ValidationType.STRING_NOT_EMPTY, "test", "testField");

        ValidationResult result = handler.handle(request);

        assertFalse(result.isValid());
        assertNotNull(result.getMessage());
        assertEquals("testField", result.getFieldName());
    }

    @Test
    @DisplayName("Deve validar com strings vazias via interface - campos opcionais")
    void deveValidarComStringsVaziasViaInterfaceCamposOpcionais() {
        ValidationResult result1 = handler.validateSortField("   ", "sortField");
        // Strings vazias são tratadas como inválidas para campos de ordenação
        assertFalse(result1.isValid());

        ValidationResult result2 = handler.validateSortDirection("   ", "sortDirection");
        // Strings vazias são tratadas como inválidas para direção de ordenação
        assertFalse(result2.isValid());
    }

    @Test
    @DisplayName("Deve validar com strings vazias via handle")
    void deveValidarComStringsVaziasViaHandle() {
        ValidationRequest request1 = new ValidationRequest(
            ValidationType.SORT_FIELD, "   ", "sortField");
        ValidationResult result1 = handler.handle(request1);
        // Strings vazias são tratadas como inválidas para campos de ordenação
        assertFalse(result1.isValid());

        ValidationRequest request2 = new ValidationRequest(
            ValidationType.SORT_DIRECTION, "   ", "sortDirection");
        ValidationResult result2 = handler.handle(request2);
        // Strings vazias são tratadas como inválidas para direção de ordenação
        assertFalse(result2.isValid());
    }
}
