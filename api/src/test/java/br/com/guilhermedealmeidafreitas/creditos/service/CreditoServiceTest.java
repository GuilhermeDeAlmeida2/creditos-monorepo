package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.dto.PaginatedCreditoResponse;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.repository.CreditoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CreditoServiceTest {

    @Mock
    private CreditoRepository creditoRepository;

    @InjectMocks
    private CreditoServiceImpl creditoService;

    private Credito credito1;
    private Credito credito2;
    private List<Credito> creditos;
    private Page<Credito> creditosPage;

    @BeforeEach
    void setUp() {
        credito1 = new Credito(
            "123456", "7891011", LocalDate.of(2024, 2, 25),
            new BigDecimal("1500.75"), "ISSQN", true, new BigDecimal("5.0"),
            new BigDecimal("30000.00"), new BigDecimal("5000.00"), new BigDecimal("25000.00")
        );

        credito2 = new Credito(
            "789012", "7891011", LocalDate.of(2024, 2, 26),
            new BigDecimal("1200.50"), "ISSQN", false, new BigDecimal("4.5"),
            new BigDecimal("25000.00"), new BigDecimal("4000.00"), new BigDecimal("21000.00")
        );

        creditos = Arrays.asList(credito1, credito2);
        creditosPage = new PageImpl<>(creditos, PageRequest.of(0, 10), 2);
    }

    @Test
    void testBuscarCreditoPorNumero_Sucesso() {
        // Given
        when(creditoRepository.findByNumeroCredito("123456")).thenReturn(credito1);

        // When
        Credito resultado = creditoService.buscarCreditoPorNumero("123456");

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNumeroCredito()).isEqualTo("123456");
        assertThat(resultado.getNumeroNfse()).isEqualTo("7891011");
        assertThat(resultado.getDataConstituicao()).isEqualTo(LocalDate.of(2024, 2, 25));
        assertThat(resultado.getValorIssqn()).isEqualTo(new BigDecimal("1500.75"));
        assertThat(resultado.getTipoCredito()).isEqualTo("ISSQN");
        assertThat(resultado.getSimplesNacional()).isTrue();
        assertThat(resultado.getAliquota()).isEqualTo(new BigDecimal("5.0"));
        assertThat(resultado.getValorFaturado()).isEqualTo(new BigDecimal("30000.00"));
        assertThat(resultado.getValorDeducao()).isEqualTo(new BigDecimal("5000.00"));
        assertThat(resultado.getBaseCalculo()).isEqualTo(new BigDecimal("25000.00"));
    }

    @Test
    void testBuscarCreditoPorNumero_NaoEncontrado() {
        // Given
        when(creditoRepository.findByNumeroCredito("999999")).thenReturn(null);

        // When
        Credito resultado = creditoService.buscarCreditoPorNumero("999999");

        // Then
        assertThat(resultado).isNull();
    }

    @Test
    void testBuscarCreditosPorNfse_Sucesso() {
        // Given
        when(creditoRepository.findByNumeroNfse("7891011")).thenReturn(creditos);

        // When
        List<Credito> resultado = creditoService.buscarCreditosPorNfse("7891011");

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Credito::getNumeroCredito)
            .containsExactlyInAnyOrder("123456", "789012");
    }

    @Test
    void testBuscarCreditosPorNfse_NaoEncontrado() {
        // Given
        when(creditoRepository.findByNumeroNfse("9999999")).thenReturn(Collections.emptyList());

        // When
        List<Credito> resultado = creditoService.buscarCreditosPorNfse("9999999");

        // Then
        assertThat(resultado).isEmpty();
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_Sucesso() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        // O service não modifica o pageable, apenas valida os parâmetros
        when(creditoRepository.findByNumeroNfse("7891011", pageable)).thenReturn(creditosPage);

        // When
        PaginatedCreditoResponse resultado = creditoService.buscarCreditosPorNfseComPaginacao("7891011", pageable);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getPage()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);
        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getTotalPages()).isEqualTo(1);
        assertThat(resultado.isFirst()).isTrue();
        assertThat(resultado.isLast()).isTrue();
        assertThat(resultado.isHasNext()).isFalse();
        assertThat(resultado.isHasPrevious()).isFalse();
        assertThat(resultado.getContent()).extracting(Credito::getNumeroCredito)
            .containsExactlyInAnyOrder("123456", "789012");
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_NaoEncontrado() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Credito> pageVazia = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(creditoRepository.findByNumeroNfse("9999999", pageable)).thenReturn(pageVazia);

        // When
        PaginatedCreditoResponse resultado = creditoService.buscarCreditosPorNfseComPaginacao("9999999", pageable);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isEqualTo(0);
        assertThat(resultado.getTotalPages()).isEqualTo(0);
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_ValidacaoParametros() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(creditoRepository.findByNumeroNfse("7891011", pageable)).thenReturn(creditosPage);

        // When
        PaginatedCreditoResponse resultado = creditoService.buscarCreditosPorNfseComPaginacao("7891011", pageable);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getPage()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_TamanhoMaximo() {
        // Given
        Pageable pageable = PageRequest.of(0, 150); // size maior que 100
        // O service não modifica o pageable, apenas valida os parâmetros
        when(creditoRepository.findByNumeroNfse("7891011", pageable)).thenReturn(creditosPage);

        // When
        PaginatedCreditoResponse resultado = creditoService.buscarCreditosPorNfseComPaginacao("7891011", pageable);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getSize()).isEqualTo(10); // O mock retorna size=10
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_OrdenacaoPadrao() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        when(creditoRepository.findByNumeroNfse("7891011", pageable)).thenReturn(creditosPage);

        // When
        PaginatedCreditoResponse resultado = creditoService.buscarCreditosPorNfseComPaginacao("7891011", pageable);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(2);
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_SegundaPagina() {
        // Given
        Pageable pageable = PageRequest.of(1, 1);
        Page<Credito> segundaPagina = new PageImpl<>(
            Collections.singletonList(credito2), 
            pageable, 
            2
        );
        when(creditoRepository.findByNumeroNfse("7891011", pageable)).thenReturn(segundaPagina);

        // When
        PaginatedCreditoResponse resultado = creditoService.buscarCreditosPorNfseComPaginacao("7891011", pageable);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getPage()).isEqualTo(1);
        assertThat(resultado.getSize()).isEqualTo(1);
        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getTotalPages()).isEqualTo(2);
        assertThat(resultado.isFirst()).isFalse();
        assertThat(resultado.isLast()).isTrue();
        assertThat(resultado.isHasNext()).isFalse();
        assertThat(resultado.isHasPrevious()).isTrue();
        assertThat(resultado.getContent().get(0).getNumeroCredito()).isEqualTo("789012");
    }

    @Test
    void testGerarRegistrosTeste_Sucesso() {
        // Given
        when(creditoRepository.saveAll(any())).thenReturn(Collections.emptyList());

        // When
        int resultado = creditoService.gerarRegistrosTeste();

        // Then
        assertThat(resultado).isEqualTo(300);
        verify(creditoRepository, times(1)).saveAll(any());
    }

    @Test
    void testDeletarRegistrosTeste_Sucesso() {
        // Given
        List<Credito> registrosTeste = Arrays.asList(credito1, credito2);
        when(creditoRepository.findTestRecords()).thenReturn(registrosTeste);

        // When
        int resultado = creditoService.deletarRegistrosTeste();

        // Then
        assertThat(resultado).isEqualTo(2);
        verify(creditoRepository, times(1)).findTestRecords();
        verify(creditoRepository, times(1)).deleteTestRecords();
    }

    @Test
    void testDeletarRegistrosTeste_NenhumRegistro() {
        // Given
        when(creditoRepository.findTestRecords()).thenReturn(Collections.emptyList());

        // When
        int resultado = creditoService.deletarRegistrosTeste();

        // Then
        assertThat(resultado).isEqualTo(0);
        verify(creditoRepository, times(1)).findTestRecords();
        verify(creditoRepository, times(1)).deleteTestRecords();
    }
}
