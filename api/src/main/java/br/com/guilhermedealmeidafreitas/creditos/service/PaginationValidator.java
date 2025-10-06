package br.com.guilhermedealmeidafreitas.creditos.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Serviço responsável pela validação e ajuste de parâmetros de paginação
 */
@Service
public class PaginationValidator {
    
    // Campos válidos para ordenação
    private static final List<String> CAMPOS_VALIDOS_ORDENACAO = Arrays.asList(
        "id", "numeroCredito", "numeroNfse", "dataConstituicao", 
        "valorIssqn", "tipoCredito", "simplesNacional", "aliquota", 
        "valorFaturado", "valorDeducao", "baseCalculo"
    );
    
    // Limites de paginação
    private static final int TAMANHO_MAXIMO_PAGINA = 100;
    private static final int TAMANHO_PADRAO_PAGINA = 10;
    private static final int PAGINA_MINIMA = 0;
    private static final String CAMPO_ORDENACAO_PADRAO = "dataConstituicao";
    private static final String DIRECAO_ORDENACAO_PADRAO = "desc";
    
    /**
     * Valida e ajusta os parâmetros de paginação
     * @param pageable Configurações de paginação originais
     * @return Configurações de paginação validadas
     */
    public Pageable validarPageable(Pageable pageable) {
        int page = validarNumeroPagina(pageable.getPageNumber());
        int size = validarTamanhoPagina(pageable.getPageSize());
        Sort sort = validarOrdenacao(pageable.getSort());
        
        return PageRequest.of(page, size, sort);
    }
    
    /**
     * Valida e ajusta parâmetros de paginação com ordenação customizada
     * @param page Número da página
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDirection Direção da ordenação
     * @return Configurações de paginação validadas
     */
    public Pageable validarPageable(int page, int size, String sortBy, String sortDirection) {
        int pageValidada = validarNumeroPagina(page);
        int sizeValidada = validarTamanhoPagina(size);
        String campoValidado = validarCampoOrdenacao(sortBy);
        Sort.Direction direcaoValidada = validarDirecaoOrdenacao(sortDirection);
        
        return PageRequest.of(pageValidada, sizeValidada, Sort.by(direcaoValidada, campoValidado));
    }
    
    /**
     * Valida o número da página
     * @param page Número da página
     * @return Número da página validado
     */
    public int validarNumeroPagina(int page) {
        return Math.max(page, PAGINA_MINIMA);
    }
    
    /**
     * Valida o tamanho da página
     * @param size Tamanho da página
     * @return Tamanho da página validado
     */
    public int validarTamanhoPagina(int size) {
        if (size <= 0) {
            return TAMANHO_PADRAO_PAGINA;
        }
        return Math.min(size, TAMANHO_MAXIMO_PAGINA);
    }
    
    /**
     * Valida o campo de ordenação
     * @param sortBy Campo para ordenação
     * @return Campo de ordenação validado
     */
    public String validarCampoOrdenacao(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return CAMPO_ORDENACAO_PADRAO;
        }
        
        String campoNormalizado = sortBy.trim();
        if (CAMPOS_VALIDOS_ORDENACAO.contains(campoNormalizado)) {
            return campoNormalizado;
        }
        
        return CAMPO_ORDENACAO_PADRAO;
    }
    
    /**
     * Valida a direção da ordenação
     * @param sortDirection Direção da ordenação
     * @return Direção da ordenação validada
     */
    public Sort.Direction validarDirecaoOrdenacao(String sortDirection) {
        if (sortDirection == null || sortDirection.trim().isEmpty()) {
            return Sort.Direction.valueOf(DIRECAO_ORDENACAO_PADRAO.toUpperCase());
        }
        
        String direcaoNormalizada = sortDirection.trim().toLowerCase();
        if ("asc".equals(direcaoNormalizada)) {
            return Sort.Direction.ASC;
        } else if ("desc".equals(direcaoNormalizada)) {
            return Sort.Direction.DESC;
        }
        
        return Sort.Direction.valueOf(DIRECAO_ORDENACAO_PADRAO.toUpperCase());
    }
    
    /**
     * Valida a ordenação completa
     * @param sort Configuração de ordenação
     * @return Configuração de ordenação validada
     */
    public Sort validarOrdenacao(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return Sort.by(Sort.Direction.valueOf(DIRECAO_ORDENACAO_PADRAO.toUpperCase()), CAMPO_ORDENACAO_PADRAO);
        }
        
        // Se a ordenação já está configurada, mantém como está
        return sort;
    }
    
    /**
     * Retorna a lista de campos válidos para ordenação
     * @return Lista de campos válidos
     */
    public List<String> getCamposValidosOrdenacao() {
        return CAMPOS_VALIDOS_ORDENACAO;
    }
    
    /**
     * Verifica se um campo é válido para ordenação
     * @param campo Campo a ser verificado
     * @return true se o campo é válido
     */
    public boolean isCampoValidoOrdenacao(String campo) {
        return campo != null && CAMPOS_VALIDOS_ORDENACAO.contains(campo.trim());
    }
}
