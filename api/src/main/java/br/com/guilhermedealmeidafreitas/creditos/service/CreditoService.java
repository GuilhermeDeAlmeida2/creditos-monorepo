package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.dto.PaginatedCreditoResponse;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface para serviços relacionados a créditos
 */
public interface CreditoService {
    
    /**
     * Busca um crédito específico por número do crédito
     * @param numeroCredito Número do crédito
     * @return Crédito encontrado ou null se não existir
     */
    Credito buscarCreditoPorNumero(String numeroCredito);
    
    /**
     * Busca todos os créditos por número da NFS-e
     * @param numeroNfse Número da NFS-e
     * @return Lista de créditos encontrados
     */
    List<Credito> buscarCreditosPorNfse(String numeroNfse);
    
    /**
     * Busca créditos por número da NFS-e com paginação
     * @param numeroNfse Número da NFS-e
     * @param pageable Configurações de paginação
     * @return Resposta paginada com créditos encontrados
     */
    PaginatedCreditoResponse buscarCreditosPorNfseComPaginacao(String numeroNfse, Pageable pageable);
    
    /**
     * Gera 300 registros aleatórios de teste
     * @return Número de registros gerados
     */
    int gerarRegistrosTeste();
    
    /**
     * Remove todos os registros de teste
     * @return Número de registros removidos
     */
    int deletarRegistrosTeste();
}
