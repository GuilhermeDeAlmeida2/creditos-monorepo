package br.com.guilhermedealmeidafreitas.creditos.repository;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditoRepository extends JpaRepository<Credito, Long> {
    
    /**
     * Busca todos os créditos por número da NFS-e
     * @param numeroNfse Número da NFS-e
     * @return Lista de créditos encontrados
     */
    List<Credito> findByNumeroNfse(String numeroNfse);
    
    /**
     * Busca todos os créditos com paginação
     * @param pageable Configurações de paginação
     * @return Página de créditos
     */
    Page<Credito> findAll(Pageable pageable);
    
    /**
     * Busca créditos por número da NFS-e com paginação
     * @param numeroNfse Número da NFS-e
     * @param pageable Configurações de paginação
     * @return Página de créditos encontrados
     */
    Page<Credito> findByNumeroNfse(String numeroNfse, Pageable pageable);
    
    /**
     * Busca créditos por tipo de crédito com paginação
     * @param tipoCredito Tipo do crédito
     * @param pageable Configurações de paginação
     * @return Página de créditos encontrados
     */
    Page<Credito> findByTipoCredito(String tipoCredito, Pageable pageable);
    
    /**
     * Busca créditos por simples nacional com paginação
     * @param simplesNacional Se é simples nacional
     * @param pageable Configurações de paginação
     * @return Página de créditos encontrados
     */
    Page<Credito> findBySimplesNacional(Boolean simplesNacional, Pageable pageable);
    
    /**
     * Busca créditos com filtros múltiplos e paginação
     * @param numeroNfse Número da NFS-e (opcional)
     * @param tipoCredito Tipo do crédito (opcional)
     * @param simplesNacional Se é simples nacional (opcional)
     * @param pageable Configurações de paginação
     * @return Página de créditos encontrados
     */
    @Query("SELECT c FROM Credito c WHERE " +
           "(:numeroNfse IS NULL OR c.numeroNfse = :numeroNfse) AND " +
           "(:tipoCredito IS NULL OR c.tipoCredito = :tipoCredito) AND " +
           "(:simplesNacional IS NULL OR c.simplesNacional = :simplesNacional)")
    Page<Credito> findByFilters(@Param("numeroNfse") String numeroNfse,
                                @Param("tipoCredito") String tipoCredito,
                                @Param("simplesNacional") Boolean simplesNacional,
                                Pageable pageable);
}

