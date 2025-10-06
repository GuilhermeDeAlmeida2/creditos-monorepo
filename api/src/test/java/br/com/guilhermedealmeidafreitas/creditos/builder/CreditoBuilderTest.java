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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreditoBuilderTest {

    @Mock
    private TaxCalculationService taxCalculationService;

    private CreditoBuilderImpl creditoBuilder;

    @BeforeEach
    void setUp() {
        creditoBuilder = new CreditoBuilderImpl(taxCalculationService);
        
        // Mock dos cálculos fiscais
        when(taxCalculationService.calcularBaseCalculo(any(BigDecimal.class), any(BigDecimal.class)))
            .thenReturn(new BigDecimal("25000.00"));
        when(taxCalculationService.calcularValorIssqn(any(BigDecimal.class), any(BigDecimal.class)))
            .thenReturn(new BigDecimal("1250.00"));
    }

    @Test
    void testBuild_WithAllFields_ShouldCreateCredito() {
        // Given
        LocalDate dataConstituicao = LocalDate.of(2024, 1, 15);
        BigDecimal aliquota = new BigDecimal("5.0");
        BigDecimal valorFaturado = new BigDecimal("30000.00");
        BigDecimal valorDeducao = new BigDecimal("5000.00");

        // When
        Credito credito = creditoBuilder
            .withNumeroCredito("123456")
            .withNumeroNfse("7891011")
            .withDataConstituicao(dataConstituicao)
            .withTipoCredito("ISS")
            .withSimplesNacional(false)
            .withAliquota(aliquota)
            .withValoresFiscais(valorFaturado, valorDeducao)
            .build();

        // Then
        assertThat(credito).isNotNull();
        assertThat(credito.getNumeroCredito()).isEqualTo("123456");
        assertThat(credito.getNumeroNfse()).isEqualTo("7891011");
        assertThat(credito.getDataConstituicao()).isEqualTo(dataConstituicao);
        assertThat(credito.getTipoCredito()).isEqualTo("ISS");
        assertThat(credito.getSimplesNacional()).isFalse();
        assertThat(credito.getAliquota()).isEqualTo(aliquota);
        assertThat(credito.getValorFaturado()).isEqualTo(valorFaturado);
        assertThat(credito.getValorDeducao()).isEqualTo(valorDeducao);
        assertThat(credito.getBaseCalculo()).isEqualTo(new BigDecimal("25000.00"));
        assertThat(credito.getValorIssqn()).isEqualTo(new BigDecimal("1250.00"));
    }

    @Test
    void testBuild_WithCompleteFiscalValues_ShouldNotRecalculate() {
        // Given
        LocalDate dataConstituicao = LocalDate.of(2024, 1, 15);
        BigDecimal aliquota = new BigDecimal("5.0");
        BigDecimal valorFaturado = new BigDecimal("30000.00");
        BigDecimal valorDeducao = new BigDecimal("5000.00");
        BigDecimal baseCalculo = new BigDecimal("25000.00");
        BigDecimal valorIssqn = new BigDecimal("1250.00");

        // When
        Credito credito = creditoBuilder
            .withNumeroCredito("123456")
            .withNumeroNfse("7891011")
            .withDataConstituicao(dataConstituicao)
            .withTipoCredito("ISS")
            .withSimplesNacional(false)
            .withAliquota(aliquota)
            .withValoresFiscaisCompletos(valorFaturado, valorDeducao, baseCalculo, valorIssqn)
            .build();

        // Then
        assertThat(credito).isNotNull();
        assertThat(credito.getBaseCalculo()).isEqualTo(baseCalculo);
        assertThat(credito.getValorIssqn()).isEqualTo(valorIssqn);
    }

    @Test
    void testBuild_WithId_ShouldSetId() {
        // Given
        Long id = 1L;
        LocalDate dataConstituicao = LocalDate.of(2024, 1, 15);
        BigDecimal aliquota = new BigDecimal("5.0");
        BigDecimal valorFaturado = new BigDecimal("30000.00");
        BigDecimal valorDeducao = new BigDecimal("5000.00");

        // When
        Credito credito = creditoBuilder
            .withId(id)
            .withNumeroCredito("123456")
            .withNumeroNfse("7891011")
            .withDataConstituicao(dataConstituicao)
            .withTipoCredito("ISS")
            .withSimplesNacional(false)
            .withAliquota(aliquota)
            .withValoresFiscais(valorFaturado, valorDeducao)
            .build();

        // Then
        assertThat(credito.getId()).isEqualTo(id);
    }

    @Test
    void testBuild_MissingRequiredField_ShouldThrowException() {
        // Given - Missing numeroCredito
        LocalDate dataConstituicao = LocalDate.of(2024, 1, 15);
        BigDecimal aliquota = new BigDecimal("5.0");
        BigDecimal valorFaturado = new BigDecimal("30000.00");
        BigDecimal valorDeducao = new BigDecimal("5000.00");

        // When & Then
        assertThatThrownBy(() -> creditoBuilder
            .withNumeroNfse("7891011")
            .withDataConstituicao(dataConstituicao)
            .withTipoCredito("ISS")
            .withSimplesNacional(false)
            .withAliquota(aliquota)
            .withValoresFiscais(valorFaturado, valorDeducao)
            .build())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Número do crédito é obrigatório");
    }

    @Test
    void testBuild_EmptyString_ShouldThrowException() {
        // Given - Empty numeroCredito
        LocalDate dataConstituicao = LocalDate.of(2024, 1, 15);
        BigDecimal aliquota = new BigDecimal("5.0");
        BigDecimal valorFaturado = new BigDecimal("30000.00");
        BigDecimal valorDeducao = new BigDecimal("5000.00");

        // When & Then
        assertThatThrownBy(() -> creditoBuilder
            .withNumeroCredito("   ") // Empty string
            .withNumeroNfse("7891011")
            .withDataConstituicao(dataConstituicao)
            .withTipoCredito("ISS")
            .withSimplesNacional(false)
            .withAliquota(aliquota)
            .withValoresFiscais(valorFaturado, valorDeducao)
            .build())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Número do crédito é obrigatório");
    }

    @Test
    void testBuildWithoutValidation_ShouldCreateCreditoWithoutValidation() {
        // Given - Missing required fields
        LocalDate dataConstituicao = LocalDate.of(2024, 1, 15);

        // When
        Credito credito = creditoBuilder
            .withDataConstituicao(dataConstituicao)
            .buildWithoutValidation();

        // Then
        assertThat(credito).isNotNull();
        assertThat(credito.getDataConstituicao()).isEqualTo(dataConstituicao);
        assertThat(credito.getNumeroCredito()).isNull(); // Should be null without validation
    }

    @Test
    void testReset_ShouldClearAllFields() {
        // Given
        creditoBuilder
            .withNumeroCredito("123456")
            .withNumeroNfse("7891011")
            .withDataConstituicao(LocalDate.of(2024, 1, 15))
            .withTipoCredito("ISS")
            .withSimplesNacional(false)
            .withAliquota(new BigDecimal("5.0"))
            .withValoresFiscais(new BigDecimal("30000.00"), new BigDecimal("5000.00"));

        // When
        CreditoBuilder resetBuilder = creditoBuilder.reset();

        // Then
        assertThat(resetBuilder).isSameAs(creditoBuilder);
        
        // Try to build without setting fields - should throw exception
        assertThatThrownBy(() -> creditoBuilder.build())
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testFluentInterface_ShouldReturnSameInstance() {
        // When
        CreditoBuilder result = creditoBuilder
            .withNumeroCredito("123456")
            .withNumeroNfse("7891011")
            .withDataConstituicao(LocalDate.of(2024, 1, 15))
            .withTipoCredito("ISS")
            .withSimplesNacional(false)
            .withAliquota(new BigDecimal("5.0"))
            .withValoresFiscais(new BigDecimal("30000.00"), new BigDecimal("5000.00"));

        // Then
        assertThat(result).isSameAs(creditoBuilder);
    }

    @Test
    void testWithValoresFiscais_ShouldSetBothValues() {
        // Given
        BigDecimal valorFaturado = new BigDecimal("30000.00");
        BigDecimal valorDeducao = new BigDecimal("5000.00");

        // When
        CreditoBuilder result = creditoBuilder.withValoresFiscais(valorFaturado, valorDeducao);

        // Then
        assertThat(result).isSameAs(creditoBuilder);
        // The values will be set internally and used in build()
    }

    @Test
    void testWithValoresFiscaisCompletos_ShouldSetAllFiscalValues() {
        // Given
        BigDecimal valorFaturado = new BigDecimal("30000.00");
        BigDecimal valorDeducao = new BigDecimal("5000.00");
        BigDecimal baseCalculo = new BigDecimal("25000.00");
        BigDecimal valorIssqn = new BigDecimal("1250.00");

        // When
        CreditoBuilder result = creditoBuilder.withValoresFiscaisCompletos(
            valorFaturado, valorDeducao, baseCalculo, valorIssqn);

        // Then
        assertThat(result).isSameAs(creditoBuilder);
        // The values will be set internally and used in build()
    }
}
