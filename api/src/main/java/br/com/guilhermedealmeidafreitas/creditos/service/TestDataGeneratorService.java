package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.repository.CreditoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Serviço responsável pela geração e gerenciamento de dados de teste
 */
@Service
public class TestDataGeneratorService {
    
    private final CreditoRepository creditoRepository;
    private final TaxCalculationService taxCalculationService;
    
    /**
     * Construtor para injeção de dependências seguindo o Dependency Inversion Principle (DIP).
     * Torna as dependências explícitas e facilita testes unitários.
     */
    public TestDataGeneratorService(CreditoRepository creditoRepository,
                                  TaxCalculationService taxCalculationService) {
        this.creditoRepository = creditoRepository;
        this.taxCalculationService = taxCalculationService;
    }
    
    /**
     * Gera 300 registros aleatórios de teste
     * @return Número de registros gerados
     */
    @Transactional
    public int gerarRegistrosTeste() {
        List<Credito> registrosTeste = new ArrayList<>();
        Random random = new Random();
        
        // Arrays com valores válidos para gerar dados realistas
        String[] tiposCredito = {"ISS", "IPI", "ICMS", "PIS", "COFINS", "IR", "CSLL"};
        boolean[] valoresSimplesNacional = {true, false};
        
        // Gerar 10 NFS-e diferentes, cada uma com 30 créditos (300 total)
        // Estrutura: TESTE_NFSE001 terá créditos TESTE000001 a TESTE000030
        //           TESTE_NFSE002 terá créditos TESTE000031 a TESTE000060
        //           ... e assim por diante até TESTE_NFSE010
        for (int nfseIndex = 1; nfseIndex <= 10; nfseIndex++) {
            String numeroNfse = String.format("TESTE_NFSE%03d", nfseIndex);
            
            // Cada NFS-e terá 30 créditos associados
            for (int creditoIndex = 1; creditoIndex <= 30; creditoIndex++) {
                // Calcular o índice global do crédito (1-300)
                int creditoGlobalIndex = (nfseIndex - 1) * 30 + creditoIndex;
                String numeroCredito = String.format("TESTE%06d", creditoGlobalIndex);
                
                // Gerar dados aleatórios mas válidos
                LocalDate dataConstituicao = LocalDate.now().minusDays(random.nextInt(365));
                
                // Valores monetários realistas
                BigDecimal valorFaturado = BigDecimal.valueOf(random.nextDouble() * 50000 + 1000)
                    .setScale(2, RoundingMode.HALF_UP);
                BigDecimal valorDeducao = valorFaturado.multiply(BigDecimal.valueOf(random.nextDouble() * 0.3))
                    .setScale(2, RoundingMode.HALF_UP);
                BigDecimal baseCalculo = valorFaturado.subtract(valorDeducao);
                
                // Alíquota entre 1% e 15%
                BigDecimal aliquota = BigDecimal.valueOf(random.nextDouble() * 14 + 1)
                    .setScale(2, RoundingMode.HALF_UP);
                
                String tipoCredito = tiposCredito[random.nextInt(tiposCredito.length)];
                Boolean simplesNacional = valoresSimplesNacional[random.nextInt(valoresSimplesNacional.length)];
                
                // REFATORAÇÃO: Usar o factory method da entidade que calcula automaticamente os valores fiscais
                Credito credito = Credito.criarComCalculosFiscais(
                    numeroCredito,
                    numeroNfse,
                    dataConstituicao,
                    tipoCredito,
                    simplesNacional,
                    aliquota,
                    valorFaturado,
                    valorDeducao
                );
                
                registrosTeste.add(credito);
            }
        }
        
        // Salvar todos os registros em batch
        creditoRepository.saveAll(registrosTeste);
        
        return registrosTeste.size();
    }
    
    /**
     * Remove todos os registros de teste
     * @return Número de registros removidos
     */
    @Transactional
    public int deletarRegistrosTeste() {
        // Buscar registros de teste antes de deletar para contar
        List<Credito> registrosTeste = creditoRepository.findTestRecords();
        int quantidade = registrosTeste.size();
        
        // Deletar registros de teste
        creditoRepository.deleteTestRecords();
        
        return quantidade;
    }
}
