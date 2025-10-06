package br.com.guilhermedealmeidafreitas.creditos.integration;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.repository.CreditoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes integrados para a camada de dados (Repository).
 * Testa apenas a interação com o banco de dados usando JPA.
 * 
 * Características:
 * - Usa @DataJpaTest para configurar apenas a camada JPA
 * - Usa TestEntityManager para operações de persistência
 * - Testa queries customizadas e paginação
 * - Isolado de outras camadas da aplicação
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration")
@DisplayName("Testes Integrados - Repository Créditos")
class CreditoRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CreditoRepository creditoRepository;

    private Credito credito1;
    private Credito credito2;
    private Credito credito3;

    @BeforeEach
    void setUp() {
        // Limpar dados antes de cada teste
        creditoRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // Criar dados de teste
        credito1 = createTestCredito("123456", "7891011", LocalDate.of(2024, 2, 25));
        credito2 = createTestCredito("789012", "7891011", LocalDate.of(2024, 2, 26));
        credito3 = createTestCredito("345678", "9999999", LocalDate.of(2024, 2, 27));
    }

    @Test
    @DisplayName("Deve salvar e buscar crédito por ID")
    void testSaveAndFindById() {
        // Given - Salvar crédito
        Credito savedCredito = creditoRepository.save(credito1);
        entityManager.flush();

        // When - Buscar por ID
        Optional<Credito> foundCredito = creditoRepository.findById(savedCredito.getId());

        // Then
        assertTrue(foundCredito.isPresent());
        assertEquals(credito1.getNumeroCredito(), foundCredito.get().getNumeroCredito());
        assertEquals(credito1.getNumeroNfse(), foundCredito.get().getNumeroNfse());
        assertEquals(credito1.getTipoCredito(), foundCredito.get().getTipoCredito());
    }

    @Test
    @DisplayName("Deve buscar créditos por número NFS-e")
    void testFindByNumeroNfse() {
        // Given - Salvar créditos
        creditoRepository.saveAll(List.of(credito1, credito2, credito3));
        entityManager.flush();

        // When - Buscar por NFS-e
        List<Credito> creditos = creditoRepository.findByNumeroNfse("7891011");

        // Then
        assertThat(creditos).hasSize(2);
        assertThat(creditos).extracting(Credito::getNumeroCredito)
            .containsExactlyInAnyOrder("123456", "789012");
    }

    @Test
    @DisplayName("Deve buscar crédito por número de crédito")
    void testFindByNumeroCredito() {
        // Given - Salvar crédito
        creditoRepository.save(credito1);
        entityManager.flush();

        // When - Buscar por número de crédito
        Credito foundCredito = creditoRepository.findByNumeroCredito("123456");

        // Then
        assertNotNull(foundCredito);
        assertEquals("123456", foundCredito.getNumeroCredito());
        assertEquals("7891011", foundCredito.getNumeroNfse());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando NFS-e não existe")
    void testFindByNumeroNfse_NotFound() {
        // Given - Salvar créditos
        creditoRepository.saveAll(List.of(credito1, credito2));
        entityManager.flush();

        // When - Buscar NFS-e que não existe
        List<Credito> creditos = creditoRepository.findByNumeroNfse("9999999");

        // Then
        assertThat(creditos).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando crédito não existe")
    void testFindByNumeroCredito_NotFound() {
        // Given - Salvar crédito
        creditoRepository.save(credito1);
        entityManager.flush();

        // When - Buscar crédito que não existe
        Credito foundCredito = creditoRepository.findByNumeroCredito("999999");

        // Then
        assertNull(foundCredito);
    }

    @Test
    @DisplayName("Deve buscar créditos com paginação")
    void testFindByNumeroNfseWithPagination() {
        // Given - Criar múltiplos créditos
        List<Credito> creditos = createMultipleTestCreditos("7891011", 15);
        creditoRepository.saveAll(creditos);
        entityManager.flush();

        // When - Buscar com paginação
        Pageable pageable = PageRequest.of(0, 10, Sort.by("dataConstituicao").descending());
        Page<Credito> page = creditoRepository.findByNumeroNfse("7891011", pageable);

        // Then
        assertEquals(15, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertEquals(10, page.getNumberOfElements());
        assertTrue(page.isFirst());
        assertFalse(page.isLast());
        assertTrue(page.hasNext());
    }

    @Test
    @DisplayName("Deve buscar segunda página corretamente")
    void testFindByNumeroNfseWithPagination_SecondPage() {
        // Given - Criar múltiplos créditos
        List<Credito> creditos = createMultipleTestCreditos("7891011", 15);
        creditoRepository.saveAll(creditos);
        entityManager.flush();

        // When - Buscar segunda página
        Pageable pageable = PageRequest.of(1, 10, Sort.by("dataConstituicao").descending());
        Page<Credito> page = creditoRepository.findByNumeroNfse("7891011", pageable);

        // Then
        assertEquals(15, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertEquals(5, page.getNumberOfElements()); // Apenas 5 na segunda página
        assertFalse(page.isFirst());
        assertTrue(page.isLast());
        assertFalse(page.hasNext());
        assertTrue(page.hasPrevious());
    }

    @Test
    @DisplayName("Deve ordenar créditos por data de constituição")
    void testOrderByDataConstituicao() {
        // Given - Salvar créditos com datas diferentes
        Credito creditoAntigo = createTestCredito("111111", "7891011", LocalDate.of(2024, 1, 1));
        Credito creditoNovo = createTestCredito("222222", "7891011", LocalDate.of(2024, 3, 1));
        creditoRepository.saveAll(List.of(creditoAntigo, creditoNovo));
        entityManager.flush();

        // When - Buscar ordenado por data (descendente)
        Pageable pageable = PageRequest.of(0, 10, Sort.by("dataConstituicao").descending());
        Page<Credito> page = creditoRepository.findByNumeroNfse("7891011", pageable);

        // Then
        List<Credito> creditos = page.getContent();
        assertThat(creditos).hasSize(2);
        assertEquals("222222", creditos.get(0).getNumeroCredito()); // Mais recente primeiro
        assertEquals("111111", creditos.get(1).getNumeroCredito()); // Mais antigo por último
    }

    @Test
    @DisplayName("Deve ordenar créditos por valor ISSQN")
    void testOrderByValorIssqn() {
        // Given - Criar créditos com valores diferentes
        Credito creditoMenor = createTestCreditoWithValue("111111", "7891011", new BigDecimal("1000.00"));
        Credito creditoMaior = createTestCreditoWithValue("222222", "7891011", new BigDecimal("2000.00"));
        creditoRepository.saveAll(List.of(creditoMenor, creditoMaior));
        entityManager.flush();

        // When - Buscar ordenado por valor (descendente)
        Pageable pageable = PageRequest.of(0, 10, Sort.by("valorIssqn").descending());
        Page<Credito> page = creditoRepository.findByNumeroNfse("7891011", pageable);

        // Then
        List<Credito> creditos = page.getContent();
        assertThat(creditos).hasSize(2);
        assertEquals("222222", creditos.get(0).getNumeroCredito()); // Maior valor primeiro
        assertEquals("111111", creditos.get(1).getNumeroCredito()); // Menor valor por último
    }

    @Test
    @DisplayName("Deve contar total de créditos")
    void testCount() {
        // Given - Salvar créditos
        creditoRepository.saveAll(List.of(credito1, credito2, credito3));
        entityManager.flush();

        // When - Contar total
        long total = creditoRepository.count();

        // Then
        assertEquals(3, total);
    }

    @Test
    @DisplayName("Deve deletar créditos de teste")
    void testDeleteTestRecords() {
        // Given - Salvar créditos
        List<Credito> creditos = createMultipleTestCreditos("7891011", 5);
        creditoRepository.saveAll(creditos);
        entityManager.flush();
        assertEquals(5, creditoRepository.count());

        // When - Deletar registros de teste
        creditoRepository.deleteAll();
        entityManager.flush();

        // Then
        assertEquals(0, creditoRepository.count());
    }

    @Test
    @DisplayName("Deve buscar créditos por tipo")
    void testFindByTipoCredito() {
        // Given - Criar créditos com tipos diferentes
        Credito creditoIssqn = createTestCreditoWithType("111111", "7891011", "ISSQN");
        Credito creditoIcms = createTestCreditoWithType("222222", "7891011", "ICMS");
        Credito creditoIssqn2 = createTestCreditoWithType("333333", "7891011", "ISSQN");
        creditoRepository.saveAll(List.of(creditoIssqn, creditoIcms, creditoIssqn2));
        entityManager.flush();

        // When - Buscar por tipo ISSQN usando filtros
        Pageable pageable = PageRequest.of(0, 10);
        Page<Credito> page = creditoRepository.findByFilters("7891011", "ISSQN", null, pageable);

        // Then
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent()).extracting(Credito::getTipoCredito)
            .containsOnly("ISSQN");
    }

    @Test
    @DisplayName("Deve buscar créditos por Simples Nacional")
    void testFindBySimplesNacional() {
        // Given - Criar créditos com Simples Nacional diferentes
        Credito creditoSimples = createTestCreditoWithSimplesNacional("111111", "7891011", true);
        Credito creditoNormal = createTestCreditoWithSimplesNacional("222222", "7891011", false);
        Credito creditoSimples2 = createTestCreditoWithSimplesNacional("333333", "7891011", true);
        creditoRepository.saveAll(List.of(creditoSimples, creditoNormal, creditoSimples2));
        entityManager.flush();

        // When - Buscar apenas Simples Nacional usando filtros
        Pageable pageable = PageRequest.of(0, 10);
        Page<Credito> page = creditoRepository.findByFilters("7891011", null, true, pageable);

        // Then
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent()).extracting(Credito::getSimplesNacional)
            .containsOnly(true);
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
     * Método auxiliar para criar crédito com valor específico
     */
    private Credito createTestCreditoWithValue(String numeroCredito, String numeroNfse, BigDecimal valorIssqn) {
        return new Credito(
            numeroCredito, 
            numeroNfse, 
            LocalDate.of(2024, 2, 25),
            valorIssqn, 
            "ISSQN", 
            true, 
            new BigDecimal("5.0"),
            new BigDecimal("30000.00"), 
            new BigDecimal("5000.00"), 
            new BigDecimal("25000.00")
        );
    }

    /**
     * Método auxiliar para criar crédito com tipo específico
     */
    private Credito createTestCreditoWithType(String numeroCredito, String numeroNfse, String tipoCredito) {
        return new Credito(
            numeroCredito, 
            numeroNfse, 
            LocalDate.of(2024, 2, 25),
            new BigDecimal("1500.75"), 
            tipoCredito, 
            true, 
            new BigDecimal("5.0"),
            new BigDecimal("30000.00"), 
            new BigDecimal("5000.00"), 
            new BigDecimal("25000.00")
        );
    }

    /**
     * Método auxiliar para criar crédito com Simples Nacional específico
     */
    private Credito createTestCreditoWithSimplesNacional(String numeroCredito, String numeroNfse, boolean simplesNacional) {
        return new Credito(
            numeroCredito, 
            numeroNfse, 
            LocalDate.of(2024, 2, 25),
            new BigDecimal("1500.75"), 
            "ISSQN", 
            simplesNacional, 
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

    @AfterEach
    void tearDown() {
        // Limpar dados de teste após cada teste
        creditoRepository.deleteAll();
        // Forçar commit da transação para garantir limpeza
        entityManager.flush();
    }
}
