package br.com.guilhermedealmeidafreitas.creditos.command;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.repository.CreditoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Comando para deletar dados de teste.
 * Implementa Command Pattern para operações de teste.
 * 
 * REFATORAÇÃO: Implementa Command Pattern para organizar
 * e padronizar operações de teste no sistema.
 */
@Component
public class DeleteTestDataCommand extends BaseCommand {
    
    // Parâmetros suportados
    public static final String PARAM_DELETE_ALL = "deleteAll";
    public static final String PARAM_NUMERO_PREFIX = "numeroPrefix";
    public static final String PARAM_NFSE_PREFIX = "nfsePrefix";
    public static final String PARAM_TIPO_CREDITO = "tipoCredito";
    public static final String PARAM_SIMPLES_NACIONAL = "simplesNacional";
    public static final String PARAM_CONFIRM_DELETE = "confirmDelete";
    
    // Valores padrão
    private static final boolean DEFAULT_DELETE_ALL = true;
    private static final String DEFAULT_NUMERO_PREFIX = "TESTE";
    private static final String DEFAULT_NFSE_PREFIX = "TESTE_NFSE";
    private static final boolean DEFAULT_CONFIRM_DELETE = false;
    
    private final CreditoRepository creditoRepository;
    
    // Armazenar dados deletados para undo
    private List<Credito> deletedCreditos;
    
    @Autowired
    public DeleteTestDataCommand(CreditoRepository creditoRepository) {
        super("DeleteTestData", 
              "Remove dados de teste do sistema de créditos", 
              "TEST_DATA_DELETION");
        this.creditoRepository = creditoRepository;
        this.deletedCreditos = null;
    }
    
    @Override
    protected CommandResult doExecute() throws Exception {
        // Verificar confirmação de exclusão
        boolean confirmDelete = getParameter(PARAM_CONFIRM_DELETE, DEFAULT_CONFIRM_DELETE);
        if (!confirmDelete) {
            throw new CommandException("Exclusão não confirmada. Use confirmDelete=true para confirmar.", getName());
        }
        
        // Obter parâmetros
        boolean deleteAll = getParameter(PARAM_DELETE_ALL, DEFAULT_DELETE_ALL);
        String numeroPrefix = getParameter(PARAM_NUMERO_PREFIX, DEFAULT_NUMERO_PREFIX);
        String nfsePrefix = getParameter(PARAM_NFSE_PREFIX, DEFAULT_NFSE_PREFIX);
        String tipoCredito = getParameter(PARAM_TIPO_CREDITO, null);
        Boolean simplesNacional = getParameter(PARAM_SIMPLES_NACIONAL, null);
        
        List<Credito> creditosToDelete;
        
        if (deleteAll) {
            // Deletar todos os registros de teste
            creditosToDelete = creditoRepository.findTestRecords();
        } else {
            // Deletar registros específicos baseados nos parâmetros
            creditosToDelete = findCreditosByParameters(numeroPrefix, nfsePrefix, tipoCredito, simplesNacional);
        }
        
        if (creditosToDelete.isEmpty()) {
            return CommandResult.success("Nenhum registro de teste encontrado para deletar", 0, getActualExecutionTime());
        }
        
        // Armazenar para possível undo
        deletedCreditos = List.copyOf(creditosToDelete);
        
        // Deletar registros
        creditoRepository.deleteAll(creditosToDelete);
        
        // Criar metadados do resultado
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("deletedCount", creditosToDelete.size());
        metadata.put("deleteAll", deleteAll);
        metadata.put("numeroPrefix", numeroPrefix);
        metadata.put("nfsePrefix", nfsePrefix);
        if (tipoCredito != null) {
            metadata.put("tipoCredito", tipoCredito);
        }
        if (simplesNacional != null) {
            metadata.put("simplesNacional", simplesNacional);
        }
        
        String message = String.format("Deletados %d registros de teste", creditosToDelete.size());
        
        return CommandResult.success(message, creditosToDelete.size(), getActualExecutionTime(), metadata);
    }
    
    @Override
    protected CommandResult doUndo() throws Exception {
        if (deletedCreditos == null || deletedCreditos.isEmpty()) {
            return CommandResult.success("Nenhum dado para restaurar", 0, getActualExecutionTime());
        }
        
        // Restaurar os créditos deletados
        List<Credito> restoredCreditos = creditoRepository.saveAll(deletedCreditos);
        int restoredCount = restoredCreditos.size();
        
        String message = String.format("Restaurados %d registros de teste", restoredCount);
        return CommandResult.success(message, restoredCount, getActualExecutionTime());
    }
    
    @Override
    public void validate() throws CommandValidationException {
        // Validar parâmetros de string
        if (hasParameter(PARAM_NUMERO_PREFIX)) {
            String numeroPrefix = getParameter(PARAM_NUMERO_PREFIX, null);
            if (numeroPrefix != null && numeroPrefix.trim().isEmpty()) {
                throw new CommandValidationException("Prefixo do número não pode ser vazio", getName(), PARAM_NUMERO_PREFIX);
            }
        }
        
        if (hasParameter(PARAM_NFSE_PREFIX)) {
            String nfsePrefix = getParameter(PARAM_NFSE_PREFIX, null);
            if (nfsePrefix != null && nfsePrefix.trim().isEmpty()) {
                throw new CommandValidationException("Prefixo da NFS-e não pode ser vazio", getName(), PARAM_NFSE_PREFIX);
            }
        }
        
        if (hasParameter(PARAM_TIPO_CREDITO)) {
            String tipoCredito = getParameter(PARAM_TIPO_CREDITO, null);
            if (tipoCredito != null && tipoCredito.trim().isEmpty()) {
                throw new CommandValidationException("Tipo de crédito não pode ser vazio", getName(), PARAM_TIPO_CREDITO);
            }
        }
    }
    
    @Override
    public long getEstimatedExecutionTime() {
        // Estimativa baseada no tipo de operação
        boolean deleteAll = getParameter(PARAM_DELETE_ALL, DEFAULT_DELETE_ALL);
        if (deleteAll) {
            // Operação mais pesada - estimar 2000ms
            return 2000;
        } else {
            // Operação mais leve - estimar 500ms
            return 500;
        }
    }
    
    /**
     * Encontra créditos baseados nos parâmetros fornecidos.
     * 
     * @param numeroPrefix Prefixo do número
     * @param nfsePrefix Prefixo da NFS-e
     * @param tipoCredito Tipo do crédito
     * @param simplesNacional Se é do Simples Nacional
     * @return Lista de créditos encontrados
     */
    private List<Credito> findCreditosByParameters(String numeroPrefix, String nfsePrefix, 
                                                  String tipoCredito, Boolean simplesNacional) {
        // Implementação simplificada - em um cenário real, isso seria feito no repository
        // com queries específicas baseadas nos parâmetros
        
        List<Credito> allTestRecords = creditoRepository.findTestRecords();
        
        return allTestRecords.stream()
            .filter(credito -> {
                // Filtrar por prefixo do número
                if (numeroPrefix != null && !credito.getNumeroCredito().startsWith(numeroPrefix)) {
                    return false;
                }
                
                // Filtrar por prefixo da NFS-e
                if (nfsePrefix != null && !credito.getNumeroNfse().startsWith(nfsePrefix)) {
                    return false;
                }
                
                // Filtrar por tipo de crédito
                if (tipoCredito != null && !credito.getTipoCredito().equals(tipoCredito)) {
                    return false;
                }
                
                // Filtrar por Simples Nacional
                if (simplesNacional != null && !credito.getSimplesNacional().equals(simplesNacional)) {
                    return false;
                }
                
                return true;
            })
            .toList();
    }
    
    /**
     * Cria uma instância do comando para deletar todos os dados de teste.
     * 
     * @return Comando configurado
     */
    public static DeleteTestDataCommand createDeleteAll() {
        DeleteTestDataCommand command = new DeleteTestDataCommand(null);
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_DELETE_ALL, true);
        params.put(PARAM_CONFIRM_DELETE, true);
        command.setParameters(params);
        return command;
    }
    
    /**
     * Cria uma instância do comando para deletar dados específicos.
     * 
     * @param numeroPrefix Prefixo do número
     * @param nfsePrefix Prefixo da NFS-e
     * @return Comando configurado
     */
    public static DeleteTestDataCommand createDeleteSpecific(String numeroPrefix, String nfsePrefix) {
        DeleteTestDataCommand command = new DeleteTestDataCommand(null);
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_DELETE_ALL, false);
        params.put(PARAM_NUMERO_PREFIX, numeroPrefix);
        params.put(PARAM_NFSE_PREFIX, nfsePrefix);
        params.put(PARAM_CONFIRM_DELETE, true);
        command.setParameters(params);
        return command;
    }
    
    /**
     * Cria uma instância do comando para deletar por tipo de crédito.
     * 
     * @param tipoCredito Tipo do crédito
     * @return Comando configurado
     */
    public static DeleteTestDataCommand createDeleteByType(String tipoCredito) {
        DeleteTestDataCommand command = new DeleteTestDataCommand(null);
        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_DELETE_ALL, false);
        params.put(PARAM_TIPO_CREDITO, tipoCredito);
        params.put(PARAM_CONFIRM_DELETE, true);
        command.setParameters(params);
        return command;
    }
}
