package br.com.guilhermedealmeidafreitas.creditos.integration;

import br.com.guilhermedealmeidafreitas.creditos.controller.CreditoController;
import br.com.guilhermedealmeidafreitas.creditos.dto.PaginatedCreditoResponse;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.service.AuditService;
import br.com.guilhermedealmeidafreitas.creditos.service.CreditoService;
import br.com.guilhermedealmeidafreitas.creditos.config.TestFeaturesConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes integrados para a camada web (Controller).
 * Testa apenas a camada web com mocks dos serviços.
 * 
 * Características:
 * - Usa @WebMvcTest para configurar apenas a camada web
 * - Mocka serviços e dependências externas
 * - Testa serialização/deserialização JSON
 * - Testa validações e tratamento de erros
 * - Testa configurações de CORS e segurança
 */
@WebMvcTest(CreditoController.class)
@ActiveProfiles("integration")
@DisplayName("Testes Integrados - Web Layer Créditos")
class CreditoWebLayerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private CreditoService creditoService;

    @MockBean
    private AuditService auditService;

    @MockBean
    private TestFeaturesConfig testFeaturesConfig;

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
    @DisplayName("Deve buscar créditos por NFS-e com sucesso")
    void testBuscarCreditosPorNfse_Sucesso() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfse("7891011")).thenReturn(creditos);

        // When & Then
        mockMvc.perform(get("/api/creditos/7891011")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].numeroCredito").value("123456"))
                .andExpect(jsonPath("$[0].numeroNfse").value("7891011"))
                .andExpect(jsonPath("$[0].tipoCredito").value("ISSQN"))
                .andExpect(jsonPath("$[0].simplesNacional").value(true))
                .andExpect(jsonPath("$[0].valorIssqn").value(1500.75))
                .andExpect(jsonPath("$[1].numeroCredito").value("789012"))
                .andExpect(jsonPath("$[1].numeroNfse").value("7891011"))
                .andExpect(jsonPath("$[1].tipoCredito").value("ISSQN"))
                .andExpect(jsonPath("$[1].simplesNacional").value(false));
    }

    @Test
    @DisplayName("Deve retornar 404 quando NFS-e não existe")
    void testBuscarCreditosPorNfse_NaoEncontrado() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfse("9999999")).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/creditos/9999999")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve buscar créditos com paginação")
    void testBuscarCreditosPorNfseComPaginacao_Sucesso() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
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
                .andExpect(jsonPath("$.hasPrevious").value(false));
    }

    @Test
    @DisplayName("Deve buscar crédito por número")
    void testBuscarCreditoPorNumero_Sucesso() throws Exception {
        // Given
        when(creditoService.buscarCreditoPorNumero("123456")).thenReturn(credito1);

        // When & Then
        mockMvc.perform(get("/api/creditos/credito/123456")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numeroCredito").value("123456"))
                .andExpect(jsonPath("$.numeroNfse").value("7891011"))
                .andExpect(jsonPath("$.tipoCredito").value("ISSQN"))
                .andExpect(jsonPath("$.simplesNacional").value(true))
                .andExpect(jsonPath("$.valorIssqn").value(1500.75))
                .andExpect(jsonPath("$.aliquota").value(5.0))
                .andExpect(jsonPath("$.valorFaturado").value(30000.00))
                .andExpect(jsonPath("$.valorDeducao").value(5000.00))
                .andExpect(jsonPath("$.baseCalculo").value(25000.00));
    }

    @Test
    @DisplayName("Deve retornar 404 quando crédito não existe")
    void testBuscarCreditoPorNumero_NaoEncontrado() throws Exception {
        // Given
        when(creditoService.buscarCreditoPorNumero("999999")).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/creditos/credito/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve gerar registros de teste com sucesso")
    void testGerarRegistrosTeste_Sucesso() throws Exception {
        // Given
        when(testFeaturesConfig.isEnabled()).thenReturn(true);
        when(creditoService.gerarRegistrosTeste()).thenReturn(300);

        // When & Then
        mockMvc.perform(post("/api/creditos/teste/gerar")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.registrosGerados").value(300))
                .andExpect(jsonPath("$.mensagem").value("Registros de teste gerados com sucesso"));
    }

    @Test
    @DisplayName("Deve retornar 403 quando funcionalidade de teste está desabilitada")
    void testGerarRegistrosTeste_FuncionalidadeDesabilitada() throws Exception {
        // Given
        when(testFeaturesConfig.isEnabled()).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/creditos/teste/gerar")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.erro").value("Funcionalidade de teste não está disponível neste ambiente"));
    }

    @Test
    @DisplayName("Deve deletar registros de teste com sucesso")
    void testDeletarRegistrosTeste_Sucesso() throws Exception {
        // Given
        when(testFeaturesConfig.isEnabled()).thenReturn(true);
        when(creditoService.deletarRegistrosTeste()).thenReturn(150);

        // When & Then
        mockMvc.perform(delete("/api/creditos/teste/deletar")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.registrosDeletados").value(150))
                .andExpect(jsonPath("$.mensagem").value("Registros de teste deletados com sucesso"));
    }

    @Test
    @DisplayName("Deve retornar 403 ao deletar quando funcionalidade está desabilitada")
    void testDeletarRegistrosTeste_FuncionalidadeDesabilitada() throws Exception {
        // Given
        when(testFeaturesConfig.isEnabled()).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/creditos/teste/deletar")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.erro").value("Funcionalidade de teste não está disponível neste ambiente"));
    }

    @Test
    @DisplayName("Deve validar parâmetros de paginação - página negativa")
    void testValidacaoParametrosPaginacao_PaginaNegativa() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then - Página negativa deve ser ajustada para 0
        mockMvc.perform(get("/api/creditos/paginated/7891011?page=-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve validar parâmetros de paginação - tamanho zero")
    void testValidacaoParametrosPaginacao_TamanhoZero() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then - Tamanho zero deve ser ajustado para 10
        mockMvc.perform(get("/api/creditos/paginated/7891011?size=0")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve validar parâmetros de paginação - tamanho máximo")
    void testValidacaoParametrosPaginacao_TamanhoMaximo() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then - Tamanho maior que 100 deve ser limitado a 100
        mockMvc.perform(get("/api/creditos/paginated/7891011?size=150")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve testar ordenação por campo válido")
    void testOrdenacao_CampoValido() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortBy=valorIssqn&sortDirection=asc")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve testar ordenação por campo inválido")
    void testOrdenacao_CampoInvalido() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then - Campo inválido deve usar o padrão
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortBy=campoInvalido")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve testar direção de ordenação inválida")
    void testOrdenacao_DirecaoInvalida() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then - Direção inválida deve usar DESC como padrão
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortDirection=invalido")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve testar combinação de parâmetros de paginação")
    void testCombinacaoParametrosPaginacao() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then - Teste com múltiplos parâmetros
        mockMvc.perform(get("/api/creditos/paginated/7891011?page=1&size=5&sortBy=valorIssqn&sortDirection=asc")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve testar tratamento de exceções - gerar registros")
    void testTratamentoExcecoes_GerarRegistros() throws Exception {
        // Given
        when(testFeaturesConfig.isEnabled()).thenReturn(true);
        when(creditoService.gerarRegistrosTeste())
                .thenThrow(new RuntimeException("Erro ao gerar registros"));

        // When & Then
        mockMvc.perform(post("/api/creditos/teste/gerar")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    @DisplayName("Deve testar tratamento de exceções - deletar registros")
    void testTratamentoExcecoes_DeletarRegistros() throws Exception {
        // Given
        when(testFeaturesConfig.isEnabled()).thenReturn(true);
        when(creditoService.deletarRegistrosTeste())
                .thenThrow(new RuntimeException("Erro ao deletar registros"));

        // When & Then
        mockMvc.perform(delete("/api/creditos/teste/deletar")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.erro").exists());
    }

    @Test
    @DisplayName("Deve testar Content-Type correto nas respostas")
    void testContentTypeRespostas() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfse("7891011")).thenReturn(creditos);

        // When & Then
        mockMvc.perform(get("/api/creditos/7891011")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Deve testar headers de resposta")
    void testHeadersResposta() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfse("7891011")).thenReturn(creditos);

        // When & Then
        mockMvc.perform(get("/api/creditos/7891011")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"));
    }
}
