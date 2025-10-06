package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.exception.CreditoExceptions;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Serviço focado apenas em cálculos fiscais - SRP
 * Mantém DRY (cálculos centralizados)
 */
@Service
public class TaxCalculationService {
    
    /**
     * Calcula valor do ISS - método simples e direto
     */
    public BigDecimal calcularValorIssqn(BigDecimal baseCalculo, BigDecimal aliquota) {
        if (baseCalculo == null || aliquota == null) {
            return BigDecimal.ZERO;
        }
        
        if (baseCalculo.compareTo(BigDecimal.ZERO) < 0) {
            throw CreditoExceptions.validation("Base de cálculo não pode ser negativa", "baseCalculo");
        }
        
        if (aliquota.compareTo(BigDecimal.ZERO) < 0) {
            throw CreditoExceptions.validation("Alíquota não pode ser negativa", "aliquota");
        }
        
        // Valor do ISS = base de cálculo * (alíquota / 100)
        return baseCalculo.multiply(aliquota.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
                          .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcula base de cálculo - método simples e direto
     */
    public BigDecimal calcularBaseCalculo(BigDecimal valorFaturado, BigDecimal valorDeducao) {
        if (valorFaturado == null || valorDeducao == null) {
            return BigDecimal.ZERO;
        }
        
        if (valorFaturado.compareTo(BigDecimal.ZERO) < 0) {
            throw CreditoExceptions.validation("Valor faturado não pode ser negativo", "valorFaturado");
        }
        
        if (valorDeducao.compareTo(BigDecimal.ZERO) < 0) {
            throw CreditoExceptions.validation("Valor dedução não pode ser negativo", "valorDeducao");
        }
        
        if (valorDeducao.compareTo(valorFaturado) > 0) {
            throw CreditoExceptions.validation("Valor dedução não pode ser maior que valor faturado", "valorDeducao");
        }
        
        return valorFaturado.subtract(valorDeducao);
    }
    
    /**
     * Valida se uma alíquota está dentro dos limites permitidos
     */
    public boolean validarAliquota(BigDecimal aliquota, BigDecimal limiteMinimo, BigDecimal limiteMaximo) {
        if (aliquota == null) {
            return false;
        }
        
        return aliquota.compareTo(limiteMinimo) >= 0 && aliquota.compareTo(limiteMaximo) <= 0;
    }
    
    /**
     * Valida se uma alíquota está dentro dos limites padrão (0% a 100%)
     */
    public boolean validarAliquota(BigDecimal aliquota) {
        return validarAliquota(aliquota, BigDecimal.ZERO, BigDecimal.valueOf(100));
    }
    
    /**
     * Factory method para criar crédito com cálculos automáticos
     */
    public Credito criarCreditoComCalculos(String numeroCredito, String numeroNfse, 
                                          LocalDate dataConstituicao, String tipoCredito, 
                                          Boolean simplesNacional, BigDecimal aliquota, 
                                          BigDecimal valorFaturado, BigDecimal valorDeducao) {
        
        BigDecimal baseCalculo = calcularBaseCalculo(valorFaturado, valorDeducao);
        BigDecimal valorIssqn = calcularValorIssqn(baseCalculo, aliquota);
        
        return new Credito(numeroCredito, numeroNfse, dataConstituicao, valorIssqn, 
                          tipoCredito, simplesNacional, aliquota, valorFaturado, 
                          valorDeducao, baseCalculo);
    }
}
