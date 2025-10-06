package br.com.guilhermedealmeidafreitas.creditos.factory;

import br.com.guilhermedealmeidafreitas.creditos.constants.ValidationConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para PageableFactory.
 * 
 * @author Guilherme de Almeida Freitas
 */
@DisplayName("PageableFactory Tests")
class PageableFactoryTest {
    
    private PageableFactory pageableFactory;
    
    @BeforeEach
    void setUp() {
        pageableFactory = new PageableFactory();
    }
    
    @Test
    @DisplayName("Deve criar Pageable com parâmetros válidos")
    void shouldCreatePageableWithValidParameters() {
        // Given
        int page = 2;
        int size = 20;
        String sortBy = "numeroCredito";
        String sortDirection = "DESC";
        
        // When
        Pageable pageable = pageableFactory.createPageable(page, size, sortBy, sortDirection);
        
        // Then
        assertNotNull(pageable);
        assertEquals(page, pageable.getPageNumber());
        assertEquals(size, pageable.getPageSize());
        assertEquals(sortBy, pageable.getSort().getOrderFor(sortBy).getProperty());
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor(sortBy).getDirection());
    }
    
    @Test
    @DisplayName("Deve corrigir página negativa para 0")
    void shouldCorrectNegativePageToZero() {
        // Given
        int page = -1;
        int size = 10;
        String sortBy = "id";
        String sortDirection = "ASC";
        
        // When
        Pageable pageable = pageableFactory.createPageable(page, size, sortBy, sortDirection);
        
        // Then
        assertEquals(0, pageable.getPageNumber());
    }
    
    @Test
    @DisplayName("Deve corrigir tamanho inválido para padrão")
    void shouldCorrectInvalidSizeToDefault() {
        // Given
        int page = 0;
        int size = 0; // Tamanho inválido
        String sortBy = "id";
        String sortDirection = "ASC";
        
        // When
        Pageable pageable = pageableFactory.createPageable(page, size, sortBy, sortDirection);
        
        // Then
        assertEquals(ValidationConstants.DEFAULT_PAGE_SIZE, pageable.getPageSize());
    }
    
    @Test
    @DisplayName("Deve limitar tamanho máximo")
    void shouldLimitMaximumSize() {
        // Given
        int page = 0;
        int size = 200; // Excede o máximo
        String sortBy = "id";
        String sortDirection = "ASC";
        
        // When
        Pageable pageable = pageableFactory.createPageable(page, size, sortBy, sortDirection);
        
        // Then
        assertEquals(ValidationConstants.MAX_PAGE_SIZE, pageable.getPageSize());
    }
    
    @Test
    @DisplayName("Deve usar campo de ordenação padrão quando inválido")
    void shouldUseDefaultSortFieldWhenInvalid() {
        // Given
        int page = 0;
        int size = 10;
        String sortBy = "campoInvalido";
        String sortDirection = "ASC";
        
        // When
        Pageable pageable = pageableFactory.createPageable(page, size, sortBy, sortDirection);
        
        // Then
        assertEquals(ValidationConstants.DEFAULT_SORT_FIELD, 
                    pageable.getSort().getOrderFor(ValidationConstants.DEFAULT_SORT_FIELD).getProperty());
    }
    
    @Test
    @DisplayName("Deve usar direção ASC quando inválida")
    void shouldUseAscDirectionWhenInvalid() {
        // Given
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String sortDirection = "INVALID";
        
        // When
        Pageable pageable = pageableFactory.createPageable(page, size, sortBy, sortDirection);
        
        // Then
        assertEquals(Sort.Direction.ASC, 
                    pageable.getSort().getOrderFor("id").getDirection());
    }
    
    @Test
    @DisplayName("Deve criar Pageable a partir de objetos")
    void shouldCreatePageableFromObjects() {
        // Given
        Object pageParam = "2";
        Object sizeParam = 20;
        Object sortByParam = "numeroCredito";
        Object sortDirectionParam = "DESC";
        
        // When
        Pageable pageable = pageableFactory.createPageableFromObjects(
            pageParam, sizeParam, sortByParam, sortDirectionParam);
        
        // Then
        assertNotNull(pageable);
        assertEquals(2, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
        assertEquals("numeroCredito", pageable.getSort().getOrderFor("numeroCredito").getProperty());
        assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor("numeroCredito").getDirection());
    }
    
    @Test
    @DisplayName("Deve criar Pageable padrão")
    void shouldCreateDefaultPageable() {
        // When
        Pageable pageable = pageableFactory.createDefaultPageable();
        
        // Then
        assertNotNull(pageable);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(ValidationConstants.DEFAULT_PAGE_SIZE, pageable.getPageSize());
        assertEquals(ValidationConstants.DEFAULT_SORT_FIELD, 
                    pageable.getSort().getOrderFor(ValidationConstants.DEFAULT_SORT_FIELD).getProperty());
        assertEquals(Sort.Direction.ASC, 
                    pageable.getSort().getOrderFor(ValidationConstants.DEFAULT_SORT_FIELD).getDirection());
    }
    
    @Test
    @DisplayName("Deve criar Pageable sem ordenação")
    void shouldCreatePageableWithoutSort() {
        // Given
        int page = 1;
        int size = 15;
        
        // When
        Pageable pageable = pageableFactory.createPageableWithoutSort(page, size);
        
        // Then
        assertNotNull(pageable);
        assertEquals(page, pageable.getPageNumber());
        assertEquals(size, pageable.getPageSize());
        assertTrue(pageable.getSort().isUnsorted());
    }
    
    @Test
    @DisplayName("Deve tratar valores null corretamente")
    void shouldHandleNullValuesCorrectly() {
        // When
        Pageable pageable = pageableFactory.createPageableFromObjects(null, null, null, null);
        
        // Then
        assertNotNull(pageable);
        assertEquals(0, pageable.getPageNumber());
        assertEquals(ValidationConstants.DEFAULT_PAGE_SIZE, pageable.getPageSize());
        assertEquals(ValidationConstants.DEFAULT_SORT_FIELD, 
                    pageable.getSort().getOrderFor(ValidationConstants.DEFAULT_SORT_FIELD).getProperty());
        assertEquals(Sort.Direction.ASC, 
                    pageable.getSort().getOrderFor(ValidationConstants.DEFAULT_SORT_FIELD).getDirection());
    }
    
    @Test
    @DisplayName("Deve lançar exceção para parâmetros inválidos")
    void shouldThrowExceptionForInvalidParameters() {
        // Given
        Object invalidPageParam = "notANumber";
        Object sizeParam = 10;
        Object sortByParam = "id";
        Object sortDirectionParam = "ASC";
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            pageableFactory.createPageableFromObjects(
                invalidPageParam, sizeParam, sortByParam, sortDirectionParam);
        });
    }
    
    @Test
    @DisplayName("Deve validar todos os campos de ordenação válidos")
    void shouldValidateAllValidSortFields() {
        // Given
        int page = 0;
        int size = 10;
        String sortDirection = "ASC";
        
        // When & Then
        for (String sortField : ValidationConstants.VALID_SORT_FIELDS) {
            Pageable pageable = pageableFactory.createPageable(page, size, sortField, sortDirection);
            assertEquals(sortField, pageable.getSort().getOrderFor(sortField).getProperty());
        }
    }
}
