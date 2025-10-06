package br.com.guilhermedealmeidafreitas.creditos.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para CommandFactory.
 * 
 * Este teste cobre todos os cenários da factory de comandos,
 * incluindo criação de diferentes tipos de comandos e validações.
 */
@ExtendWith(MockitoExtension.class)
class CommandFactoryTest {

    @Mock
    private GenerateTestDataCommand generateTestDataCommand;

    @Mock
    private DeleteTestDataCommand deleteTestDataCommand;

    private CommandFactory commandFactory;

    @BeforeEach
    void setUp() {
        commandFactory = new CommandFactory(generateTestDataCommand, deleteTestDataCommand);
    }

    @Test
    @DisplayName("Deve criar comando de geração de dados de teste com parâmetros")
    void deveCriarComandoDeGeracaoDeDadosDeTesteComParametros() {
        // Given
        String type = CommandFactory.TYPE_GENERATE_TEST_DATA;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("count", 100);

        // When
        Command command = commandFactory.createCommand(type, parameters);

        // Then
        assertNotNull(command);
        assertTrue(command instanceof GenerateTestDataCommand);
    }

    @Test
    @DisplayName("Deve criar comando de geração de dados de teste sem parâmetros")
    void deveCriarComandoDeGeracaoDeDadosDeTesteSemParametros() {
        // Given
        String type = CommandFactory.TYPE_GENERATE_TEST_DATA;

        // When
        Command command = commandFactory.createCommand(type, null);

        // Then
        assertNotNull(command);
        assertTrue(command instanceof GenerateTestDataCommand);
    }

    @Test
    @DisplayName("Deve criar comando de exclusão de dados de teste com parâmetros")
    void deveCriarComandoDeExclusaoDeDadosDeTesteComParametros() {
        // Given
        String type = CommandFactory.TYPE_DELETE_TEST_DATA;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("deleteAll", true);

        // When
        Command command = commandFactory.createCommand(type, parameters);

        // Then
        assertNotNull(command);
        assertTrue(command instanceof DeleteTestDataCommand);
    }

    @Test
    @DisplayName("Deve criar comando de exclusão de dados de teste sem parâmetros")
    void deveCriarComandoDeExclusaoDeDadosDeTesteSemParametros() {
        // Given
        String type = CommandFactory.TYPE_DELETE_TEST_DATA;

        // When
        Command command = commandFactory.createCommand(type, null);

        // Then
        assertNotNull(command);
        assertTrue(command instanceof DeleteTestDataCommand);
    }

    @Test
    @DisplayName("Deve criar comando de limpeza de dados de teste")
    void deveCriarComandoDeLimpezaDeDadosDeTeste() {
        // Given
        String type = CommandFactory.TYPE_CLEAR_TEST_DATA;

        // When
        Command command = commandFactory.createCommand(type, null);

        // Then
        assertNotNull(command);
        assertTrue(command instanceof DeleteTestDataCommand);
    }

    @Test
    @DisplayName("Deve criar comando de validação de dados de teste")
    void deveCriarComandoDeValidacaoDeDadosDeTeste() {
        // Given
        String type = CommandFactory.TYPE_VALIDATE_TEST_DATA;

        // When
        Command command = commandFactory.createCommand(type, null);

        // Then
        assertNotNull(command);
        assertTrue(command instanceof BaseCommand);
        assertEquals("ValidateTestData", command.getName());
    }

    @Test
    @DisplayName("Deve lançar exceção para tipo de comando nulo")
    void deveLancarExcecaoParaTipoDeComandoNulo() {
        // Given
        String type = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> commandFactory.createCommand(type, null)
        );
        
        assertEquals("Tipo do comando não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para tipo de comando vazio")
    void deveLancarExcecaoParaTipoDeComandoVazio() {
        // Given
        String type = "";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> commandFactory.createCommand(type, null)
        );
        
        assertEquals("Tipo do comando não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para tipo de comando com espaços")
    void deveLancarExcecaoParaTipoDeComandoComEspacos() {
        // Given
        String type = "   ";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> commandFactory.createCommand(type, null)
        );
        
        assertEquals("Tipo do comando não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para tipo de comando não suportado")
    void deveLancarExcecaoParaTipoDeComandoNaoSuportado() {
        // Given
        String type = "INVALID_COMMAND";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> commandFactory.createCommand(type, null)
        );
        
        assertEquals("Tipo de comando não suportado: INVALID_COMMAND", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar tipo de comando em minúsculas")
    void deveAceitarTipoDeComandoEmMinusculas() {
        // Given
        String type = "generate_test_data";

        // When
        Command command = commandFactory.createCommand(type, null);

        // Then
        assertNotNull(command);
        assertTrue(command instanceof GenerateTestDataCommand);
    }

    @Test
    @DisplayName("Deve aceitar tipo de comando com case misto")
    void deveAceitarTipoDeComandoComCaseMisto() {
        // Given
        String type = "Delete_Test_Data";

        // When
        Command command = commandFactory.createCommand(type, null);

        // Then
        assertNotNull(command);
        assertTrue(command instanceof DeleteTestDataCommand);
    }

    @Test
    @DisplayName("Deve criar comando de geração de dados de teste usando método de conveniência")
    void deveCriarComandoDeGeracaoDeDadosDeTesteUsandoMetodoDeConveniencia() {
        // When
        Command command = commandFactory.createGenerateTestDataCommand();

        // Then
        assertNotNull(command);
        assertTrue(command instanceof GenerateTestDataCommand);
    }

    @Test
    @DisplayName("Deve criar comando de geração de dados de teste com contagem personalizada")
    void deveCriarComandoDeGeracaoDeDadosDeTesteComContagemPersonalizada() {
        // Given
        int count = 500;

        // When
        Command command = commandFactory.createGenerateTestDataCommand(count);

        // Then
        assertNotNull(command);
        assertTrue(command instanceof GenerateTestDataCommand);
    }

    @Test
    @DisplayName("Deve criar comando de geração de dados de teste com configuração personalizada")
    void deveCriarComandoDeGeracaoDeDadosDeTesteComConfiguracaoPersonalizada() {
        // Given
        int nfseCount = 5;
        int creditosPerNfse = 20;

        // When
        Command command = commandFactory.createGenerateTestDataCommand(nfseCount, creditosPerNfse);

        // Then
        assertNotNull(command);
        assertTrue(command instanceof GenerateTestDataCommand);
    }

    @Test
    @DisplayName("Deve criar comando para deletar todos os dados de teste")
    void deveCriarComandoParaDeletarTodosOsDadosDeTeste() {
        // When
        Command command = commandFactory.createDeleteAllTestDataCommand();

        // Then
        assertNotNull(command);
        assertTrue(command instanceof DeleteTestDataCommand);
    }

    @Test
    @DisplayName("Deve criar comando para deletar dados de teste específicos")
    void deveCriarComandoParaDeletarDadosDeTesteEspecificos() {
        // Given
        String numeroPrefix = "TESTE_123";
        String nfsePrefix = "NFSE_TESTE";

        // When
        Command command = commandFactory.createDeleteSpecificTestDataCommand(numeroPrefix, nfsePrefix);

        // Then
        assertNotNull(command);
        assertTrue(command instanceof DeleteTestDataCommand);
    }

    @Test
    @DisplayName("Deve criar comando para deletar dados de teste por tipo")
    void deveCriarComandoParaDeletarDadosDeTestePorTipo() {
        // Given
        String tipoCredito = "ISS";

        // When
        Command command = commandFactory.createDeleteTestDataByTypeCommand(tipoCredito);

        // Then
        assertNotNull(command);
        assertTrue(command instanceof DeleteTestDataCommand);
    }

    @Test
    @DisplayName("Deve criar comando de limpeza usando método de conveniência")
    void deveCriarComandoDeLimpezaUsandoMetodoDeConveniencia() {
        // When
        Command command = commandFactory.createClearTestDataCommand();

        // Then
        assertNotNull(command);
        assertTrue(command instanceof DeleteTestDataCommand);
    }

    @Test
    @DisplayName("Deve criar comando de validação usando método de conveniência")
    void deveCriarComandoDeValidacaoUsandoMetodoDeConveniencia() {
        // When
        Command command = commandFactory.createValidateTestDataCommand();

        // Then
        assertNotNull(command);
        assertTrue(command instanceof BaseCommand);
        assertEquals("ValidateTestData", command.getName());
    }

    @Test
    @DisplayName("Deve retornar tipos de comando suportados")
    void deveRetornarTiposDeComandoSuportados() {
        // When
        String[] supportedTypes = commandFactory.getSupportedCommandTypes();

        // Then
        assertNotNull(supportedTypes);
        assertEquals(4, supportedTypes.length);
        
        String[] expectedTypes = {
            CommandFactory.TYPE_GENERATE_TEST_DATA,
            CommandFactory.TYPE_DELETE_TEST_DATA,
            CommandFactory.TYPE_CLEAR_TEST_DATA,
            CommandFactory.TYPE_VALIDATE_TEST_DATA
        };
        
        for (String expectedType : expectedTypes) {
            assertTrue(contains(supportedTypes, expectedType));
        }
    }

    @Test
    @DisplayName("Deve verificar se tipo de comando é suportado")
    void deveVerificarSeTipoDeComandoESuportado() {
        // When & Then
        assertTrue(commandFactory.isCommandTypeSupported(CommandFactory.TYPE_GENERATE_TEST_DATA));
        assertTrue(commandFactory.isCommandTypeSupported(CommandFactory.TYPE_DELETE_TEST_DATA));
        assertTrue(commandFactory.isCommandTypeSupported(CommandFactory.TYPE_CLEAR_TEST_DATA));
        assertTrue(commandFactory.isCommandTypeSupported(CommandFactory.TYPE_VALIDATE_TEST_DATA));
    }

    @Test
    @DisplayName("Deve retornar false para tipo de comando não suportado")
    void deveRetornarFalseParaTipoDeComandoNaoSuportado() {
        // When & Then
        assertFalse(commandFactory.isCommandTypeSupported("INVALID_COMMAND"));
        assertFalse(commandFactory.isCommandTypeSupported(""));
    }

    @Test
    @DisplayName("Deve retornar false para tipo de comando nulo")
    void deveRetornarFalseParaTipoDeComandoNulo() {
        // When & Then
        assertFalse(commandFactory.isCommandTypeSupported(null));
    }

    @Test
    @DisplayName("Deve aceitar tipo de comando em minúsculas na verificação")
    void deveAceitarTipoDeComandoEmMinusculasNaVerificacao() {
        // When & Then
        assertTrue(commandFactory.isCommandTypeSupported("generate_test_data"));
        assertTrue(commandFactory.isCommandTypeSupported("DELETE_TEST_DATA"));
        assertTrue(commandFactory.isCommandTypeSupported("Clear_Test_Data"));
    }

    @Test
    @DisplayName("Deve mesclar parâmetros no comando de limpeza")
    void deveMesclarParametrosNoComandoDeLimpeza() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("customParam", "customValue");

        // When
        Command command = commandFactory.createCommand(CommandFactory.TYPE_CLEAR_TEST_DATA, parameters);

        // Then
        assertNotNull(command);
        assertTrue(command instanceof DeleteTestDataCommand);
    }

    @Test
    @DisplayName("Deve criar comando de validação com parâmetros")
    void deveCriarComandoDeValidacaoComParametros() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("validateAll", true);

        // When
        Command command = commandFactory.createCommand(CommandFactory.TYPE_VALIDATE_TEST_DATA, parameters);

        // Then
        assertNotNull(command);
        assertTrue(command instanceof BaseCommand);
        assertEquals("ValidateTestData", command.getName());
    }

    @Test
    @DisplayName("Deve criar comando de geração com parâmetros vazios")
    void deveCriarComandoDeGeracaoComParametrosVazios() {
        // Given
        Map<String, Object> parameters = new HashMap<>();

        // When
        Command command = commandFactory.createCommand(CommandFactory.TYPE_GENERATE_TEST_DATA, parameters);

        // Then
        assertNotNull(command);
        assertTrue(command instanceof GenerateTestDataCommand);
    }

    @Test
    @DisplayName("Deve criar comando de exclusão com parâmetros vazios")
    void deveCriarComandoDeExclusaoComParametrosVazios() {
        // Given
        Map<String, Object> parameters = new HashMap<>();

        // When
        Command command = commandFactory.createCommand(CommandFactory.TYPE_DELETE_TEST_DATA, parameters);

        // Then
        assertNotNull(command);
        assertTrue(command instanceof DeleteTestDataCommand);
    }

    // Método auxiliar para verificar se um array contém um elemento
    private boolean contains(String[] array, String element) {
        for (String item : array) {
            if (item.equals(element)) {
                return true;
            }
        }
        return false;
    }
}
