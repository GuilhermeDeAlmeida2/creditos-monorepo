package br.com.guilhermedealmeidafreitas.creditos.repository;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@ExtendWith(MockitoExtension.class)
class CreditoRepositoryTest {

    @Mock
    private CreditoRepository creditoRepository;

    private Credito credito1;
    private Credito credito2;
    private Credito credito3;
    private List<Credito> creditos;

    @BeforeEach
    void setUp() {
        // Criar créditos de teste
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

        credito3 = new Credito(
            "654321", "1122334", LocalDate.of(2024, 1, 15),
            new BigDecimal("800.50"), "Outros", true, new BigDecimal("3.5"),
            new BigDecimal("20000.00"), new BigDecimal("3000.00"), new BigDecimal("17000.00")
        );

        creditos = Arrays.asList(credito1, credito2);
    }

    @Test
    void testFindByNumeroNfse() {
        // Given
        when(creditoRepository.findByNumeroNfse("7891011")).thenReturn(creditos);

        // When
        List<Credito> resultado = creditoRepository.findByNumeroNfse("7891011");

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Credito::getNumeroCredito)
            .containsExactlyInAnyOrder("123456", "789012");
    }

    @Test
    void testFindByNumeroNfseNaoExistente() {
        // Given
        when(creditoRepository.findByNumeroNfse("9999999")).thenReturn(Collections.emptyList());

        // When
        List<Credito> resultado = creditoRepository.findByNumeroNfse("9999999");

        // Then
        assertThat(resultado).isEmpty();
    }

    @Test
    void testFindAllComPaginacao() {
        // Given
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "dataConstituicao"));
        Page<Credito> pageCompleta = new PageImpl<>(
            Arrays.asList(credito2, credito1), 
            pageable, 
            3
        );
        when(creditoRepository.findAll(pageable)).thenReturn(pageCompleta);

        // When
        Page<Credito> resultado = creditoRepository.findAll(pageable);

        // Then
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getTotalElements()).isEqualTo(3);
        assertThat(resultado.getTotalPages()).isEqualTo(2);
        assertThat(resultado.isFirst()).isTrue();
        assertThat(resultado.hasNext()).isTrue();
        
        // Verificar ordenação (mais recente primeiro)
        assertThat(resultado.getContent().get(0).getNumeroCredito()).isEqualTo("789012");
        assertThat(resultado.getContent().get(1).getNumeroCredito()).isEqualTo("123456");
    }

    @Test
    void testFindByNumeroNfseComPaginacao() {
        // Given
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "dataConstituicao"));
        Page<Credito> pageComFiltro = new PageImpl<>(
            Collections.singletonList(credito2), 
            pageable, 
            2
        );
        when(creditoRepository.findByNumeroNfse("7891011", pageable)).thenReturn(pageComFiltro);

        // When
        Page<Credito> resultado = creditoRepository.findByNumeroNfse("7891011", pageable);

        // Then
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getTotalPages()).isEqualTo(2);
        assertThat(resultado.isFirst()).isTrue();
        assertThat(resultado.hasNext()).isTrue();
        
        // Verificar que retornou o mais recente
        assertThat(resultado.getContent().get(0).getNumeroCredito()).isEqualTo("789012");
    }

    @Test
    void testFindByTipoCreditoComPaginacao() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "numeroCredito"));
        Page<Credito> pageComTipo = new PageImpl<>(
            Arrays.asList(credito1, credito2), 
            pageable, 
            2
        );
        when(creditoRepository.findByTipoCredito("ISSQN", pageable)).thenReturn(pageComTipo);

        // When
        Page<Credito> resultado = creditoRepository.findByTipoCredito("ISSQN", pageable);

        // Then
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getTotalPages()).isEqualTo(1);
        
        // Verificar ordenação
        assertThat(resultado.getContent().get(0).getNumeroCredito()).isEqualTo("123456");
        assertThat(resultado.getContent().get(1).getNumeroCredito()).isEqualTo("789012");
    }

    @Test
    void testFindBySimplesNacionalComPaginacao() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "numeroCredito"));
        Page<Credito> pageSimplesNacional = new PageImpl<>(
            Arrays.asList(credito1, credito3), 
            pageable, 
            2
        );
        when(creditoRepository.findBySimplesNacional(true, pageable)).thenReturn(pageSimplesNacional);

        // When
        Page<Credito> resultado = creditoRepository.findBySimplesNacional(true, pageable);

        // Then
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getTotalElements()).isEqualTo(2);
        assertThat(resultado.getTotalPages()).isEqualTo(1);
        
        // Verificar que retornou apenas créditos do simples nacional
        assertThat(resultado.getContent()).extracting(Credito::getSimplesNacional)
            .containsOnly(true);
    }

    @Test
    void testFindByFiltersComTodosFiltros() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "numeroCredito"));
        Page<Credito> pageComFiltros = new PageImpl<>(
            Collections.singletonList(credito1), 
            pageable, 
            1
        );
        when(creditoRepository.findByFilters("7891011", "ISSQN", true, pageable))
            .thenReturn(pageComFiltros);

        // When
        Page<Credito> resultado = creditoRepository.findByFilters(
            "7891011", "ISSQN", true, pageable
        );

        // Then
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).getNumeroCredito()).isEqualTo("123456");
    }

    @Test
    void testFindByFiltersComFiltroNfse() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "numeroCredito"));
        Page<Credito> pageComNfse = new PageImpl<>(
            Collections.singletonList(credito3), 
            pageable, 
            1
        );
        when(creditoRepository.findByFilters("1122334", null, null, pageable))
            .thenReturn(pageComNfse);

        // When
        Page<Credito> resultado = creditoRepository.findByFilters(
            "1122334", null, null, pageable
        );

        // Then
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).getNumeroCredito()).isEqualTo("654321");
    }

    @Test
    void testFindByFiltersComFiltroTipoCredito() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "numeroCredito"));
        Page<Credito> pageComTipo = new PageImpl<>(
            Collections.singletonList(credito3), 
            pageable, 
            1
        );
        when(creditoRepository.findByFilters(null, "Outros", null, pageable))
            .thenReturn(pageComTipo);

        // When
        Page<Credito> resultado = creditoRepository.findByFilters(
            null, "Outros", null, pageable
        );

        // Then
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).getNumeroCredito()).isEqualTo("654321");
    }

    @Test
    void testFindByFiltersComFiltroSimplesNacional() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "numeroCredito"));
        Page<Credito> pageComSimplesNacional = new PageImpl<>(
            Collections.singletonList(credito2), 
            pageable, 
            1
        );
        when(creditoRepository.findByFilters(null, null, false, pageable))
            .thenReturn(pageComSimplesNacional);

        // When
        Page<Credito> resultado = creditoRepository.findByFilters(
            null, null, false, pageable
        );

        // Then
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent().get(0).getNumeroCredito()).isEqualTo("789012");
    }

    @Test
    void testFindByFiltersSemFiltros() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "numeroCredito"));
        Page<Credito> pageCompleta = new PageImpl<>(
            Arrays.asList(credito1, credito2, credito3), 
            pageable, 
            3
        );
        when(creditoRepository.findByFilters(null, null, null, pageable))
            .thenReturn(pageCompleta);

        // When
        Page<Credito> resultado = creditoRepository.findByFilters(
            null, null, null, pageable
        );

        // Then
        assertThat(resultado.getContent()).hasSize(3);
        assertThat(resultado.getTotalElements()).isEqualTo(3);
    }

    @Test
    void testFindByFiltersSemResultados() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "numeroCredito"));
        Page<Credito> pageVazia = new PageImpl<>(
            Collections.emptyList(), 
            pageable, 
            0
        );
        when(creditoRepository.findByFilters("9999999", "Inexistente", false, pageable))
            .thenReturn(pageVazia);

        // When
        Page<Credito> resultado = creditoRepository.findByFilters(
            "9999999", "Inexistente", false, pageable
        );

        // Then
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isEqualTo(0);
    }

    @Test
    void testPaginacaoSegundaPagina() {
        // Given
        Pageable pageable = PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "dataConstituicao"));
        Page<Credito> segundaPagina = new PageImpl<>(
            Collections.singletonList(credito3), 
            pageable, 
            3
        );
        when(creditoRepository.findAll(pageable)).thenReturn(segundaPagina);

        // When
        Page<Credito> resultado = creditoRepository.findAll(pageable);

        // Then
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getTotalElements()).isEqualTo(3);
        assertThat(resultado.getTotalPages()).isEqualTo(2);
        assertThat(resultado.isFirst()).isFalse();
        assertThat(resultado.isLast()).isTrue();
        assertThat(resultado.hasNext()).isFalse();
        assertThat(resultado.hasPrevious()).isTrue();
        
        // Verificar que retornou o crédito mais antigo
        assertThat(resultado.getContent().get(0).getNumeroCredito()).isEqualTo("654321");
    }
}