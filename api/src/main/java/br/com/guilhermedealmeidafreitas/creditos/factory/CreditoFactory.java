package br.com.guilhermedealmeidafreitas.creditos.factory;

import br.com.guilhermedealmeidafreitas.creditos.builder.CreditoBuilderFactory;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Factory melhorada para criação de objetos Credito.
 * Implementa Improved Factory Pattern com suporte a diferentes tipos de criação.
 * 
 * REFATORAÇÃO: Implementa Improved Factory Pattern para organizar
 * e padronizar a criação de objetos Credito no sistema.
 */
@Component
public class CreditoFactory extends BaseFactory<Credito> {
    
    // Tipos de crédito suportados
    public static final String TYPE_SIMPLES_NACIONAL = "SIMPLES_NACIONAL";
    public static final String TYPE_REGULAR_TAXPAYER = "REGULAR_TAXPAYER";
    public static final String TYPE_TEST_DATA = "TEST_DATA";
    public static final String TYPE_ISS_CREDITO = "ISS_CREDITO";
    public static final String TYPE_BASIC = "BASIC";
    
    // Parâmetros suportados
    public static final String PARAM_NUMERO_CREDITO = "numeroCredito";
    public static final String PARAM_NUMERO_NFSE = "numeroNfse";
    public static final String PARAM_DATA_CONSTITUICAO = "dataConstituicao";
    public static final String PARAM_TIPO_CREDITO = "tipoCredito";
    public static final String PARAM_SIMPLES_NACIONAL = "simplesNacional";
    public static final String PARAM_ALIQUOTA = "aliquota";
    public static final String PARAM_VALOR_FATURADO = "valorFaturado";
    public static final String PARAM_VALOR_DEDUCAO = "valorDeducao";
    public static final String PARAM_BASE_CALCULO = "baseCalculo";
    public static final String PARAM_VALOR_ISSSN = "valorIssqn";
    public static final String PARAM_ID = "id";
    
    private final CreditoBuilderFactory creditoBuilderFactory;
    
    @Autowired
    public CreditoFactory(CreditoBuilderFactory creditoBuilderFactory) {
        super("CreditoFactory", 
              "Factory para criação de objetos Credito", 
              Credito.class);
        this.creditoBuilderFactory = creditoBuilderFactory;
    }
    
    @Override
    public Credito create(Map<String, Object> parameters) {
        validateParameters(parameters);
        
        String type = getParameter(parameters, "type", TYPE_BASIC);
        
        return createCreditoByType(type, parameters);
    }
    
    @Override
    public Map<String, String> getSupportedParameters() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put(PARAM_NUMERO_CREDITO, "Número do crédito (obrigatório)");
        parameters.put(PARAM_NUMERO_NFSE, "Número da NFS-e (obrigatório)");
        parameters.put(PARAM_DATA_CONSTITUICAO, "Data de constituição (obrigatório)");
        parameters.put(PARAM_TIPO_CREDITO, "Tipo do crédito (opcional, padrão: BASIC)");
        parameters.put(PARAM_SIMPLES_NACIONAL, "Se é do Simples Nacional (opcional)");
        parameters.put(PARAM_ALIQUOTA, "Alíquota (opcional)");
        parameters.put(PARAM_VALOR_FATURADO, "Valor faturado (opcional)");
        parameters.put(PARAM_VALOR_DEDUCAO, "Valor dedução (opcional)");
        parameters.put(PARAM_BASE_CALCULO, "Base de cálculo (opcional)");
        parameters.put(PARAM_VALOR_ISSSN, "Valor do ISS (opcional)");
        parameters.put(PARAM_ID, "ID do crédito (opcional)");
        return parameters;
    }
    
    @Override
    public void validateParameters(Map<String, Object> parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("Parâmetros não podem ser nulos");
        }
        
        // Validações obrigatórias
        if (!hasParameter(parameters, PARAM_NUMERO_CREDITO)) {
            throw new IllegalArgumentException("Parâmetro 'numeroCredito' é obrigatório");
        }
        
        if (!hasParameter(parameters, PARAM_NUMERO_NFSE)) {
            throw new IllegalArgumentException("Parâmetro 'numeroNfse' é obrigatório");
        }
        
        if (!hasParameter(parameters, PARAM_DATA_CONSTITUICAO)) {
            throw new IllegalArgumentException("Parâmetro 'dataConstituicao' é obrigatório");
        }
        
        // Validações de tipo
        String numeroCredito = getParameter(parameters, PARAM_NUMERO_CREDITO, "");
        if (numeroCredito.trim().isEmpty()) {
            throw new IllegalArgumentException("Número do crédito não pode ser vazio");
        }
        
        String numeroNfse = getParameter(parameters, PARAM_NUMERO_NFSE, "");
        if (numeroNfse.trim().isEmpty()) {
            throw new IllegalArgumentException("Número da NFS-e não pode ser vazio");
        }
        
        Object dataConstituicao = getParameter(parameters, PARAM_DATA_CONSTITUICAO, null);
        if (dataConstituicao == null) {
            throw new IllegalArgumentException("Data de constituição não pode ser nula");
        }
        
        if (!(dataConstituicao instanceof LocalDate)) {
            throw new IllegalArgumentException("Data de constituição deve ser do tipo LocalDate");
        }
    }
    
    /**
     * Cria crédito baseado no tipo especificado.
     * 
     * @param type Tipo do crédito
     * @param parameters Parâmetros para criação
     * @return Crédito criado
     */
    private Credito createCreditoByType(String type, Map<String, Object> parameters) {
        return switch (type.toUpperCase()) {
            case TYPE_SIMPLES_NACIONAL -> createSimplesNacionalCredito(parameters);
            case TYPE_REGULAR_TAXPAYER -> createRegularTaxpayerCredito(parameters);
            case TYPE_TEST_DATA -> createTestDataCredito(parameters);
            case TYPE_ISS_CREDITO -> createIssCredito(parameters);
            case TYPE_BASIC -> createBasicCredito(parameters);
            default -> createBasicCredito(parameters);
        };
    }
    
    /**
     * Cria crédito do Simples Nacional.
     */
    private Credito createSimplesNacionalCredito(Map<String, Object> parameters) {
        String numeroCredito = getRequiredParameter(parameters, PARAM_NUMERO_CREDITO);
        String numeroNfse = getRequiredParameter(parameters, PARAM_NUMERO_NFSE);
        LocalDate dataConstituicao = getRequiredParameter(parameters, PARAM_DATA_CONSTITUICAO);
        String tipoCredito = getParameter(parameters, PARAM_TIPO_CREDITO, "ISS");
        BigDecimal aliquota = getRequiredParameter(parameters, PARAM_ALIQUOTA);
        BigDecimal valorFaturado = getRequiredParameter(parameters, PARAM_VALOR_FATURADO);
        BigDecimal valorDeducao = getRequiredParameter(parameters, PARAM_VALOR_DEDUCAO);
        
        return creditoBuilderFactory.forSimplesNacional(
            numeroCredito, numeroNfse, dataConstituicao, tipoCredito,
            aliquota, valorFaturado, valorDeducao
        ).build();
    }
    
    /**
     * Cria crédito de contribuinte regular.
     */
    private Credito createRegularTaxpayerCredito(Map<String, Object> parameters) {
        String numeroCredito = getRequiredParameter(parameters, PARAM_NUMERO_CREDITO);
        String numeroNfse = getRequiredParameter(parameters, PARAM_NUMERO_NFSE);
        LocalDate dataConstituicao = getRequiredParameter(parameters, PARAM_DATA_CONSTITUICAO);
        String tipoCredito = getParameter(parameters, PARAM_TIPO_CREDITO, "ISS");
        BigDecimal aliquota = getRequiredParameter(parameters, PARAM_ALIQUOTA);
        BigDecimal valorFaturado = getRequiredParameter(parameters, PARAM_VALOR_FATURADO);
        BigDecimal valorDeducao = getRequiredParameter(parameters, PARAM_VALOR_DEDUCAO);
        
        return creditoBuilderFactory.forRegularTaxpayer(
            numeroCredito, numeroNfse, dataConstituicao, tipoCredito,
            aliquota, valorFaturado, valorDeducao
        ).build();
    }
    
    /**
     * Cria crédito de dados de teste.
     */
    private Credito createTestDataCredito(Map<String, Object> parameters) {
        String numeroCredito = getRequiredParameter(parameters, PARAM_NUMERO_CREDITO);
        String numeroNfse = getRequiredParameter(parameters, PARAM_NUMERO_NFSE);
        LocalDate dataConstituicao = getRequiredParameter(parameters, PARAM_DATA_CONSTITUICAO);
        String tipoCredito = getParameter(parameters, PARAM_TIPO_CREDITO, "ISS");
        BigDecimal aliquota = getRequiredParameter(parameters, PARAM_ALIQUOTA);
        BigDecimal valorFaturado = getRequiredParameter(parameters, PARAM_VALOR_FATURADO);
        BigDecimal valorDeducao = getRequiredParameter(parameters, PARAM_VALOR_DEDUCAO);
        
        return creditoBuilderFactory.forTestData(
            numeroCredito, numeroNfse, dataConstituicao, tipoCredito,
            aliquota, valorFaturado, valorDeducao
        ).build();
    }
    
    /**
     * Cria crédito ISS específico.
     */
    private Credito createIssCredito(Map<String, Object> parameters) {
        String numeroCredito = getRequiredParameter(parameters, PARAM_NUMERO_CREDITO);
        String numeroNfse = getRequiredParameter(parameters, PARAM_NUMERO_NFSE);
        LocalDate dataConstituicao = getRequiredParameter(parameters, PARAM_DATA_CONSTITUICAO);
        BigDecimal aliquota = getRequiredParameter(parameters, PARAM_ALIQUOTA);
        BigDecimal valorFaturado = getRequiredParameter(parameters, PARAM_VALOR_FATURADO);
        BigDecimal valorDeducao = getRequiredParameter(parameters, PARAM_VALOR_DEDUCAO);
        
        return creditoBuilderFactory.forIssCredito(
            numeroCredito, numeroNfse, dataConstituicao,
            aliquota, valorFaturado, valorDeducao
        ).build();
    }
    
    /**
     * Cria crédito básico.
     */
    private Credito createBasicCredito(Map<String, Object> parameters) {
        String numeroCredito = getRequiredParameter(parameters, PARAM_NUMERO_CREDITO);
        String numeroNfse = getRequiredParameter(parameters, PARAM_NUMERO_NFSE);
        LocalDate dataConstituicao = getRequiredParameter(parameters, PARAM_DATA_CONSTITUICAO);
        String tipoCredito = getParameter(parameters, PARAM_TIPO_CREDITO, "ISS");
        
        return creditoBuilderFactory.withBasicData(
            numeroCredito, numeroNfse, dataConstituicao, tipoCredito
        ).build();
    }
    
    // ===== MÉTODOS DE CONVENIÊNCIA =====
    
    /**
     * Cria crédito do Simples Nacional.
     */
    public Credito createSimplesNacional(String numeroCredito, String numeroNfse, 
                                        LocalDate dataConstituicao, String tipoCredito,
                                        BigDecimal aliquota, BigDecimal valorFaturado, 
                                        BigDecimal valorDeducao) {
        return create(Map.of(
            "type", TYPE_SIMPLES_NACIONAL,
            PARAM_NUMERO_CREDITO, numeroCredito,
            PARAM_NUMERO_NFSE, numeroNfse,
            PARAM_DATA_CONSTITUICAO, dataConstituicao,
            PARAM_TIPO_CREDITO, tipoCredito,
            PARAM_ALIQUOTA, aliquota,
            PARAM_VALOR_FATURADO, valorFaturado,
            PARAM_VALOR_DEDUCAO, valorDeducao
        ));
    }
    
    /**
     * Cria crédito de contribuinte regular.
     */
    public Credito createRegularTaxpayer(String numeroCredito, String numeroNfse, 
                                        LocalDate dataConstituicao, String tipoCredito,
                                        BigDecimal aliquota, BigDecimal valorFaturado, 
                                        BigDecimal valorDeducao) {
        return create(Map.of(
            "type", TYPE_REGULAR_TAXPAYER,
            PARAM_NUMERO_CREDITO, numeroCredito,
            PARAM_NUMERO_NFSE, numeroNfse,
            PARAM_DATA_CONSTITUICAO, dataConstituicao,
            PARAM_TIPO_CREDITO, tipoCredito,
            PARAM_ALIQUOTA, aliquota,
            PARAM_VALOR_FATURADO, valorFaturado,
            PARAM_VALOR_DEDUCAO, valorDeducao
        ));
    }
    
    /**
     * Cria crédito de dados de teste.
     */
    public Credito createTestData(String numeroCredito, String numeroNfse, 
                                 LocalDate dataConstituicao, String tipoCredito,
                                 BigDecimal aliquota, BigDecimal valorFaturado, 
                                 BigDecimal valorDeducao) {
        return create(Map.of(
            "type", TYPE_TEST_DATA,
            PARAM_NUMERO_CREDITO, numeroCredito,
            PARAM_NUMERO_NFSE, numeroNfse,
            PARAM_DATA_CONSTITUICAO, dataConstituicao,
            PARAM_TIPO_CREDITO, tipoCredito,
            PARAM_ALIQUOTA, aliquota,
            PARAM_VALOR_FATURADO, valorFaturado,
            PARAM_VALOR_DEDUCAO, valorDeducao
        ));
    }
    
    /**
     * Cria crédito ISS específico.
     */
    public Credito createIssCredito(String numeroCredito, String numeroNfse, 
                                   LocalDate dataConstituicao, BigDecimal aliquota, 
                                   BigDecimal valorFaturado, BigDecimal valorDeducao) {
        return create(Map.of(
            "type", TYPE_ISS_CREDITO,
            PARAM_NUMERO_CREDITO, numeroCredito,
            PARAM_NUMERO_NFSE, numeroNfse,
            PARAM_DATA_CONSTITUICAO, dataConstituicao,
            PARAM_ALIQUOTA, aliquota,
            PARAM_VALOR_FATURADO, valorFaturado,
            PARAM_VALOR_DEDUCAO, valorDeducao
        ));
    }
    
    /**
     * Cria crédito básico.
     */
    public Credito createBasic(String numeroCredito, String numeroNfse, 
                              LocalDate dataConstituicao, String tipoCredito) {
        return create(Map.of(
            "type", TYPE_BASIC,
            PARAM_NUMERO_CREDITO, numeroCredito,
            PARAM_NUMERO_NFSE, numeroNfse,
            PARAM_DATA_CONSTITUICAO, dataConstituicao,
            PARAM_TIPO_CREDITO, tipoCredito
        ));
    }
}
