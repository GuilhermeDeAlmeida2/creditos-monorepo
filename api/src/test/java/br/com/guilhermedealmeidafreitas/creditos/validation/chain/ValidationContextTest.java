package br.com.guilhermedealmeidafreitas.creditos.validation.chain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a classe ValidationContext.
 * 
 * Cobertura atual: 77.2%
 * Meta: > 80%
 */
class ValidationContextTest {

    // Testes para valores do enum
    @Test
    @DisplayName("Deve ter todos os valores do enum corretos")
    void deveTerTodosOsValoresDoEnumCorretos() {
        ValidationContext[] values = ValidationContext.values();
        
        assertEquals(15, values.length);
        
        // Verificar que todos os valores esperados estão presentes
        assertTrue(java.util.Arrays.asList(values).contains(ValidationContext.DEFAULT));
        assertTrue(java.util.Arrays.asList(values).contains(ValidationContext.API_REST));
        assertTrue(java.util.Arrays.asList(values).contains(ValidationContext.USER_INPUT));
        assertTrue(java.util.Arrays.asList(values).contains(ValidationContext.TEST_DATA));
        assertTrue(java.util.Arrays.asList(values).contains(ValidationContext.PRODUCTION));
        assertTrue(java.util.Arrays.asList(values).contains(ValidationContext.DEVELOPMENT));
        assertTrue(java.util.Arrays.asList(values).contains(ValidationContext.PAGINATION));
        assertTrue(java.util.Arrays.asList(values).contains(ValidationContext.SORTING));
        assertTrue(java.util.Arrays.asList(values).contains(ValidationContext.FILTERING));
        assertTrue(java.util.Arrays.asList(values).contains(ValidationContext.FISCAL));
        assertTrue(java.util.Arrays.asList(values).contains(ValidationContext.CREDIT));
        assertTrue(java.util.Arrays.asList(values).contains(ValidationContext.NFSE));
        assertTrue(java.util.Arrays.asList(values).contains(ValidationContext.SIMPLES_NACIONAL));
        assertTrue(java.util.Arrays.asList(values).contains(ValidationContext.ALIQUOTA));
        assertTrue(java.util.Arrays.asList(values).contains(ValidationContext.MONETARY));
    }

    // Testes para método getDescription
    @Test
    @DisplayName("Deve retornar descrições corretas para todos os contextos")
    void deveRetornarDescricoesCorretasParaTodosOsContextos() {
        assertEquals("Validação padrão", ValidationContext.DEFAULT.getDescription());
        assertEquals("Validação de API REST", ValidationContext.API_REST.getDescription());
        assertEquals("Validação de entrada do usuário", ValidationContext.USER_INPUT.getDescription());
        assertEquals("Validação de dados de teste", ValidationContext.TEST_DATA.getDescription());
        assertEquals("Validação de dados de produção", ValidationContext.PRODUCTION.getDescription());
        assertEquals("Validação de dados de desenvolvimento", ValidationContext.DEVELOPMENT.getDescription());
        assertEquals("Validação de paginação", ValidationContext.PAGINATION.getDescription());
        assertEquals("Validação de ordenação", ValidationContext.SORTING.getDescription());
        assertEquals("Validação de filtros", ValidationContext.FILTERING.getDescription());
        assertEquals("Validação de dados fiscais", ValidationContext.FISCAL.getDescription());
        assertEquals("Validação de dados de crédito", ValidationContext.CREDIT.getDescription());
        assertEquals("Validação de dados de NFS-e", ValidationContext.NFSE.getDescription());
        assertEquals("Validação de dados do Simples Nacional", ValidationContext.SIMPLES_NACIONAL.getDescription());
        assertEquals("Validação de dados de alíquota", ValidationContext.ALIQUOTA.getDescription());
        assertEquals("Validação de dados monetários", ValidationContext.MONETARY.getDescription());
    }

    // Testes para método toString
    @Test
    @DisplayName("Deve retornar toString correto para todos os contextos")
    void deveRetornarToStringCorretoParaTodosOsContextos() {
        assertEquals("DEFAULT: Validação padrão", ValidationContext.DEFAULT.toString());
        assertEquals("API_REST: Validação de API REST", ValidationContext.API_REST.toString());
        assertEquals("USER_INPUT: Validação de entrada do usuário", ValidationContext.USER_INPUT.toString());
        assertEquals("TEST_DATA: Validação de dados de teste", ValidationContext.TEST_DATA.toString());
        assertEquals("PRODUCTION: Validação de dados de produção", ValidationContext.PRODUCTION.toString());
        assertEquals("DEVELOPMENT: Validação de dados de desenvolvimento", ValidationContext.DEVELOPMENT.toString());
        assertEquals("PAGINATION: Validação de paginação", ValidationContext.PAGINATION.toString());
        assertEquals("SORTING: Validação de ordenação", ValidationContext.SORTING.toString());
        assertEquals("FILTERING: Validação de filtros", ValidationContext.FILTERING.toString());
        assertEquals("FISCAL: Validação de dados fiscais", ValidationContext.FISCAL.toString());
        assertEquals("CREDIT: Validação de dados de crédito", ValidationContext.CREDIT.toString());
        assertEquals("NFSE: Validação de dados de NFS-e", ValidationContext.NFSE.toString());
        assertEquals("SIMPLES_NACIONAL: Validação de dados do Simples Nacional", ValidationContext.SIMPLES_NACIONAL.toString());
        assertEquals("ALIQUOTA: Validação de dados de alíquota", ValidationContext.ALIQUOTA.toString());
        assertEquals("MONETARY: Validação de dados monetários", ValidationContext.MONETARY.toString());
    }

    // Testes para método valueOf
    @Test
    @DisplayName("Deve retornar enum correto para valueOf")
    void deveRetornarEnumCorretoParaValueOf() {
        assertEquals(ValidationContext.DEFAULT, ValidationContext.valueOf("DEFAULT"));
        assertEquals(ValidationContext.API_REST, ValidationContext.valueOf("API_REST"));
        assertEquals(ValidationContext.USER_INPUT, ValidationContext.valueOf("USER_INPUT"));
        assertEquals(ValidationContext.TEST_DATA, ValidationContext.valueOf("TEST_DATA"));
        assertEquals(ValidationContext.PRODUCTION, ValidationContext.valueOf("PRODUCTION"));
        assertEquals(ValidationContext.DEVELOPMENT, ValidationContext.valueOf("DEVELOPMENT"));
        assertEquals(ValidationContext.PAGINATION, ValidationContext.valueOf("PAGINATION"));
        assertEquals(ValidationContext.SORTING, ValidationContext.valueOf("SORTING"));
        assertEquals(ValidationContext.FILTERING, ValidationContext.valueOf("FILTERING"));
        assertEquals(ValidationContext.FISCAL, ValidationContext.valueOf("FISCAL"));
        assertEquals(ValidationContext.CREDIT, ValidationContext.valueOf("CREDIT"));
        assertEquals(ValidationContext.NFSE, ValidationContext.valueOf("NFSE"));
        assertEquals(ValidationContext.SIMPLES_NACIONAL, ValidationContext.valueOf("SIMPLES_NACIONAL"));
        assertEquals(ValidationContext.ALIQUOTA, ValidationContext.valueOf("ALIQUOTA"));
        assertEquals(ValidationContext.MONETARY, ValidationContext.valueOf("MONETARY"));
    }

    @Test
    @DisplayName("Deve lançar exceção para valueOf com nome inválido")
    void deveLancarExcecaoParaValueOfComNomeInvalido() {
        assertThrows(IllegalArgumentException.class, () -> ValidationContext.valueOf("INVALID"));
        assertThrows(IllegalArgumentException.class, () -> ValidationContext.valueOf("DEFAULT_"));
        assertThrows(IllegalArgumentException.class, () -> ValidationContext.valueOf(""));
        assertThrows(NullPointerException.class, () -> ValidationContext.valueOf(null));
    }

    // Testes para verificar que cada enum tem nome e descrição únicos
    @Test
    @DisplayName("Deve ter nomes únicos para todos os enums")
    void deveTerNomesUnicosParaTodosOsEnums() {
        ValidationContext[] values = ValidationContext.values();
        java.util.Set<String> names = new java.util.HashSet<>();
        
        for (ValidationContext context : values) {
            assertTrue(names.add(context.name()), 
                "Nome duplicado encontrado: " + context.name());
        }
        
        assertEquals(values.length, names.size());
    }

    @Test
    @DisplayName("Deve ter descrições únicas para todos os enums")
    void deveTerDescricoesUnicasParaTodosOsEnums() {
        ValidationContext[] values = ValidationContext.values();
        java.util.Set<String> descriptions = new java.util.HashSet<>();
        
        for (ValidationContext context : values) {
            assertTrue(descriptions.add(context.getDescription()), 
                "Descrição duplicada encontrada: " + context.getDescription());
        }
        
        assertEquals(values.length, descriptions.size());
    }

    // Testes para verificar que todas as descrições são não-nulas e não-vazias
    @Test
    @DisplayName("Deve ter descrições não-nulas e não-vazias")
    void deveTerDescricoesNaoNulasENaoVazias() {
        for (ValidationContext context : ValidationContext.values()) {
            assertNotNull(context.getDescription(), 
                "Descrição nula para: " + context.name());
            assertFalse(context.getDescription().trim().isEmpty(), 
                "Descrição vazia para: " + context.name());
        }
    }

    // Testes para verificar que toString sempre retorna formato correto
    @Test
    @DisplayName("Deve ter formato toString consistente")
    void deveTerFormatoToStringConsistente() {
        for (ValidationContext context : ValidationContext.values()) {
            String toString = context.toString();
            assertTrue(toString.contains(": "), 
                "toString deve conter ': ' para: " + context.name());
            
            String[] parts = toString.split(": ", 2);
            assertEquals(2, parts.length, 
                "toString deve ter exatamente um ': ' para: " + context.name());
            
            assertEquals(context.name(), parts[0], 
                "Primeira parte deve ser o nome do enum para: " + context.name());
            assertEquals(context.getDescription(), parts[1], 
                "Segunda parte deve ser a descrição para: " + context.name());
        }
    }

    // Testes para verificar que o enum é comparável
    @Test
    @DisplayName("Deve ser comparável corretamente")
    void deveSerComparavelCorretamente() {
        ValidationContext[] values = ValidationContext.values();
        
        // Verificar que compareTo funciona
        for (int i = 0; i < values.length - 1; i++) {
            assertTrue(values[i].compareTo(values[i + 1]) < 0, 
                "Enum deve ser ordenado corretamente");
        }
        
        // Verificar que enums iguais retornam 0
        for (ValidationContext context : values) {
            assertEquals(0, context.compareTo(context), 
                "Enum deve ser igual a si mesmo");
        }
    }

    // Testes para verificar que o enum implementa Comparable
    @Test
    @DisplayName("Deve implementar Comparable corretamente")
    void deveImplementarComparableCorretamente() {
        assertTrue(ValidationContext.DEFAULT instanceof Comparable, 
            "ValidationContext deve implementar Comparable");
    }
}
