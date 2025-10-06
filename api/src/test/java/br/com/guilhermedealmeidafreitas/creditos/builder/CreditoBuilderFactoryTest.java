package br.com.guilhermedealmeidafreitas.creditos.builder;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.service.TaxCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreditoBuilderFactoryTest {

    @Mock
    private CreditoBuilder creditoBuilder;

    @Mock
    private TaxCalculationService taxCalculationService;

    private CreditoBuilderFactory creditoBuilderFactory;

    @BeforeEach
    void setUp() {
        creditoBuilderFactory = new CreditoBuilderFactory(creditoBuilder);
        
        // Mock dos cálculos fiscais
        when(taxCalculationService.calcularBaseCalculo(any(BigDecimal.class), any(BigDecimal.class)))
            .thenReturn(new BigDecimal("25000.00"));
        when(taxCalculationService.calcularValorIssqn(any(BigDecimal.class), any(BigDecimal.class)))
            .thenReturn(new BigDecimal("1250.00"));
        
        // Mock do builder
        when(creditoBuilder.reset()).thenReturn(creditoBuilder);
        when(creditoBuilder.withNumeroCredito(any(String.class))).thenReturn(creditoBuilder);
        when(creditoBuilder.withNumeroNfse(any(String.class))).thenReturn(creditoBuilder);
        when(creditoBuilder.withDataConstituicao(any(LocalDate.class))).thenReturn(creditoBuilder);
        when(creditoBuilder.withTipoCredito(any(String.class))).thenReturn(creditoBuilder);
        when(creditoBuilder.withSimplesNacional(any(Boolean.class))).thenReturn(creditoBuilder);
        when(creditoBuilder.withAliquota(any(BigDecimal.class))).thenReturn(creditoBuilder);
        when(creditoBuilder.withValoresFiscais(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(creditoBuilder);
        when(creditoBuilder.withValoresFiscaisCompletos(any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class))).thenReturn(creditoBuilder);
        when(creditoBuilder.withId(any(Long.class))).thenReturn(creditoBuilder);
    }

    @Test
    void testNewBuilder_ShouldReturnResetBuilder() {
        // When
        CreditoBuilder result = creditoBuilderFactory.newBuilder();

        // Then
        assertThat(result).isSameAs(creditoBuilder);
    }

    @Test
    void testWithBasicData_ShouldConfigureBasicFields() {
        // Given
        String numeroCredito = "123456";
        String numeroNfse = "7891011";
        LocalDate dataConstituicao = LocalDate.of(2024, 1, 15);
        String tipoCredito = "ISS";

        // When
        CreditoBuilder result = creditoBuilderFactory.withBasicData(
            numeroCredito, numeroNfse, dataConstituicao, tipoCredito);

        // Then
        assertThat(result).isSameAs(creditoBuilder);
    }

    @Test
    void testForSimplesNacional_ShouldConfigureForSimplesNacional() {
        // Given
        String numeroCredito = "123456";
        String numeroNfse = "7891011";
        LocalDate dataConstituicao = LocalDate.of(2024, 1, 15);
        String tipoCredito = "ISS";
        BigDecimal aliquota = new BigDecimal("5.0");
        BigDecimal valorFaturado = new BigDecimal("30000.00");
        BigDecimal valorDeducao = new BigDecimal("5000.00");

        // When
        CreditoBuilder result = creditoBuilderFactory.forSimplesNacional(
            numeroCredito, numeroNfse, dataConstituicao, tipoCredito, 
            aliquota, valorFaturado, valorDeducao);

        // Then
        assertThat(result).isSameAs(creditoBuilder);
    }

    @Test
    void testForRegularTaxpayer_ShouldConfigureForRegularTaxpayer() {
        // Given
        String numeroCredito = "123456";
        String numeroNfse = "7891011";
        LocalDate dataConstituicao = LocalDate.of(2024, 1, 15);
        String tipoCredito = "ISS";
        BigDecimal aliquota = new BigDecimal("5.0");
        BigDecimal valorFaturado = new BigDecimal("30000.00");
        BigDecimal valorDeducao = new BigDecimal("5000.00");

        // When
        CreditoBuilder result = creditoBuilderFactory.forRegularTaxpayer(
            numeroCredito, numeroNfse, dataConstituicao, tipoCredito, 
            aliquota, valorFaturado, valorDeducao);

        // Then
        assertThat(result).isSameAs(creditoBuilder);
    }

    @Test
    void testForTestData_ShouldConfigureForTestData() {
        // Given
        String numeroCredito = "TESTE123456";
        String numeroNfse = "TESTE_NFSE001";
        LocalDate dataConstituicao = LocalDate.of(2024, 1, 15);
        String tipoCredito = "ISS";
        BigDecimal aliquota = new BigDecimal("5.0");
        BigDecimal valorFaturado = new BigDecimal("30000.00");
        BigDecimal valorDeducao = new BigDecimal("5000.00");

        // When
        CreditoBuilder result = creditoBuilderFactory.forTestData(
            numeroCredito, numeroNfse, dataConstituicao, tipoCredito, 
            aliquota, valorFaturado, valorDeducao);

        // Then
        assertThat(result).isSameAs(creditoBuilder);
    }

    @Test
    void testForIssCredito_ShouldConfigureForIssCredito() {
        // Given
        String numeroCredito = "123456";
        String numeroNfse = "7891011";
        LocalDate dataConstituicao = LocalDate.of(2024, 1, 15);
        BigDecimal aliquota = new BigDecimal("5.0");
        BigDecimal valorFaturado = new BigDecimal("30000.00");
        BigDecimal valorDeducao = new BigDecimal("5000.00");

        // When
        CreditoBuilder result = creditoBuilderFactory.forIssCredito(
            numeroCredito, numeroNfse, dataConstituicao, 
            aliquota, valorFaturado, valorDeducao);

        // Then
        assertThat(result).isSameAs(creditoBuilder);
    }

    @Test
    void testFromExisting_ShouldConfigureFromExistingCredito() {
        // Given
        Credito existingCredito = new Credito();
        existingCredito.setId(1L);
        existingCredito.setNumeroCredito("123456");
        existingCredito.setNumeroNfse("7891011");
        existingCredito.setDataConstituicao(LocalDate.of(2024, 1, 15));
        existingCredito.setTipoCredito("ISS");
        existingCredito.setSimplesNacional(false);
        existingCredito.setAliquota(new BigDecimal("5.0"));
        existingCredito.setValorFaturado(new BigDecimal("30000.00"));
        existingCredito.setValorDeducao(new BigDecimal("5000.00"));
        existingCredito.setBaseCalculo(new BigDecimal("25000.00"));
        existingCredito.setValorIssqn(new BigDecimal("1250.00"));

        // When
        CreditoBuilder result = creditoBuilderFactory.fromExisting(existingCredito);

        // Then
        assertThat(result).isSameAs(creditoBuilder);
    }
}
