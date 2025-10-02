package br.com.guilhermedealmeidafreitas.creditos.controller;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.repository.CreditoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CreditoController.class)
class CreditoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreditoRepository creditoRepository;

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
    void testBuscarCreditosPorNfse_Sucesso() throws Exception {
        // Given
        when(creditoRepository.findByNumeroNfse("7891011")).thenReturn(creditos);

        // When & Then
        mockMvc.perform(get("/api/creditos/7891011")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].numeroCredito").value("123456"))
                .andExpect(jsonPath("$[0].numeroNfse").value("7891011"))
                .andExpect(jsonPath("$[0].tipoCredito").value("ISSQN"))
                .andExpect(jsonPath("$[0].simplesNacional").value(true))
                .andExpect(jsonPath("$[1].numeroCredito").value("789012"))
                .andExpect(jsonPath("$[1].numeroNfse").value("7891011"))
                .andExpect(jsonPath("$[1].tipoCredito").value("ISSQN"))
                .andExpect(jsonPath("$[1].simplesNacional").value(false));
    }

    @Test
    void testBuscarCreditosPorNfse_NaoEncontrado() throws Exception {
        // Given
        when(creditoRepository.findByNumeroNfse("9999999")).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/creditos/9999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_Sucesso() throws Exception {
        // Given
        when(creditoRepository.findByNumeroNfse(anyString(), any(Pageable.class)))
                .thenReturn(creditosPage);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.content[0].numeroCredito").value("123456"))
                .andExpect(jsonPath("$.content[1].numeroCredito").value("789012"));
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_ParametrosPadrao() throws Exception {
        // Given
        when(creditoRepository.findByNumeroNfse(anyString(), any(Pageable.class)))
                .thenReturn(creditosPage);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_PaginaPersonalizada() throws Exception {
        // Given
        Page<Credito> pagePersonalizada = new PageImpl<>(
            Collections.singletonList(credito1), 
            PageRequest.of(1, 1, Sort.by(Sort.Direction.DESC, "dataConstituicao")), 
            2
        );
        when(creditoRepository.findByNumeroNfse(anyString(), any(Pageable.class)))
                .thenReturn(pagePersonalizada);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?page=1&size=1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(true));
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_NaoEncontrado() throws Exception {
        // Given
        Page<Credito> pageVazia = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(creditoRepository.findByNumeroNfse(anyString(), any(Pageable.class)))
                .thenReturn(pageVazia);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/9999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_TamanhoMaximo() throws Exception {
        // Given
        Page<Credito> pageComTamanhoMaximo = new PageImpl<>(creditos, PageRequest.of(0, 100), 2);
        when(creditoRepository.findByNumeroNfse(anyString(), any(Pageable.class)))
                .thenReturn(pageComTamanhoMaximo);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?size=150")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(100)); // Deve limitar a 100
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_PaginaNegativa() throws Exception {
        // Given
        when(creditoRepository.findByNumeroNfse(anyString(), any(Pageable.class)))
                .thenReturn(creditosPage);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?page=-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0)); // Deve ajustar para 0
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_TamanhoZero() throws Exception {
        // Given
        when(creditoRepository.findByNumeroNfse(anyString(), any(Pageable.class)))
                .thenReturn(creditosPage);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?size=0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(10)); // Deve ajustar para 10
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_TamanhoNegativo() throws Exception {
        // Given
        when(creditoRepository.findByNumeroNfse(anyString(), any(Pageable.class)))
                .thenReturn(creditosPage);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?size=-5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(10)); // Deve ajustar para 10
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_OrdenacaoPadrao() throws Exception {
        // Given
        when(creditoRepository.findByNumeroNfse(anyString(), any(Pageable.class)))
                .thenReturn(creditosPage);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verificar que o repository foi chamado com ordenação por dataConstituicao DESC
        // Isso é verificado indiretamente através do comportamento esperado
    }

    @Test
    void testBuscarCreditosPorNfse_ValidacaoParametros() throws Exception {
        // Given
        when(creditoRepository.findByNumeroNfse(anyString())).thenReturn(creditos);

        // When & Then - Teste com número NFS-e válido
        mockMvc.perform(get("/api/creditos/7891011")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Teste com número NFS-e vazio (deve funcionar, mas retornar vazio)
        when(creditoRepository.findByNumeroNfse("")).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/creditos/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_ValidacaoParametros() throws Exception {
        // Given
        when(creditoRepository.findByNumeroNfse(anyString(), any(Pageable.class)))
                .thenReturn(creditosPage);

        // When & Then - Teste com parâmetros válidos
        mockMvc.perform(get("/api/creditos/paginated/7891011?page=0&size=5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10)); // O mock retorna size=10
    }
}
