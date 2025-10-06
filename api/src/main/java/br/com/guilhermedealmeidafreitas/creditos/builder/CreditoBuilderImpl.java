package br.com.guilhermedealmeidafreitas.creditos.builder;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.service.TaxCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Implementação do Builder Pattern para Credito.
 * Fornece construção fluente e flexível com validações e cálculos automáticos.
 * 
 * REFATORAÇÃO: Implementa Builder Pattern para melhorar a criação
 * de objetos complexos com muitos parâmetros.
 */
@Component
public class CreditoBuilderImpl implements CreditoBuilder {
    
    private final TaxCalculationService taxCalculationService;
    
    // Campos do Credito
    private Long id;
    private String numeroCredito;
    private String numeroNfse;
    private LocalDate dataConstituicao;
    private String tipoCredito;
    private Boolean simplesNacional;
    private BigDecimal aliquota;
    private BigDecimal valorFaturado;
    private BigDecimal valorDeducao;
    private BigDecimal baseCalculo;
    private BigDecimal valorIssqn;
    
    @Autowired
    public CreditoBuilderImpl(TaxCalculationService taxCalculationService) {
        this.taxCalculationService = taxCalculationService;
        reset();
    }
    
    @Override
    public CreditoBuilder withNumeroCredito(String numeroCredito) {
        this.numeroCredito = numeroCredito;
        return this;
    }
    
    @Override
    public CreditoBuilder withNumeroNfse(String numeroNfse) {
        this.numeroNfse = numeroNfse;
        return this;
    }
    
    @Override
    public CreditoBuilder withDataConstituicao(LocalDate dataConstituicao) {
        this.dataConstituicao = dataConstituicao;
        return this;
    }
    
    @Override
    public CreditoBuilder withTipoCredito(String tipoCredito) {
        this.tipoCredito = tipoCredito;
        return this;
    }
    
    @Override
    public CreditoBuilder withSimplesNacional(Boolean simplesNacional) {
        this.simplesNacional = simplesNacional;
        return this;
    }
    
    @Override
    public CreditoBuilder withAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
        return this;
    }
    
    @Override
    public CreditoBuilder withValorFaturado(BigDecimal valorFaturado) {
        this.valorFaturado = valorFaturado;
        return this;
    }
    
    @Override
    public CreditoBuilder withValorDeducao(BigDecimal valorDeducao) {
        this.valorDeducao = valorDeducao;
        return this;
    }
    
    @Override
    public CreditoBuilder withValoresFiscais(BigDecimal valorFaturado, BigDecimal valorDeducao) {
        this.valorFaturado = valorFaturado;
        this.valorDeducao = valorDeducao;
        // Base de cálculo será calculada automaticamente no build()
        return this;
    }
    
    @Override
    public CreditoBuilder withValoresFiscaisCompletos(BigDecimal valorFaturado, BigDecimal valorDeducao, 
                                                     BigDecimal baseCalculo, BigDecimal valorIssqn) {
        this.valorFaturado = valorFaturado;
        this.valorDeducao = valorDeducao;
        this.baseCalculo = baseCalculo;
        this.valorIssqn = valorIssqn;
        return this;
    }
    
    @Override
    public CreditoBuilder withId(Long id) {
        this.id = id;
        return this;
    }
    
    @Override
    public Credito build() {
        // Validar campos obrigatórios
        validateRequiredFields();
        
        // Calcular valores fiscais se necessário
        calculateFiscalValues();
        
        // Criar e retornar o objeto Credito
        Credito credito = new Credito();
        credito.setId(id);
        credito.setNumeroCredito(numeroCredito);
        credito.setNumeroNfse(numeroNfse);
        credito.setDataConstituicao(dataConstituicao);
        credito.setTipoCredito(tipoCredito);
        credito.setSimplesNacional(simplesNacional);
        credito.setAliquota(aliquota);
        credito.setValorFaturado(valorFaturado);
        credito.setValorDeducao(valorDeducao);
        credito.setBaseCalculo(baseCalculo);
        credito.setValorIssqn(valorIssqn);
        
        return credito;
    }
    
    @Override
    public Credito buildWithoutValidation() {
        // Criar objeto sem validações (para casos especiais)
        Credito credito = new Credito();
        credito.setId(id);
        credito.setNumeroCredito(numeroCredito);
        credito.setNumeroNfse(numeroNfse);
        credito.setDataConstituicao(dataConstituicao);
        credito.setTipoCredito(tipoCredito);
        credito.setSimplesNacional(simplesNacional);
        credito.setAliquota(aliquota);
        credito.setValorFaturado(valorFaturado);
        credito.setValorDeducao(valorDeducao);
        credito.setBaseCalculo(baseCalculo);
        credito.setValorIssqn(valorIssqn);
        
        return credito;
    }
    
    @Override
    public CreditoBuilder reset() {
        this.id = null;
        this.numeroCredito = null;
        this.numeroNfse = null;
        this.dataConstituicao = null;
        this.tipoCredito = null;
        this.simplesNacional = null;
        this.aliquota = null;
        this.valorFaturado = null;
        this.valorDeducao = null;
        this.baseCalculo = null;
        this.valorIssqn = null;
        return this;
    }
    
    /**
     * Valida campos obrigatórios.
     * 
     * @throws IllegalArgumentException se algum campo obrigatório estiver ausente
     */
    private void validateRequiredFields() {
        if (numeroCredito == null || numeroCredito.trim().isEmpty()) {
            throw new IllegalArgumentException("Número do crédito é obrigatório");
        }
        
        if (numeroNfse == null || numeroNfse.trim().isEmpty()) {
            throw new IllegalArgumentException("Número da NFS-e é obrigatório");
        }
        
        if (dataConstituicao == null) {
            throw new IllegalArgumentException("Data de constituição é obrigatória");
        }
        
        if (tipoCredito == null || tipoCredito.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo do crédito é obrigatório");
        }
        
        if (simplesNacional == null) {
            throw new IllegalArgumentException("Simples Nacional é obrigatório");
        }
        
        if (aliquota == null) {
            throw new IllegalArgumentException("Alíquota é obrigatória");
        }
        
        if (valorFaturado == null) {
            throw new IllegalArgumentException("Valor faturado é obrigatório");
        }
        
        if (valorDeducao == null) {
            throw new IllegalArgumentException("Valor dedução é obrigatório");
        }
    }
    
    /**
     * Calcula valores fiscais automaticamente se necessário.
     */
    private void calculateFiscalValues() {
        // Calcular base de cálculo se não foi definida
        if (baseCalculo == null && valorFaturado != null && valorDeducao != null) {
            baseCalculo = taxCalculationService.calcularBaseCalculo(valorFaturado, valorDeducao);
        }
        
        // Calcular valor do ISS se não foi definido
        if (valorIssqn == null && baseCalculo != null && aliquota != null) {
            valorIssqn = taxCalculationService.calcularValorIssqn(baseCalculo, aliquota);
        }
    }
}
