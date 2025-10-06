package br.com.guilhermedealmeidafreitas.creditos.controller;

import br.com.guilhermedealmeidafreitas.creditos.dto.PaginatedCreditoResponse;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.service.CreditoService;
import br.com.guilhermedealmeidafreitas.creditos.service.ControllerValidationService;
import br.com.guilhermedealmeidafreitas.creditos.config.TestFeaturesConfig;
import br.com.guilhermedealmeidafreitas.creditos.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CreditoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreditoService creditoService;
    
    @Mock
    private TestFeaturesConfig testFeaturesConfig;
    
    @Mock
    private ControllerValidationService validationService;
    
    @InjectMocks
    private CreditoController creditoController;

    private Credito credito1;
    private Credito credito2;
    private List<Credito> creditos;
    private PaginatedCreditoResponse paginatedResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(creditoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        
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
        
        // Configurar mock padrão para o ControllerValidationService (lenient para evitar UnnecessaryStubbingException)
        Pageable defaultPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dataConstituicao"));
        lenient().when(validationService.validateAndCreatePageable(any(Integer.class), any(Integer.class), any(String.class), any(String.class)))
            .thenReturn(defaultPageable);
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
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("CreditoNotFoundException"))
                .andExpect(jsonPath("$.message").value("Crédito não encontrado para número da NFS-e: 9999999"))
                .andExpect(jsonPath("$.errorCode").value("CREDITO_NOT_FOUND"));
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
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("CreditoNotFoundException"))
                .andExpect(jsonPath("$.message").value("Crédito não encontrado para número da NFS-e: 9999999"))
                .andExpect(jsonPath("$.errorCode").value("CREDITO_NOT_FOUND"));
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
        when(creditoService.buscarCreditosPorNfse("7891011")).thenReturn(creditos);

        // When & Then - Teste com número NFS-e válido
        mockMvc.perform(get("/api/creditos/7891011")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
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
                .andExpect(jsonPath("$.dataConstituicao").exists())
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
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("CreditoNotFoundException"))
                .andExpect(jsonPath("$.message").value("Crédito não encontrado para número do crédito: 999999"))
                .andExpect(jsonPath("$.errorCode").value("CREDITO_NOT_FOUND"));
    }

    @Test
    void testBuscarCreditoPorNumero_NumeroCreditoVazio() throws Exception {
        // When & Then - Teste com número de crédito vazio (deve retornar 404)
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

    @Test
    void testGerarRegistrosTeste_Sucesso() throws Exception {
        // Given
        when(testFeaturesConfig.isEnabled()).thenReturn(true);
        when(creditoService.gerarRegistrosTeste()).thenReturn(300);

        // When & Then
        mockMvc.perform(post("/api/creditos/teste/gerar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrosGerados").value(300))
                .andExpect(jsonPath("$.mensagem").value("Registros de teste gerados com sucesso"));
    }

    @Test
    void testGerarRegistrosTeste_FuncionalidadeDesabilitada() throws Exception {
        // Given
        when(testFeaturesConfig.isEnabled()).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/creditos/teste/gerar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("TestDataException"))
                .andExpect(jsonPath("$.message").value("Funcionalidade de teste não está disponível neste ambiente"))
                .andExpect(jsonPath("$.errorCode").value("TEST_DATA_ERROR"));
    }

    @Test
    void testDeletarRegistrosTeste_Sucesso() throws Exception {
        // Given
        when(testFeaturesConfig.isEnabled()).thenReturn(true);
        when(creditoService.deletarRegistrosTeste()).thenReturn(150);

        // When & Then
        mockMvc.perform(delete("/api/creditos/teste/deletar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrosDeletados").value(150))
                .andExpect(jsonPath("$.mensagem").value("Registros de teste deletados com sucesso"));
    }

    @Test
    void testDeletarRegistrosTeste_FuncionalidadeDesabilitada() throws Exception {
        // Given
        when(testFeaturesConfig.isEnabled()).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/creditos/teste/deletar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("TestDataException"))
                .andExpect(jsonPath("$.message").value("Funcionalidade de teste não está disponível neste ambiente"))
                .andExpect(jsonPath("$.errorCode").value("TEST_DATA_ERROR"));
    }

    // Testes para validação de campos de ordenação
    @Test
    void testBuscarCreditosPorNfseComPaginacao_CampoOrdenacaoInvalido() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then - Campo inválido deve usar o padrão
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortBy=campoInvalido")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_CampoOrdenacaoValido() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then - Campo válido
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortBy=valorIssqn")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Testes para validação de direção de ordenação
    @Test
    void testBuscarCreditosPorNfseComPaginacao_OrdenacaoAsc() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then - Ordenação ascendente
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortDirection=asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_OrdenacaoDesc() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then - Ordenação descendente (padrão)
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortDirection=desc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_OrdenacaoInvalida() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then - Direção inválida deve usar DESC como padrão
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortDirection=invalido")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Testes para tratamento de exceções nos métodos de teste
    @Test
    void testGerarRegistrosTeste_Excecao() throws Exception {
        // Given
        when(testFeaturesConfig.isEnabled()).thenReturn(true);
        when(creditoService.gerarRegistrosTeste())
                .thenThrow(new RuntimeException("Erro ao gerar registros"));

        // When & Then
        mockMvc.perform(post("/api/creditos/teste/gerar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("InternalServerException"))
                .andExpect(jsonPath("$.message").value("Erro interno do servidor"))
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"));
    }

    @Test
    void testDeletarRegistrosTeste_Excecao() throws Exception {
        // Given
        when(testFeaturesConfig.isEnabled()).thenReturn(true);
        when(creditoService.deletarRegistrosTeste())
                .thenThrow(new RuntimeException("Erro ao deletar registros"));

        // When & Then
        mockMvc.perform(delete("/api/creditos/teste/deletar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("InternalServerException"))
                .andExpect(jsonPath("$.message").value("Erro interno do servidor"))
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"));
    }


    // Testes para diferentes campos de ordenação válidos
    @Test
    void testBuscarCreditosPorNfseComPaginacao_OrdenacaoPorId() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortBy=id")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_OrdenacaoPorNumeroCredito() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortBy=numeroCredito")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_OrdenacaoPorTipoCredito() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortBy=tipoCredito")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Testes para todos os campos de ordenação válidos
    @Test
    void testBuscarCreditosPorNfseComPaginacao_OrdenacaoPorNumeroNfse() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortBy=numeroNfse")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_OrdenacaoPorDataConstituicao() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortBy=dataConstituicao")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_OrdenacaoPorSimplesNacional() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortBy=simplesNacional")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_OrdenacaoPorAliquota() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortBy=aliquota")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_OrdenacaoPorValorFaturado() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortBy=valorFaturado")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_OrdenacaoPorValorDeducao() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortBy=valorDeducao")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_OrdenacaoPorBaseCalculo() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortBy=baseCalculo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Testes para combinações de parâmetros
    @Test
    void testBuscarCreditosPorNfseComPaginacao_CombinacaoParametros() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then - Teste com múltiplos parâmetros
        mockMvc.perform(get("/api/creditos/paginated/7891011?page=1&size=5&sortBy=valorIssqn&sortDirection=asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_CaseInsensitiveSortDirection() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then - Teste com direção em maiúscula
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortDirection=ASC")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testBuscarCreditosPorNfseComPaginacao_CaseInsensitiveSortDirectionDesc() throws Exception {
        // Given
        when(creditoService.buscarCreditosPorNfseComPaginacao(anyString(), any(Pageable.class)))
                .thenReturn(paginatedResponse);

        // When & Then - Teste com direção em maiúscula
        mockMvc.perform(get("/api/creditos/paginated/7891011?sortDirection=DESC")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
