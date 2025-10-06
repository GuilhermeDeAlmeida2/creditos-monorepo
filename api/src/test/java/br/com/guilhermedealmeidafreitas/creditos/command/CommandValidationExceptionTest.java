package br.com.guilhermedealmeidafreitas.creditos.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para CommandValidationException.
 * 
 * Este teste cobre todos os cenários da exceção de validação de comandos,
 * incluindo construtores, métodos e formatação de mensagens.
 */
@ExtendWith(MockitoExtension.class)
class CommandValidationExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem, nome do comando e campo")
    void deveCriarExcecaoComMensagemNomeComandoECampo() {
        // Given
        String message = "Campo obrigatório";
        String commandName = "TestCommand";
        String field = "nome";

        // When
        CommandValidationException exception = new CommandValidationException(message, commandName, field);

        // Then
        assertNotNull(exception);
        assertEquals(String.format("[%s] %s (campo: %s)", commandName, message, field), exception.getMessage());
        assertEquals(commandName, exception.getCommandName());
        assertEquals(field, exception.getField());
    }

    @Test
    @DisplayName("Deve criar exceção com mensagem, nome do comando, campo e causa")
    void deveCriarExcecaoComMensagemNomeComandoCampoECausa() {
        // Given
        String message = "Campo obrigatório";
        String commandName = "TestCommand";
        String field = "nome";
        Throwable cause = new IllegalArgumentException("Erro interno");

        // When
        CommandValidationException exception = new CommandValidationException(message, commandName, field, cause);

        // Then
        assertNotNull(exception);
        assertEquals(String.format("[%s] %s (campo: %s)", commandName, message, field), exception.getMessage());
        assertEquals(commandName, exception.getCommandName());
        assertEquals(field, exception.getField());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve retornar campo corretamente")
    void deveRetornarCampoCorretamente() {
        // Given
        String message = "Campo obrigatório";
        String commandName = "TestCommand";
        String field = "email";

        // When
        CommandValidationException exception = new CommandValidationException(message, commandName, field);

        // Then
        assertEquals(field, exception.getField());
    }

    @Test
    @DisplayName("Deve formatar mensagem corretamente quando campo não é nulo")
    void deveFormatarMensagemCorretamenteQuandoCampoNaoENulo() {
        // Given
        String message = "Campo obrigatório";
        String commandName = "TestCommand";
        String field = "nome";

        // When
        CommandValidationException exception = new CommandValidationException(message, commandName, field);

        // Then
        String expectedMessage = String.format("[%s] %s (campo: %s)", commandName, message, field);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve formatar mensagem corretamente quando campo é nulo")
    void deveFormatarMensagemCorretamenteQuandoCampoENulo() {
        // Given
        String message = "Campo obrigatório";
        String commandName = "TestCommand";
        String field = null;

        // When
        CommandValidationException exception = new CommandValidationException(message, commandName, field);

        // Then
        String expectedMessage = String.format("[%s] %s", commandName, message);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve formatar mensagem corretamente quando campo é vazio")
    void deveFormatarMensagemCorretamenteQuandoCampoEVazio() {
        // Given
        String message = "Campo obrigatório";
        String commandName = "TestCommand";
        String field = "";

        // When
        CommandValidationException exception = new CommandValidationException(message, commandName, field);

        // Then
        String expectedMessage = String.format("[%s] %s (campo: %s)", commandName, message, field);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve herdar de CommandException")
    void deveHerdarDeCommandException() {
        // Given
        String message = "Campo obrigatório";
        String commandName = "TestCommand";
        String field = "nome";

        // When
        CommandValidationException exception = new CommandValidationException(message, commandName, field);

        // Then
        assertTrue(exception instanceof CommandException);
    }

    @Test
    @DisplayName("Deve manter causa quando fornecida no construtor")
    void deveManterCausaQuandoFornecidaNoConstrutor() {
        // Given
        String message = "Campo obrigatório";
        String commandName = "TestCommand";
        String field = "nome";
        Throwable cause = new RuntimeException("Erro interno");

        // When
        CommandValidationException exception = new CommandValidationException(message, commandName, field, cause);

        // Then
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Deve permitir causa nula")
    void devePermitirCausaNula() {
        // Given
        String message = "Campo obrigatório";
        String commandName = "TestCommand";
        String field = "nome";
        Throwable cause = null;

        // When
        CommandValidationException exception = new CommandValidationException(message, commandName, field, cause);

        // Then
        assertNull(exception.getCause());
        assertEquals(String.format("[%s] %s (campo: %s)", commandName, message, field), exception.getMessage());
        assertEquals(commandName, exception.getCommandName());
        assertEquals(field, exception.getField());
    }

    @Test
    @DisplayName("Deve permitir mensagem nula")
    void devePermitirMensagemNula() {
        // Given
        String message = null;
        String commandName = "TestCommand";
        String field = "nome";

        // When
        CommandValidationException exception = new CommandValidationException(message, commandName, field);

        // Then
        assertNotNull(exception);
        assertEquals(String.format("[%s] %s (campo: %s)", commandName, message, field), exception.getMessage());
        assertEquals(commandName, exception.getCommandName());
        assertEquals(field, exception.getField());
    }

    @Test
    @DisplayName("Deve permitir nome do comando nulo")
    void devePermitirNomeDoComandoNulo() {
        // Given
        String message = "Campo obrigatório";
        String commandName = null;
        String field = "nome";

        // When
        CommandValidationException exception = new CommandValidationException(message, commandName, field);

        // Then
        assertNotNull(exception);
        assertEquals(String.format("%s (campo: %s)", message, field), exception.getMessage());
        assertEquals(commandName, exception.getCommandName());
        assertEquals(field, exception.getField());
    }

    @Test
    @DisplayName("Deve formatar mensagem com caracteres especiais no campo")
    void deveFormatarMensagemComCaracteresEspeciaisNoCampo() {
        // Given
        String message = "Campo obrigatório";
        String commandName = "TestCommand";
        String field = "campo-com_especiais@123";

        // When
        CommandValidationException exception = new CommandValidationException(message, commandName, field);

        // Then
        String expectedMessage = String.format("[%s] %s (campo: %s)", commandName, message, field);
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Deve formatar mensagem com campo contendo espaços")
    void deveFormatarMensagemComCampoContendoEspacos() {
        // Given
        String message = "Campo obrigatório";
        String commandName = "TestCommand";
        String field = "campo com espaços";

        // When
        CommandValidationException exception = new CommandValidationException(message, commandName, field);

        // Then
        String expectedMessage = String.format("[%s] %s (campo: %s)", commandName, message, field);
        assertEquals(expectedMessage, exception.getMessage());
    }
}