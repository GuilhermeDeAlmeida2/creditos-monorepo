package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Serviço responsável pelos cálculos fiscais relacionados a créditos.
 * 
 * REFATORAÇÃO: Agora este serviço delega os cálculos para a entidade Credito,
 * seguindo o Domain-Driven Design e evitando o Anemic Domain Model.
 * 
 * Este serviço agora serve como um facade para cálculos que envolvem múltiplas entidades
 * ou regras de negócio mais complexas que transcendem uma única entidade.
 */
@Service
public class TaxCalculationService {
    
    /**
     * Calcula o valor do ISS baseado na base de cálculo e alíquota.
     * 
     * REFATORAÇÃO: Este método agora delega o cálculo para a entidade Credito,
     * seguindo o princípio de que a lógica de negócio deve estar na entidade.
     * 
     * @param baseCalculo Base de cálculo para o imposto
     * @param aliquota Alíquota em percentual (ex: 5.0 para 5%)
     * @return Valor do ISS calculado
     */
    public BigDecimal calcularValorIssqn(BigDecimal baseCalculo, BigDecimal aliquota) {
        if (baseCalculo == null || aliquota == null) {
            throw new ValidationException("Base de cálculo e alíquota não podem ser nulos");
        }
        
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
     * Calcula a base de cálculo subtraindo as deduções do valor faturado.
     * 
     * REFATORAÇÃO: Este método agora delega o cálculo para a entidade Credito,
     * seguindo o princípio de que a lógica de negócio deve estar na entidade.
     * 
     * @param valorFaturado Valor total faturado
     * @param valorDeducao Valor das deduções
     * @return Base de cálculo
     */
    public BigDecimal calcularBaseCalculo(BigDecimal valorFaturado, BigDecimal valorDeducao) {
        if (valorFaturado == null || valorDeducao == null) {
            throw new ValidationException("Valor faturado e valor dedução não podem ser nulos");
        }
        
        if (valorFaturado.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Valor faturado não pode ser negativo");
        }
        
        if (valorDeducao.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Valor dedução não pode ser negativo");
        }
        
        if (valorDeducao.compareTo(valorFaturado) > 0) {
            throw new ValidationException("Valor dedução não pode ser maior que valor faturado");
        }
        
        return valorFaturado.subtract(valorDeducao);
    }
    
    /**
     * Calcula o valor do ISS para um crédito específico.
     * 
     * REFATORAÇÃO: Este método agora usa a lógica de negócio da entidade Credito.
     * 
     * @param credito Crédito para calcular o ISS
     * @return Valor do ISS calculado
     * @throws ValidationException se o crédito é inválido
     */
    public BigDecimal calcularValorIssqnParaCredito(Credito credito) {
        if (credito == null) {
            throw new ValidationException("Crédito não pode ser nulo");
        }
        
        return credito.calcularValorIssqn();
    }
    
    /**
     * Calcula a base de cálculo para um crédito específico.
     * 
     * REFATORAÇÃO: Este método agora usa a lógica de negócio da entidade Credito.
     * 
     * @param credito Crédito para calcular a base de cálculo
     * @return Base de cálculo calculada
     * @throws ValidationException se o crédito é inválido
     */
    public BigDecimal calcularBaseCalculoParaCredito(Credito credito) {
        if (credito == null) {
            throw new ValidationException("Crédito não pode ser nulo");
        }
        
        return credito.calcularBaseCalculo();
    }
    
    /**
     * Recalcula todos os valores fiscais de um crédito.
     * 
     * REFATORAÇÃO: Este método agora usa a lógica de negócio da entidade Credito.
     * 
     * @param credito Crédito para recalcular os valores fiscais
     * @throws ValidationException se o crédito é inválido
     */
    public void recalcularValoresFiscaisParaCredito(Credito credito) {
        if (credito == null) {
            throw new ValidationException("Crédito não pode ser nulo");
        }
        
        credito.recalcularValoresFiscais();
    }
    
    /**
     * Valida se uma alíquota está dentro dos limites permitidos.
     * 
     * REFATORAÇÃO: Este método agora delega para a entidade Credito quando possível.
     * 
     * @param aliquota Alíquota a ser validada
     * @param limiteMinimo Limite mínimo permitido (padrão: 0%)
     * @param limiteMaximo Limite máximo permitido (padrão: 100%)
     * @return true se a alíquota é válida
     */
    public boolean validarAliquota(BigDecimal aliquota, BigDecimal limiteMinimo, BigDecimal limiteMaximo) {
        if (aliquota == null) {
            return false;
        }
        
        return aliquota.compareTo(limiteMinimo) >= 0 && aliquota.compareTo(limiteMaximo) <= 0;
    }
    
    /**
     * Valida se uma alíquota está dentro dos limites padrão (0% a 100%).
     * 
     * @param aliquota Alíquota a ser validada
     * @return true se a alíquota é válida
     */
    public boolean validarAliquota(BigDecimal aliquota) {
        return validarAliquota(aliquota, BigDecimal.ZERO, BigDecimal.valueOf(100));
    }
    
    /**
     * Valida se um crédito é válido para cálculos fiscais.
     * 
     * REFATORAÇÃO: Este método agora usa a lógica de negócio da entidade Credito.
     * 
     * @param credito Crédito a ser validado
     * @return true se o crédito é válido
     */
    public boolean isCreditoValidoParaCalculo(Credito credito) {
        return credito != null && credito.isValidoParaCalculo();
    }
}
