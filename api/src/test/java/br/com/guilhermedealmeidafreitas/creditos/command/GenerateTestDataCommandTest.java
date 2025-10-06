package br.com.guilhermedealmeidafreitas.creditos.command;

import br.com.guilhermedealmeidafreitas.creditos.builder.CreditoBuilderFactory;
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
 * Testes unitários para GenerateTestDataCommand.
 * 
 * Este teste cobre todos os cenários do comando de geração de dados de teste,
 * incluindo execução, undo, validação e diferentes parâmetros.
 */
@ExtendWith(MockitoExtension.class)
class GenerateTestDataCommandTest {

    @Mock
    private CreditoRepository creditoRepository;

    @Mock
    private CreditoBuilderFactory creditoBuilderFactory;

    @Mock
    private Credito credito;

    private GenerateTestDataCommand generateTestDataCommand;

    @BeforeEach
    void setUp() {
        generateTestDataCommand = new GenerateTestDataCommand(creditoRepository, creditoBuilderFactory);
    }

    @Test
    @DisplayName("Deve executar comando com parâmetros padrão")
    void deveExecutarComandoComParametrosPadrao() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        generateTestDataCommand.setParameters(parameters);

        List<Credito> generatedCreditos = createTestCreditos(300);
        when(creditoBuilderFactory.forTestData(anyString(), anyString(), any(LocalDate.class), 
                anyString(), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(mockBuilder());
        when(creditoRepository.saveAll(any())).thenReturn(generatedCreditos);

        // When
        CommandResult result = generateTestDataCommand.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Gerados 300 registros de teste"));
        assertEquals(300, result.getData());
        
        Map<String, Object> metadata = result.getMetadata();
        assertEquals(300, metadata.get("generatedCount"));
        assertEquals(10, metadata.get("nfseCount"));
        assertEquals(30, metadata.get("creditosPerNfse"));
        
        verify(creditoRepository).saveAll(any());
    }

    @Test
    @DisplayName("Deve executar comando com parâmetros personalizados")
    void deveExecutarComandoComParametrosPersonalizados() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(GenerateTestDataCommand.PARAM_NFSE_COUNT, 5);
        parameters.put(GenerateTestDataCommand.PARAM_CREDITOS_PER_NFSE, 10);
        parameters.put(GenerateTestDataCommand.PARAM_VALOR_MIN, 500.0);
        parameters.put(GenerateTestDataCommand.PARAM_VALOR_MAX, 2000.0);
        parameters.put(GenerateTestDataCommand.PARAM_ALIQUOTA_MIN, 2.0);
        parameters.put(GenerateTestDataCommand.PARAM_ALIQUOTA_MAX, 8.0);
        generateTestDataCommand.setParameters(parameters);

        List<Credito> generatedCreditos = createTestCreditos(50);
        when(creditoBuilderFactory.forTestData(anyString(), anyString(), any(LocalDate.class), 
                anyString(), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(mockBuilder());
        when(creditoRepository.saveAll(any())).thenReturn(generatedCreditos);

        // When
        CommandResult result = generateTestDataCommand.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Gerados 50 registros de teste"));
        assertEquals(50, result.getData());
        
        Map<String, Object> metadata = result.getMetadata();
        assertEquals(50, metadata.get("generatedCount"));
        assertEquals(5, metadata.get("nfseCount"));
        assertEquals(10, metadata.get("creditosPerNfse"));
        assertEquals("500.00 - 2000.00", metadata.get("valorRange"));
        assertEquals("2.00% - 8.00%", metadata.get("aliquotaRange"));
        
        verify(creditoRepository).saveAll(any());
    }

    @Test
    @DisplayName("Deve executar comando com tipos de crédito personalizados")
    void deveExecutarComandoComTiposDeCreditoPersonalizados() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(GenerateTestDataCommand.PARAM_NFSE_COUNT, 2);
        parameters.put(GenerateTestDataCommand.PARAM_CREDITOS_PER_NFSE, 5);
        parameters.put(GenerateTestDataCommand.PARAM_TIPOS_CREDITO, new String[]{"ISS", "ICMS"});
        generateTestDataCommand.setParameters(parameters);

        List<Credito> generatedCreditos = createTestCreditos(10);
        when(creditoBuilderFactory.forTestData(anyString(), anyString(), any(LocalDate.class), 
                anyString(), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(mockBuilder());
        when(creditoRepository.saveAll(any())).thenReturn(generatedCreditos);

        // When
        CommandResult result = generateTestDataCommand.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(10, result.getData());
        
        Map<String, Object> metadata = result.getMetadata();
        assertEquals(10, metadata.get("generatedCount"));
        assertEquals(2, metadata.get("nfseCount"));
        assertEquals(5, metadata.get("creditosPerNfse"));
        assertArrayEquals(new String[]{"ISS", "ICMS"}, (String[]) metadata.get("tiposCredito"));
        
        verify(creditoRepository).saveAll(any());
    }

    @Test
    @DisplayName("Deve executar undo com sucesso")
    void deveExecutarUndoComSucesso() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(GenerateTestDataCommand.PARAM_NFSE_COUNT, 2);
        parameters.put(GenerateTestDataCommand.PARAM_CREDITOS_PER_NFSE, 5);
        generateTestDataCommand.setParameters(parameters);

        List<Credito> generatedCreditos = createTestCreditos(10);
        when(creditoBuilderFactory.forTestData(anyString(), anyString(), any(LocalDate.class), 
                anyString(), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(mockBuilder());
        when(creditoRepository.saveAll(any())).thenReturn(generatedCreditos);

        // Primeiro executa o comando
        generateTestDataCommand.execute();

        // When - Executa undo
        CommandResult result = generateTestDataCommand.undo();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Removidos 10 registros de teste", result.getMessage());
        assertEquals(10, result.getData());
        
        verify(creditoRepository).deleteAll(generatedCreditos);
    }

    @Test
    @DisplayName("Deve retornar sucesso no undo quando não há dados para remover")
    void deveRetornarSucessoNoUndoQuandoNaoHaDadosParaRemover() throws Exception {
        // When
        CommandResult result = generateTestDataCommand.undo();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Nenhum dado de teste para remover", result.getMessage());
        assertEquals(0, result.getData());
        
        verify(creditoRepository, never()).deleteAll(any());
    }

    @Test
    @DisplayName("Deve validar parâmetro count")
    void deveValidarParametroCount() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(GenerateTestDataCommand.PARAM_COUNT, 0);
        generateTestDataCommand.setParameters(parameters);

        // When & Then
        CommandValidationException exception = assertThrows(
            CommandValidationException.class,
            () -> generateTestDataCommand.validate()
        );
        
        assertEquals("Contagem deve ser maior que zero", exception.getMessage());
        assertEquals(GenerateTestDataCommand.PARAM_COUNT, exception.getField());
    }

    @Test
    @DisplayName("Deve validar parâmetro nfseCount")
    void deveValidarParametroNfseCount() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(GenerateTestDataCommand.PARAM_NFSE_COUNT, -1);
        generateTestDataCommand.setParameters(parameters);

        // When & Then
        CommandValidationException exception = assertThrows(
            CommandValidationException.class,
            () -> generateTestDataCommand.validate()
        );
        
        assertEquals("Número de NFS-e deve ser maior que zero", exception.getMessage());
        assertEquals(GenerateTestDataCommand.PARAM_NFSE_COUNT, exception.getField());
    }

    @Test
    @DisplayName("Deve validar parâmetro creditosPerNfse")
    void deveValidarParametroCreditosPerNfse() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(GenerateTestDataCommand.PARAM_CREDITOS_PER_NFSE, 0);
        generateTestDataCommand.setParameters(parameters);

        // When & Then
        CommandValidationException exception = assertThrows(
            CommandValidationException.class,
            () -> generateTestDataCommand.validate()
        );
        
        assertEquals("Créditos por NFS-e deve ser maior que zero", exception.getMessage());
        assertEquals(GenerateTestDataCommand.PARAM_CREDITOS_PER_NFSE, exception.getField());
    }

    @Test
    @DisplayName("Deve validar range de valores")
    void deveValidarRangeDeValores() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(GenerateTestDataCommand.PARAM_VALOR_MIN, 1000.0);
        parameters.put(GenerateTestDataCommand.PARAM_VALOR_MAX, 500.0); // Max menor que min
        generateTestDataCommand.setParameters(parameters);

        // When & Then
        CommandValidationException exception = assertThrows(
            CommandValidationException.class,
            () -> generateTestDataCommand.validate()
        );
        
        assertEquals("Valor mínimo deve ser menor que o máximo", exception.getMessage());
        assertEquals(GenerateTestDataCommand.PARAM_VALOR_MIN, exception.getField());
    }

    @Test
    @DisplayName("Deve validar range de alíquotas")
    void deveValidarRangeDeAliquotas() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(GenerateTestDataCommand.PARAM_ALIQUOTA_MIN, 10.0);
        parameters.put(GenerateTestDataCommand.PARAM_ALIQUOTA_MAX, 5.0); // Max menor que min
        generateTestDataCommand.setParameters(parameters);

        // When & Then
        CommandValidationException exception = assertThrows(
            CommandValidationException.class,
            () -> generateTestDataCommand.validate()
        );
        
        assertEquals("Alíquota mínima deve ser menor que a máxima", exception.getMessage());
        assertEquals(GenerateTestDataCommand.PARAM_ALIQUOTA_MIN, exception.getField());
    }

    @Test
    @DisplayName("Deve passar na validação com parâmetros válidos")
    void devePassarNaValidacaoComParametrosValidos() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(GenerateTestDataCommand.PARAM_COUNT, 100);
        parameters.put(GenerateTestDataCommand.PARAM_NFSE_COUNT, 5);
        parameters.put(GenerateTestDataCommand.PARAM_CREDITOS_PER_NFSE, 20);
        parameters.put(GenerateTestDataCommand.PARAM_VALOR_MIN, 1000.0);
        parameters.put(GenerateTestDataCommand.PARAM_VALOR_MAX, 5000.0);
        parameters.put(GenerateTestDataCommand.PARAM_ALIQUOTA_MIN, 2.0);
        parameters.put(GenerateTestDataCommand.PARAM_ALIQUOTA_MAX, 10.0);
        generateTestDataCommand.setParameters(parameters);

        // When & Then - Não deve lançar exceção
        assertDoesNotThrow(() -> generateTestDataCommand.validate());
    }

    @Test
    @DisplayName("Deve usar valores padrão quando parâmetros não são fornecidos")
    void deveUsarValoresPadraoQuandoParametrosNaoSaoFornecidos() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        generateTestDataCommand.setParameters(parameters);

        List<Credito> generatedCreditos = createTestCreditos(300);
        when(creditoBuilderFactory.forTestData(anyString(), anyString(), any(LocalDate.class), 
                anyString(), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(mockBuilder());
        when(creditoRepository.saveAll(any())).thenReturn(generatedCreditos);

        // When
        CommandResult result = generateTestDataCommand.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        
        Map<String, Object> metadata = result.getMetadata();
        assertEquals(300, metadata.get("generatedCount"));
        assertEquals(10, metadata.get("nfseCount")); // DEFAULT_NFSE_COUNT
        assertEquals(30, metadata.get("creditosPerNfse")); // DEFAULT_CREDITOS_PER_NFSE
        assertEquals("1000.00 - 50000.00", metadata.get("valorRange")); // DEFAULT_VALOR_MIN/MAX
        assertEquals("1.00% - 15.00%", metadata.get("aliquotaRange")); // DEFAULT_ALIQUOTA_MIN/MAX
    }

    @Test
    @DisplayName("Deve incluir metadados corretos no resultado")
    void deveIncluirMetadadosCorretosNoResultado() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(GenerateTestDataCommand.PARAM_NFSE_COUNT, 3);
        parameters.put(GenerateTestDataCommand.PARAM_CREDITOS_PER_NFSE, 7);
        parameters.put(GenerateTestDataCommand.PARAM_TIPOS_CREDITO, new String[]{"ISS", "ICMS"});
        generateTestDataCommand.setParameters(parameters);

        List<Credito> generatedCreditos = createTestCreditos(21);
        when(creditoBuilderFactory.forTestData(anyString(), anyString(), any(LocalDate.class), 
                anyString(), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(mockBuilder());
        when(creditoRepository.saveAll(any())).thenReturn(generatedCreditos);

        // When
        CommandResult result = generateTestDataCommand.execute();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        
        Map<String, Object> metadata = result.getMetadata();
        assertEquals(21, metadata.get("generatedCount"));
        assertEquals(3, metadata.get("nfseCount"));
        assertEquals(7, metadata.get("creditosPerNfse"));
        assertArrayEquals(new String[]{"ISS", "ICMS"}, (String[]) metadata.get("tiposCredito"));
        assertTrue(metadata.containsKey("valorRange"));
        assertTrue(metadata.containsKey("aliquotaRange"));
    }

    @Test
    @DisplayName("Deve executar undo após execução bem-sucedida")
    void deveExecutarUndoAposExecucaoBemSucedida() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(GenerateTestDataCommand.PARAM_NFSE_COUNT, 2);
        parameters.put(GenerateTestDataCommand.PARAM_CREDITOS_PER_NFSE, 5);
        generateTestDataCommand.setParameters(parameters);

        List<Credito> generatedCreditos = createTestCreditos(10);
        when(creditoBuilderFactory.forTestData(anyString(), anyString(), any(LocalDate.class), 
                anyString(), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(mockBuilder());
        when(creditoRepository.saveAll(any())).thenReturn(generatedCreditos);

        // When
        CommandResult executeResult = generateTestDataCommand.execute();
        CommandResult undoResult = generateTestDataCommand.undo();

        // Then
        assertTrue(executeResult.isSuccess());
        assertTrue(undoResult.isSuccess());
        assertEquals(10, executeResult.getData());
        assertEquals(10, undoResult.getData());
        
        verify(creditoRepository).saveAll(any());
        verify(creditoRepository).deleteAll(generatedCreditos);
    }

    @Test
    @DisplayName("Deve limpar dados anteriores antes de gerar novos")
    void deveLimparDadosAnterioresAntesDeGerarNovos() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(GenerateTestDataCommand.PARAM_NFSE_COUNT, 1);
        parameters.put(GenerateTestDataCommand.PARAM_CREDITOS_PER_NFSE, 2);
        generateTestDataCommand.setParameters(parameters);

        List<Credito> firstGeneration = createTestCreditos(2);
        List<Credito> secondGeneration = createTestCreditos(2);
        
        when(creditoBuilderFactory.forTestData(anyString(), anyString(), any(LocalDate.class), 
                anyString(), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(mockBuilder());
        when(creditoRepository.saveAll(any()))
                .thenReturn(firstGeneration)
                .thenReturn(secondGeneration);

        // When - Executa duas vezes
        CommandResult firstResult = generateTestDataCommand.execute();
        CommandResult secondResult = generateTestDataCommand.execute();

        // Then
        assertTrue(firstResult.isSuccess());
        assertTrue(secondResult.isSuccess());
        assertEquals(2, firstResult.getData());
        assertEquals(2, secondResult.getData());
        
        verify(creditoRepository, times(2)).saveAll(any());
    }

    @Test
    @DisplayName("Deve validar parâmetros com valores nulos")
    void deveValidarParametrosComValoresNulos() throws Exception {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(GenerateTestDataCommand.PARAM_COUNT, null);
        parameters.put(GenerateTestDataCommand.PARAM_NFSE_COUNT, null);
        generateTestDataCommand.setParameters(parameters);

        // When & Then - Não deve lançar exceção para valores nulos
        assertDoesNotThrow(() -> generateTestDataCommand.validate());
    }

    @Test
    @DisplayName("Deve criar instância para gerar dados de teste")
    void deveCriarInstanciaParaGerarDadosDeTeste() {
        // When
        GenerateTestDataCommand command = new GenerateTestDataCommand(creditoRepository, creditoBuilderFactory);

        // Then
        assertNotNull(command);
        assertEquals("GenerateTestData", command.getName());
    }

    // Método auxiliar para criar créditos de teste
    private List<Credito> createTestCreditos(int count) {
        List<Credito> creditos = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            creditos.add(credito);
        }
        return creditos;
    }

    // Método auxiliar para criar um mock do builder
    private br.com.guilhermedealmeidafreitas.creditos.builder.CreditoBuilder mockBuilder() {
        br.com.guilhermedealmeidafreitas.creditos.builder.CreditoBuilder builder = 
                mock(br.com.guilhermedealmeidafreitas.creditos.builder.CreditoBuilder.class);
        when(builder.build()).thenReturn(credito);
        return builder;
    }
}
