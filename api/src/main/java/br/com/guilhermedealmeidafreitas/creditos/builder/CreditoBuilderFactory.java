package br.com.guilhermedealmeidafreitas.creditos.builder;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

/**
 * Factory para criação de CreditoBuilder.
 * Fornece métodos de conveniência para cenários comuns de criação de créditos.
 * 
 * REFATORAÇÃO: Implementa Factory Pattern em conjunto com Builder Pattern
 * para facilitar a criação de objetos Credito.
 */
@Component
public class CreditoBuilderFactory {
    
    private final CreditoBuilder creditoBuilder;
    
    @Autowired
    public CreditoBuilderFactory(CreditoBuilder creditoBuilder) {
        this.creditoBuilder = creditoBuilder;
    }
    
    /**
     * Cria um novo builder resetado.
     * 
     * @return Novo CreditoBuilder
     */
    public CreditoBuilder newBuilder() {
        return creditoBuilder.reset();
    }
    
    /**
     * Cria um builder com dados básicos pré-configurados.
     * 
     * @param numeroCredito Número do crédito
     * @param numeroNfse Número da NFS-e
     * @param dataConstituicao Data de constituição
     * @param tipoCredito Tipo do crédito
     * @return Builder com dados básicos configurados
     */
    public CreditoBuilder withBasicData(String numeroCredito, String numeroNfse, 
                                       LocalDate dataConstituicao, String tipoCredito) {
        return newBuilder()
            .withNumeroCredito(numeroCredito)
            .withNumeroNfse(numeroNfse)
            .withDataConstituicao(dataConstituicao)
            .withTipoCredito(tipoCredito);
    }
    
    /**
     * Cria um builder para crédito do Simples Nacional.
     * 
     * @param numeroCredito Número do crédito
     * @param numeroNfse Número da NFS-e
     * @param dataConstituicao Data de constituição
     * @param tipoCredito Tipo do crédito
     * @param aliquota Alíquota
     * @param valorFaturado Valor faturado
     * @param valorDeducao Valor dedução
     * @return Builder configurado para Simples Nacional
     */
    public CreditoBuilder forSimplesNacional(String numeroCredito, String numeroNfse, 
                                            LocalDate dataConstituicao, String tipoCredito,
                                            BigDecimal aliquota, BigDecimal valorFaturado, 
                                            BigDecimal valorDeducao) {
        return withBasicData(numeroCredito, numeroNfse, dataConstituicao, tipoCredito)
            .withSimplesNacional(true)
            .withAliquota(aliquota)
            .withValoresFiscais(valorFaturado, valorDeducao);
    }
    
    /**
     * Cria um builder para crédito fora do Simples Nacional.
     * 
     * @param numeroCredito Número do crédito
     * @param numeroNfse Número da NFS-e
     * @param dataConstituicao Data de constituição
     * @param tipoCredito Tipo do crédito
     * @param aliquota Alíquota
     * @param valorFaturado Valor faturado
     * @param valorDeducao Valor dedução
     * @return Builder configurado para fora do Simples Nacional
     */
    public CreditoBuilder forRegularTaxpayer(String numeroCredito, String numeroNfse, 
                                            LocalDate dataConstituicao, String tipoCredito,
                                            BigDecimal aliquota, BigDecimal valorFaturado, 
                                            BigDecimal valorDeducao) {
        return withBasicData(numeroCredito, numeroNfse, dataConstituicao, tipoCredito)
            .withSimplesNacional(false)
            .withAliquota(aliquota)
            .withValoresFiscais(valorFaturado, valorDeducao);
    }
    
    /**
     * Cria um builder para crédito de teste.
     * 
     * @param numeroCredito Número do crédito (deve começar com "TESTE")
     * @param numeroNfse Número da NFS-e
     * @param dataConstituicao Data de constituição
     * @param tipoCredito Tipo do crédito
     * @param aliquota Alíquota
     * @param valorFaturado Valor faturado
     * @param valorDeducao Valor dedução
     * @return Builder configurado para dados de teste
     */
    public CreditoBuilder forTestData(String numeroCredito, String numeroNfse, 
                                     LocalDate dataConstituicao, String tipoCredito,
                                     BigDecimal aliquota, BigDecimal valorFaturado, 
                                     BigDecimal valorDeducao) {
        return withBasicData(numeroCredito, numeroNfse, dataConstituicao, tipoCredito)
            .withSimplesNacional(new Random().nextBoolean()) // Aleatório para teste
            .withAliquota(aliquota)
            .withValoresFiscais(valorFaturado, valorDeducao);
    }
    
    /**
     * Cria um builder a partir de um objeto Credito existente.
     * Útil para criar cópias ou modificações.
     * 
     * @param credito Credito existente
     * @return Builder com dados do crédito existente
     */
    public CreditoBuilder fromExisting(Credito credito) {
        return newBuilder()
            .withId(credito.getId())
            .withNumeroCredito(credito.getNumeroCredito())
            .withNumeroNfse(credito.getNumeroNfse())
            .withDataConstituicao(credito.getDataConstituicao())
            .withTipoCredito(credito.getTipoCredito())
            .withSimplesNacional(credito.getSimplesNacional())
            .withAliquota(credito.getAliquota())
            .withValoresFiscaisCompletos(
                credito.getValorFaturado(),
                credito.getValorDeducao(),
                credito.getBaseCalculo(),
                credito.getValorIssqn()
            );
    }
    
    /**
     * Cria um builder para crédito ISS padrão.
     * 
     * @param numeroCredito Número do crédito
     * @param numeroNfse Número da NFS-e
     * @param dataConstituicao Data de constituição
     * @param aliquota Alíquota do ISS
     * @param valorFaturado Valor faturado
     * @param valorDeducao Valor dedução
     * @return Builder configurado para crédito ISS
     */
    public CreditoBuilder forIssCredito(String numeroCredito, String numeroNfse, 
                                       LocalDate dataConstituicao, BigDecimal aliquota, 
                                       BigDecimal valorFaturado, BigDecimal valorDeducao) {
        return withBasicData(numeroCredito, numeroNfse, dataConstituicao, "ISS")
            .withSimplesNacional(false) // ISS geralmente não é Simples Nacional
            .withAliquota(aliquota)
            .withValoresFiscais(valorFaturado, valorDeducao);
    }
}
