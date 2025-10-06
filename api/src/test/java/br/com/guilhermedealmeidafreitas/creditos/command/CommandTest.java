package br.com.guilhermedealmeidafreitas.creditos.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommandTest {

    private TestCommand testCommand;

    @BeforeEach
    void setUp() {
        testCommand = new TestCommand();
    }

    @Test
    void testCommandCreation_ShouldHaveCorrectInitialState() {
        // Then
        assertThat(testCommand.getName()).isEqualTo("TestCommand");
        assertThat(testCommand.getDescription()).isEqualTo("Comando de teste");
        assertThat(testCommand.getType()).isEqualTo("TEST");
        assertThat(testCommand.getStatus()).isEqualTo(CommandStatus.CREATED);
        assertThat(testCommand.canExecute()).isTrue();
        assertThat(testCommand.canUndo()).isFalse();
        assertThat(testCommand.getCreatedAt()).isNotNull();
        assertThat(testCommand.getLastExecutedAt()).isNull();
        assertThat(testCommand.getActualExecutionTime()).isEqualTo(0);
    }

    @Test
    void testExecute_ShouldExecuteSuccessfully() throws CommandException {
        // When
        CommandResult result = testCommand.execute();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo("Comando executado com sucesso");
        assertThat(result.getData()).isEqualTo("test-data");
        assertThat(testCommand.getStatus()).isEqualTo(CommandStatus.EXECUTED);
        assertThat(testCommand.getLastExecutedAt()).isNotNull();
        assertThat(testCommand.getActualExecutionTime()).isGreaterThan(0);
        assertThat(testCommand.canExecute()).isFalse();
        assertThat(testCommand.canUndo()).isTrue();
    }

    @Test
    void testExecute_WhenAlreadyExecuted_ShouldThrowException() throws CommandException {
        // Given
        testCommand.execute();

        // When & Then
        assertThatThrownBy(() -> testCommand.execute())
            .isInstanceOf(CommandException.class)
            .hasMessageContaining("Comando não pode ser executado no estado atual");
    }

    @Test
    void testUndo_ShouldUndoSuccessfully() throws CommandException {
        // Given
        testCommand.execute();

        // When
        CommandResult result = testCommand.undo();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo("Comando desfeito com sucesso");
        assertThat(testCommand.getStatus()).isEqualTo(CommandStatus.UNDONE);
        assertThat(testCommand.canExecute()).isTrue();
        assertThat(testCommand.canUndo()).isFalse();
    }

    @Test
    void testUndo_WhenNotExecuted_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> testCommand.undo())
            .isInstanceOf(CommandException.class)
            .hasMessageContaining("Comando não pode ser desfeito no estado atual");
    }

    @Test
    void testSetParameters_ShouldSetParametersCorrectly() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("param1", "value1");
        parameters.put("param2", 123);

        // When
        testCommand.setParameters(parameters);

        // Then
        Map<String, Object> retrievedParams = testCommand.getParameters();
        assertThat(retrievedParams).containsEntry("param1", "value1");
        assertThat(retrievedParams).containsEntry("param2", 123);
    }

    @Test
    void testGetInfo_ShouldReturnCorrectInfo() {
        // When
        String info = testCommand.getInfo();

        // Then
        assertThat(info).contains("TestCommand");
        assertThat(info).contains("TEST");
        assertThat(info).contains("CREATED");
    }

    @Test
    void testGetEstimatedExecutionTime_ShouldReturnDefaultValue() {
        // When
        long estimatedTime = testCommand.getEstimatedExecutionTime();

        // Then
        assertThat(estimatedTime).isEqualTo(1000); // Valor padrão da BaseCommand
    }

    @Test
    void testValidate_ShouldNotThrowException() {
        // When & Then
        try {
            testCommand.validate();
            // Se chegou aqui, não houve exceção
            assertThat(true).isTrue();
        } catch (Exception e) {
            // Se houve exceção, o teste falha
            assertThat(e).isNull();
        }
    }

    @Test
    void testToString_ShouldReturnInfo() {
        // When
        String toString = testCommand.toString();

        // Then
        assertThat(toString).isEqualTo(testCommand.getInfo());
    }

    /**
     * Classe de teste para Command.
     */
    private static class TestCommand extends BaseCommand {
        
        public TestCommand() {
            super("TestCommand", "Comando de teste", "TEST");
        }
        
        @Override
        protected CommandResult doExecute() throws Exception {
            // Simular execução
            Thread.sleep(10);
            return CommandResult.success("Comando executado com sucesso", "test-data", getActualExecutionTime());
        }
        
        @Override
        protected CommandResult doUndo() throws Exception {
            // Simular undo
            Thread.sleep(5);
            return CommandResult.success("Comando desfeito com sucesso", null, getActualExecutionTime());
        }
    }
}
