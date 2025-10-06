package br.com.guilhermedealmeidafreitas.creditos.command;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.repository.CreditoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para DeleteTestDataCommand.
 * 
 * Este teste cobre todos os cenários do comando de exclusão de dados de teste,
 * incluindo execução, undo, validação e diferentes parâmetros.
 */
@ExtendWith(MockitoExtension.class)
class DeleteTestDataCommandTest {

    @Mock
    private CreditoRepository creditoRepository;

    private DeleteTestDataCommand deleteTestDataCommand;

    @BeforeEach
    void setUp() {
        deleteTestDataCommand = new DeleteTestDataCommand(creditoRepository);
    }

    @Test
    @DisplayName("Deve executar comando para deletar todos os dados de teste")
    void deveExecutarComandoParaDeletarTodosOsDadosDeTeste() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_DELETE_ALL, true);
        parameters.put(DeleteTestDataCommand.PARAM_CONFIRM_DELETE, true);
        deleteTestDataCommand.setParameters(parameters);

        List<Credito> testCreditos = createTestCreditos(3);
        when(creditoRepository.findTestRecords()).thenReturn(testCreditos);

        // When
        CommandResult result = deleteTestDataCommand.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Deletados 3 registros de teste", result.getMessage());
        assertEquals(3, result.getData());
        
        verify(creditoRepository).findTestRecords();
        verify(creditoRepository).deleteAll(testCreditos);
    }

    @Test
    @DisplayName("Deve executar comando para deletar dados específicos por prefixo")
    void deveExecutarComandoParaDeletarDadosEspecificosPorPrefixo() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_DELETE_ALL, false);
        parameters.put(DeleteTestDataCommand.PARAM_NUMERO_PREFIX, "TESTE_123");
        parameters.put(DeleteTestDataCommand.PARAM_CONFIRM_DELETE, true);
        deleteTestDataCommand.setParameters(parameters);

        List<Credito> allTestCreditos = createTestCreditos(5);
        List<Credito> filteredCreditos = Arrays.asList(allTestCreditos.get(0), allTestCreditos.get(1));
        when(creditoRepository.findTestRecords()).thenReturn(allTestCreditos);

        // When
        CommandResult result = deleteTestDataCommand.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Deletados"));
        
        verify(creditoRepository).findTestRecords();
        verify(creditoRepository).deleteAll(any());
    }

    @Test
    @DisplayName("Deve executar comando para deletar dados por tipo de crédito")
    void deveExecutarComandoParaDeletarDadosPorTipoDeCredito() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_DELETE_ALL, false);
        parameters.put(DeleteTestDataCommand.PARAM_TIPO_CREDITO, "ISS");
        parameters.put(DeleteTestDataCommand.PARAM_CONFIRM_DELETE, true);
        deleteTestDataCommand.setParameters(parameters);

        List<Credito> allTestCreditos = createTestCreditos(5);
        when(creditoRepository.findTestRecords()).thenReturn(allTestCreditos);

        // When
        CommandResult result = deleteTestDataCommand.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Deletados"));
        
        verify(creditoRepository).findTestRecords();
        verify(creditoRepository).deleteAll(any());
    }

    @Test
    @DisplayName("Deve executar comando para deletar dados por Simples Nacional")
    void deveExecutarComandoParaDeletarDadosPorSimplesNacional() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_DELETE_ALL, false);
        parameters.put(DeleteTestDataCommand.PARAM_SIMPLES_NACIONAL, true);
        parameters.put(DeleteTestDataCommand.PARAM_CONFIRM_DELETE, true);
        deleteTestDataCommand.setParameters(parameters);

        List<Credito> allTestCreditos = createTestCreditos(5);
        when(creditoRepository.findTestRecords()).thenReturn(allTestCreditos);

        // When
        CommandResult result = deleteTestDataCommand.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Deletados"));
        
        verify(creditoRepository).findTestRecords();
        verify(creditoRepository).deleteAll(any());
    }

    @Test
    @DisplayName("Deve retornar sucesso quando nenhum registro é encontrado")
    void deveRetornarSucessoQuandoNenhumRegistroEFoiEncontrado() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_DELETE_ALL, true);
        parameters.put(DeleteTestDataCommand.PARAM_CONFIRM_DELETE, true);
        deleteTestDataCommand.setParameters(parameters);

        when(creditoRepository.findTestRecords()).thenReturn(Collections.emptyList());

        // When
        CommandResult result = deleteTestDataCommand.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Nenhum registro de teste encontrado para deletar", result.getMessage());
        assertEquals(0, result.getData());
        
        verify(creditoRepository).findTestRecords();
        verify(creditoRepository, never()).deleteAll(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando confirmação não é fornecida")
    void deveLancarExcecaoQuandoConfirmacaoNaoEFornecida() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_DELETE_ALL, true);
        // PARAM_CONFIRM_DELETE não é definido (default é false)
        deleteTestDataCommand.setParameters(parameters);

        // When & Then
        CommandException exception = assertThrows(
            CommandException.class,
            () -> deleteTestDataCommand.execute()
        );
        
        assertEquals("Exclusão não confirmada. Use confirmDelete=true para confirmar.", exception.getMessage());
        assertEquals("DeleteTestData", exception.getCommandName());
    }

    @Test
    @DisplayName("Deve executar undo com sucesso")
    void deveExecutarUndoComSucesso() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_DELETE_ALL, true);
        parameters.put(DeleteTestDataCommand.PARAM_CONFIRM_DELETE, true);
        deleteTestDataCommand.setParameters(parameters);

        List<Credito> testCreditos = createTestCreditos(3);
        when(creditoRepository.findTestRecords()).thenReturn(testCreditos);
        when(creditoRepository.saveAll(testCreditos)).thenReturn(testCreditos);

        // Primeiro executa o comando
        deleteTestDataCommand.execute();

        // When - Executa undo
        CommandResult result = deleteTestDataCommand.undo();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Restaurados 3 registros de teste", result.getMessage());
        assertEquals(3, result.getData());
        
        verify(creditoRepository).saveAll(testCreditos);
    }

    @Test
    @DisplayName("Deve retornar sucesso no undo quando não há dados para restaurar")
    void deveRetornarSucessoNoUndoQuandoNaoHaDadosParaRestaurar() throws Exception {
        // When
        CommandResult result = deleteTestDataCommand.undo();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Nenhum dado para restaurar", result.getMessage());
        assertEquals(0, result.getData());
        
        verify(creditoRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Deve validar parâmetros corretamente")
    void deveValidarParametrosCorretamente() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_NUMERO_PREFIX, "");
        parameters.put(DeleteTestDataCommand.PARAM_NFSE_PREFIX, "");
        parameters.put(DeleteTestDataCommand.PARAM_TIPO_CREDITO, "");
        deleteTestDataCommand.setParameters(parameters);

        // When & Then
        assertThrows(CommandValidationException.class, () -> deleteTestDataCommand.validate());
    }

    @Test
    @DisplayName("Deve validar parâmetros com espaços em branco")
    void deveValidarParametrosComEspacosEmBranco() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_NUMERO_PREFIX, "   ");
        deleteTestDataCommand.setParameters(parameters);

        // When & Then
        CommandValidationException exception = assertThrows(
            CommandValidationException.class,
            () -> deleteTestDataCommand.validate()
        );
        
        assertEquals("Prefixo do número não pode ser vazio", exception.getMessage());
        assertEquals(DeleteTestDataCommand.PARAM_NUMERO_PREFIX, exception.getField());
    }

    @Test
    @DisplayName("Deve validar parâmetros de NFS-e com espaços em branco")
    void deveValidarParametrosDeNfseComEspacosEmBranco() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_NFSE_PREFIX, "   ");
        deleteTestDataCommand.setParameters(parameters);

        // When & Then
        CommandValidationException exception = assertThrows(
            CommandValidationException.class,
            () -> deleteTestDataCommand.validate()
        );
        
        assertEquals("Prefixo da NFS-e não pode ser vazio", exception.getMessage());
        assertEquals(DeleteTestDataCommand.PARAM_NFSE_PREFIX, exception.getField());
    }

    @Test
    @DisplayName("Deve validar parâmetros de tipo de crédito com espaços em branco")
    void deveValidarParametrosDeTipoDeCreditoComEspacosEmBranco() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_TIPO_CREDITO, "   ");
        deleteTestDataCommand.setParameters(parameters);

        // When & Then
        CommandValidationException exception = assertThrows(
            CommandValidationException.class,
            () -> deleteTestDataCommand.validate()
        );
        
        assertEquals("Tipo de crédito não pode ser vazio", exception.getMessage());
        assertEquals(DeleteTestDataCommand.PARAM_TIPO_CREDITO, exception.getField());
    }

    @Test
    @DisplayName("Deve passar na validação com parâmetros válidos")
    void devePassarNaValidacaoComParametrosValidos() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_NUMERO_PREFIX, "TESTE_123");
        parameters.put(DeleteTestDataCommand.PARAM_NFSE_PREFIX, "NFSE_TESTE");
        parameters.put(DeleteTestDataCommand.PARAM_TIPO_CREDITO, "ISS");
        deleteTestDataCommand.setParameters(parameters);

        // When & Then - Não deve lançar exceção
        assertDoesNotThrow(() -> deleteTestDataCommand.validate());
    }

    @Test
    @DisplayName("Deve estimar tempo de execução para deletar todos")
    void deveEstimarTempoDeExecucaoParaDeletarTodos() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_DELETE_ALL, true);
        deleteTestDataCommand.setParameters(parameters);

        // When
        long estimatedTime = deleteTestDataCommand.getEstimatedExecutionTime();

        // Then
        assertEquals(2000, estimatedTime);
    }

    @Test
    @DisplayName("Deve estimar tempo de execução para deletar específicos")
    void deveEstimarTempoDeExecucaoParaDeletarEspecificos() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_DELETE_ALL, false);
        deleteTestDataCommand.setParameters(parameters);

        // When
        long estimatedTime = deleteTestDataCommand.getEstimatedExecutionTime();

        // Then
        assertEquals(500, estimatedTime);
    }

    @Test
    @DisplayName("Deve usar valores padrão quando parâmetros não são fornecidos")
    void deveUsarValoresPadraoQuandoParametrosNaoSaoFornecidos() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_CONFIRM_DELETE, true);
        deleteTestDataCommand.setParameters(parameters);

        when(creditoRepository.findTestRecords()).thenReturn(Collections.emptyList());

        // When
        CommandResult result = deleteTestDataCommand.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        
        // Verifica se os valores padrão foram usados
        Map<String, Object> metadata = result.getMetadata();
        assertTrue((Boolean) metadata.get("deleteAll")); // DEFAULT_DELETE_ALL = true
        assertEquals("TESTE", metadata.get("numeroPrefix")); // DEFAULT_NUMERO_PREFIX
        assertEquals("TESTE_NFSE", metadata.get("nfsePrefix")); // DEFAULT_NFSE_PREFIX
    }

    @Test
    @DisplayName("Deve incluir metadados corretos no resultado")
    void deveIncluirMetadadosCorretosNoResultado() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_DELETE_ALL, false);
        parameters.put(DeleteTestDataCommand.PARAM_NUMERO_PREFIX, "TESTE_123");
        parameters.put(DeleteTestDataCommand.PARAM_NFSE_PREFIX, "NFSE_TESTE");
        parameters.put(DeleteTestDataCommand.PARAM_TIPO_CREDITO, "ISS");
        parameters.put(DeleteTestDataCommand.PARAM_SIMPLES_NACIONAL, true);
        parameters.put(DeleteTestDataCommand.PARAM_CONFIRM_DELETE, true);
        deleteTestDataCommand.setParameters(parameters);

        List<Credito> testCreditos = createTestCreditos(2);
        when(creditoRepository.findTestRecords()).thenReturn(testCreditos);

        // When
        CommandResult result = deleteTestDataCommand.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        
        Map<String, Object> metadata = result.getMetadata();
        assertEquals(2, metadata.get("deletedCount"));
        assertFalse((Boolean) metadata.get("deleteAll"));
        assertEquals("TESTE_123", metadata.get("numeroPrefix"));
        assertEquals("NFSE_TESTE", metadata.get("nfsePrefix"));
        assertEquals("ISS", metadata.get("tipoCredito"));
        assertEquals(true, metadata.get("simplesNacional"));
    }

    @Test
    @DisplayName("Deve filtrar créditos corretamente por múltiplos parâmetros")
    void deveFiltrarCreditosCorretamentePorMultiplosParametros() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_DELETE_ALL, false);
        parameters.put(DeleteTestDataCommand.PARAM_NUMERO_PREFIX, "TESTE_123");
        parameters.put(DeleteTestDataCommand.PARAM_TIPO_CREDITO, "ISS");
        parameters.put(DeleteTestDataCommand.PARAM_CONFIRM_DELETE, true);
        deleteTestDataCommand.setParameters(parameters);

        List<Credito> allTestCreditos = createTestCreditos(5);
        when(creditoRepository.findTestRecords()).thenReturn(allTestCreditos);

        // When
        CommandResult result = deleteTestDataCommand.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        
        verify(creditoRepository).findTestRecords();
        verify(creditoRepository).deleteAll(any());
    }

    @Test
    @DisplayName("Deve executar undo após execução bem-sucedida")
    void deveExecutarUndoAposExecucaoBemSucedida() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DeleteTestDataCommand.PARAM_DELETE_ALL, true);
        parameters.put(DeleteTestDataCommand.PARAM_CONFIRM_DELETE, true);
        deleteTestDataCommand.setParameters(parameters);

        List<Credito> testCreditos = createTestCreditos(3);
        when(creditoRepository.findTestRecords()).thenReturn(testCreditos);
        when(creditoRepository.saveAll(testCreditos)).thenReturn(testCreditos);

        // When
        CommandResult executeResult = deleteTestDataCommand.execute();
        CommandResult undoResult = deleteTestDataCommand.undo();

        // Then
        assertTrue(executeResult.isSuccess());
        assertTrue(undoResult.isSuccess());
        assertEquals(3, executeResult.getData());
        assertEquals(3, undoResult.getData());
        
        verify(creditoRepository).deleteAll(testCreditos);
        verify(creditoRepository).saveAll(testCreditos);
    }

    @Test
    @DisplayName("Deve criar instância para deletar todos os dados de teste")
    void deveCriarInstanciaParaDeletarTodosOsDadosDeTeste() {
        // When
        DeleteTestDataCommand command = new DeleteTestDataCommand(creditoRepository);

        // Then
        assertNotNull(command);
        assertEquals("DeleteTestData", command.getName());
    }

    // Método auxiliar para criar créditos de teste
    private List<Credito> createTestCreditos(int count) {
        List<Credito> creditos = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Credito credito = new Credito();
            credito.setId((long) (i + 1));
            credito.setNumeroCredito("TESTE_" + (i + 1));
            credito.setNumeroNfse("TESTE_NFSE_" + (i + 1));
            credito.setTipoCredito("ISS");
            // Usar apenas campos que existem na entidade
            credito.setSimplesNacional(i % 2 == 0);
            creditos.add(credito);
        }
        return creditos;
    }
}
