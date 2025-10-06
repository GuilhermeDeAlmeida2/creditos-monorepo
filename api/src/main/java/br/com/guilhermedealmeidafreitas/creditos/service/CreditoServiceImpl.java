package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.dto.PaginatedCreditoResponse;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.repository.CreditoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementação única e simples - SRP respeitado
 * Cada método tem uma responsabilidade clara
 */
@Service
public class CreditoServiceImpl implements CreditoService {
    
    private final CreditoRepository creditoRepository;
    private final ValidationService validationService;
    private final TestDataGeneratorService testDataGeneratorService;
    
    // Construtor para DIP
    public CreditoServiceImpl(CreditoRepository creditoRepository,
                            ValidationService validationService,
                            TestDataGeneratorService testDataGeneratorService) {
        this.creditoRepository = creditoRepository;
        this.validationService = validationService;
        this.testDataGeneratorService = testDataGeneratorService;
    }
    
    @Override
    public Credito buscarCreditoPorNumero(String numeroCredito) {
        validationService.validateStringInput(numeroCredito, "Número do crédito");
        return creditoRepository.findByNumeroCredito(numeroCredito);
    }
    
    @Override
    public List<Credito> buscarCreditosPorNfse(String numeroNfse) {
        validationService.validateStringInput(numeroNfse, "Número da NFS-e");
        return creditoRepository.findByNumeroNfse(numeroNfse);
    }
    
    @Override
    public PaginatedCreditoResponse buscarCreditosPorNfseComPaginacao(String numeroNfse, Pageable pageable) {
        validationService.validateStringInput(numeroNfse, "Número da NFS-e");
        
        // Usar o ValidationService para validar o Pageable
        Pageable validPageable = validationService.validateAndCreatePageable(
            pageable.getPageNumber(), 
            pageable.getPageSize(), 
            pageable.getSort().toString().contains("ASC") ? "asc" : "desc", 
            "desc"
        );
        
        Page<Credito> creditosPage = creditoRepository.findByNumeroNfse(numeroNfse, validPageable);
        
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
    public int gerarRegistrosTeste() {
        return testDataGeneratorService.gerarRegistrosTeste();
    }
    
    @Override
    public int deletarRegistrosTeste() {
        return testDataGeneratorService.deletarRegistrosTeste();
    }
}
