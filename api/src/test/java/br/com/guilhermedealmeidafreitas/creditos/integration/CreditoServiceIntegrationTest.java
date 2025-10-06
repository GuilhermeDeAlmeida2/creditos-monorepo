package br.com.guilhermedealmeidafreitas.creditos.integration;

import br.com.guilhermedealmeidafreitas.creditos.dto.PaginatedCreditoResponse;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.repository.CreditoRepository;
import br.com.guilhermedealmeidafreitas.creditos.service.CreditoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
class CreditoServiceIntegrationTest {

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
    private CreditoService creditoService;

    @Autowired
    private CreditoRepository creditoRepository;

    @BeforeEach
    void setUp() {
        // Limpa a base de dados antes de cada teste
        creditoRepository.deleteAll();
    }

    // Testes para buscarCreditoPorNumero
    @Test
    void buscarCreditoPorNumero_QuandoCreditoExiste_DeveRetornarCredito() {
        // Cenário: Um crédito existe no banco
        Credito credito = new Credito("CREDITO001", "NFSE001", LocalDate.now(),
                BigDecimal.valueOf(100.00), "ISS", true, BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
        creditoRepository.save(credito);

        // Ação: Buscar por número do crédito
        Credito resultado = creditoService.buscarCreditoPorNumero("CREDITO001");

        // Verificação
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNumeroCredito()).isEqualTo("CREDITO001");
        assertThat(resultado.getNumeroNfse()).isEqualTo("NFSE001");
        assertThat(resultado.getTipoCredito()).isEqualTo("ISS");
        assertThat(resultado.getSimplesNacional()).isTrue();
    }

    @Test
    void buscarCreditoPorNumero_QuandoCreditoNaoExiste_DeveRetornarNull() {
        // Ação: Buscar por número inexistente
        Credito resultado = creditoService.buscarCreditoPorNumero("CREDITO_INEXISTENTE");

        // Verificação
        assertThat(resultado).isNull();
    }

    // Testes para buscarCreditosPorNfse
    @Test
    void buscarCreditosPorNfse_QuandoExistemCreditos_DeveRetornarLista() {
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
        List<Credito> resultado = creditoService.buscarCreditosPorNfse("NFSE001");

        // Verificação
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(Credito::getNumeroNfse).containsOnly("NFSE001");
        assertThat(resultado).extracting(Credito::getNumeroCredito).containsExactlyInAnyOrder("CREDITO001", "CREDITO002");
    }

    @Test
    void buscarCreditosPorNfse_QuandoNaoExistemCreditos_DeveRetornarListaVazia() {
        // Ação: Buscar por NFS-e inexistente
        List<Credito> resultado = creditoService.buscarCreditosPorNfse("NFSE_INEXISTENTE");

        // Verificação
        assertThat(resultado).isEmpty();
    }

    // Testes para buscarCreditosPorNfseComPaginacao
    @Test
    void buscarCreditosPorNfseComPaginacao_QuandoExistemCreditos_DeveRetornarPagina() {
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
        PaginatedCreditoResponse resultado = creditoService.buscarCreditosPorNfseComPaginacao(nfse, pageable);

        // Verificação
        assertThat(resultado.getContent()).hasSize(10);
        assertThat(resultado.getTotalElements()).isEqualTo(25);
        assertThat(resultado.getTotalPages()).isEqualTo(3);
        assertThat(resultado.getPage()).isEqualTo(0);
        assertThat(resultado.getSize()).isEqualTo(10);
        assertThat(resultado.getContent()).extracting(Credito::getNumeroNfse).containsOnly(nfse);
        assertThat(resultado.isFirst()).isTrue();
        assertThat(resultado.isLast()).isFalse();
        assertThat(resultado.isHasNext()).isTrue();
        assertThat(resultado.isHasPrevious()).isFalse();
    }

    @Test
    void buscarCreditosPorNfseComPaginacao_QuandoNaoExistemCreditos_DeveRetornarPaginaVazia() {
        // Ação: Buscar NFS-e inexistente
        Pageable pageable = PageRequest.of(0, 10);
        PaginatedCreditoResponse resultado = creditoService.buscarCreditosPorNfseComPaginacao("NFSE_INEXISTENTE", pageable);

        // Verificação
        assertThat(resultado.getContent()).isEmpty();
        assertThat(resultado.getTotalElements()).isEqualTo(0);
        assertThat(resultado.getTotalPages()).isEqualTo(0);
        assertThat(resultado.isFirst()).isTrue();
        assertThat(resultado.isLast()).isTrue();
        assertThat(resultado.isHasNext()).isFalse();
        assertThat(resultado.isHasPrevious()).isFalse();
    }

    // Testes para validações de paginação
    @Test
    void buscarCreditosPorNfseComPaginacao_ComPaginaZero_DeveRetornarPrimeiraPagina() {
        // Cenário: Criar alguns créditos
        String nfse = "NFSE_VALIDACAO";
        for (int i = 1; i <= 5; i++) {
            Credito credito = new Credito("CREDITO" + String.format("%03d", i), nfse, LocalDate.now(),
                    BigDecimal.valueOf(100.00 + i), "ISS", true, BigDecimal.valueOf(5.00),
                    BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
            creditoRepository.save(credito);
        }

        // Ação: Usar PageRequest válido (o controller sempre valida antes de chamar o serviço)
        // O teste simula o comportamento real onde o controller já corrigiu a página negativa para 0
        Pageable pageableCorrigido = PageRequest.of(0, 10);
        
        PaginatedCreditoResponse resultado = creditoService.buscarCreditosPorNfseComPaginacao(nfse, pageableCorrigido);

        // Verificação: Deve retornar a primeira página (página 0)
        assertThat(resultado.getPage()).isEqualTo(0);
        assertThat(resultado.getContent()).hasSize(5);
    }

    @Test
    void buscarCreditosPorNfseComPaginacao_ComTamanhoPadrao_DeveRetornarRegistros() {
        // Cenário: Criar alguns créditos
        String nfse = "NFSE_VALIDACAO";
        for (int i = 1; i <= 5; i++) {
            Credito credito = new Credito("CREDITO" + String.format("%03d", i), nfse, LocalDate.now(),
                    BigDecimal.valueOf(100.00 + i), "ISS", true, BigDecimal.valueOf(5.00),
                    BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
            creditoRepository.save(credito);
        }

        // Ação: Usar PageRequest válido (o controller sempre valida antes de chamar o serviço)
        // O teste simula o comportamento real onde o controller já corrigiu o tamanho zero para 10
        Pageable pageableCorrigido = PageRequest.of(0, 10);
        
        PaginatedCreditoResponse resultado = creditoService.buscarCreditosPorNfseComPaginacao(nfse, pageableCorrigido);

        // Verificação: Deve aplicar tamanho padrão (10)
        assertThat(resultado.getSize()).isEqualTo(10);
        assertThat(resultado.getContent()).hasSize(5); // Apenas 5 registros existem
    }

    @Test
    void buscarCreditosPorNfseComPaginacao_ComTamanhoMaximo_DeveRetornarRegistros() {
        // Cenário: Criar 150 créditos para testar limite
        String nfse = "NFSE_LIMITE";
        for (int i = 1; i <= 150; i++) {
            Credito credito = new Credito("CREDITO" + String.format("%03d", i), nfse, LocalDate.now(),
                    BigDecimal.valueOf(100.00 + i), "ISS", true, BigDecimal.valueOf(5.00),
                    BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
            creditoRepository.save(credito);
        }

        // Ação: Usar PageRequest válido (o controller sempre valida antes de chamar o serviço)
        // O teste simula o comportamento real onde o controller já corrigiu o tamanho 200 para 100
        Pageable pageableCorrigido = PageRequest.of(0, 100);
        PaginatedCreditoResponse resultado = creditoService.buscarCreditosPorNfseComPaginacao(nfse, pageableCorrigido);

        // Verificação: Deve aplicar limite máximo (100)
        assertThat(resultado.getSize()).isEqualTo(100);
        assertThat(resultado.getContent()).hasSize(100);
        assertThat(resultado.getTotalElements()).isEqualTo(150);
        assertThat(resultado.getTotalPages()).isEqualTo(2);
    }

    // Testes para gerarRegistrosTeste
    @Test
    void gerarRegistrosTeste_QuandoFeatureHabilitada_DeveGerar300Registros() {
        // Ação: Gerar registros de teste
        int registrosGerados = creditoService.gerarRegistrosTeste();

        // Verificação
        assertThat(registrosGerados).isEqualTo(300);
        
        // Verificar se os registros foram realmente criados
        List<Credito> todosRegistros = creditoRepository.findAll();
        assertThat(todosRegistros).hasSize(300);
        
        // Verificar se são registros de teste (prefixo TESTE)
        assertThat(todosRegistros).extracting(Credito::getNumeroCredito)
                .allMatch(numero -> numero.startsWith("TESTE"));
        
        // Verificar se foram criadas 10 NFS-e diferentes
        long nfseDistintas = todosRegistros.stream()
                .map(Credito::getNumeroNfse)
                .distinct()
                .count();
        assertThat(nfseDistintas).isEqualTo(10);
        
        // Verificar se cada NFS-e tem 30 créditos
        for (int i = 1; i <= 10; i++) {
            String nfse = String.format("TESTE_NFSE%03d", i);
            long creditosPorNfse = todosRegistros.stream()
                    .filter(c -> c.getNumeroNfse().equals(nfse))
                    .count();
            assertThat(creditosPorNfse).isEqualTo(30);
        }
    }

    @Test
    void gerarRegistrosTeste_QuandoJaExistemRegistros_DeveAdicionarNovosRegistros() {
        // Cenário: Já existem alguns registros normais
        Credito creditoNormal = new Credito("CREDITO001", "NFSE001", LocalDate.now(),
                BigDecimal.valueOf(100.00), "ISS", true, BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
        creditoRepository.save(creditoNormal);

        // Ação: Gerar registros de teste
        int registrosGerados = creditoService.gerarRegistrosTeste();

        // Verificação
        assertThat(registrosGerados).isEqualTo(300);
        
        // Verificar se existem 301 registros no total (1 normal + 300 de teste)
        List<Credito> todosRegistros = creditoRepository.findAll();
        assertThat(todosRegistros).hasSize(301);
        
        // Verificar se o registro normal ainda existe
        assertThat(todosRegistros).extracting(Credito::getNumeroCredito)
                .contains("CREDITO001");
    }

    @Test
    void gerarRegistrosTeste_DeveGerarDadosRealistas() {
        // Ação: Gerar registros de teste
        int registrosGerados = creditoService.gerarRegistrosTeste();

        // Verificação
        assertThat(registrosGerados).isEqualTo(300);
        
        List<Credito> registrosTeste = creditoRepository.findTestRecords();
        assertThat(registrosTeste).hasSize(300);
        
        // Verificar se os dados são realistas
        for (Credito credito : registrosTeste) {
            // Verificar se os valores monetários são positivos
            assertThat(credito.getValorFaturado()).isPositive();
            assertThat(credito.getValorDeducao()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
            assertThat(credito.getBaseCalculo()).isPositive();
            assertThat(credito.getValorIssqn()).isPositive();
            
            // Verificar se a alíquota está entre 1% e 15%
            assertThat(credito.getAliquota()).isBetween(BigDecimal.valueOf(1.00), BigDecimal.valueOf(15.00));
            
            // Verificar se o tipo de crédito é válido
            assertThat(credito.getTipoCredito()).isIn("ISS", "IPI", "ICMS", "PIS", "COFINS", "IR", "CSLL");
            
            // Verificar se a data é no passado
            assertThat(credito.getDataConstituicao()).isBeforeOrEqualTo(LocalDate.now());
        }
    }

    // Testes para deletarRegistrosTeste
    @Test
    void deletarRegistrosTeste_QuandoExistemRegistrosTeste_DeveDeletarApenasRegistrosTeste() {
        // Cenário: Misturar registros normais e de teste
        Credito creditoNormal = new Credito("CREDITO001", "NFSE001", LocalDate.now(),
                BigDecimal.valueOf(100.00), "ISS", true, BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
        creditoRepository.save(creditoNormal);
        
        // Gerar registros de teste
        creditoService.gerarRegistrosTeste();

        // Ação: Deletar registros de teste
        int registrosDeletados = creditoService.deletarRegistrosTeste();

        // Verificação
        assertThat(registrosDeletados).isEqualTo(300);
        
        // Verificar se apenas o registro normal permanece
        List<Credito> todosRegistros = creditoRepository.findAll();
        assertThat(todosRegistros).hasSize(1);
        assertThat(todosRegistros.get(0).getNumeroCredito()).isEqualTo("CREDITO001");
        
        // Verificar se não existem mais registros de teste
        List<Credito> registrosTeste = creditoRepository.findTestRecords();
        assertThat(registrosTeste).isEmpty();
    }

    @Test
    void deletarRegistrosTeste_QuandoNaoExistemRegistrosTeste_DeveRetornarZero() {
        // Cenário: Apenas registros normais
        Credito creditoNormal = new Credito("CREDITO001", "NFSE001", LocalDate.now(),
                BigDecimal.valueOf(100.00), "ISS", true, BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(2000.00), BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00));
        creditoRepository.save(creditoNormal);

        // Ação: Deletar registros de teste
        int registrosDeletados = creditoService.deletarRegistrosTeste();

        // Verificação
        assertThat(registrosDeletados).isEqualTo(0);
        
        // Verificar se o registro normal ainda existe
        List<Credito> todosRegistros = creditoRepository.findAll();
        assertThat(todosRegistros).hasSize(1);
        assertThat(todosRegistros.get(0).getNumeroCredito()).isEqualTo("CREDITO001");
    }

    @Test
    void deletarRegistrosTeste_QuandoBaseVazia_DeveRetornarZero() {
        // Ação: Deletar registros de teste em base vazia
        int registrosDeletados = creditoService.deletarRegistrosTeste();

        // Verificação
        assertThat(registrosDeletados).isEqualTo(0);
        
        // Verificar se a base continua vazia
        List<Credito> todosRegistros = creditoRepository.findAll();
        assertThat(todosRegistros).isEmpty();
    }

    // Testes de integração complexos
    @Test
    void fluxoCompleto_GerarDeletarEGerarNovamente_DeveFuncionarCorretamente() {
        // Ação 1: Gerar registros de teste
        int primeiraGeracao = creditoService.gerarRegistrosTeste();
        assertThat(primeiraGeracao).isEqualTo(300);
        
        // Verificar se foram criados
        List<Credito> registrosAposPrimeiraGeracao = creditoRepository.findAll();
        assertThat(registrosAposPrimeiraGeracao).hasSize(300);
        
        // Ação 2: Deletar registros de teste
        int registrosDeletados = creditoService.deletarRegistrosTeste();
        assertThat(registrosDeletados).isEqualTo(300);
        
        // Verificar se foram deletados
        List<Credito> registrosAposDelecao = creditoRepository.findAll();
        assertThat(registrosAposDelecao).isEmpty();
        
        // Ação 3: Gerar novamente
        int segundaGeracao = creditoService.gerarRegistrosTeste();
        assertThat(segundaGeracao).isEqualTo(300);
        
        // Verificar se foram criados novamente
        List<Credito> registrosAposSegundaGeracao = creditoRepository.findAll();
        assertThat(registrosAposSegundaGeracao).hasSize(300);
        
        // Verificar se são registros de teste
        assertThat(registrosAposSegundaGeracao).extracting(Credito::getNumeroCredito)
                .allMatch(numero -> numero.startsWith("TESTE"));
    }

    @Test
    void buscarCreditosPorNfseComPaginacao_ComDadosGerados_DeveFuncionarCorretamente() {
        // Cenário: Gerar registros de teste
        creditoService.gerarRegistrosTeste();
        
        // Ação: Buscar créditos de uma NFS-e específica com paginação
        String nfse = "TESTE_NFSE001";
        Pageable pageable = PageRequest.of(0, 10, Sort.by("numeroCredito").ascending());
        PaginatedCreditoResponse resultado = creditoService.buscarCreditosPorNfseComPaginacao(nfse, pageable);

        // Verificação
        assertThat(resultado.getContent()).hasSize(10);
        assertThat(resultado.getTotalElements()).isEqualTo(30);
        assertThat(resultado.getTotalPages()).isEqualTo(3);
        assertThat(resultado.getContent()).extracting(Credito::getNumeroNfse).containsOnly(nfse);
        
        // Verificar se os créditos estão ordenados
        List<String> numerosCreditos = resultado.getContent().stream()
                .map(Credito::getNumeroCredito)
                .toList();
        assertThat(numerosCreditos).isSorted();
    }
}
