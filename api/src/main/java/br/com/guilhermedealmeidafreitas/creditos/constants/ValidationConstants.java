package br.com.guilhermedealmeidafreitas.creditos.constants;

import java.util.Set;

/**
 * Constantes centralizadas para validações do sistema.
 * 
 * REFATORAÇÃO DRY: Centraliza todas as constantes de validação que estavam
 * duplicadas em múltiplos arquivos, eliminando violações do princípio DRY.
 * 
 * @author Guilherme de Almeida Freitas
 */
public class ValidationConstants {
    
    /**
     * Campos válidos para ordenação em consultas paginadas.
     * Centraliza a definição que estava duplicada em 3 locais diferentes.
     */
    public static final Set<String> VALID_SORT_FIELDS = Set.of(
        "id", "numeroCredito", "numeroNfse", "dataConstituicao", 
        "valorIssqn", "tipoCredito", "simplesNacional", "aliquota", 
        "valorFaturado", "valorDeducao", "baseCalculo"
    );
    
    /**
     * Tamanho padrão da página para paginação.
     */
    public static final int DEFAULT_PAGE_SIZE = 10;
    
    /**
     * Tamanho máximo permitido para páginas.
     */
    public static final int MAX_PAGE_SIZE = 100;
    
    /**
     * Campo padrão para ordenação.
     */
    public static final String DEFAULT_SORT_FIELD = "id";
    
    /**
     * Direção padrão para ordenação.
     */
    public static final String DEFAULT_SORT_DIRECTION = "ASC";
    
    /**
     * Construtor privado para evitar instanciação da classe utilitária.
     */
    private ValidationConstants() {
        throw new UnsupportedOperationException("Classe utilitária não deve ser instanciada");
    }
}
