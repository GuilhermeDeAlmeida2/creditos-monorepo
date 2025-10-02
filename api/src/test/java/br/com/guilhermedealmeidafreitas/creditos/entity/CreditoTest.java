package br.com.guilhermedealmeidafreitas.creditos.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CreditoTest {

    private Credito credito;

    @BeforeEach
    void setUp() {
        credito = new Credito();
    }

    @Test
    void testConstrutorVazio() {
        assertThat(credito).isNotNull();
        assertThat(credito.getId()).isNull();
        assertThat(credito.getNumeroCredito()).isNull();
        assertThat(credito.getNumeroNfse()).isNull();
        assertThat(credito.getDataConstituicao()).isNull();
        assertThat(credito.getValorIssqn()).isNull();
        assertThat(credito.getTipoCredito()).isNull();
        assertThat(credito.getSimplesNacional()).isNull();
        assertThat(credito.getAliquota()).isNull();
        assertThat(credito.getValorFaturado()).isNull();
        assertThat(credito.getValorDeducao()).isNull();
        assertThat(credito.getBaseCalculo()).isNull();
    }

    @Test
    void testConstrutorComParametros() {
        // Given
        String numeroCredito = "123456";
        String numeroNfse = "7891011";
        LocalDate dataConstituicao = LocalDate.of(2024, 2, 25);
        BigDecimal valorIssqn = new BigDecimal("1500.75");
        String tipoCredito = "ISSQN";
        Boolean simplesNacional = true;
        BigDecimal aliquota = new BigDecimal("5.0");
        BigDecimal valorFaturado = new BigDecimal("30000.00");
        BigDecimal valorDeducao = new BigDecimal("5000.00");
        BigDecimal baseCalculo = new BigDecimal("25000.00");

        // When
        Credito creditoCompleto = new Credito(
            numeroCredito, numeroNfse, dataConstituicao, valorIssqn,
            tipoCredito, simplesNacional, aliquota, valorFaturado,
            valorDeducao, baseCalculo
        );

        // Then
        assertThat(creditoCompleto.getNumeroCredito()).isEqualTo(numeroCredito);
        assertThat(creditoCompleto.getNumeroNfse()).isEqualTo(numeroNfse);
        assertThat(creditoCompleto.getDataConstituicao()).isEqualTo(dataConstituicao);
        assertThat(creditoCompleto.getValorIssqn()).isEqualTo(valorIssqn);
        assertThat(creditoCompleto.getTipoCredito()).isEqualTo(tipoCredito);
        assertThat(creditoCompleto.getSimplesNacional()).isEqualTo(simplesNacional);
        assertThat(creditoCompleto.getAliquota()).isEqualTo(aliquota);
        assertThat(creditoCompleto.getValorFaturado()).isEqualTo(valorFaturado);
        assertThat(creditoCompleto.getValorDeducao()).isEqualTo(valorDeducao);
        assertThat(creditoCompleto.getBaseCalculo()).isEqualTo(baseCalculo);
    }

    @Test
    void testSettersEGetters() {
        // Given
        Long id = 1L;
        String numeroCredito = "123456";
        String numeroNfse = "7891011";
        LocalDate dataConstituicao = LocalDate.of(2024, 2, 25);
        BigDecimal valorIssqn = new BigDecimal("1500.75");
        String tipoCredito = "ISSQN";
        Boolean simplesNacional = true;
        BigDecimal aliquota = new BigDecimal("5.0");
        BigDecimal valorFaturado = new BigDecimal("30000.00");
        BigDecimal valorDeducao = new BigDecimal("5000.00");
        BigDecimal baseCalculo = new BigDecimal("25000.00");

        // When
        credito.setId(id);
        credito.setNumeroCredito(numeroCredito);
        credito.setNumeroNfse(numeroNfse);
        credito.setDataConstituicao(dataConstituicao);
        credito.setValorIssqn(valorIssqn);
        credito.setTipoCredito(tipoCredito);
        credito.setSimplesNacional(simplesNacional);
        credito.setAliquota(aliquota);
        credito.setValorFaturado(valorFaturado);
        credito.setValorDeducao(valorDeducao);
        credito.setBaseCalculo(baseCalculo);

        // Then
        assertThat(credito.getId()).isEqualTo(id);
        assertThat(credito.getNumeroCredito()).isEqualTo(numeroCredito);
        assertThat(credito.getNumeroNfse()).isEqualTo(numeroNfse);
        assertThat(credito.getDataConstituicao()).isEqualTo(dataConstituicao);
        assertThat(credito.getValorIssqn()).isEqualTo(valorIssqn);
        assertThat(credito.getTipoCredito()).isEqualTo(tipoCredito);
        assertThat(credito.getSimplesNacional()).isEqualTo(simplesNacional);
        assertThat(credito.getAliquota()).isEqualTo(aliquota);
        assertThat(credito.getValorFaturado()).isEqualTo(valorFaturado);
        assertThat(credito.getValorDeducao()).isEqualTo(valorDeducao);
        assertThat(credito.getBaseCalculo()).isEqualTo(baseCalculo);
    }

    @Test
    void testEquals() {
        // Given
        Credito credito1 = new Credito("123456", "7891011", LocalDate.now(), 
            new BigDecimal("1500.75"), "ISSQN", true, new BigDecimal("5.0"),
            new BigDecimal("30000.00"), new BigDecimal("5000.00"), new BigDecimal("25000.00"));
        
        Credito credito2 = new Credito("123456", "7891011", LocalDate.now(), 
            new BigDecimal("1500.75"), "ISSQN", true, new BigDecimal("5.0"),
            new BigDecimal("30000.00"), new BigDecimal("5000.00"), new BigDecimal("25000.00"));

        // When & Then
        assertThat(credito1).isEqualTo(credito2);
    }

    @Test
    void testHashCode() {
        // Given
        Credito credito1 = new Credito("123456", "7891011", LocalDate.now(), 
            new BigDecimal("1500.75"), "ISSQN", true, new BigDecimal("5.0"),
            new BigDecimal("30000.00"), new BigDecimal("5000.00"), new BigDecimal("25000.00"));
        
        Credito credito2 = new Credito("123456", "7891011", LocalDate.now(), 
            new BigDecimal("1500.75"), "ISSQN", true, new BigDecimal("5.0"),
            new BigDecimal("30000.00"), new BigDecimal("5000.00"), new BigDecimal("25000.00"));

        // When & Then
        assertThat(credito1.hashCode()).isEqualTo(credito2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        credito.setId(1L);
        credito.setNumeroCredito("123456");
        credito.setNumeroNfse("7891011");

        // When
        String toString = credito.toString();

        // Then
        assertThat(toString).isNotNull();
        assertThat(toString).contains("123456");
        assertThat(toString).contains("7891011");
    }
}
