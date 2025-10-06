package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.dto.PaginatedCreditoResponse;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.repository.CreditoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interface única e simples - ISP respeitado
 */
public interface CreditoService {
    // Consultas
    Credito buscarCreditoPorNumero(String numeroCredito);
    List<Credito> buscarCreditosPorNfse(String numeroNfse);
    PaginatedCreditoResponse buscarCreditosPorNfseComPaginacao(String numeroNfse, Pageable pageable);
    
    // Testes (apenas em desenvolvimento)
    int gerarRegistrosTeste();
    int deletarRegistrosTeste();
}

