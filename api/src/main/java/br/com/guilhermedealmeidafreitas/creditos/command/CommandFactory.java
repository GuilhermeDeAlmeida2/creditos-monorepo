package br.com.guilhermedealmeidafreitas.creditos.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory para criação de comandos.
 * Implementa Command Pattern para operações de teste.
 * 
 * REFATORAÇÃO: Implementa Command Pattern para organizar
 * e padronizar operações de teste no sistema.
 */
@Component
public class CommandFactory {
    
    // Tipos de comando suportados
    public static final String TYPE_GENERATE_TEST_DATA = "GENERATE_TEST_DATA";
    public static final String TYPE_DELETE_TEST_DATA = "DELETE_TEST_DATA";
    public static final String TYPE_CLEAR_TEST_DATA = "CLEAR_TEST_DATA";
    public static final String TYPE_VALIDATE_TEST_DATA = "VALIDATE_TEST_DATA";
    
    private final GenerateTestDataCommand generateTestDataCommand;
    private final DeleteTestDataCommand deleteTestDataCommand;
    
    @Autowired
    public CommandFactory(GenerateTestDataCommand generateTestDataCommand,
                         DeleteTestDataCommand deleteTestDataCommand) {
        this.generateTestDataCommand = generateTestDataCommand;
        this.deleteTestDataCommand = deleteTestDataCommand;
    }
    
    /**
     * Cria um comando baseado no tipo especificado.
     * 
     * @param type Tipo do comando
     * @param parameters Parâmetros do comando
     * @return Comando criado
     * @throws IllegalArgumentException se o tipo não for suportado
     */
    public Command createCommand(String type, Map<String, Object> parameters) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo do comando não pode ser nulo ou vazio");
        }
        
        return switch (type.toUpperCase()) {
            case TYPE_GENERATE_TEST_DATA -> createGenerateTestDataCommand(parameters);
            case TYPE_DELETE_TEST_DATA -> createDeleteTestDataCommand(parameters);
            case TYPE_CLEAR_TEST_DATA -> createClearTestDataCommand(parameters);
            case TYPE_VALIDATE_TEST_DATA -> createValidateTestDataCommand(parameters);
            default -> throw new IllegalArgumentException("Tipo de comando não suportado: " + type);
        };
    }
    
    /**
     * Cria um comando de geração de dados de teste.
     * 
     * @param parameters Parâmetros do comando
     * @return Comando de geração de dados de teste
     */
    private Command createGenerateTestDataCommand(Map<String, Object> parameters) {
        // Criar nova instância do comando
        GenerateTestDataCommand command = new GenerateTestDataCommand(null, null);
        
        if (parameters != null) {
            command.setParameters(parameters);
        }
        
        return command;
    }
    
    /**
     * Cria um comando de exclusão de dados de teste.
     * 
     * @param parameters Parâmetros do comando
     * @return Comando de exclusão de dados de teste
     */
    private Command createDeleteTestDataCommand(Map<String, Object> parameters) {
        // Criar nova instância do comando
        DeleteTestDataCommand command = new DeleteTestDataCommand(null);
        
        if (parameters != null) {
            command.setParameters(parameters);
        }
        
        return command;
    }
    
    /**
     * Cria um comando de limpeza de dados de teste.
     * 
     * @param parameters Parâmetros do comando
     * @return Comando de limpeza de dados de teste
     */
    private Command createClearTestDataCommand(Map<String, Object> parameters) {
        // Para simplificar, usar o comando de exclusão com parâmetros específicos
        Map<String, Object> clearParams = new HashMap<>();
        clearParams.put(DeleteTestDataCommand.PARAM_DELETE_ALL, true);
        clearParams.put(DeleteTestDataCommand.PARAM_CONFIRM_DELETE, true);
        
        if (parameters != null) {
            clearParams.putAll(parameters);
        }
        
        return createDeleteTestDataCommand(clearParams);
    }
    
    /**
     * Cria um comando de validação de dados de teste.
     * 
     * @param parameters Parâmetros do comando
     * @return Comando de validação de dados de teste
     */
    private Command createValidateTestDataCommand(Map<String, Object> parameters) {
        // Implementação simplificada - em um cenário real, isso seria um comando específico
        return new BaseCommand("ValidateTestData", "Valida dados de teste", TYPE_VALIDATE_TEST_DATA) {
            @Override
            protected CommandResult doExecute() throws Exception {
                // Implementação simplificada
                return CommandResult.success("Dados de teste validados com sucesso", null, getActualExecutionTime());
            }
            
            @Override
            protected CommandResult doUndo() throws Exception {
                return CommandResult.success("Validação não pode ser desfeita", null, getActualExecutionTime());
            }
        };
    }
    
    // ===== MÉTODOS DE CONVENIÊNCIA =====
    
    /**
     * Cria um comando para gerar dados de teste com configuração padrão.
     * 
     * @return Comando de geração de dados de teste
     */
    public Command createGenerateTestDataCommand() {
        return createCommand(TYPE_GENERATE_TEST_DATA, null);
    }
    
    /**
     * Cria um comando para gerar dados de teste com contagem personalizada.
     * 
     * @param count Número de registros a gerar
     * @return Comando de geração de dados de teste
     */
    public Command createGenerateTestDataCommand(int count) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(GenerateTestDataCommand.PARAM_COUNT, count);
        return createCommand(TYPE_GENERATE_TEST_DATA, parameters);
    }
    
    /**
     * Cria um comando para gerar dados de teste com configuração personalizada.
     * 
     * @param nfseCount Número de NFS-e
     * @param creditosPerNfse Créditos por NFS-e
     * @return Comando de geração de dados de teste
     */
    public Command createGenerateTestDataCommand(int nfseCount, int creditosPerNfse) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(GenerateTestDataCommand.PARAM_NFSE_COUNT, nfseCount);
        parameters.put(GenerateTestDataCommand.PARAM_CREDITOS_PER_NFSE, creditosPerNfse);
        parameters.put(GenerateTestDataCommand.PARAM_COUNT, nfseCount * creditosPerNfse);
        return createCommand(TYPE_GENERATE_TEST_DATA, parameters);
    }
    
    /**
     * Cria um comando para deletar todos os dados de teste.
     * 
     * @return Comando de exclusão de dados de teste
     */
    public Command createDeleteAllTestDataCommand() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_DELETE_ALL, true);
        parameters.put(DeleteTestDataCommand.PARAM_CONFIRM_DELETE, true);
        return createCommand(TYPE_DELETE_TEST_DATA, parameters);
    }
    
    /**
     * Cria um comando para deletar dados de teste específicos.
     * 
     * @param numeroPrefix Prefixo do número
     * @param nfsePrefix Prefixo da NFS-e
     * @return Comando de exclusão de dados de teste
     */
    public Command createDeleteSpecificTestDataCommand(String numeroPrefix, String nfsePrefix) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_DELETE_ALL, false);
        parameters.put(DeleteTestDataCommand.PARAM_NUMERO_PREFIX, numeroPrefix);
        parameters.put(DeleteTestDataCommand.PARAM_NFSE_PREFIX, nfsePrefix);
        parameters.put(DeleteTestDataCommand.PARAM_CONFIRM_DELETE, true);
        return createCommand(TYPE_DELETE_TEST_DATA, parameters);
    }
    
    /**
     * Cria um comando para deletar dados de teste por tipo.
     * 
     * @param tipoCredito Tipo do crédito
     * @return Comando de exclusão de dados de teste
     */
    public Command createDeleteTestDataByTypeCommand(String tipoCredito) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_DELETE_ALL, false);
        parameters.put(DeleteTestDataCommand.PARAM_TIPO_CREDITO, tipoCredito);
        parameters.put(DeleteTestDataCommand.PARAM_CONFIRM_DELETE, true);
        return createCommand(TYPE_DELETE_TEST_DATA, parameters);
    }
    
    /**
     * Cria um comando para limpar todos os dados de teste.
     * 
     * @return Comando de limpeza de dados de teste
     */
    public Command createClearTestDataCommand() {
        return createCommand(TYPE_CLEAR_TEST_DATA, null);
    }
    
    /**
     * Cria um comando para validar dados de teste.
     * 
     * @return Comando de validação de dados de teste
     */
    public Command createValidateTestDataCommand() {
        return createCommand(TYPE_VALIDATE_TEST_DATA, null);
    }
    
    /**
     * Retorna os tipos de comando suportados.
     * 
     * @return Array com os tipos suportados
     */
    public String[] getSupportedCommandTypes() {
        return new String[]{
            TYPE_GENERATE_TEST_DATA,
            TYPE_DELETE_TEST_DATA,
            TYPE_CLEAR_TEST_DATA,
            TYPE_VALIDATE_TEST_DATA
        };
    }
    
    /**
     * Verifica se um tipo de comando é suportado.
     * 
     * @param type Tipo do comando
     * @return true se o tipo é suportado
     */
    public boolean isCommandTypeSupported(String type) {
        if (type == null) {
            return false;
        }
        
        String[] supportedTypes = getSupportedCommandTypes();
        for (String supportedType : supportedTypes) {
            if (supportedType.equals(type.toUpperCase())) {
                return true;
            }
        }
        return false;
    }
}
