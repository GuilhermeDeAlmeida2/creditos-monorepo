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
        
        // Criar Pageable com ordenação padrão por data de constituição (mais recente primeiro)
        return org.springframework.data.domain.PageRequest.of(page, size, 
            org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "dataConstituicao"));
    }
}
