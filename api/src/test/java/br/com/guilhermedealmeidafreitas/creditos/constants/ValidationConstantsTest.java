package br.com.guilhermedealmeidafreitas.creditos.constants;

import br.com.guilhermedealmeidafreitas.creditos.config.ValidationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("Testes para ValidationConstants")
class ValidationConstantsTest {

    @Mock
    private ValidationConfig validationConfig;

    @Mock
    private ValidationConfig.Pagination pagination;

    @Mock
    private ValidationConfig.StringValidation stringValidation;

    @Mock
    private ValidationConfig.NumberValidation numberValidation;

    @Mock
    private ValidationConfig.SortFields sortFields;

    private ValidationConstants validationConstants;

    @BeforeEach
    void setUp() {
        // Configurar mocks
        when(validationConfig.getPagination()).thenReturn(pagination);
        when(validationConfig.getStringValidation()).thenReturn(stringValidation);
        when(validationConfig.getNumberValidation()).thenReturn(numberValidation);
        when(validationConfig.getSortFields()).thenReturn(sortFields);

        // Configurar valores padrão para os mocks
        when(pagination.getDefaultPageSize()).thenReturn(20);
        when(pagination.getMaxPageSize()).thenReturn(100);
        when(pagination.getMinPageSize()).thenReturn(1);
        when(pagination.getDefaultPage()).thenReturn(0);

        when(stringValidation.getDefaultMinLength()).thenReturn(1);
        when(stringValidation.getDefaultMaxLength()).thenReturn(255);
        when(stringValidation.isTrimEnabled()).thenReturn(true);
        when(stringValidation.isAllowEmpty()).thenReturn(false);

        when(numberValidation.isAllowNegative()).thenReturn(false);
        when(numberValidation.isAllowZero()).thenReturn(true);
        when(numberValidation.getDefaultPrecision()).thenReturn(2);
        when(numberValidation.getMaxPrecision()).thenReturn(10);

        when(sortFields.getValidSortFields()).thenReturn(Set.of("id", "nome", "data"));
        when(sortFields.getDefaultSortField()).thenReturn("id");
        when(sortFields.getDefaultSortDirection()).thenReturn("ASC");

        validationConstants = new ValidationConstants(validationConfig);
    }

    @Test
    @DisplayName("Deve criar ValidationConstants com sucesso")
    void deveCriarValidationConstantsComSucesso() {
        assertNotNull(validationConstants);
        assertTrue(validationConstants instanceof ValidationConstants);
    }

    @Test
    @DisplayName("Deve retornar campos válidos para ordenação")
    void deveRetornarCamposValidosParaOrdenacao() {
        Set<String> validSortFields = validationConstants.getValidSortFields();

        assertNotNull(validSortFields);
        assertEquals(3, validSortFields.size());
        assertTrue(validSortFields.contains("id"));
        assertTrue(validSortFields.contains("nome"));
        assertTrue(validSortFields.contains("data"));
    }

    @Test
    @DisplayName("Deve retornar tamanho padrão da página")
    void deveRetornarTamanhoPadraoDaPagina() {
        int defaultPageSize = validationConstants.getDefaultPageSize();

        assertEquals(20, defaultPageSize);
    }

    @Test
    @DisplayName("Deve retornar tamanho máximo da página")
    void deveRetornarTamanhoMaximoDaPagina() {
        int maxPageSize = validationConstants.getMaxPageSize();

        assertEquals(100, maxPageSize);
    }

    @Test
    @DisplayName("Deve retornar tamanho mínimo da página")
    void deveRetornarTamanhoMinimoDaPagina() {
        int minPageSize = validationConstants.getMinPageSize();

        assertEquals(1, minPageSize);
    }

    @Test
    @DisplayName("Deve retornar página padrão")
    void deveRetornarPaginaPadrao() {
        int defaultPage = validationConstants.getDefaultPage();

        assertEquals(0, defaultPage);
    }

    @Test
    @DisplayName("Deve retornar campo padrão para ordenação")
    void deveRetornarCampoPadraoParaOrdenacao() {
        String defaultSortField = validationConstants.getDefaultSortField();

        assertEquals("id", defaultSortField);
    }

    @Test
    @DisplayName("Deve retornar direção padrão para ordenação")
    void deveRetornarDirecaoPadraoParaOrdenacao() {
        String defaultSortDirection = validationConstants.getDefaultSortDirection();

        assertEquals("ASC", defaultSortDirection);
    }

    @Test
    @DisplayName("Deve retornar comprimento mínimo padrão para strings")
    void deveRetornarComprimentoMinimoPadraoParaStrings() {
        int defaultMinLength = validationConstants.getDefaultMinLength();

        assertEquals(1, defaultMinLength);
    }

    @Test
    @DisplayName("Deve retornar comprimento máximo padrão para strings")
    void deveRetornarComprimentoMaximoPadraoParaStrings() {
        int defaultMaxLength = validationConstants.getDefaultMaxLength();

        assertEquals(255, defaultMaxLength);
    }

    @Test
    @DisplayName("Deve retornar se trim está habilitado para strings")
    void deveRetornarSeTrimEstaHabilitadoParaStrings() {
        boolean trimEnabled = validationConstants.isTrimEnabled();

        assertTrue(trimEnabled);
    }

    @Test
    @DisplayName("Deve retornar se strings vazias são permitidas")
    void deveRetornarSeStringsVaziasSaoPermitidas() {
        boolean allowEmpty = validationConstants.isAllowEmpty();

        assertFalse(allowEmpty);
    }

    @Test
    @DisplayName("Deve retornar se números negativos são permitidos")
    void deveRetornarSeNumerosNegativosSaoPermitidos() {
        boolean allowNegative = validationConstants.isAllowNegative();

        assertFalse(allowNegative);
    }

    @Test
    @DisplayName("Deve retornar se zero é permitido em números")
    void deveRetornarSeZeroEPermitidoEmNumeros() {
        boolean allowZero = validationConstants.isAllowZero();

        assertTrue(allowZero);
    }

    @Test
    @DisplayName("Deve retornar precisão padrão para números decimais")
    void deveRetornarPrecisaoPadraoParaNumerosDecimais() {
        int defaultPrecision = validationConstants.getDefaultPrecision();

        assertEquals(2, defaultPrecision);
    }

    @Test
    @DisplayName("Deve retornar precisão máxima para números decimais")
    void deveRetornarPrecisaoMaximaParaNumerosDecimais() {
        int maxPrecision = validationConstants.getMaxPrecision();

        assertEquals(10, maxPrecision);
    }

    @Test
    @DisplayName("Deve usar configurações diferentes quando alteradas")
    void deveUsarConfiguracoesDiferentesQuandoAlteradas() {
        // Alterar configurações
        when(pagination.getDefaultPageSize()).thenReturn(50);
        when(stringValidation.isTrimEnabled()).thenReturn(false);
        when(numberValidation.isAllowNegative()).thenReturn(true);

        // Criar nova instância para simular mudança de configuração
        ValidationConstants newConstants = new ValidationConstants(validationConfig);

        assertEquals(50, newConstants.getDefaultPageSize());
        assertFalse(newConstants.isTrimEnabled());
        assertTrue(newConstants.isAllowNegative());
    }

    @Test
    @DisplayName("Deve retornar valores consistentes entre chamadas")
    void deveRetornarValoresConsistentesEntreChamadas() {
        // Chamar múltiplas vezes
        int firstCall = validationConstants.getDefaultPageSize();
        int secondCall = validationConstants.getDefaultPageSize();
        int thirdCall = validationConstants.getDefaultPageSize();

        assertEquals(firstCall, secondCall);
        assertEquals(secondCall, thirdCall);
        assertEquals(20, firstCall);
    }

    @Test
    @DisplayName("Deve retornar configurações de paginação corretas")
    void deveRetornarConfiguracoesDePaginacaoCorretas() {
        assertEquals(20, validationConstants.getDefaultPageSize());
        assertEquals(100, validationConstants.getMaxPageSize());
        assertEquals(1, validationConstants.getMinPageSize());
        assertEquals(0, validationConstants.getDefaultPage());
    }

    @Test
    @DisplayName("Deve retornar configurações de string corretas")
    void deveRetornarConfiguracoesDeStringCorretas() {
        assertEquals(1, validationConstants.getDefaultMinLength());
        assertEquals(255, validationConstants.getDefaultMaxLength());
        assertTrue(validationConstants.isTrimEnabled());
        assertFalse(validationConstants.isAllowEmpty());
    }

    @Test
    @DisplayName("Deve retornar configurações de número corretas")
    void deveRetornarConfiguracoesDeNumeroCorretas() {
        assertFalse(validationConstants.isAllowNegative());
        assertTrue(validationConstants.isAllowZero());
        assertEquals(2, validationConstants.getDefaultPrecision());
        assertEquals(10, validationConstants.getMaxPrecision());
    }

    @Test
    @DisplayName("Deve retornar configurações de ordenação corretas")
    void deveRetornarConfiguracoesDeOrdenacaoCorretas() {
        Set<String> validSortFields = validationConstants.getValidSortFields();
        assertEquals("id", validationConstants.getDefaultSortField());
        assertEquals("ASC", validationConstants.getDefaultSortDirection());
        
        assertNotNull(validSortFields);
        assertTrue(validSortFields.contains("id"));
        assertTrue(validSortFields.contains("nome"));
        assertTrue(validSortFields.contains("data"));
    }

    @Test
    @DisplayName("Deve lidar com conjuntos vazios de campos de ordenação")
    void deveLidarComConjuntosVaziosDeCamposDeOrdenacao() {
        when(sortFields.getValidSortFields()).thenReturn(Set.of());

        Set<String> validSortFields = validationConstants.getValidSortFields();

        assertNotNull(validSortFields);
        assertTrue(validSortFields.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar valores booleanos corretos")
    void deveRetornarValoresBooleanosCorretos() {
        assertTrue(validationConstants.isTrimEnabled());
        assertFalse(validationConstants.isAllowEmpty());
        assertFalse(validationConstants.isAllowNegative());
        assertTrue(validationConstants.isAllowZero());
    }

    @Test
    @DisplayName("Deve retornar valores numéricos corretos")
    void deveRetornarValoresNumericosCorretos() {
        assertEquals(20, validationConstants.getDefaultPageSize());
        assertEquals(100, validationConstants.getMaxPageSize());
        assertEquals(1, validationConstants.getMinPageSize());
        assertEquals(0, validationConstants.getDefaultPage());
        assertEquals(1, validationConstants.getDefaultMinLength());
        assertEquals(255, validationConstants.getDefaultMaxLength());
        assertEquals(2, validationConstants.getDefaultPrecision());
        assertEquals(10, validationConstants.getMaxPrecision());
    }
}