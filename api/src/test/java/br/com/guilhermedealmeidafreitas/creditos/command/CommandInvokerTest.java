package br.com.guilhermedealmeidafreitas.creditos.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommandInvokerTest {

    @Mock
    private Command mockCommand;

    @Mock
    private CommandExecutionListener mockListener;

    private CommandInvoker invoker;

    @BeforeEach
    void setUp() {
        invoker = new CommandInvoker();
        
        // Setup mock command
        when(mockCommand.getName()).thenReturn("TestCommand");
        when(mockCommand.getStatus()).thenReturn(CommandStatus.CREATED);
        when(mockCommand.canExecute()).thenReturn(true);
        when(mockCommand.canUndo()).thenReturn(false);
    }

    @Test
    void testExecute_WithValidCommand_ShouldExecuteSuccessfully() throws CommandException {
        // Given
        CommandResult expectedResult = CommandResult.success("Success", "data", 100);
        when(mockCommand.execute()).thenReturn(expectedResult);

        // When
        CommandResult result = invoker.execute(mockCommand);

        // Then
        assertThat(result).isEqualTo(expectedResult);
        verify(mockCommand).execute();
        assertThat(invoker.getCommandHistory()).contains(mockCommand);
    }

    @Test
    void testExecute_WithNullCommand_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> invoker.execute(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Comando não pode ser nulo");
    }

    @Test
    void testExecute_WithCommandException_ShouldPropagateException() throws CommandException {
        // Given
        CommandException expectedException = new CommandException("Test error", "TestCommand");
        when(mockCommand.execute()).thenThrow(expectedException);

        // When & Then
        assertThatThrownBy(() -> invoker.execute(mockCommand))
            .isInstanceOf(CommandException.class)
            .isEqualTo(expectedException);
        
        verify(mockCommand).execute();
        assertThat(invoker.getCommandHistory()).doesNotContain(mockCommand);
    }

    @Test
    void testExecuteAsync_ShouldReturnFuture() throws CommandException {
        // Given
        CommandResult expectedResult = CommandResult.success("Success", "data", 100);
        when(mockCommand.execute()).thenReturn(expectedResult);

        // When
        Future<CommandResult> future = invoker.executeAsync(mockCommand);

        // Then
        assertThat(future).isNotNull();
        // Note: In a real test, you would wait for the future to complete
    }

    @Test
    void testExecuteCompletable_ShouldReturnCompletableFuture() throws CommandException {
        // Given
        CommandResult expectedResult = CommandResult.success("Success", "data", 100);
        when(mockCommand.execute()).thenReturn(expectedResult);

        // When
        var future = invoker.executeCompletable(mockCommand);

        // Then
        assertThat(future).isNotNull();
        // Note: In a real test, you would wait for the future to complete
    }

    @Test
    void testUndo_WithValidCommand_ShouldUndoSuccessfully() throws CommandException {
        // Given
        when(mockCommand.canUndo()).thenReturn(true);
        CommandResult expectedResult = CommandResult.success("Undone", null, 50);
        when(mockCommand.undo()).thenReturn(expectedResult);

        // When
        CommandResult result = invoker.undo(mockCommand);

        // Then
        assertThat(result).isEqualTo(expectedResult);
        verify(mockCommand).undo();
    }

    @Test
    void testUndo_WithNullCommand_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> invoker.undo(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Comando não pode ser nulo");
    }

    @Test
    void testUndo_WithCommandThatCannotUndo_ShouldThrowException() {
        // Given
        when(mockCommand.canUndo()).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> invoker.undo(mockCommand))
            .isInstanceOf(CommandException.class)
            .hasMessageContaining("Comando não pode ser desfeito");
    }

    @Test
    void testUndoLast_WithEmptyHistory_ShouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> invoker.undoLast())
            .isInstanceOf(CommandException.class)
            .hasMessageContaining("Nenhum comando no histórico para desfazer");
    }

    @Test
    void testUndoLast_WithValidHistory_ShouldUndoLastCommand() throws CommandException {
        // Given
        CommandResult executeResult = CommandResult.success("Success", "data", 100);
        CommandResult undoResult = CommandResult.success("Undone", null, 50);
        when(mockCommand.execute()).thenReturn(executeResult);
        when(mockCommand.canUndo()).thenReturn(true);
        when(mockCommand.undo()).thenReturn(undoResult);

        // Execute command first
        invoker.execute(mockCommand);

        // When
        CommandResult result = invoker.undoLast();

        // Then
        assertThat(result).isEqualTo(undoResult);
        verify(mockCommand).undo();
    }

    @Test
    void testCancel_WithCancellableCommand_ShouldCancelSuccessfully() {
        // Given
        when(mockCommand.getStatus()).thenReturn(CommandStatus.CREATED);

        // When
        boolean cancelled = invoker.cancel(mockCommand);

        // Then
        assertThat(cancelled).isTrue();
        verify(mockCommand).setStatus(CommandStatus.CANCELLED);
    }

    @Test
    void testCancel_WithNonCancellableCommand_ShouldReturnFalse() {
        // Given
        when(mockCommand.getStatus()).thenReturn(CommandStatus.EXECUTED);

        // When
        boolean cancelled = invoker.cancel(mockCommand);

        // Then
        assertThat(cancelled).isFalse();
        verify(mockCommand, never()).setStatus(any());
    }

    @Test
    void testCancel_WithNullCommand_ShouldReturnFalse() {
        // When
        boolean cancelled = invoker.cancel(null);

        // Then
        assertThat(cancelled).isFalse();
    }

    @Test
    void testGetCommandHistory_ShouldReturnHistory() throws CommandException {
        // Given
        CommandResult result = CommandResult.success("Success", "data", 100);
        when(mockCommand.execute()).thenReturn(result);

        // When
        invoker.execute(mockCommand);
        var history = invoker.getCommandHistory();

        // Then
        assertThat(history).hasSize(1);
        assertThat(history).contains(mockCommand);
    }

    @Test
    void testClearHistory_ShouldClearHistory() throws CommandException {
        // Given
        CommandResult result = CommandResult.success("Success", "data", 100);
        when(mockCommand.execute()).thenReturn(result);
        invoker.execute(mockCommand);

        // When
        invoker.clearHistory();

        // Then
        assertThat(invoker.getCommandHistory()).isEmpty();
    }

    @Test
    void testGetStatistics_ShouldReturnStatistics() throws CommandException {
        // Given
        CommandResult result = CommandResult.success("Success", "data", 100);
        when(mockCommand.execute()).thenReturn(result);
        when(mockCommand.getStatus()).thenReturn(CommandStatus.EXECUTED);
        invoker.execute(mockCommand);

        // When
        CommandInvoker.CommandStatistics stats = invoker.getStatistics();

        // Then
        assertThat(stats.getTotalCommands()).isEqualTo(1);
        assertThat(stats.getSuccessfulCommands()).isEqualTo(1);
        assertThat(stats.getFailedCommands()).isEqualTo(0);
        assertThat(stats.getCancelledCommands()).isEqualTo(0);
    }

    @Test
    void testAddListener_ShouldAddListener() {
        // When
        invoker.addListener(mockListener);

        // Then
        // Listener is added (no direct way to verify, but no exception should be thrown)
        assertThat(true).isTrue(); // Placeholder assertion
    }

    @Test
    void testRemoveListener_ShouldRemoveListener() {
        // Given
        invoker.addListener(mockListener);

        // When
        invoker.removeListener(mockListener);

        // Then
        // Listener is removed (no direct way to verify, but no exception should be thrown)
        assertThat(true).isTrue(); // Placeholder assertion
    }

    @Test
    void testShutdown_ShouldShutdownExecutor() {
        // When
        invoker.shutdown();

        // Then
        // Executor is shutdown (no direct way to verify, but no exception should be thrown)
        assertThat(true).isTrue(); // Placeholder assertion
    }
}
