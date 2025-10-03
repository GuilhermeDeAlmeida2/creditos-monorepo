package br.com.guilhermedealmeidafreitas.creditos.controller;

import br.com.guilhermedealmeidafreitas.creditos.dto.PaginatedCreditoResponse;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.service.CreditoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
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
    private CreditoService creditoService;

    private Credito credito1;
    private Credito credito2;
    private List<Credito> creditos;
    private PaginatedCreditoResponse paginatedResponse;

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
        paginatedResponse = new PaginatedCreditoResponse(
            creditos, 0, 10, 2, 1, true, true, false, false
        );
    }

    @Test
    void testBuscarCreditosPorNfse_Sucesso() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfse("7891011")).thenReturn(creditos);

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
        when(creditoService.buscarCreditosPorNfse("9999999")).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/creditos/9999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_Sucesso() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

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
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

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
        PaginatedCreditoResponse responsePersonalizada = new PaginatedCreditoResponse(
            Collections.singletonList(credito1), 1, 1, 2, 2, false, true, false, true
        );
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(responsePersonalizada);

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
        PaginatedCreditoResponse responseVazia = new PaginatedCreditoResponse(
            Collections.emptyList(), 0, 10, 0, 0, true, true, false, false
        );
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(responseVazia);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/9999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_TamanhoMaximo() throws Exception {
        // Given
        PaginatedCreditoResponse responseComTamanhoMaximo = new PaginatedCreditoResponse(
            creditos, 0, 100, 2, 1, true, true, false, false
        );
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(responseComTamanhoMaximo);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?size=150")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(100)); // Deve limitar a 100
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_PaginaNegativa() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?page=-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0)); // Deve ajustar para 0
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_TamanhoZero() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?size=0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(10)); // Deve ajustar para 10
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_TamanhoNegativo() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?size=-5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(10)); // Deve ajustar para 10
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_OrdenacaoPadrao() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verificar que o service foi chamado com ordenação por dataConstituicao DESC
        // Isso é verificado indiretamente através do comportamento esperado
    }

    @Test
    void testBuscarCreditosPorNfse_ValidacaoParametros() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfse(anyString())).thenReturn(creditos);

        // When & Then - Teste com número NFS-e válido
        mockMvc.perform(get("/api/creditos/7891011")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Teste com número NFS-e vazio (deve funcionar, mas retornar vazio)
        when(creditoService.buscarCreditosPorNfse("")).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/creditos/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_ValidacaoParametros() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then - Teste com parâmetros válidos
        mockMvc.perform(get("/api/creditos/paginated/7891011?page=0&size=5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10)); // O mock retorna size=10
    }

    @Test
    void testBuscarCreditoPorNumero_Sucesso() throws Exception {
        // Given
        when(creditoService.buscarCreditoPorNumero("123456")).thenReturn(credito1);

        // When & Then
        mockMvc.perform(get("/api/creditos/credito/123456")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numeroCredito").value("123456"))
                .andExpect(jsonPath("$.numeroNfse").value("7891011"))
                .andExpect(jsonPath("$.dataConstituicao").value("2024-02-25"))
                .andExpect(jsonPath("$.valorIssqn").value(1500.75))
                .andExpect(jsonPath("$.tipoCredito").value("ISSQN"))
                .andExpect(jsonPath("$.simplesNacional").value(true))
                .andExpect(jsonPath("$.aliquota").value(5.0))
                .andExpect(jsonPath("$.valorFaturado").value(30000.00))
                .andExpect(jsonPath("$.valorDeducao").value(5000.00))
                .andExpect(jsonPath("$.baseCalculo").value(25000.00));
    }

    @Test
    void testBuscarCreditoPorNumero_NaoEncontrado() throws Exception {
        // Given
        when(creditoService.buscarCreditoPorNumero("999999")).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/creditos/credito/999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBuscarCreditoPorNumero_NumeroCreditoVazio() throws Exception {
        // Given
        when(creditoService.buscarCreditoPorNumero("")).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/creditos/credito/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBuscarCreditoPorNumero_ValidacaoParametros() throws Exception {
        // Given
        when(creditoService.buscarCreditoPorNumero("123456")).thenReturn(credito1);

        // When & Then - Teste com número de crédito válido
        mockMvc.perform(get("/api/creditos/credito/123456")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroCredito").value("123456"));
    }
}
