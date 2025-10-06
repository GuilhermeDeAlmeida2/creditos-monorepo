package br.com.guilhermedealmeidafreitas.creditos.constants;

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

    @Test
    @DisplayName("Deve conter todos os campos válidos para ordenação")
    void deveConterTodosOsCamposValidosParaOrdenacao() {
        // Arrange & Act
        Set<String> validSortFields = ValidationConstants.VALID_SORT_FIELDS;
        
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
        Set<String> validSortFields = ValidationConstants.VALID_SORT_FIELDS;
        
        // Assert
        assertEquals(11, validSortFields.size(), "Deve conter exatamente 11 campos válidos");
    }

    @Test
    @DisplayName("Deve ter valores padrão corretos para paginação")
    void deveTerValoresPadraoCorretosParaPaginacao() {
        // Assert
        assertEquals(10, ValidationConstants.DEFAULT_PAGE_SIZE, "Tamanho padrão da página deve ser 10");
        assertEquals(100, ValidationConstants.MAX_PAGE_SIZE, "Tamanho máximo da página deve ser 100");
        assertEquals("id", ValidationConstants.DEFAULT_SORT_FIELD, "Campo padrão de ordenação deve ser 'id'");
        assertEquals("ASC", ValidationConstants.DEFAULT_SORT_DIRECTION, "Direção padrão deve ser 'ASC'");
    }

    @Test
    @DisplayName("Deve ter valores positivos para tamanhos de página")
    void deveTerValoresPositivosParaTamanhosDePagina() {
        // Assert
        assertTrue(ValidationConstants.DEFAULT_PAGE_SIZE > 0, "Tamanho padrão deve ser positivo");
        assertTrue(ValidationConstants.MAX_PAGE_SIZE > 0, "Tamanho máximo deve ser positivo");
        assertTrue(ValidationConstants.MAX_PAGE_SIZE >= ValidationConstants.DEFAULT_PAGE_SIZE, 
                  "Tamanho máximo deve ser maior ou igual ao padrão");
    }

    @Test
    @DisplayName("Deve ter direções de ordenação válidas")
    void deveTerDirecoesDeOrdenacaoValidas() {
        // Assert
        assertTrue("ASC".equals(ValidationConstants.DEFAULT_SORT_DIRECTION) || 
                  "DESC".equals(ValidationConstants.DEFAULT_SORT_DIRECTION), 
                  "Direção padrão deve ser 'ASC' ou 'DESC'");
    }

    @Test
    @DisplayName("Deve ter campo padrão de ordenação válido")
    void deveTerCampoPadraoDeOrdenacaoValido() {
        // Assert
        assertTrue(ValidationConstants.VALID_SORT_FIELDS.contains(ValidationConstants.DEFAULT_SORT_FIELD),
                  "Campo padrão de ordenação deve estar na lista de campos válidos");
    }

    @Test
    @DisplayName("Deve ser uma classe utilitária (não instanciável)")
    void deveSerUmaClasseUtilitaria() {
        // Arrange & Act & Assert
        assertThrows(Exception.class, () -> {
            // Tenta instanciar a classe usando reflexão
            var constructor = ValidationConstants.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }, "Classe utilitária deve lançar exceção ao tentar instanciar");
    }
}
