package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.dto.PaginatedCreditoResponse;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.repository.CreditoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementação específica para operações de consulta de créditos.
 * Demonstra o Interface Segregation Principle (ISP) - esta implementação
 * só precisa implementar métodos de consulta, não métodos de teste.
 */
@Service
public class CreditoQueryServiceImpl implements CreditoQueryService {
    
    private final CreditoRepository creditoRepository;
    private final PaginationValidator paginationValidator;
    
    /**
     * Construtor para injeção de dependências seguindo o Dependency Inversion Principle (DIP).
     * Torna as dependências explícitas e facilita testes unitários.
     */
    public CreditoQueryServiceImpl(CreditoRepository creditoRepository,
                                 PaginationValidator paginationValidator) {
        this.creditoRepository = creditoRepository;
        this.paginationValidator = paginationValidator;
    }
    
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
        // Validar parâmetros usando o serviço dedicado
        Pageable validPageable = paginationValidator.validarPageable(pageable);
        
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
}
