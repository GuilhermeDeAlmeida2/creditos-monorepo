package br.com.guilhermedealmeidafreitas.creditos.integration;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.repository.CreditoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste de integração para o CreditoController.
 * Utiliza TestContainers para criar um banco PostgreSQL isolado para cada teste.
 * 
 * Este teste valida a integração completa entre:
 * - Controller (endpoints REST)
 * - Service (lógica de negócio)
 * - Repository (acesso a dados)
 * - Database (PostgreSQL via TestContainers)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("integration")
@Testcontainers
@Transactional
class CreditoControllerIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("creditos_test")
            .withUsername("creditos_user")
            .withPassword("creditos_pass");


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Configurações do PostgreSQL
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        
        // Kafka desabilitado para os testes de integração
        registry.add("spring.kafka.bootstrap-servers", () -> "");
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CreditoRepository creditoRepository;


    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Limpar dados antes de cada teste
        creditoRepository.deleteAll();
    }

    @Test
    void buscarCreditoPorNumero_QuandoCreditoExiste_DeveRetornarCredito() throws Exception {
        // Arrange
        Credito credito = criarCreditoTeste("CREDITO001", "NFSE001");
        creditoRepository.save(credito);

        // Act & Assert
        mockMvc.perform(get("/api/creditos/credito/CREDITO001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numeroCredito", is("CREDITO001")))
                .andExpect(jsonPath("$.numeroNfse", is("NFSE001")))
                .andExpect(jsonPath("$.tipoCredito", is("ISS")))
                .andExpect(jsonPath("$.simplesNacional", is(true)))
                .andExpect(jsonPath("$.valorIssqn", is(100.00)))
                .andExpect(jsonPath("$.aliquota", is(5.00)))
                .andExpect(jsonPath("$.valorFaturado", is(2000.00)))
                .andExpect(jsonPath("$.valorDeducao", is(0.00)))
                .andExpect(jsonPath("$.baseCalculo", is(2000.00)));
    }

    @Test
    void buscarCreditoPorNumero_QuandoCreditoNaoExiste_DeveRetornar404() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/creditos/credito/CREDITO_INEXISTENTE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarCreditosPorNfse_QuandoExistemCreditos_DeveRetornarLista() throws Exception {
        // Arrange
        List<Credito> creditos = List.of(
                criarCreditoTeste("CREDITO001", "NFSE001"),
                criarCreditoTeste("CREDITO002", "NFSE001"),
                criarCreditoTeste("CREDITO003", "NFSE001")
        );
        creditoRepository.saveAll(creditos);

        // Act & Assert
        mockMvc.perform(get("/api/creditos/NFSE001")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].numeroCredito", is("CREDITO001")))
                .andExpect(jsonPath("$[1].numeroCredito", is("CREDITO002")))
                .andExpect(jsonPath("$[2].numeroCredito", is("CREDITO003")))
                .andExpect(jsonPath("$[*].numeroNfse", everyItem(is("NFSE001"))));
    }

    @Test
    void buscarCreditosPorNfse_QuandoNaoExistemCreditos_DeveRetornar404() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/creditos/NFSE_INEXISTENTE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarCreditosPorNfseComPaginacao_QuandoExistemCreditos_DeveRetornarPagina() throws Exception {
        // Arrange
        List<Credito> creditos = List.of(
                criarCreditoTeste("CREDITO001", "NFSE001"),
                criarCreditoTeste("CREDITO002", "NFSE001"),
                criarCreditoTeste("CREDITO003", "NFSE001"),
                criarCreditoTeste("CREDITO004", "NFSE001"),
                criarCreditoTeste("CREDITO005", "NFSE001")
        );
        creditoRepository.saveAll(creditos);

        // Act & Assert - Primeira página
        mockMvc.perform(get("/api/creditos/paginated/NFSE001")
                .param("page", "0")
                .param("size", "2")
                .param("sortBy", "numeroCredito")
                .param("sortDirection", "asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].numeroCredito", is("CREDITO001")))
                .andExpect(jsonPath("$.content[1].numeroCredito", is("CREDITO002")))
                .andExpect(jsonPath("$.totalElements", is(5)))
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.last", is(false)))
                .andExpect(jsonPath("$.hasNext", is(true)))
                .andExpect(jsonPath("$.hasPrevious", is(false)));
    }

    @Test
    void buscarCreditosPorNfseComPaginacao_QuandoNaoExistemCreditos_DeveRetornar404() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/creditos/paginated/NFSE_INEXISTENTE")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarCreditosPorNfseComPaginacao_ComParametrosInvalidos_DeveAplicarValoresPadrao() throws Exception {
        // Arrange
        List<Credito> creditos = List.of(
                criarCreditoTeste("CREDITO001", "NFSE001"),
                criarCreditoTeste("CREDITO002", "NFSE001")
        );
        creditoRepository.saveAll(creditos);

        // Act & Assert - Página negativa deve ser ajustada para 0
        mockMvc.perform(get("/api/creditos/paginated/NFSE001")
                .param("page", "-1")
                .param("size", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.size", is(10))); // Tamanho padrão
    }

    @Test
    void gerarRegistrosTeste_QuandoFeatureHabilitada_DeveGerarRegistros() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/creditos/teste/gerar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.registrosGerados", is(300)))
                .andExpect(jsonPath("$.mensagem", is("Registros de teste gerados com sucesso")));

        // Verificar se os registros foram criados
        List<Credito> registrosTeste = creditoRepository.findTestRecords();
        assert registrosTeste.size() == 300;
    }

    @Test
    void deletarRegistrosTeste_QuandoExistemRegistros_DeveDeletarTodos() throws Exception {
        // Arrange - Gerar registros de teste primeiro
        mockMvc.perform(post("/api/creditos/teste/gerar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verificar que foram criados
        List<Credito> registrosAntes = creditoRepository.findTestRecords();
        assert registrosAntes.size() == 300;

        // Act & Assert
        mockMvc.perform(delete("/api/creditos/teste/deletar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.registrosDeletados", is(300)))
                .andExpect(jsonPath("$.mensagem", is("Registros de teste deletados com sucesso")));

        // Verificar que foram deletados
        List<Credito> registrosDepois = creditoRepository.findTestRecords();
        assert registrosDepois.isEmpty();
    }

    @Test
    void deletarRegistrosTeste_QuandoNaoExistemRegistros_DeveRetornarZero() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/creditos/teste/deletar")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.registrosDeletados", is(0)))
                .andExpect(jsonPath("$.mensagem", is("Registros de teste deletados com sucesso")));
    }

    /**
     * Cria um objeto Credito para testes com dados padrão
     */
    private Credito criarCreditoTeste(String numeroCredito, String numeroNfse) {
        return new Credito(
                numeroCredito,
                numeroNfse,
                LocalDate.now(),
                new BigDecimal("100.00"),
                "ISS",
                true,
                new BigDecimal("5.00"),
                new BigDecimal("2000.00"),
                new BigDecimal("0.00"),
                new BigDecimal("2000.00")
        );
    }
}
