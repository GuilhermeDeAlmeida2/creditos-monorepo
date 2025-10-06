package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.dto.PaginatedCreditoResponse;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.repository.CreditoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implementação dos serviços relacionados a créditos
 */
@Service
public class CreditoServiceImpl implements CreditoService {
    
    @Autowired
    private CreditoRepository creditoRepository;
    
    @Override
    public Credito buscarCreditoPorNumero(String numeroCredito) {
        return creditoRepository.findByNumeroCredito(numeroCredito);
    }
    
    @Override
    public List<Credito> buscarCreditosPorNfse(String numeroNfse) {
        return creditoRepository.findByNumeroNfse(numeroNfse);
    }
    
    @Override
    public PaginatedCreditoResponse buscarCreditosPorNfseComPaginacao(String numeroNfse, Pageable pageable) {
        // Validar parâmetros
        Pageable validPageable = validarPageable(pageable);
        
        // Buscar créditos por NFS-e com paginação
        Page<Credito> creditosPage = creditoRepository.findByNumeroNfse(numeroNfse, validPageable);
        
        // Converter para DTO
        return new PaginatedCreditoResponse(
            creditosPage.getContent(),
            creditosPage.getNumber(),
            creditosPage.getSize(),
            creditosPage.getTotalElements(),
            creditosPage.getTotalPages(),
            creditosPage.isFirst(),
            creditosPage.isLast(),
            creditosPage.hasNext(),
            creditosPage.hasPrevious()
        );
    }
    
    @Override
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
                
                // Valor do ISS = base de cálculo * alíquota
                BigDecimal valorIssqn = baseCalculo.multiply(aliquota.divide(BigDecimal.valueOf(100)))
                    .setScale(2, RoundingMode.HALF_UP);
                
                String tipoCredito = tiposCredito[random.nextInt(tiposCredito.length)];
                Boolean simplesNacional = valoresSimplesNacional[random.nextInt(valoresSimplesNacional.length)];
                
                Credito credito = new Credito(
                    numeroCredito,
                    numeroNfse,
                    dataConstituicao,
                    valorIssqn,
                    tipoCredito,
                    simplesNacional,
                    aliquota,
                    valorFaturado,
                    valorDeducao,
                    baseCalculo
                );
                
                registrosTeste.add(credito);
            }
        }
        
        // Salvar todos os registros em batch
        creditoRepository.saveAll(registrosTeste);
        
        return registrosTeste.size();
    }
    
    @Override
    @Transactional
    public int deletarRegistrosTeste() {
        // Buscar registros de teste antes de deletar para contar
        List<Credito> registrosTeste = creditoRepository.findTestRecords();
        int quantidade = registrosTeste.size();
        
        // Deletar registros de teste
        creditoRepository.deleteTestRecords();
        
        return quantidade;
    }
    
    /**
     * Valida e ajusta os parâmetros de paginação
     * @param pageable Configurações de paginação originais
     * @return Configurações de paginação validadas
     */
    private Pageable validarPageable(Pageable pageable) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        
        // Validar página
        if (page < 0) page = 0;
        
        // Validar tamanho
        if (size <= 0) size = 10;
        if (size > 100) size = 100; // Limite máximo
        
        // Retornar novo Pageable com os valores validados
        return PageRequest.of(page, size, pageable.getSort());
    }
}
