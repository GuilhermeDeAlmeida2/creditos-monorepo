package br.com.guilhermedealmeidafreitas.creditos.integration;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.repository.CreditoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("integration")
@Testcontainers
@Transactional
class CreditoRepositoryIntegrationTest {

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
    private CreditoRepository creditoRepository;

    @BeforeEach
    void setUp() {
        // Limpa a base de dados antes de cada teste
        creditoRepository.deleteAll();
    }

    // Testes para findByNumeroCredito
    @Test
    void findByNumeroCredito_QuandoCreditoExiste_DeveRetornarCredito() {
        // Cenário: Um crédito existe no banco
        Credito credito = new Credito("CREDITO001", "NFSE001", LocalDate.now(),
                BigDecimal.valueOf(100.00), "ISS", true, BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
        creditoRepository.save(credito);

        // Ação: Buscar por número do crédito
        Credito resultado = creditoRepository.findByNumeroCredito("CREDITO001");

        // Verificação
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNumeroCredito()).isEqualTo("CREDITO001");
        assertThat(resultado.getNumeroNfse()).isEqualTo("NFSE001");
        assertThat(resultado.getTipoCredito()).isEqualTo("ISS");
        assertThat(resultado.getSimplesNacional()).isTrue();
    }

    @Test
    void findByNumeroCredito_QuandoCreditoNaoExiste_DeveRetornarNull() {
        // Ação: Buscar por número inexistente
        Credito resultado = creditoRepository.findByNumeroCredito("CREDITO_INEXISTENTE");

        // Verificação
        assertThat(resultado).isNull();
    }

    // Testes para findByNumeroNfse
    @Test
    void findByNumeroNfse_QuandoExistemCreditos_DeveRetornarLista() {
        // Cenário: Múltiplos créditos para a mesma NFS-e
        Credito credito1 = new Credito("CREDITO001", "NFSE001", LocalDate.now(),
                BigDecimal.valueOf(100.00), "ISS", true, BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
        Credito credito2 = new Credito("CREDITO002", "NFSE001", LocalDate.now().minusDays(1),
                BigDecimal.valueOf(150.00), "ICMS", false, BigDecimal.valueOf(7.50),
                BigDecimal.valueOf(3000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(3000.00));
        
        creditoRepository.save(credito1);
        creditoRepository.save(credito2);

        // Ação: Buscar por NFS-e
        List<Credito> resultado = creditoRepository.findByNumeroNfse("NFSE001");

        // Verificação
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Credito::getNumeroNfse).containsOnly("NFSE001");
        assertThat(resultado).extracting(Credito::getNumeroCredito).containsExactlyInAnyOrder("CREDITO001", "CREDITO002");
    }

    @Test
    void findByNumeroNfse_QuandoNaoExistemCreditos_DeveRetornarListaVazia() {
        // Ação: Buscar por NFS-e inexistente
        List<Credito> resultado = creditoRepository.findByNumeroNfse("NFSE_INEXISTENTE");

        // Verificação
        assertThat(resultado).isEmpty();
    }

    // Testes para findTestRecords
    @Test
    void findTestRecords_QuandoExistemRegistrosTeste_DeveRetornarApenasRegistrosTeste() {
        // Cenário: Misturar registros normais e de teste
        Credito creditoNormal = new Credito("CREDITO001", "NFSE001", LocalDate.now(),
                BigDecimal.valueOf(100.00), "ISS", true, BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
        Credito creditoTeste1 = new Credito("TESTE001", "NFSE_TESTE", LocalDate.now(),
                BigDecimal.valueOf(100.00), "ISS", true, BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
        Credito creditoTeste2 = new Credito("TESTE002", "NFSE_TESTE", LocalDate.now(),
                BigDecimal.valueOf(100.00), "ISS", true, BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
        
        creditoRepository.save(creditoNormal);
        creditoRepository.save(creditoTeste1);
        creditoRepository.save(creditoTeste2);

        // Ação: Buscar registros de teste
        List<Credito> resultado = creditoRepository.findTestRecords();

        // Verificação
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Credito::getNumeroCredito).containsExactlyInAnyOrder("TESTE001", "TESTE002");
        assertThat(resultado).extracting(Credito::getNumeroCredito).allMatch(numero -> numero.startsWith("TESTE"));
    }

    @Test
    void findTestRecords_QuandoNaoExistemRegistrosTeste_DeveRetornarListaVazia() {
        // Cenário: Apenas registros normais
        Credito creditoNormal = new Credito("CREDITO001", "NFSE001", LocalDate.now(),
                BigDecimal.valueOf(100.00), "ISS", true, BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
        creditoRepository.save(creditoNormal);

        // Ação: Buscar registros de teste
        List<Credito> resultado = creditoRepository.findTestRecords();

        // Verificação
        assertThat(resultado).isEmpty();
    }

    // Testes para deleteTestRecords
    @Test
    void deleteTestRecords_QuandoExistemRegistrosTeste_DeveDeletarApenasRegistrosTeste() {
        // Cenário: Misturar registros normais e de teste
        Credito creditoNormal = new Credito("CREDITO001", "NFSE001", LocalDate.now(),
                BigDecimal.valueOf(100.00), "ISS", true, BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
        Credito creditoTeste1 = new Credito("TESTE001", "NFSE_TESTE", LocalDate.now(),
                BigDecimal.valueOf(100.00), "ISS", true, BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
        Credito creditoTeste2 = new Credito("TESTE002", "NFSE_TESTE", LocalDate.now(),
                BigDecimal.valueOf(100.00), "ISS", true, BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
        
        creditoRepository.save(creditoNormal);
        creditoRepository.save(creditoTeste1);
        creditoRepository.save(creditoTeste2);

        // Ação: Deletar registros de teste
        creditoRepository.deleteTestRecords();

        // Verificação: Apenas o registro normal deve permanecer
        List<Credito> todosRegistros = creditoRepository.findAll();
        List<Credito> registrosTeste = creditoRepository.findTestRecords();
        
        assertThat(todosRegistros).hasSize(1);
        assertThat(todosRegistros.get(0).getNumeroCredito()).isEqualTo("CREDITO001");
        assertThat(registrosTeste).isEmpty();
    }

    // Testes para paginação - findByNumeroNfse com Pageable
    @Test
    void findByNumeroNfse_ComPaginacao_QuandoExistemMuitosCreditos_DeveRetornarPagina() {
        // Cenário: Criar 25 créditos para a mesma NFS-e
        String nfse = "NFSE_PAGINADA";
        for (int i = 1; i <= 25; i++) {
            Credito credito = new Credito("CREDITO" + String.format("%03d", i), nfse, LocalDate.now().minusDays(i),
                    BigDecimal.valueOf(100.00 + i), "ISS", true, BigDecimal.valueOf(5.00),
                    BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
            creditoRepository.save(credito);
        }

        // Ação: Buscar primeira página (10 itens)
        Pageable pageable = PageRequest.of(0, 10, Sort.by("dataConstituicao").descending());
        Page<Credito> resultado = creditoRepository.findByNumeroNfse(nfse, pageable);

        // Verificação
        assertThat(resultado.getContent()).hasSize(10);
        assertThat(resultado.getTotalElements()).isEqualTo(25);
        assertThat(resultado.getTotalPages()).isEqualTo(3);
        assertThat(resultado.getNumber()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);
        assertThat(resultado.getContent()).extracting(Credito::getNumeroNfse).containsOnly(nfse);
    }

    @Test
    void findByNumeroNfse_ComPaginacao_QuandoNaoExistemCreditos_DeveRetornarPaginaVazia() {
        // Ação: Buscar NFS-e inexistente
        Pageable pageable = PageRequest.of(0, 10);
        Page<Credito> resultado = creditoRepository.findByNumeroNfse("NFSE_INEXISTENTE", pageable);

        // Verificação
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isEqualTo(0);
        assertThat(resultado.getTotalPages()).isEqualTo(0);
    }

    // Testes para filtros complexos - findByFilters
    @Test
    void findByFilters_ComTodosFiltros_DeveRetornarCreditosFiltrados() {
        // Cenário: Criar créditos com diferentes características
        Credito credito1 = new Credito("CREDITO001", "NFSE001", LocalDate.now(),
                BigDecimal.valueOf(100.00), "ISS", true, BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
        Credito credito2 = new Credito("CREDITO002", "NFSE001", LocalDate.now(),
                BigDecimal.valueOf(150.00), "ICMS", false, BigDecimal.valueOf(7.50),
                BigDecimal.valueOf(3000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(3000.00));
        Credito credito3 = new Credito("CREDITO003", "NFSE002", LocalDate.now(),
                BigDecimal.valueOf(200.00), "ISS", true, BigDecimal.valueOf(10.00),
                BigDecimal.valueOf(4000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(4000.00));
        
        creditoRepository.save(credito1);
        creditoRepository.save(credito2);
        creditoRepository.save(credito3);

        // Ação: Filtrar por NFS-e, tipo e simples nacional
        Pageable pageable = PageRequest.of(0, 10);
        Page<Credito> resultado = creditoRepository.findByFilters("NFSE001", "ISS", true, pageable);

        // Verificação
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNumeroCredito()).isEqualTo("CREDITO001");
        assertThat(resultado.getContent().get(0).getNumeroNfse()).isEqualTo("NFSE001");
        assertThat(resultado.getContent().get(0).getTipoCredito()).isEqualTo("ISS");
        assertThat(resultado.getContent().get(0).getSimplesNacional()).isTrue();
    }

    @Test
    void findByFilters_ComFiltrosParciais_DeveRetornarCreditosFiltrados() {
        // Cenário: Criar créditos com diferentes características
        Credito credito1 = new Credito("CREDITO001", "NFSE001", LocalDate.now(),
                BigDecimal.valueOf(100.00), "ISS", true, BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
        Credito credito2 = new Credito("CREDITO002", "NFSE001", LocalDate.now(),
                BigDecimal.valueOf(150.00), "ICMS", false, BigDecimal.valueOf(7.50),
                BigDecimal.valueOf(3000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(3000.00));
        Credito credito3 = new Credito("CREDITO003", "NFSE002", LocalDate.now(),
                BigDecimal.valueOf(200.00), "ISS", true, BigDecimal.valueOf(10.00),
                BigDecimal.valueOf(4000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(4000.00));
        
        creditoRepository.save(credito1);
        creditoRepository.save(credito2);
        creditoRepository.save(credito3);

        // Ação: Filtrar apenas por tipo de crédito
        Pageable pageable = PageRequest.of(0, 10);
        Page<Credito> resultado = creditoRepository.findByFilters(null, "ISS", null, pageable);

        // Verificação
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getContent()).extracting(Credito::getTipoCredito).containsOnly("ISS");
        assertThat(resultado.getContent()).extracting(Credito::getNumeroCredito).containsExactlyInAnyOrder("CREDITO001", "CREDITO003");
    }

    @Test
    void findByFilters_ComFiltrosNull_DeveRetornarTodosCreditos() {
        // Cenário: Criar alguns créditos
        Credito credito1 = new Credito("CREDITO001", "NFSE001", LocalDate.now(),
                BigDecimal.valueOf(100.00), "ISS", true, BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
        Credito credito2 = new Credito("CREDITO002", "NFSE002", LocalDate.now(),
                BigDecimal.valueOf(150.00), "ICMS", false, BigDecimal.valueOf(7.50),
                BigDecimal.valueOf(3000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(3000.00));
        
        creditoRepository.save(credito1);
        creditoRepository.save(credito2);

        // Ação: Buscar sem filtros (todos null)
        Pageable pageable = PageRequest.of(0, 10);
        Page<Credito> resultado = creditoRepository.findByFilters(null, null, null, pageable);

        // Verificação
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getContent()).extracting(Credito::getNumeroCredito).containsExactlyInAnyOrder("CREDITO001", "CREDITO002");
    }

    // Testes para findByTipoCredito com paginação
    @Test
    void findByTipoCredito_ComPaginacao_DeveRetornarCreditosDoTipo() {
        // Cenário: Criar créditos de diferentes tipos
        for (int i = 1; i <= 15; i++) {
            Credito credito = new Credito("CREDITO" + String.format("%03d", i), "NFSE" + i, LocalDate.now(),
                    BigDecimal.valueOf(100.00 + i), "ISS", true, BigDecimal.valueOf(5.00),
                    BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
            creditoRepository.save(credito);
        }
        
        for (int i = 1; i <= 8; i++) {
            Credito credito = new Credito("CREDITO_ICMS" + String.format("%03d", i), "NFSE_ICMS" + i, LocalDate.now(),
                    BigDecimal.valueOf(100.00 + i), "ICMS", false, BigDecimal.valueOf(7.50),
                    BigDecimal.valueOf(3000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(3000.00));
            creditoRepository.save(credito);
        }

        // Ação: Buscar créditos do tipo ISS
        Pageable pageable = PageRequest.of(0, 10);
        Page<Credito> resultado = creditoRepository.findByTipoCredito("ISS", pageable);

        // Verificação
        assertThat(resultado.getContent()).hasSize(10);
        assertThat(resultado.getTotalElements()).isEqualTo(15);
        assertThat(resultado.getTotalPages()).isEqualTo(2);
        assertThat(resultado.getContent()).extracting(Credito::getTipoCredito).containsOnly("ISS");
    }

    // Testes para findBySimplesNacional com paginação
    @Test
    void findBySimplesNacional_ComPaginacao_DeveRetornarCreditosSimplesNacional() {
        // Cenário: Criar créditos com diferentes status de simples nacional
        for (int i = 1; i <= 12; i++) {
            Credito credito = new Credito("CREDITO_SN" + String.format("%03d", i), "NFSE_SN" + i, LocalDate.now(),
                    BigDecimal.valueOf(100.00 + i), "ISS", true, BigDecimal.valueOf(5.00),
                    BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
            creditoRepository.save(credito);
        }
        
        for (int i = 1; i <= 5; i++) {
            Credito credito = new Credito("CREDITO_NORMAL" + String.format("%03d", i), "NFSE_NORMAL" + i, LocalDate.now(),
                    BigDecimal.valueOf(100.00 + i), "ICMS", false, BigDecimal.valueOf(7.50),
                    BigDecimal.valueOf(3000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(3000.00));
            creditoRepository.save(credito);
        }

        // Ação: Buscar créditos de simples nacional
        Pageable pageable = PageRequest.of(0, 10);
        Page<Credito> resultado = creditoRepository.findBySimplesNacional(true, pageable);

        // Verificação
        assertThat(resultado.getContent()).hasSize(10);
        assertThat(resultado.getTotalElements()).isEqualTo(12);
        assertThat(resultado.getTotalPages()).isEqualTo(2);
        assertThat(resultado.getContent()).extracting(Credito::getSimplesNacional).containsOnly(true);
    }

    // Teste para ordenação complexa
    @Test
    void findAll_ComOrdenacaoComplexa_DeveRetornarCreditosOrdenados() {
        // Cenário: Criar créditos com diferentes datas
        Credito credito1 = new Credito("CREDITO001", "NFSE001", LocalDate.now().minusDays(3),
                BigDecimal.valueOf(100.00), "ISS", true, BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
        Credito credito2 = new Credito("CREDITO002", "NFSE002", LocalDate.now().minusDays(1),
                BigDecimal.valueOf(150.00), "ICMS", false, BigDecimal.valueOf(7.50),
                BigDecimal.valueOf(3000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(3000.00));
        Credito credito3 = new Credito("CREDITO003", "NFSE003", LocalDate.now().minusDays(2),
                BigDecimal.valueOf(200.00), "ISS", true, BigDecimal.valueOf(10.00),
                BigDecimal.valueOf(4000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(4000.00));
        
        creditoRepository.save(credito1);
        creditoRepository.save(credito2);
        creditoRepository.save(credito3);

        // Ação: Buscar com ordenação por data (mais recente primeiro)
        Pageable pageable = PageRequest.of(0, 10, Sort.by("dataConstituicao").descending());
        Page<Credito> resultado = creditoRepository.findAll(pageable);

        // Verificação
        assertThat(resultado.getContent()).hasSize(3);
        assertThat(resultado.getContent()).extracting(Credito::getNumeroCredito)
                .containsExactly("CREDITO002", "CREDITO003", "CREDITO001");
    }
}
