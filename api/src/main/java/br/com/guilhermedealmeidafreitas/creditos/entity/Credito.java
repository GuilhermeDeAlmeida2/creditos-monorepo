package br.com.guilhermedealmeidafreitas.creditos.entity;

import br.com.guilhermedealmeidafreitas.creditos.exception.ValidationException;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

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
    
    // ===== LÓGICA DE NEGÓCIO SIMPLES =====
    // Métodos básicos que encapsulam cálculos simples da entidade Credito
    
    /**
     * Calcula a base de cálculo subtraindo as deduções do valor faturado.
     * 
     * @return Base de cálculo calculada
     * @throws ValidationException se os valores são negativos
     */
    public BigDecimal calcularBaseCalculo() {
        if (valorFaturado == null || valorDeducao == null) {
            return BigDecimal.ZERO;
        }
        
        // Verificação básica: valores não podem ser negativos
        if (valorFaturado.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Valor faturado não pode ser negativo");
        }
        
        if (valorDeducao.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Valor dedução não pode ser negativo");
        }
        
        return valorFaturado.subtract(valorDeducao);
    }
    
    /**
     * Calcula o valor do ISS baseado na base de cálculo e alíquota.
     * 
     * @return Valor do ISS calculado
     * @throws ValidationException se os valores são negativos
     */
    public BigDecimal calcularValorIssqn() {
        if (baseCalculo == null || aliquota == null) {
            return BigDecimal.ZERO;
        }
        
        // Verificação básica: valores não podem ser negativos
        if (baseCalculo.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Base de cálculo não pode ser negativa");
        }
        
        if (aliquota.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Alíquota não pode ser negativa");
        }
        
        // Valor do ISS = base de cálculo * (alíquota / 100)
        return baseCalculo.multiply(aliquota.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
                          .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Recalcula todos os valores fiscais baseados nos valores de entrada.
     */
    public void recalcularValoresFiscais() {
        this.baseCalculo = calcularBaseCalculo();
        this.valorIssqn = calcularValorIssqn();
    }
    
    /**
     * Factory method para criar um crédito com cálculos fiscais automáticos.
     * 
     * @param numeroCredito Número do crédito
     * @param numeroNfse Número da NFS-e
     * @param dataConstituicao Data de constituição
     * @param tipoCredito Tipo do crédito
     * @param simplesNacional Se é do Simples Nacional
     * @param aliquota Alíquota
     * @param valorFaturado Valor faturado
     * @param valorDeducao Valor dedução
     * @return Crédito criado com valores fiscais calculados
     */
    public static Credito criarComCalculosFiscais(String numeroCredito, String numeroNfse, 
                                                 LocalDate dataConstituicao, String tipoCredito, 
                                                 Boolean simplesNacional, BigDecimal aliquota, 
                                                 BigDecimal valorFaturado, BigDecimal valorDeducao) {
        Credito credito = new Credito();
        credito.numeroCredito = numeroCredito;
        credito.numeroNfse = numeroNfse;
        credito.dataConstituicao = dataConstituicao;
        credito.tipoCredito = tipoCredito;
        credito.simplesNacional = simplesNacional;
        credito.aliquota = aliquota;
        credito.valorFaturado = valorFaturado;
        credito.valorDeducao = valorDeducao;
        
        // Recalcula automaticamente os valores fiscais
        credito.recalcularValoresFiscais();
        
        return credito;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credito credito = (Credito) o;
        return Objects.equals(id, credito.id) &&
               Objects.equals(numeroCredito, credito.numeroCredito) &&
               Objects.equals(numeroNfse, credito.numeroNfse) &&
               Objects.equals(dataConstituicao, credito.dataConstituicao);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, numeroCredito, numeroNfse, dataConstituicao);
    }
    
    @Override
    public String toString() {
        return "Credito{" +
                "id=" + id +
                ", numeroCredito='" + numeroCredito + '\'' +
                ", numeroNfse='" + numeroNfse + '\'' +
                ", dataConstituicao=" + dataConstituicao +
                ", valorIssqn=" + valorIssqn +
                ", tipoCredito='" + tipoCredito + '\'' +
                ", simplesNacional=" + simplesNacional +
                ", aliquota=" + aliquota +
                ", valorFaturado=" + valorFaturado +
                ", valorDeducao=" + valorDeducao +
                ", baseCalculo=" + baseCalculo +
                '}';
    }
}

