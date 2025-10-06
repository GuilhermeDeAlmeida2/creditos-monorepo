package br.com.guilhermedealmeidafreitas.creditos.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para CommandException")
class CommandExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem e nome do comando")
    void deveCriarExcecaoComMensagemENomeDoComando() {
        // Given
        String message = "Erro na execução";
        String commandName = "TestCommand";

        // When
        CommandException exception = new CommandException(message, commandName);

        // Then
        assertNotNull(exception);
        assertEquals("[TestCommand] Erro na execução", exception.getMessage());
        assertEquals(commandName, exception.getCommandName());
        assertNull(exception.getStatus());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem, nome do comando e status")
    void deveCriarExcecaoComMensagemNomeDoComandoEStatus() {
        // Given
        String message = "Erro na execução";
        String commandName = "TestCommand";
        CommandStatus status = CommandStatus.EXECUTING;

        // When
        CommandException exception = new CommandException(message, commandName, status);

        // Then
        assertNotNull(exception);
        assertEquals("[TestCommand] Erro na execução", exception.getMessage());
        assertEquals(commandName, exception.getCommandName());
        assertEquals(status, exception.getStatus());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem, nome do comando e causa")
    void deveCriarExcecaoComMensagemNomeDoComandoECausa() {
        // Given
        String message = "Erro na execução";
        String commandName = "TestCommand";
        Throwable cause = new RuntimeException("Erro interno");

        // When
        CommandException exception = new CommandException(message, commandName, cause);

        // Then
        assertNotNull(exception);
        assertEquals("[TestCommand] Erro na execução", exception.getMessage());
        assertEquals(commandName, exception.getCommandName());
        assertNull(exception.getStatus());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem, nome do comando, status e causa")
    void deveCriarExcecaoComMensagemNomeDoComandoStatusECausa() {
        // Given
        String message = "Erro na execução";
        String commandName = "TestCommand";
        CommandStatus status = CommandStatus.FAILED;
        Throwable cause = new RuntimeException("Erro interno");

        // When
        CommandException exception = new CommandException(message, commandName, status, cause);

        // Then
        assertNotNull(exception);
        assertEquals("[TestCommand] Erro na execução", exception.getMessage());
        assertEquals(commandName, exception.getCommandName());
        assertEquals(status, exception.getStatus());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve formatar mensagem corretamente quando nome do comando é nulo")
    void deveFormatarMensagemCorretamenteQuandoNomeDoComandoENulo() {
        // Given
        String message = "Erro na execução";
        String commandName = null;

        // When
        CommandException exception = new CommandException(message, commandName);

        // Then
        assertEquals("Erro na execução", exception.getMessage());
        assertNull(exception.getCommandName());
    }

    @Test
    @DisplayName("Deve formatar mensagem corretamente quando nome do comando é vazio")
    void deveFormatarMensagemCorretamenteQuandoNomeDoComandoEVazio() {
        // Given
        String message = "Erro na execução";
        String commandName = "";

        // When
        CommandException exception = new CommandException(message, commandName);

        // Then
        assertEquals("[] Erro na execução", exception.getMessage());
        assertEquals("", exception.getCommandName());
    }

    @Test
    @DisplayName("Deve herdar de Exception")
    void deveHerdarDeException() {
        // Given
        String message = "Erro na execução";
        String commandName = "TestCommand";

        // When
        CommandException exception = new CommandException(message, commandName);

        // Then
        assertTrue(exception instanceof Exception);
    }

    @Test
    @DisplayName("Deve manter causa quando fornecida no construtor")
    void deveManterCausaQuandoFornecidaNoConstrutor() {
        // Given
        String message = "Erro na execução";
        String commandName = "TestCommand";
        Throwable cause = new IllegalArgumentException("Parâmetro inválido");

        // When
        CommandException exception = new CommandException(message, commandName, cause);

        // Then
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve permitir causa nula")
    void devePermitirCausaNula() {
        // Given
        String message = "Erro na execução";
        String commandName = "TestCommand";
        Throwable cause = null;

        // When
        CommandException exception = new CommandException(message, commandName, cause);

        // Then
        assertNull(exception.getCause());
        assertEquals("[TestCommand] Erro na execução", exception.getMessage());
        assertEquals(commandName, exception.getCommandName());
    }

    @Test
    @DisplayName("Deve permitir mensagem nula")
    void devePermitirMensagemNula() {
        // Given
        String message = null;
        String commandName = "TestCommand";

        // When
        CommandException exception = new CommandException(message, commandName);

        // Then
        assertNotNull(exception);
        assertEquals("[TestCommand] null", exception.getMessage());
        assertEquals(commandName, exception.getCommandName());
    }

    @Test
    @DisplayName("Deve formatar mensagem com caracteres especiais no nome do comando")
    void deveFormatarMensagemComCaracteresEspeciaisNoNomeDoComando() {
        // Given
        String message = "Erro na execução";
        String commandName = "comando-com_especiais@123";

        // When
        CommandException exception = new CommandException(message, commandName);

        // Then
        String expectedMessage = String.format("[%s] %s", commandName, message);
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(commandName, exception.getCommandName());
    }

    @Test
    @DisplayName("Deve formatar mensagem com nome do comando contendo espaços")
    void deveFormatarMensagemComNomeDoComandoContendoEspacos() {
        // Given
        String message = "Erro na execução";
        String commandName = "comando com espaços";

        // When
        CommandException exception = new CommandException(message, commandName);

        // Then
        String expectedMessage = String.format("[%s] %s", commandName, message);
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(commandName, exception.getCommandName());
    }

    @Test
    @DisplayName("Deve retornar status corretamente")
    void deveRetornarStatusCorretamente() {
        // Given
        String message = "Erro na execução";
        String commandName = "TestCommand";
        CommandStatus status = CommandStatus.EXECUTING;

        // When
        CommandException exception = new CommandException(message, commandName, status);

        // Then
        assertEquals(status, exception.getStatus());
    }

    @Test
    @DisplayName("Deve retornar nome do comando corretamente")
    void deveRetornarNomeDoComandoCorretamente() {
        // Given
        String message = "Erro na execução";
        String commandName = "TestCommand";

        // When
        CommandException exception = new CommandException(message, commandName);

        // Then
        assertEquals(commandName, exception.getCommandName());
    }
}
