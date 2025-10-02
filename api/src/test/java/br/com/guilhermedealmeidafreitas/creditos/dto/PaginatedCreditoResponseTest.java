package br.com.guilhermedealmeidafreitas.creditos.dto;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaginatedCreditoResponseTest {

    private PaginatedCreditoResponse response;
    private List<Credito> creditos;
    private Credito credito1;
    private Credito credito2;

    @BeforeEach
    void setUp() {
        credito1 = new Credito();
        credito1.setId(1L);
        credito1.setNumeroCredito("123456");
        credito1.setNumeroNfse("7891011");
        credito1.setDataConstituicao(LocalDate.of(2024, 2, 25));
        credito1.setValorIssqn(new BigDecimal("1500.75"));
        credito1.setTipoCredito("ISSQN");
        credito1.setSimplesNacional(true);
        credito1.setAliquota(new BigDecimal("5.0"));
        credito1.setValorFaturado(new BigDecimal("30000.00"));
        credito1.setValorDeducao(new BigDecimal("5000.00"));
        credito1.setBaseCalculo(new BigDecimal("25000.00"));

        credito2 = new Credito();
        credito2.setId(2L);
        credito2.setNumeroCredito("789012");
        credito2.setNumeroNfse("7891011");
        credito2.setDataConstituicao(LocalDate.of(2024, 2, 26));
        credito2.setValorIssqn(new BigDecimal("1200.50"));
        credito2.setTipoCredito("ISSQN");
        credito2.setSimplesNacional(false);
        credito2.setAliquota(new BigDecimal("4.5"));
        credito2.setValorFaturado(new BigDecimal("25000.00"));
        credito2.setValorDeducao(new BigDecimal("4000.00"));
        credito2.setBaseCalculo(new BigDecimal("21000.00"));

        creditos = Arrays.asList(credito1, credito2);
    }

    @Test
    @DisplayName("Should create PaginatedCreditoResponse with default constructor")
    void testDefaultConstructor() {
        // When
        response = new PaginatedCreditoResponse();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isNull();
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(0);
        assertThat(response.getTotalElements()).isEqualTo(0);
        assertThat(response.getTotalPages()).isEqualTo(0);
        assertThat(response.isFirst()).isFalse();
        assertThat(response.isLast()).isFalse();
        assertThat(response.isHasNext()).isFalse();
        assertThat(response.isHasPrevious()).isFalse();
    }

    @Test
    @DisplayName("Should create PaginatedCreditoResponse with parameterized constructor")
    void testParameterizedConstructor() {
        // When
        response = new PaginatedCreditoResponse(creditos, 0, 10, 2, 1, true, true, false, false);

        // Then
        assertThat(response.getContent()).isEqualTo(creditos);
        assertThat(response.getPage()).isEqualTo(0);
        assertThat(response.getSize()).isEqualTo(10);
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.isFirst()).isTrue();
        assertThat(response.isLast()).isTrue();
        assertThat(response.isHasNext()).isFalse();
        assertThat(response.isHasPrevious()).isFalse();
    }

    @Test
    @DisplayName("Should set and get content correctly")
    void testContentGetterAndSetter() {
        // Given
        response = new PaginatedCreditoResponse();

        // When
        response.setContent(creditos);

        // Then
        assertThat(response.getContent()).isEqualTo(creditos);
        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent().get(0)).isEqualTo(credito1);
        assertThat(response.getContent().get(1)).isEqualTo(credito2);
    }

    @Test
    @DisplayName("Should set and get page correctly")
    void testPageGetterAndSetter() {
        // Given
        response = new PaginatedCreditoResponse();

        // When
        response.setPage(2);

        // Then
        assertThat(response.getPage()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should set and get size correctly")
    void testSizeGetterAndSetter() {
        // Given
        response = new PaginatedCreditoResponse();

        // When
        response.setSize(20);

        // Then
        assertThat(response.getSize()).isEqualTo(20);
    }

    @Test
    @DisplayName("Should set and get totalElements correctly")
    void testTotalElementsGetterAndSetter() {
        // Given
        response = new PaginatedCreditoResponse();

        // When
        response.setTotalElements(100L);

        // Then
        assertThat(response.getTotalElements()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Should set and get totalPages correctly")
    void testTotalPagesGetterAndSetter() {
        // Given
        response = new PaginatedCreditoResponse();

        // When
        response.setTotalPages(5);

        // Then
        assertThat(response.getTotalPages()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should set and get first correctly")
    void testFirstGetterAndSetter() {
        // Given
        response = new PaginatedCreditoResponse();

        // When
        response.setFirst(true);

        // Then
        assertThat(response.isFirst()).isTrue();
    }

    @Test
    @DisplayName("Should set and get last correctly")
    void testLastGetterAndSetter() {
        // Given
        response = new PaginatedCreditoResponse();

        // When
        response.setLast(true);

        // Then
        assertThat(response.isLast()).isTrue();
    }

    @Test
    @DisplayName("Should set and get hasNext correctly")
    void testHasNextGetterAndSetter() {
        // Given
        response = new PaginatedCreditoResponse();

        // When
        response.setHasNext(true);

        // Then
        assertThat(response.isHasNext()).isTrue();
    }

    @Test
    @DisplayName("Should set and get hasPrevious correctly")
    void testHasPreviousGetterAndSetter() {
        // Given
        response = new PaginatedCreditoResponse();

        // When
        response.setHasPrevious(true);

        // Then
        assertThat(response.isHasPrevious()).isTrue();
    }

    @Test
    @DisplayName("Should handle empty content list")
    void testEmptyContent() {
        // When
        response = new PaginatedCreditoResponse(Collections.emptyList(), 0, 10, 0, 0, true, true, false, false);

        // Then
        assertThat(response.getContent()).isEmpty();
        assertThat(response.getTotalElements()).isEqualTo(0);
        assertThat(response.getTotalPages()).isEqualTo(0);
        assertThat(response.isFirst()).isTrue();
        assertThat(response.isLast()).isTrue();
    }

    @Test
    @DisplayName("Should handle middle page correctly")
    void testMiddlePage() {
        // When
        response = new PaginatedCreditoResponse(creditos, 1, 10, 25, 3, false, false, true, true);

        // Then
        assertThat(response.getPage()).isEqualTo(1);
        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(response.isFirst()).isFalse();
        assertThat(response.isLast()).isFalse();
        assertThat(response.isHasNext()).isTrue();
        assertThat(response.isHasPrevious()).isTrue();
    }

    @Test
    @DisplayName("Should handle last page correctly")
    void testLastPage() {
        // When
        response = new PaginatedCreditoResponse(creditos, 2, 10, 25, 3, false, true, false, true);

        // Then
        assertThat(response.getPage()).isEqualTo(2);
        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(response.isFirst()).isFalse();
        assertThat(response.isLast()).isTrue();
        assertThat(response.isHasNext()).isFalse();
        assertThat(response.isHasPrevious()).isTrue();
    }
}
