package br.com.guilhermedealmeidafreitas.creditos.command;

import br.com.guilhermedealmeidafreitas.creditos.builder.CreditoBuilderFactory;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.repository.CreditoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Comando para gerar dados de teste.
 * Implementa Command Pattern para operações de teste.
 * 
 * REFATORAÇÃO: Implementa Command Pattern para organizar
 * e padronizar operações de teste no sistema.
 */
@Component
public class GenerateTestDataCommand extends BaseCommand {
    
    // Parâmetros suportados
    public static final String PARAM_COUNT = "count";
    public static final String PARAM_NFSE_COUNT = "nfseCount";
    public static final String PARAM_CREDITOS_PER_NFSE = "creditosPerNfse";
    public static final String PARAM_TIPOS_CREDITO = "tiposCredito";
    public static final String PARAM_VALOR_MIN = "valorMin";
    public static final String PARAM_VALOR_MAX = "valorMax";
    public static final String PARAM_ALIQUOTA_MIN = "aliquotaMin";
    public static final String PARAM_ALIQUOTA_MAX = "aliquotaMax";
    public static final String PARAM_DEDUCAO_PERCENT = "deducaoPercent";
    public static final String PARAM_DAYS_BACK = "daysBack";
    
    // Valores padrão
    private static final int DEFAULT_COUNT = 300;
    private static final int DEFAULT_NFSE_COUNT = 10;
    private static final int DEFAULT_CREDITOS_PER_NFSE = 30;
    private static final String[] DEFAULT_TIPOS_CREDITO = {"ISS", "IPI", "ICMS", "PIS", "COFINS", "IR", "CSLL"};
    private static final double DEFAULT_VALOR_MIN = 1000.0;
    private static final double DEFAULT_VALOR_MAX = 50000.0;
    private static final double DEFAULT_ALIQUOTA_MIN = 1.0;
    private static final double DEFAULT_ALIQUOTA_MAX = 15.0;
    private static final double DEFAULT_DEDUCAO_PERCENT = 0.3;
    private static final int DEFAULT_DAYS_BACK = 365;
    
    private final CreditoRepository creditoRepository;
    private final CreditoBuilderFactory creditoBuilderFactory;
    
    // Armazenar dados gerados para undo
    private List<Credito> generatedCreditos;
    
    @Autowired
    public GenerateTestDataCommand(CreditoRepository creditoRepository, 
                                  CreditoBuilderFactory creditoBuilderFactory) {
        super("GenerateTestData", 
              "Gera dados de teste para o sistema de créditos", 
              "TEST_DATA_GENERATION");
        this.creditoRepository = creditoRepository;
        this.creditoBuilderFactory = creditoBuilderFactory;
        this.generatedCreditos = new ArrayList<>();
    }
    
    @Override
    protected CommandResult doExecute() throws Exception {
        // Obter parâmetros com valores padrão
        int nfseCount = getParameter(PARAM_NFSE_COUNT, DEFAULT_NFSE_COUNT);
        int creditosPerNfse = getParameter(PARAM_CREDITOS_PER_NFSE, DEFAULT_CREDITOS_PER_NFSE);
        String[] tiposCredito = getParameter(PARAM_TIPOS_CREDITO, DEFAULT_TIPOS_CREDITO);
        double valorMin = getParameter(PARAM_VALOR_MIN, DEFAULT_VALOR_MIN);
        double valorMax = getParameter(PARAM_VALOR_MAX, DEFAULT_VALOR_MAX);
        double aliquotaMin = getParameter(PARAM_ALIQUOTA_MIN, DEFAULT_ALIQUOTA_MIN);
        double aliquotaMax = getParameter(PARAM_ALIQUOTA_MAX, DEFAULT_ALIQUOTA_MAX);
        double deducaoPercent = getParameter(PARAM_DEDUCAO_PERCENT, DEFAULT_DEDUCAO_PERCENT);
        int daysBack = getParameter(PARAM_DAYS_BACK, DEFAULT_DAYS_BACK);
        
        // Limpar dados anteriores
        generatedCreditos.clear();
        
        List<Credito> registrosTeste = new ArrayList<>();
        Random random = new Random();
        boolean[] valoresSimplesNacional = {true, false};
        
        // Gerar NFS-e e créditos
        for (int nfseIndex = 1; nfseIndex <= nfseCount; nfseIndex++) {
            String numeroNfse = String.format("TESTE_NFSE%03d", nfseIndex);
            
            for (int creditoIndex = 1; creditoIndex <= creditosPerNfse; creditoIndex++) {
                int creditoGlobalIndex = (nfseIndex - 1) * creditosPerNfse + creditoIndex;
                String numeroCredito = String.format("TESTE%06d", creditoGlobalIndex);
                
                // Gerar dados aleatórios mas válidos
                LocalDate dataConstituicao = LocalDate.now().minusDays(random.nextInt(daysBack));
                
                // Valores monetários realistas
                BigDecimal valorFaturado = BigDecimal.valueOf(random.nextDouble() * (valorMax - valorMin) + valorMin)
                    .setScale(2, RoundingMode.HALF_UP);
                BigDecimal valorDeducao = valorFaturado.multiply(BigDecimal.valueOf(random.nextDouble() * deducaoPercent))
                    .setScale(2, RoundingMode.HALF_UP);
                
                // Alíquota entre os valores especificados
                BigDecimal aliquota = BigDecimal.valueOf(random.nextDouble() * (aliquotaMax - aliquotaMin) + aliquotaMin)
                    .setScale(2, RoundingMode.HALF_UP);
                
                String tipoCredito = tiposCredito[random.nextInt(tiposCredito.length)];
                
                // Usar Builder Pattern para criação
                Credito credito = creditoBuilderFactory.forTestData(
                    numeroCredito,
                    numeroNfse,
                    dataConstituicao,
                    tipoCredito,
                    aliquota,
                    valorFaturado,
                    valorDeducao
                ).build();
                
                registrosTeste.add(credito);
            }
        }
        
        // Salvar todos os registros em batch
        generatedCreditos = creditoRepository.saveAll(registrosTeste);
        
        // Criar metadados do resultado
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("generatedCount", generatedCreditos.size());
        metadata.put("nfseCount", nfseCount);
        metadata.put("creditosPerNfse", creditosPerNfse);
        metadata.put("tiposCredito", tiposCredito);
        metadata.put("valorRange", String.format("%.2f - %.2f", valorMin, valorMax));
        metadata.put("aliquotaRange", String.format("%.2f%% - %.2f%%", aliquotaMin, aliquotaMax));
        
        String message = String.format("Gerados %d registros de teste em %d NFS-e", 
                                     generatedCreditos.size(), nfseCount);
        
        return CommandResult.success(message, generatedCreditos.size(), getActualExecutionTime(), metadata);
    }
    
    @Override
    protected CommandResult doUndo() throws Exception {
        if (generatedCreditos.isEmpty()) {
            return CommandResult.success("Nenhum dado de teste para remover", 0, getActualExecutionTime());
        }
        
        // Remover os créditos gerados
        creditoRepository.deleteAll(generatedCreditos);
        int removedCount = generatedCreditos.size();
        generatedCreditos.clear();
        
        String message = String.format("Removidos %d registros de teste", removedCount);
        return CommandResult.success(message, removedCount, getActualExecutionTime());
    }
    
    @Override
    public void validate() throws CommandValidationException {
        // Validar parâmetros numéricos
        if (hasParameter(PARAM_COUNT)) {
            Integer count = getParameter(PARAM_COUNT, null);
            if (count != null && count <= 0) {
                throw new CommandValidationException("Contagem deve ser maior que zero", getName(), PARAM_COUNT);
            }
        }
        
        if (hasParameter(PARAM_NFSE_COUNT)) {
            Integer nfseCount = getParameter(PARAM_NFSE_COUNT, null);
            if (nfseCount != null && nfseCount <= 0) {
                throw new CommandValidationException("Número de NFS-e deve ser maior que zero", getName(), PARAM_NFSE_COUNT);
            }
        }
        
        if (hasParameter(PARAM_CREDITOS_PER_NFSE)) {
            Integer creditosPerNfse = getParameter(PARAM_CREDITOS_PER_NFSE, null);
            if (creditosPerNfse != null && creditosPerNfse <= 0) {
                throw new CommandValidationException("Créditos por NFS-e deve ser maior que zero", getName(), PARAM_CREDITOS_PER_NFSE);
            }
        }
        
        if (hasParameter(PARAM_VALOR_MIN) && hasParameter(PARAM_VALOR_MAX)) {
            Double valorMin = getParameter(PARAM_VALOR_MIN, null);
            Double valorMax = getParameter(PARAM_VALOR_MAX, null);
            if (valorMin != null && valorMax != null && valorMin >= valorMax) {
                throw new CommandValidationException("Valor mínimo deve ser menor que o máximo", getName(), PARAM_VALOR_MIN);
            }
        }
        
        if (hasParameter(PARAM_ALIQUOTA_MIN) && hasParameter(PARAM_ALIQUOTA_MAX)) {
            Double aliquotaMin = getParameter(PARAM_ALIQUOTA_MIN, null);
            Double aliquotaMax = getParameter(PARAM_ALIQUOTA_MAX, null);
            if (aliquotaMin != null && aliquotaMax != null && aliquotaMin >= aliquotaMax) {
                throw new CommandValidationException("Alíquota mínima deve ser menor que a máxima", getName(), PARAM_ALIQUOTA_MIN);
            }
        }
        
        if (hasParameter(PARAM_DEDUCAO_PERCENT)) {
            Double deducaoPercent = getParameter(PARAM_DEDUCAO_PERCENT, null);
            if (deducaoPercent != null && (deducaoPercent < 0 || deducaoPercent > 1)) {
                throw new CommandValidationException("Percentual de dedução deve estar entre 0 e 1", getName(), PARAM_DEDUCAO_PERCENT);
            }
        }
        
        if (hasParameter(PARAM_DAYS_BACK)) {
            Integer daysBack = getParameter(PARAM_DAYS_BACK, null);
            if (daysBack != null && daysBack <= 0) {
                throw new CommandValidationException("Dias para trás deve ser maior que zero", getName(), PARAM_DAYS_BACK);
            }
        }
    }
    
    @Override
    public long getEstimatedExecutionTime() {
        int count = getParameter(PARAM_COUNT, DEFAULT_COUNT);
        // Estimativa: 10ms por registro + 1000ms overhead
        return (count * 10) + 1000;
    }
    
    /**
     * Cria uma instância do comando com parâmetros padrão.
     * 
     * @return Comando configurado com parâmetros padrão
     */
    public static GenerateTestDataCommand createDefault() {
        GenerateTestDataCommand command = new GenerateTestDataCommand(null, null);
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_COUNT, DEFAULT_COUNT);
        params.put(PARAM_NFSE_COUNT, DEFAULT_NFSE_COUNT);
        params.put(PARAM_CREDITOS_PER_NFSE, DEFAULT_CREDITOS_PER_NFSE);
        params.put(PARAM_TIPOS_CREDITO, DEFAULT_TIPOS_CREDITO);
        params.put(PARAM_VALOR_MIN, DEFAULT_VALOR_MIN);
        params.put(PARAM_VALOR_MAX, DEFAULT_VALOR_MAX);
        params.put(PARAM_ALIQUOTA_MIN, DEFAULT_ALIQUOTA_MIN);
        params.put(PARAM_ALIQUOTA_MAX, DEFAULT_ALIQUOTA_MAX);
        params.put(PARAM_DEDUCAO_PERCENT, DEFAULT_DEDUCAO_PERCENT);
        params.put(PARAM_DAYS_BACK, DEFAULT_DAYS_BACK);
        command.setParameters(params);
        return command;
    }
    
    /**
     * Cria uma instância do comando com contagem personalizada.
     * 
     * @param count Número de registros a gerar
     * @return Comando configurado
     */
    public static GenerateTestDataCommand createWithCount(int count) {
        GenerateTestDataCommand command = createDefault();
        command.setParameter(PARAM_COUNT, count);
        return command;
    }
    
    /**
     * Cria uma instância do comando com configuração personalizada.
     * 
     * @param nfseCount Número de NFS-e
     * @param creditosPerNfse Créditos por NFS-e
     * @return Comando configurado
     */
    public static GenerateTestDataCommand createWithConfiguration(int nfseCount, int creditosPerNfse) {
        GenerateTestDataCommand command = createDefault();
        command.setParameter(PARAM_NFSE_COUNT, nfseCount);
        command.setParameter(PARAM_CREDITOS_PER_NFSE, creditosPerNfse);
        command.setParameter(PARAM_COUNT, nfseCount * creditosPerNfse);
        return command;
    }
}
