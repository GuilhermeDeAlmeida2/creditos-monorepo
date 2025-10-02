package br.com.guilhermedealmeidafreitas.creditos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "credito")
public class Credito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_credito", nullable = false)
    private String numeroCredito;
    
    @Column(name = "numero_nfse", nullable = false)
    private String numeroNfse;
    
    @Column(name = "data_constituicao", nullable = false)
    private LocalDate dataConstituicao;
    
    @Column(name = "valor_issqn", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorIssqn;
    
    @Column(name = "tipo_credito", nullable = false)
    private String tipoCredito;
    
    @Column(name = "simples_nacional", nullable = false)
    private Boolean simplesNacional;
    
    @Column(name = "aliquota", nullable = false, precision = 5, scale = 2)
    private BigDecimal aliquota;
    
    @Column(name = "valor_faturado", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorFaturado;
    
    @Column(name = "valor_deducao", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorDeducao;
    
    @Column(name = "base_calculo", nullable = false, precision = 15, scale = 2)
    private BigDecimal baseCalculo;
    
    // Construtores
    public Credito() {}
    
    public Credito(String numeroCredito, String numeroNfse, LocalDate dataConstituicao, 
                   BigDecimal valorIssqn, String tipoCredito, Boolean simplesNacional, 
                   BigDecimal aliquota, BigDecimal valorFaturado, BigDecimal valorDeducao, 
                   BigDecimal baseCalculo) {
        this.numeroCredito = numeroCredito;
        this.numeroNfse = numeroNfse;
        this.dataConstituicao = dataConstituicao;
        this.valorIssqn = valorIssqn;
        this.tipoCredito = tipoCredito;
        this.simplesNacional = simplesNacional;
        this.aliquota = aliquota;
        this.valorFaturado = valorFaturado;
        this.valorDeducao = valorDeducao;
        this.baseCalculo = baseCalculo;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNumeroCredito() {
        return numeroCredito;
    }
    
    public void setNumeroCredito(String numeroCredito) {
        this.numeroCredito = numeroCredito;
    }
    
    public String getNumeroNfse() {
        return numeroNfse;
    }
    
    public void setNumeroNfse(String numeroNfse) {
        this.numeroNfse = numeroNfse;
    }
    
    public LocalDate getDataConstituicao() {
        return dataConstituicao;
    }
    
    public void setDataConstituicao(LocalDate dataConstituicao) {
        this.dataConstituicao = dataConstituicao;
    }
    
    public BigDecimal getValorIssqn() {
        return valorIssqn;
    }
    
    public void setValorIssqn(BigDecimal valorIssqn) {
        this.valorIssqn = valorIssqn;
    }
    
    public String getTipoCredito() {
        return tipoCredito;
    }
    
    public void setTipoCredito(String tipoCredito) {
        this.tipoCredito = tipoCredito;
    }
    
    public Boolean getSimplesNacional() {
        return simplesNacional;
    }
    
    public void setSimplesNacional(Boolean simplesNacional) {
        this.simplesNacional = simplesNacional;
    }
    
    public BigDecimal getAliquota() {
        return aliquota;
    }
    
    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }
    
    public BigDecimal getValorFaturado() {
        return valorFaturado;
    }
    
    public void setValorFaturado(BigDecimal valorFaturado) {
        this.valorFaturado = valorFaturado;
    }
    
    public BigDecimal getValorDeducao() {
        return valorDeducao;
    }
    
    public void setValorDeducao(BigDecimal valorDeducao) {
        this.valorDeducao = valorDeducao;
    }
    
    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }
    
    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }
}

