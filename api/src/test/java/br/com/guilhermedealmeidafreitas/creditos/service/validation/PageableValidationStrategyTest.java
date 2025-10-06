package br.com.guilhermedealmeidafreitas.creditos.service.validation;

import br.com.guilhermedealmeidafreitas.creditos.constants.ValidationConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes para a classe PageableValidationStrategy.
 * 
 * Cobertura atual: 58.6%
 * Meta: > 80%
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PageableValidationStrategyTest {

    @Mock
    private ValidationConstants validationConstants;

    private PageableValidationStrategy strategy;

    @BeforeEach
    void setUp() {
        // Configurar valores padrão para os mocks
        when(validationConstants.getMaxPageSize()).thenReturn(100);
        when(validationConstants.getDefaultPageSize()).thenReturn(20);
        when(validationConstants.getValidSortFields()).thenReturn(Set.of("id", "nome", "data", "valor"));
        
        strategy = new PageableValidationStrategy(validationConstants);
    }

    // Testes para construtor e getters básicos
    @Test
    @DisplayName("Deve criar estratégia com nome correto")
    void deveCriarEstrategiaComNomeCorreto() {
        assertEquals("PageableValidation", strategy.getStrategyName());
    }

    @Test
    @DisplayName("Deve retornar campos válidos para ordenação")
    void deveRetornarCamposValidosParaOrdenacao() {
        Set<String> validFields = strategy.getValidSortFields();
        assertEquals(Set.of("id", "nome", "data", "valor"), validFields);
    }

    @Test
    @DisplayName("Deve retornar tamanho máximo da página")
    void deveRetornarTamanhoMaximoDaPagina() {
        assertEquals(100, strategy.getMaxPageSize());
    }

    @Test
    @DisplayName("Deve retornar tamanho padrão da página")
    void deveRetornarTamanhoPadraoDaPagina() {
        assertEquals(20, strategy.getDefaultPageSize());
    }

    // Testes para método supports
    @Test
    @DisplayName("Deve suportar classe Pageable")
    void deveSuportarClassePageable() {
        assertTrue(strategy.supports(Pageable.class));
        assertTrue(strategy.supports(PageRequest.class));
    }

    @Test
    @DisplayName("Não deve suportar outras classes")
    void naoDeveSuportarOutrasClasses() {
        assertFalse(strategy.supports(String.class));
        assertFalse(strategy.supports(Integer.class));
        assertFalse(strategy.supports(Object.class));
    }

    // Testes para método validate - casos válidos
    @Test
    @DisplayName("Deve validar Pageable válido")
    void deveValidarPageableValido() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "id"));
        
        // Não deve lançar exceção
        assertDoesNotThrow(() -> strategy.validate(pageable));
    }

    @Test
    @DisplayName("Deve validar Pageable sem ordenação")
    void deveValidarPageableSemOrdenacao() {
        Pageable pageable = PageRequest.of(1, 10);
        
        // Não deve lançar exceção
        assertDoesNotThrow(() -> strategy.validate(pageable));
    }

    // Testes para método validate - casos de erro
    @Test
    @DisplayName("Deve lançar exceção para Pageable nulo")
    void deveLancarExcecaoParaPageableNulo() {
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> strategy.validate(null));
        
        assertEquals("Pageable não pode ser nulo", exception.getMessage());
        assertEquals("PageableValidation", exception.getStrategyName());
        assertEquals("pageable", exception.getFieldName());
    }

    // Testes removidos pois PageRequest.of() não permite valores inválidos
    // A validação acontece no método validateAndCreatePageable

    @Test
    @DisplayName("Deve lançar exceção para tamanho de página maior que o máximo")
    void deveLancarExcecaoParaTamanhoDePaginaMaiorQueMaximo() {
        Pageable pageable = PageRequest.of(0, 150);
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> strategy.validate(pageable));
        
        assertEquals("Tamanho da página não pode ser maior que 100", exception.getMessage());
        assertEquals("PageableValidation", exception.getStrategyName());
        assertEquals("size", exception.getFieldName());
    }

    @Test
    @DisplayName("Deve lançar exceção para campo de ordenação inválido")
    void deveLancarExcecaoParaCampoDeOrdenacaoInvalido() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "campoInvalido"));
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> strategy.validate(pageable));
        
        assertTrue(exception.getMessage().contains("Campo 'campoInvalido' não é válido para ordenação"));
        assertTrue(exception.getMessage().contains("id"));
        assertTrue(exception.getMessage().contains("nome"));
        assertEquals("PageableValidation", exception.getStrategyName());
        assertEquals("sortBy", exception.getFieldName());
    }

    // Testes para método validateAndCreatePageable
    @Test
    @DisplayName("Deve validar e criar Pageable com parâmetros válidos")
    void deveValidarECriarPageableComParametrosValidos() {
        Pageable result = strategy.validateAndCreatePageable(1, 30, "nome", "ASC");
        
        assertNotNull(result);
        assertEquals(1, result.getPageNumber());
        assertEquals(30, result.getPageSize());
        assertEquals("nome", result.getSort().getOrderFor("nome").getProperty());
        assertEquals(Sort.Direction.ASC, result.getSort().getOrderFor("nome").getDirection());
    }

    @Test
    @DisplayName("Deve normalizar página negativa para zero")
    void deveNormalizarPaginaNegativaParaZero() {
        Pageable result = strategy.validateAndCreatePageable(-1, 20, "id", "DESC");
        
        assertEquals(0, result.getPageNumber());
        assertEquals(20, result.getPageSize());
        assertEquals("id", result.getSort().getOrderFor("id").getProperty());
        assertEquals(Sort.Direction.DESC, result.getSort().getOrderFor("id").getDirection());
    }

    @Test
    @DisplayName("Deve usar tamanho padrão quando tamanho é zero ou negativo")
    void deveUsarTamanhoPadraoQuandoTamanhoEZeroOuNegativo() {
        Pageable result1 = strategy.validateAndCreatePageable(0, 0, "data", "ASC");
        Pageable result2 = strategy.validateAndCreatePageable(0, -5, "data", "ASC");
        
        assertEquals(20, result1.getPageSize());
        assertEquals(20, result2.getPageSize());
    }

    @Test
    @DisplayName("Deve limitar tamanho ao máximo permitido")
    void deveLimitartamanhoAoMaximoPermitido() {
        Pageable result = strategy.validateAndCreatePageable(0, 200, "valor", "DESC");
        
        assertEquals(100, result.getPageSize());
        assertEquals("valor", result.getSort().getOrderFor("valor").getProperty());
        assertEquals(Sort.Direction.DESC, result.getSort().getOrderFor("valor").getDirection());
    }

    @Test
    @DisplayName("Deve usar campo padrão quando campo de ordenação é inválido")
    void deveUsarCampoPadraoQuandoCampoDeOrdenacaoEInvalido() {
        Pageable result = strategy.validateAndCreatePageable(0, 20, "campoInvalido", "ASC");
        
        assertEquals("dataConstituicao", result.getSort().getOrderFor("dataConstituicao").getProperty());
        assertEquals(Sort.Direction.ASC, result.getSort().getOrderFor("dataConstituicao").getDirection());
    }

    @Test
    @DisplayName("Deve usar direção padrão quando direção é nula")
    void deveUsarDirecaoPadraoQuandoDirecaoENula() {
        Pageable result = strategy.validateAndCreatePageable(0, 20, "nome", null);
        
        assertEquals(Sort.Direction.DESC, result.getSort().getOrderFor("nome").getDirection());
    }

    @Test
    @DisplayName("Deve normalizar direção de ordenação")
    void deveNormalizarDirecaoDeOrdenacao() {
        Pageable result1 = strategy.validateAndCreatePageable(0, 20, "id", "asc");
        Pageable result2 = strategy.validateAndCreatePageable(0, 20, "id", "ASC");
        Pageable result3 = strategy.validateAndCreatePageable(0, 20, "id", "desc");
        Pageable result4 = strategy.validateAndCreatePageable(0, 20, "id", "DESC");
        Pageable result5 = strategy.validateAndCreatePageable(0, 20, "id", "qualquercoisa");
        
        assertEquals(Sort.Direction.ASC, result1.getSort().getOrderFor("id").getDirection());
        assertEquals(Sort.Direction.ASC, result2.getSort().getOrderFor("id").getDirection());
        assertEquals(Sort.Direction.DESC, result3.getSort().getOrderFor("id").getDirection());
        assertEquals(Sort.Direction.DESC, result4.getSort().getOrderFor("id").getDirection());
        assertEquals(Sort.Direction.DESC, result5.getSort().getOrderFor("id").getDirection());
    }

    // Testes para campos de ordenação válidos
    @Test
    @DisplayName("Deve aceitar todos os campos de ordenação válidos")
    void deveAceitarTodosOsCamposDeOrdenacaoValidos() {
        String[] validFields = {"id", "nome", "data", "valor"};
        
        for (String field : validFields) {
            Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, field));
            assertDoesNotThrow(() -> strategy.validate(pageable));
        }
    }

    // Testes para ordenação com múltiplos campos
    @Test
    @DisplayName("Deve validar ordenação com múltiplos campos válidos")
    void deveValidarOrdenacaoComMultiplosCamposValidos() {
        Sort sort = Sort.by(Sort.Direction.ASC, "nome").and(Sort.by(Sort.Direction.DESC, "data"));
        Pageable pageable = PageRequest.of(0, 20, sort);
        
        assertDoesNotThrow(() -> strategy.validate(pageable));
    }

    @Test
    @DisplayName("Deve rejeitar ordenação com pelo menos um campo inválido")
    void deveRejeitarOrdenacaoComPeloMenosUmCampoInvalido() {
        Sort sort = Sort.by(Sort.Direction.ASC, "nome").and(Sort.by(Sort.Direction.DESC, "campoInvalido"));
        Pageable pageable = PageRequest.of(0, 20, sort);
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> strategy.validate(pageable));
        
        assertTrue(exception.getMessage().contains("Campo 'campoInvalido' não é válido para ordenação"));
    }
}
