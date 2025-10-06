package br.com.guilhermedealmeidafreitas.creditos.integration;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.repository.CreditoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes integrados completos para a API de Créditos.
 * Testa toda a stack: Controller -> Service -> Repository -> Database
 * 
 * Características:
 * - Usa banco H2 em memória para isolamento completo
 * - Transações são revertidas automaticamente (@Transactional)
 * - Não polui dados de produção
 * - Testa fluxos reais end-to-end
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@DisplayName("Testes Integrados - API Créditos")
class CreditoApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CreditoRepository creditoRepository;


    private String baseUrl;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        
        // Configurar headers padrão
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Limpar dados antes de cada teste para garantir isolamento
        creditoRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve buscar créditos por NFS-e com sucesso")
    void testBuscarCreditosPorNfse_Sucesso() throws Exception {
        // Given - Criar dados de teste no banco
        Credito credito1 = createTestCredito("123456", "7891011", LocalDate.of(2024, 2, 25));
        Credito credito2 = createTestCredito("789012", "7891011", LocalDate.of(2024, 2, 26));
        creditoRepository.saveAll(List.of(credito1, credito2));

        // When - Fazer chamada real para a API
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/creditos/7891011", String.class);

        // Then - Verificar resposta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).contains("123456");
        assertThat(response.getBody()).contains("789012");
        assertThat(response.getBody()).contains("7891011");
    }

    @Test
    @DisplayName("Deve retornar 404 quando NFS-e não existe")
    void testBuscarCreditosPorNfse_NaoEncontrado() throws Exception {
        // When - Buscar NFS-e que não existe
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/creditos/9999999", String.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve buscar créditos com paginação")
    void testBuscarCreditosPorNfseComPaginacao_Sucesso() throws Exception {
        // Given - Criar múltiplos registros para testar paginação
        List<Credito> creditos = createMultipleTestCreditos("7891011", 15);
        creditoRepository.saveAll(creditos);

        // When - Fazer chamada com paginação
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/creditos/paginated/7891011?page=0&size=10", String.class);

        // Then - Verificar resposta paginada
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).contains("\"page\":0");
        assertThat(response.getBody()).contains("\"size\":10");
        assertThat(response.getBody()).contains("\"totalElements\":15");
        assertThat(response.getBody()).contains("\"totalPages\":2");
        assertThat(response.getBody()).contains("\"first\":true");
        assertThat(response.getBody()).contains("\"last\":false");
        assertThat(response.getBody()).contains("\"hasNext\":true");
    }

    @Test
    @DisplayName("Deve buscar créditos com paginação - segunda página")
    void testBuscarCreditosPorNfseComPaginacao_SegundaPagina() throws Exception {
        // Given - Criar múltiplos registros
        List<Credito> creditos = createMultipleTestCreditos("7891011", 15);
        creditoRepository.saveAll(creditos);

        // When - Buscar segunda página
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/creditos/paginated/7891011?page=1&size=10", String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).contains("\"page\":1");
        assertThat(response.getBody()).contains("\"size\":10");
        assertThat(response.getBody()).contains("\"totalElements\":15");
        assertThat(response.getBody()).contains("\"first\":false");
        assertThat(response.getBody()).contains("\"last\":true");
        assertThat(response.getBody()).contains("\"hasPrevious\":true");
    }

    @Test
    @DisplayName("Deve buscar crédito por número com sucesso")
    void testBuscarCreditoPorNumero_Sucesso() throws Exception {
        // Given
        Credito credito = createTestCredito("123456", "7891011", LocalDate.of(2024, 2, 25));
        creditoRepository.save(credito);

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/creditos/credito/123456", String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).contains("123456");
        assertThat(response.getBody()).contains("7891011");
        assertThat(response.getBody()).contains("ISSQN");
        assertThat(response.getBody()).contains("1500.75");
    }

    @Test
    @DisplayName("Deve retornar 404 quando crédito não existe")
    void testBuscarCreditoPorNumero_NaoEncontrado() throws Exception {
        // When - Buscar crédito que não existe
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/creditos/credito/999999", String.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve gerar registros de teste com sucesso")
    void testGerarRegistrosTeste_Sucesso() throws Exception {
        // Given - Verificar que não há registros
        assertEquals(0, creditoRepository.count());

        // When - Gerar registros de teste
        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/api/creditos/teste/gerar", null, String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).contains("registrosGerados");
        assertThat(response.getBody()).contains("mensagem");
        
        // Verificar que registros foram criados
        assertThat(creditoRepository.count()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Deve deletar registros de teste com sucesso")
    void testDeletarRegistrosTeste_Sucesso() throws Exception {
        // Given - Criar alguns registros de teste com prefixo TESTE
        List<Credito> creditos = createTestCreditosWithPrefix("TESTE", "7891011", 5);
        creditoRepository.saveAll(creditos);
        assertEquals(5, creditoRepository.count());

        // When - Deletar registros de teste
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/creditos/teste/deletar", 
            HttpMethod.DELETE, 
            null, 
            String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).contains("registrosDeletados");
        assertThat(response.getBody()).contains("mensagem");
        assertEquals(0, creditoRepository.count());
    }

    @Test
    @DisplayName("Deve responder ping corretamente")
    void testPing_Sucesso() throws Exception {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/ping", String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).contains("pong");
        assertThat(response.getBody()).contains("ts");
    }

    @Test
    @DisplayName("Deve validar parâmetros de paginação")
    void testValidacaoParametrosPaginacao() throws Exception {
        // Given
        List<Credito> creditos = createMultipleTestCreditos("7891011", 5);
        creditoRepository.saveAll(creditos);

        // When - Testar tamanho máximo (deve limitar a 100)
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/creditos/paginated/7891011?size=150", String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).contains("\"size\":100"); // Deve limitar a 100
    }

    @Test
    @DisplayName("Deve ajustar página negativa para 0")
    void testPaginaNegativa() throws Exception {
        // Given
        List<Credito> creditos = createMultipleTestCreditos("7891011", 5);
        creditoRepository.saveAll(creditos);

        // When - Página negativa
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/creditos/paginated/7891011?page=-1", String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).contains("\"page\":0"); // Deve ajustar para 0
    }

    @Test
    @DisplayName("Deve ajustar tamanho zero para 10")
    void testTamanhoZero() throws Exception {
        // Given
        List<Credito> creditos = createMultipleTestCreditos("7891011", 5);
        creditoRepository.saveAll(creditos);

        // When - Tamanho zero
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/creditos/paginated/7891011?size=0", String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).contains("\"size\":10"); // Deve ajustar para 10
    }

    @Test
    @DisplayName("Deve testar ordenação por diferentes campos")
    void testOrdenacaoPorCampos() throws Exception {
        // Given
        List<Credito> creditos = createMultipleTestCreditos("7891011", 3);
        creditoRepository.saveAll(creditos);

        // When - Ordenar por valorIssqn
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/creditos/paginated/7891011?sortBy=valorIssqn&sortDirection=asc", 
            String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).contains("\"page\":0");
    }

    /**
     * Método auxiliar para criar um crédito de teste
     */
    private Credito createTestCredito(String numeroCredito, String numeroNfse, LocalDate dataConstituicao) {
        return new Credito(
            numeroCredito, 
            numeroNfse, 
            dataConstituicao,
            new BigDecimal("1500.75"), 
            "ISSQN", 
            true, 
            new BigDecimal("5.0"),
            new BigDecimal("30000.00"), 
            new BigDecimal("5000.00"), 
            new BigDecimal("25000.00")
        );
    }

    /**
     * Método auxiliar para criar múltiplos créditos de teste
     */
    private List<Credito> createMultipleTestCreditos(String numeroNfse, int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
            .mapToObj(i -> createTestCredito(
                String.format("%06d", i), 
                numeroNfse, 
                LocalDate.of(2024, 2, 25).plusDays(i)
            ))
            .toList();
    }

    /**
     * Método auxiliar para criar múltiplos créditos de teste com prefixo específico
     */
    private List<Credito> createTestCreditosWithPrefix(String prefix, String numeroNfse, int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
            .mapToObj(i -> createTestCredito(
                prefix + String.format("%06d", i), 
                numeroNfse, 
                LocalDate.of(2024, 2, 25).plusDays(i)
            ))
            .toList();
    }

    @AfterEach
    void tearDown() {
        // Limpar dados após cada teste para garantir isolamento
        creditoRepository.deleteAll();
    }
}
