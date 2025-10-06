package br.com.guilhermedealmeidafreitas.creditos.builder;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Interface para o Builder Pattern de Credito.
 * Permite construção fluente e flexível de objetos Credito.
 * 
 * REFATORAÇÃO: Implementa Builder Pattern para melhorar a criação
 * de objetos complexos com muitos parâmetros.
 */
public interface CreditoBuilder {
    
    /**
     * Define o número do crédito.
     * 
     * @param numeroCredito Número do crédito
     * @return Builder para encadeamento fluente
     */
    CreditoBuilder withNumeroCredito(String numeroCredito);
    
    /**
     * Define o número da NFS-e.
     * 
     * @param numeroNfse Número da NFS-e
     * @return Builder para encadeamento fluente
     */
    CreditoBuilder withNumeroNfse(String numeroNfse);
    
    /**
     * Define a data de constituição.
     * 
     * @param dataConstituicao Data de constituição
     * @return Builder para encadeamento fluente
     */
    CreditoBuilder withDataConstituicao(LocalDate dataConstituicao);
    
    /**
     * Define o tipo do crédito.
     * 
     * @param tipoCredito Tipo do crédito (ex: "ISS", "IPI", "ICMS")
     * @return Builder para encadeamento fluente
     */
    CreditoBuilder withTipoCredito(String tipoCredito);
    
    /**
     * Define se é do Simples Nacional.
     * 
     * @param simplesNacional true se é do Simples Nacional
     * @return Builder para encadeamento fluente
     */
    CreditoBuilder withSimplesNacional(Boolean simplesNacional);
    
    /**
     * Define a alíquota.
     * 
     * @param aliquota Alíquota em percentual (ex: 5.0 para 5%)
     * @return Builder para encadeamento fluente
     */
    CreditoBuilder withAliquota(BigDecimal aliquota);
    
    /**
     * Define o valor faturado.
     * 
     * @param valorFaturado Valor total faturado
     * @return Builder para encadeamento fluente
     */
    CreditoBuilder withValorFaturado(BigDecimal valorFaturado);
    
    /**
     * Define o valor de dedução.
     * 
     * @param valorDeducao Valor das deduções
     * @return Builder para encadeamento fluente
     */
    CreditoBuilder withValorDeducao(BigDecimal valorDeducao);
    
    /**
     * Define valores fiscais básicos (faturado e dedução).
     * Calcula automaticamente a base de cálculo.
     * 
     * @param valorFaturado Valor total faturado
     * @param valorDeducao Valor das deduções
     * @return Builder para encadeamento fluente
     */
    CreditoBuilder withValoresFiscais(BigDecimal valorFaturado, BigDecimal valorDeducao);
    
    /**
     * Define todos os valores fiscais (faturado, dedução, base de cálculo e ISS).
     * Útil quando os valores já estão calculados.
     * 
     * @param valorFaturado Valor total faturado
     * @param valorDeducao Valor das deduções
     * @param baseCalculo Base de cálculo
     * @param valorIssqn Valor do ISS
     * @return Builder para encadeamento fluente
     */
    CreditoBuilder withValoresFiscaisCompletos(BigDecimal valorFaturado, BigDecimal valorDeducao, 
                                             BigDecimal baseCalculo, BigDecimal valorIssqn);
    
    /**
     * Define o ID (geralmente usado apenas para testes ou atualizações).
     * 
     * @param id ID do crédito
     * @return Builder para encadeamento fluente
     */
    CreditoBuilder withId(Long id);
    
    /**
     * Constrói o objeto Credito com validações e cálculos automáticos.
     * 
     * @return Objeto Credito construído e validado
     * @throws IllegalArgumentException se algum parâmetro obrigatório estiver ausente ou inválido
     */
    Credito build();
    
    /**
     * Constrói o objeto Credito sem validações (para casos especiais).
     * Use com cuidado - não recomenda-se para uso geral.
     * 
     * @return Objeto Credito construído sem validações
     */
    Credito buildWithoutValidation();
    
    /**
     * Reseta o builder para construir um novo objeto.
     * 
     * @return Builder resetado
     */
    CreditoBuilder reset();
}
