package br.com.guilhermedealmeidafreitas.creditos.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommandHistoryTest {

    @Mock
    private Command mockCommand1;

    @Mock
    private Command mockCommand2;

    @Mock
    private Command mockCommand3;

    private CommandHistory history;

    @BeforeEach
    void setUp() {
        history = new CommandHistory();
        
        // Setup mock commands
        when(mockCommand1.getName()).thenReturn("Command1");
        when(mockCommand1.getType()).thenReturn("TYPE1");
        when(mockCommand1.getCreatedAt()).thenReturn(LocalDateTime.now().minusHours(2));
        when(mockCommand1.getLastExecutedAt()).thenReturn(LocalDateTime.now().minusHours(2));
        when(mockCommand1.getActualExecutionTime()).thenReturn(100L);
        
        when(mockCommand2.getName()).thenReturn("Command2");
        when(mockCommand2.getType()).thenReturn("TYPE2");
        when(mockCommand2.getCreatedAt()).thenReturn(LocalDateTime.now().minusHours(1));
        when(mockCommand2.getLastExecutedAt()).thenReturn(LocalDateTime.now().minusHours(1));
        when(mockCommand2.getActualExecutionTime()).thenReturn(200L);
        
        when(mockCommand3.getName()).thenReturn("Command1"); // Same name as command1
        when(mockCommand3.getType()).thenReturn("TYPE1");
        when(mockCommand3.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(mockCommand3.getLastExecutedAt()).thenReturn(LocalDateTime.now());
        when(mockCommand3.getActualExecutionTime()).thenReturn(150L);
    }

    @Test
    void testAddExecutedCommand_ShouldAddToHistory() {
        // When
        history.addExecutedCommand(mockCommand1);

        // Then
        assertThat(history.getExecutedCommands()).contains(mockCommand1);
        assertThat(history.getUndoneCommands()).isEmpty();
        assertThat(history.size()).isEqualTo(1);
    }

    @Test
    void testAddExecutedCommand_WithNullCommand_ShouldNotAdd() {
        // When
        history.addExecutedCommand(null);

        // Then
        assertThat(history.getExecutedCommands()).isEmpty();
        assertThat(history.size()).isEqualTo(0);
    }

    @Test
    void testUndoLastCommand_WithEmptyHistory_ShouldReturnNull() {
        // When
        Command result = history.undoLastCommand();

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testUndoLastCommand_WithValidHistory_ShouldMoveToUndone() {
        // Given
        history.addExecutedCommand(mockCommand1);
        history.addExecutedCommand(mockCommand2);

        // When
        Command undoneCommand = history.undoLastCommand();

        // Then
        assertThat(undoneCommand).isEqualTo(mockCommand2);
        assertThat(history.getExecutedCommands()).containsOnly(mockCommand1);
        assertThat(history.getUndoneCommands()).containsOnly(mockCommand2);
        assertThat(history.size()).isEqualTo(2);
    }

    @Test
    void testRedoLastCommand_WithEmptyUndoneHistory_ShouldReturnNull() {
        // When
        Command result = history.redoLastCommand();

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testRedoLastCommand_WithValidUndoneHistory_ShouldMoveToExecuted() {
        // Given
        history.addExecutedCommand(mockCommand1);
        history.undoLastCommand();

        // When
        Command redoneCommand = history.redoLastCommand();

        // Then
        assertThat(redoneCommand).isEqualTo(mockCommand1);
        assertThat(history.getExecutedCommands()).containsOnly(mockCommand1);
        assertThat(history.getUndoneCommands()).isEmpty();
        assertThat(history.size()).isEqualTo(1);
    }

    @Test
    void testCanUndo_WithEmptyHistory_ShouldReturnFalse() {
        // When
        boolean canUndo = history.canUndo();

        // Then
        assertThat(canUndo).isFalse();
    }

    @Test
    void testCanUndo_WithValidHistory_ShouldReturnTrue() {
        // Given
        history.addExecutedCommand(mockCommand1);

        // When
        boolean canUndo = history.canUndo();

        // Then
        assertThat(canUndo).isTrue();
    }

    @Test
    void testCanRedo_WithEmptyUndoneHistory_ShouldReturnFalse() {
        // When
        boolean canRedo = history.canRedo();

        // Then
        assertThat(canRedo).isFalse();
    }

    @Test
    void testCanRedo_WithValidUndoneHistory_ShouldReturnTrue() {
        // Given
        history.addExecutedCommand(mockCommand1);
        history.undoLastCommand();

        // When
        boolean canRedo = history.canRedo();

        // Then
        assertThat(canRedo).isTrue();
    }

    @Test
    void testGetLastExecutedCommand_WithEmptyHistory_ShouldReturnNull() {
        // When
        Command result = history.getLastExecutedCommand();

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testGetLastExecutedCommand_WithValidHistory_ShouldReturnLastCommand() {
        // Given
        history.addExecutedCommand(mockCommand1);
        history.addExecutedCommand(mockCommand2);

        // When
        Command result = history.getLastExecutedCommand();

        // Then
        assertThat(result).isEqualTo(mockCommand2);
    }

    @Test
    void testGetLastUndoneCommand_WithEmptyUndoneHistory_ShouldReturnNull() {
        // When
        Command result = history.getLastUndoneCommand();

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testGetLastUndoneCommand_WithValidUndoneHistory_ShouldReturnLastUndoneCommand() {
        // Given
        history.addExecutedCommand(mockCommand1);
        history.addExecutedCommand(mockCommand2);
        history.undoLastCommand();

        // When
        Command result = history.getLastUndoneCommand();

        // Then
        assertThat(result).isEqualTo(mockCommand2);
    }

    @Test
    void testGetAllCommands_ShouldReturnAllCommands() {
        // Given
        history.addExecutedCommand(mockCommand1);
        history.addExecutedCommand(mockCommand2);
        history.undoLastCommand();

        // When
        List<Command> allCommands = history.getAllCommands();

        // Then
        assertThat(allCommands).hasSize(2);
        assertThat(allCommands).contains(mockCommand1, mockCommand2);
    }

    @Test
    void testFindCommandsByName_WithValidName_ShouldReturnMatchingCommands() {
        // Given
        history.addExecutedCommand(mockCommand1);
        history.addExecutedCommand(mockCommand2);
        history.addExecutedCommand(mockCommand3);

        // When
        List<Command> result = history.findCommandsByName("Command1");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(mockCommand1, mockCommand3);
    }

    @Test
    void testFindCommandsByName_WithInvalidName_ShouldReturnEmptyList() {
        // Given
        history.addExecutedCommand(mockCommand1);

        // When
        List<Command> result = history.findCommandsByName("NonExistentCommand");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testFindCommandsByName_WithNullName_ShouldReturnEmptyList() {
        // Given
        history.addExecutedCommand(mockCommand1);

        // When
        List<Command> result = history.findCommandsByName(null);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testFindCommandsByType_WithValidType_ShouldReturnMatchingCommands() {
        // Given
        history.addExecutedCommand(mockCommand1);
        history.addExecutedCommand(mockCommand2);
        history.addExecutedCommand(mockCommand3);

        // When
        List<Command> result = history.findCommandsByType("TYPE1");

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(mockCommand1, mockCommand3);
    }

    @Test
    void testFindCommandsByType_WithInvalidType_ShouldReturnEmptyList() {
        // Given
        history.addExecutedCommand(mockCommand1);

        // When
        List<Command> result = history.findCommandsByType("NonExistentType");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testFindCommandsByDateRange_WithValidRange_ShouldReturnMatchingCommands() {
        // Given
        history.addExecutedCommand(mockCommand1);
        history.addExecutedCommand(mockCommand2);
        history.addExecutedCommand(mockCommand3);

        LocalDateTime start = LocalDateTime.now().minusHours(3);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        // When
        List<Command> result = history.findCommandsByDateRange(start, end);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).contains(mockCommand1, mockCommand2, mockCommand3);
    }

    @Test
    void testFindCommandsByDateRange_WithInvalidRange_ShouldReturnEmptyList() {
        // Given
        history.addExecutedCommand(mockCommand1);

        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);

        // When
        List<Command> result = history.findCommandsByDateRange(start, end);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testGetStatistics_ShouldReturnCorrectStatistics() {
        // Given
        history.addExecutedCommand(mockCommand1);
        history.addExecutedCommand(mockCommand2);
        history.addExecutedCommand(mockCommand3);
        history.undoLastCommand();

        // When
        CommandHistory.HistoryStatistics stats = history.getStatistics();

        // Then
        assertThat(stats.getTotalCommands()).isEqualTo(3);
        assertThat(stats.getExecutedCommands()).isEqualTo(2);
        assertThat(stats.getUndoneCommands()).isEqualTo(1);
        assertThat(stats.getTotalExecutionTime()).isEqualTo(450L); // 100 + 200 + 150
        assertThat(stats.getAverageExecutionTime()).isEqualTo(150.0); // 450 / 3
        assertThat(stats.getCommandsByName()).containsEntry("Command1", 2);
        assertThat(stats.getCommandsByName()).containsEntry("Command2", 1);
        assertThat(stats.getCommandsByType()).containsEntry("TYPE1", 2);
        assertThat(stats.getCommandsByType()).containsEntry("TYPE2", 1);
    }

    @Test
    void testClear_ShouldClearAllHistory() {
        // Given
        history.addExecutedCommand(mockCommand1);
        history.addExecutedCommand(mockCommand2);
        history.undoLastCommand();

        // When
        history.clear();

        // Then
        assertThat(history.getExecutedCommands()).isEmpty();
        assertThat(history.getUndoneCommands()).isEmpty();
        assertThat(history.size()).isEqualTo(0);
    }

    @Test
    void testClearUndoneCommands_ShouldClearOnlyUndoneCommands() {
        // Given
        history.addExecutedCommand(mockCommand1);
        history.addExecutedCommand(mockCommand2);
        history.undoLastCommand();

        // When
        history.clearUndoneCommands();

        // Then
        assertThat(history.getExecutedCommands()).containsOnly(mockCommand1);
        assertThat(history.getUndoneCommands()).isEmpty();
        assertThat(history.size()).isEqualTo(1);
    }

    @Test
    void testGetMaxHistorySize_ShouldReturnCorrectSize() {
        // When
        int maxSize = history.getMaxHistorySize();

        // Then
        assertThat(maxSize).isEqualTo(100);
    }

    @Test
    void testGetCreatedAt_ShouldReturnCreationTime() {
        // When
        LocalDateTime createdAt = history.getCreatedAt();

        // Then
        assertThat(createdAt).isNotNull();
    }

    @Test
    void testHistoryWithCustomMaxSize_ShouldRespectMaxSize() {
        // Given
        CommandHistory customHistory = new CommandHistory(2);
        customHistory.addExecutedCommand(mockCommand1);
        customHistory.addExecutedCommand(mockCommand2);
        customHistory.addExecutedCommand(mockCommand3);

        // When
        List<Command> executedCommands = customHistory.getExecutedCommands();

        // Then
        assertThat(executedCommands).hasSize(2);
        assertThat(executedCommands).contains(mockCommand2, mockCommand3); // First command should be removed
    }
}
