package br.com.guilhermedealmeidafreitas.creditos.constants;

import br.com.guilhermedealmeidafreitas.creditos.config.ValidationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a classe ValidationConstants.
 * 
 * Verifica se as constantes centralizadas estão corretas e se a classe
 * está funcionando conforme esperado após a refatoração DRY.
 */
@DisplayName("ValidationConstants - Testes de Constantes Centralizadas")
class ValidationConstantsTest {

    private ValidationConstants validationConstants;

    @BeforeEach
    void setUp() {
        ValidationConfig validationConfig = new ValidationConfig();
        validationConstants = new ValidationConstants(validationConfig);
    }

    @Test
    @DisplayName("Deve conter todos os campos válidos para ordenação")
    void deveConterTodosOsCamposValidosParaOrdenacao() {
        // Arrange & Act
        Set<String> validSortFields = validationConstants.getValidSortFields();
        
        // Assert
        assertNotNull(validSortFields, "VALID_SORT_FIELDS não deve ser nulo");
        assertFalse(validSortFields.isEmpty(), "VALID_SORT_FIELDS não deve estar vazio");
        
        // Verifica se contém todos os campos esperados
        assertTrue(validSortFields.contains("id"), "Deve conter campo 'id'");
        assertTrue(validSortFields.contains("numeroCredito"), "Deve conter campo 'numeroCredito'");
        assertTrue(validSortFields.contains("numeroNfse"), "Deve conter campo 'numeroNfse'");
        assertTrue(validSortFields.contains("dataConstituicao"), "Deve conter campo 'dataConstituicao'");
        assertTrue(validSortFields.contains("valorIssqn"), "Deve conter campo 'valorIssqn'");
        assertTrue(validSortFields.contains("tipoCredito"), "Deve conter campo 'tipoCredito'");
        assertTrue(validSortFields.contains("simplesNacional"), "Deve conter campo 'simplesNacional'");
        assertTrue(validSortFields.contains("aliquota"), "Deve conter campo 'aliquota'");
        assertTrue(validSortFields.contains("valorFaturado"), "Deve conter campo 'valorFaturado'");
        assertTrue(validSortFields.contains("valorDeducao"), "Deve conter campo 'valorDeducao'");
        assertTrue(validSortFields.contains("baseCalculo"), "Deve conter campo 'baseCalculo'");
        
        // Verifica se não contém campos inválidos
        assertFalse(validSortFields.contains("campoInexistente"), "Não deve conter campos inexistentes");
        assertFalse(validSortFields.contains(""), "Não deve conter string vazia");
    }

    @Test
    @DisplayName("Deve ter o tamanho correto de campos válidos")
    void deveTerOTamanhoCorretoDeCamposValidos() {
        // Arrange & Act
        Set<String> validSortFields = validationConstants.getValidSortFields();
        
        // Assert
        assertEquals(11, validSortFields.size(), "Deve conter exatamente 11 campos válidos");
    }

    @Test
    @DisplayName("Deve ter valores padrão corretos para paginação")
    void deveTerValoresPadraoCorretosParaPaginacao() {
        // Assert
        assertEquals(10, validationConstants.getDefaultPageSize(), "Tamanho padrão da página deve ser 10");
        assertEquals(100, validationConstants.getMaxPageSize(), "Tamanho máximo da página deve ser 100");
        assertEquals("id", validationConstants.getDefaultSortField(), "Campo padrão de ordenação deve ser 'id'");
        assertEquals("ASC", validationConstants.getDefaultSortDirection(), "Direção padrão deve ser 'ASC'");
    }

    @Test
    @DisplayName("Deve ter valores positivos para tamanhos de página")
    void deveTerValoresPositivosParaTamanhosDePagina() {
        // Assert
        assertTrue(validationConstants.getDefaultPageSize() > 0, "Tamanho padrão deve ser positivo");
        assertTrue(validationConstants.getMaxPageSize() > 0, "Tamanho máximo deve ser positivo");
        assertTrue(validationConstants.getMaxPageSize() >= validationConstants.getDefaultPageSize(), 
                  "Tamanho máximo deve ser maior ou igual ao padrão");
    }

    @Test
    @DisplayName("Deve ter direções de ordenação válidas")
    void deveTerDirecoesDeOrdenacaoValidas() {
        // Assert
        assertTrue("ASC".equals(validationConstants.getDefaultSortDirection()) || 
                  "DESC".equals(validationConstants.getDefaultSortDirection()), 
                  "Direção padrão deve ser 'ASC' ou 'DESC'");
    }

    @Test
    @DisplayName("Deve ter campo padrão de ordenação válido")
    void deveTerCampoPadraoDeOrdenacaoValido() {
        // Assert
        assertTrue(validationConstants.getValidSortFields().contains(validationConstants.getDefaultSortField()),
                  "Campo padrão de ordenação deve estar na lista de campos válidos");
    }

    @Test
    @DisplayName("Deve ser um componente Spring válido")
    void deveSerUmComponenteSpringValido() {
        // Assert
        assertNotNull(validationConstants, "ValidationConstants deve ser instanciável como componente Spring");
        assertNotNull(validationConstants.getValidSortFields(), "ValidSortFields deve estar disponível");
        assertNotNull(validationConstants.getDefaultSortField(), "DefaultSortField deve estar disponível");
    }
}
