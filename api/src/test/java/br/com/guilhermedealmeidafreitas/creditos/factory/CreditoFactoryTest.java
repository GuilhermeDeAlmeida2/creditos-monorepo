package br.com.guilhermedealmeidafreitas.creditos.factory;

import br.com.guilhermedealmeidafreitas.creditos.builder.CreditoBuilderFactory;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreditoFactoryTest {

    @Mock
    private CreditoBuilderFactory creditoBuilderFactory;

    @Mock
    private br.com.guilhermedealmeidafreitas.creditos.builder.CreditoBuilder creditoBuilder;

    private CreditoFactory factory;
    private Credito mockCredito;

    @BeforeEach
    void setUp() {
        factory = new CreditoFactory(creditoBuilderFactory);
        
        // Mock do Credito
        mockCredito = new Credito(
            "TESTE001", "NFSE001", LocalDate.now(), 
            BigDecimal.valueOf(100.00), "ISS", true, 
            BigDecimal.valueOf(5.0), BigDecimal.valueOf(1000.00), 
            BigDecimal.valueOf(100.00), BigDecimal.valueOf(900.00)
        );
        
        // Mock do CreditoBuilder
        when(creditoBuilderFactory.withBasicData(anyString(), anyString(), any(LocalDate.class), anyString()))
            .thenReturn(creditoBuilder);
        when(creditoBuilderFactory.forSimplesNacional(anyString(), anyString(), any(LocalDate.class), anyString(), 
                                                     any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class)))
            .thenReturn(creditoBuilder);
        when(creditoBuilderFactory.forRegularTaxpayer(anyString(), anyString(), any(LocalDate.class), anyString(), 
                                                     any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class)))
            .thenReturn(creditoBuilder);
        when(creditoBuilderFactory.forTestData(anyString(), anyString(), any(LocalDate.class), anyString(), 
                                              any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class)))
            .thenReturn(creditoBuilder);
        when(creditoBuilderFactory.forIssCredito(anyString(), anyString(), any(LocalDate.class), 
                                                any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class)))
            .thenReturn(creditoBuilder);
        
        when(creditoBuilder.build()).thenReturn(mockCredito);
    }

    @Test
    void testCreate_WithValidParameters_ShouldCreateCredito() {
        // Given
        Map<String, Object> parameters = Map.of(
            CreditoFactory.PARAM_NUMERO_CREDITO, "TESTE001",
            CreditoFactory.PARAM_NUMERO_NFSE, "NFSE001",
            CreditoFactory.PARAM_DATA_CONSTITUICAO, LocalDate.now(),
            CreditoFactory.PARAM_TIPO_CREDITO, CreditoFactory.TYPE_BASIC
        );

        // When
        Credito credito = factory.create(parameters);

        // Then
        assertThat(credito).isNotNull();
        assertThat(credito).isEqualTo(mockCredito);
    }

    @Test
    void testCreate_WithMissingRequiredParameters_ShouldThrowException() {
        // Given
        Map<String, Object> parameters = Map.of(
            CreditoFactory.PARAM_NUMERO_CREDITO, "TESTE001"
        );

        // When & Then
        assertThatThrownBy(() -> factory.create(parameters))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Parâmetro 'numeroNfse' é obrigatório");
    }

    @Test
    void testCreate_WithEmptyNumeroCredito_ShouldThrowException() {
        // Given
        Map<String, Object> parameters = Map.of(
            CreditoFactory.PARAM_NUMERO_CREDITO, "   ",
            CreditoFactory.PARAM_NUMERO_NFSE, "NFSE001",
            CreditoFactory.PARAM_DATA_CONSTITUICAO, LocalDate.now()
        );

        // When & Then
        assertThatThrownBy(() -> factory.create(parameters))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Número do crédito não pode ser vazio");
    }

    @Test
    void testCreate_WithNullParameters_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> factory.create((Map<String, Object>) null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Parâmetros não podem ser nulos");
    }

    @Test
    void testCreate_WithInvalidDataConstituicaoType_ShouldThrowException() {
        // Given
        Map<String, Object> parameters = Map.of(
            CreditoFactory.PARAM_NUMERO_CREDITO, "TESTE001",
            CreditoFactory.PARAM_NUMERO_NFSE, "NFSE001",
            CreditoFactory.PARAM_DATA_CONSTITUICAO, "invalid-date"
        );

        // When & Then
        assertThatThrownBy(() -> factory.create(parameters))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Data de constituição deve ser do tipo LocalDate");
    }

    @Test
    void testCreateSimplesNacional_ShouldCreateSimplesNacionalCredito() {
        // Given
        String numeroCredito = "TESTE001";
        String numeroNfse = "NFSE001";
        LocalDate dataConstituicao = LocalDate.now();
        String tipoCredito = "ISS";
        BigDecimal aliquota = BigDecimal.valueOf(5.0);
        BigDecimal valorFaturado = BigDecimal.valueOf(1000.00);
        BigDecimal valorDeducao = BigDecimal.valueOf(100.00);

        // When
        Credito credito = factory.createSimplesNacional(
            numeroCredito, numeroNfse, dataConstituicao, tipoCredito,
            aliquota, valorFaturado, valorDeducao
        );

        // Then
        assertThat(credito).isNotNull();
        assertThat(credito).isEqualTo(mockCredito);
    }

    @Test
    void testCreateRegularTaxpayer_ShouldCreateRegularTaxpayerCredito() {
        // Given
        String numeroCredito = "TESTE001";
        String numeroNfse = "NFSE001";
        LocalDate dataConstituicao = LocalDate.now();
        String tipoCredito = "ISS";
        BigDecimal aliquota = BigDecimal.valueOf(5.0);
        BigDecimal valorFaturado = BigDecimal.valueOf(1000.00);
        BigDecimal valorDeducao = BigDecimal.valueOf(100.00);

        // When
        Credito credito = factory.createRegularTaxpayer(
            numeroCredito, numeroNfse, dataConstituicao, tipoCredito,
            aliquota, valorFaturado, valorDeducao
        );

        // Then
        assertThat(credito).isNotNull();
        assertThat(credito).isEqualTo(mockCredito);
    }

    @Test
    void testCreateTestData_ShouldCreateTestDataCredito() {
        // Given
        String numeroCredito = "TESTE001";
        String numeroNfse = "NFSE001";
        LocalDate dataConstituicao = LocalDate.now();
        String tipoCredito = "ISS";
        BigDecimal aliquota = BigDecimal.valueOf(5.0);
        BigDecimal valorFaturado = BigDecimal.valueOf(1000.00);
        BigDecimal valorDeducao = BigDecimal.valueOf(100.00);

        // When
        Credito credito = factory.createTestData(
            numeroCredito, numeroNfse, dataConstituicao, tipoCredito,
            aliquota, valorFaturado, valorDeducao
        );

        // Then
        assertThat(credito).isNotNull();
        assertThat(credito).isEqualTo(mockCredito);
    }

    @Test
    void testCreateIssCredito_ShouldCreateIssCredito() {
        // Given
        String numeroCredito = "TESTE001";
        String numeroNfse = "NFSE001";
        LocalDate dataConstituicao = LocalDate.now();
        BigDecimal aliquota = BigDecimal.valueOf(5.0);
        BigDecimal valorFaturado = BigDecimal.valueOf(1000.00);
        BigDecimal valorDeducao = BigDecimal.valueOf(100.00);

        // When
        Credito credito = factory.createIssCredito(
            numeroCredito, numeroNfse, dataConstituicao,
            aliquota, valorFaturado, valorDeducao
        );

        // Then
        assertThat(credito).isNotNull();
        assertThat(credito).isEqualTo(mockCredito);
    }

    @Test
    void testCreateBasic_ShouldCreateBasicCredito() {
        // Given
        String numeroCredito = "TESTE001";
        String numeroNfse = "NFSE001";
        LocalDate dataConstituicao = LocalDate.now();
        String tipoCredito = "ISS";

        // When
        Credito credito = factory.createBasic(
            numeroCredito, numeroNfse, dataConstituicao, tipoCredito
        );

        // Then
        assertThat(credito).isNotNull();
        assertThat(credito).isEqualTo(mockCredito);
    }

    @Test
    void testGetFactoryName_ShouldReturnCorrectName() {
        // When
        String name = factory.getFactoryName();

        // Then
        assertThat(name).isEqualTo("CreditoFactory");
    }

    @Test
    void testGetDescription_ShouldReturnCorrectDescription() {
        // When
        String description = factory.getDescription();

        // Then
        assertThat(description).isEqualTo("Factory para criação de objetos Credito");
    }

    @Test
    void testGetProductType_ShouldReturnCorrectType() {
        // When
        Class<?> productType = factory.getProductType();

        // Then
        assertThat(productType).isEqualTo(Credito.class);
    }

    @Test
    void testGetSupportedParameters_ShouldReturnAllParameters() {
        // When
        Map<String, String> parameters = factory.getSupportedParameters();

        // Then
        assertThat(parameters).isNotEmpty();
        assertThat(parameters).containsKey(CreditoFactory.PARAM_NUMERO_CREDITO);
        assertThat(parameters).containsKey(CreditoFactory.PARAM_NUMERO_NFSE);
        assertThat(parameters).containsKey(CreditoFactory.PARAM_DATA_CONSTITUICAO);
        assertThat(parameters).containsKey(CreditoFactory.PARAM_TIPO_CREDITO);
        assertThat(parameters).containsKey(CreditoFactory.PARAM_SIMPLES_NACIONAL);
        assertThat(parameters).containsKey(CreditoFactory.PARAM_ALIQUOTA);
        assertThat(parameters).containsKey(CreditoFactory.PARAM_VALOR_FATURADO);
        assertThat(parameters).containsKey(CreditoFactory.PARAM_VALOR_DEDUCAO);
    }

    @Test
    void testCanCreate_WithValidParameters_ShouldReturnTrue() {
        // Given
        Map<String, Object> parameters = Map.of(
            CreditoFactory.PARAM_NUMERO_CREDITO, "TESTE001",
            CreditoFactory.PARAM_NUMERO_NFSE, "NFSE001",
            CreditoFactory.PARAM_DATA_CONSTITUICAO, LocalDate.now()
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
            CreditoFactory.PARAM_NUMERO_CREDITO, "TESTE001"
        );

        // When
        boolean canCreate = factory.canCreate(parameters);

        // Then
        assertThat(canCreate).isFalse();
    }
}
