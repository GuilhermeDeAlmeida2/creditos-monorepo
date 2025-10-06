package br.com.guilhermedealmeidafreitas.creditos.factory;

import br.com.guilhermedealmeidafreitas.creditos.exception.CreditoException;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FactoryRegistryTest {

    @Mock
    private CreditoExceptionFactory creditoExceptionFactory;

    @Mock
    private CreditoFactory creditoFactory;

    private FactoryRegistry registry;

    @BeforeEach
    void setUp() {
        // Mock das factories
        when(creditoExceptionFactory.getFactoryName()).thenReturn("CreditoExceptionFactory");
        when(creditoExceptionFactory.getProductType()).thenReturn((Class) CreditoException.class);
        when(creditoExceptionFactory.getDescription()).thenReturn("Factory para criação de exceções");

        when(creditoFactory.getFactoryName()).thenReturn("CreditoFactory");
        when(creditoFactory.getProductType()).thenReturn((Class) Credito.class);
        when(creditoFactory.getDescription()).thenReturn("Factory para criação de créditos");

        // Criar registry com as factories mockadas
        registry = new FactoryRegistry(List.of(creditoExceptionFactory, creditoFactory));
    }

    @Test
    void testGetFactory_WithValidName_ShouldReturnFactory() {
        // When
        Optional<AbstractFactory<?>> factory = registry.getFactory("CreditoExceptionFactory");

        // Then
        assertThat(factory).isPresent();
        assertThat(factory.get()).isEqualTo(creditoExceptionFactory);
    }

    @Test
    void testGetFactory_WithInvalidName_ShouldReturnEmpty() {
        // When
        Optional<AbstractFactory<?>> factory = registry.getFactory("NonExistentFactory");

        // Then
        assertThat(factory).isEmpty();
    }

    @Test
    void testGetFactory_WithProductType_ShouldReturnFactory() {
        // When
        Optional<AbstractFactory<CreditoException>> factory = registry.getFactory(CreditoException.class);

        // Then
        assertThat(factory).isPresent();
        assertThat(factory.get()).isEqualTo(creditoExceptionFactory);
    }

    @Test
    void testGetFactory_WithInvalidProductType_ShouldReturnEmpty() {
        // When
        Optional<AbstractFactory<String>> factory = registry.getFactory(String.class);

        // Then
        assertThat(factory).isEmpty();
    }

    @Test
    void testGetFactories_WithProductType_ShouldReturnAllFactories() {
        // When
        List<AbstractFactory<CreditoException>> factories = registry.getFactories(CreditoException.class);

        // Then
        assertThat(factories).hasSize(1);
        assertThat(factories).contains(creditoExceptionFactory);
    }

    @Test
    void testGetAllFactories_ShouldReturnAllFactories() {
        // When
        List<AbstractFactory<?>> factories = registry.getAllFactories();

        // Then
        assertThat(factories).hasSize(2);
        assertThat(factories).contains(creditoExceptionFactory, creditoFactory);
    }

    @Test
    void testGetFactoryInfo_ShouldReturnFactoryInfo() {
        // When
        Map<String, String> info = registry.getFactoryInfo();

        // Then
        assertThat(info).hasSize(2);
        assertThat(info).containsKey("CreditoExceptionFactory");
        assertThat(info).containsKey("CreditoFactory");
        assertThat(info.get("CreditoExceptionFactory")).contains("CreditoException");
        assertThat(info.get("CreditoFactory")).contains("Credito");
    }

    @Test
    void testGetFactoryStatistics_ShouldReturnStatistics() {
        // When
        Map<String, Object> statistics = registry.getFactoryStatistics();

        // Then
        assertThat(statistics).containsKey("totalFactories");
        assertThat(statistics).containsKey("uniqueProductTypes");
        assertThat(statistics).containsKey("productTypeDistribution");
        
        assertThat(statistics.get("totalFactories")).isEqualTo(2);
        assertThat(statistics.get("uniqueProductTypes")).isEqualTo(2);
    }

    @Test
    void testHasFactory_WithExistingFactory_ShouldReturnTrue() {
        // When
        boolean hasFactory = registry.hasFactory("CreditoExceptionFactory");

        // Then
        assertThat(hasFactory).isTrue();
    }

    @Test
    void testHasFactory_WithNonExistingFactory_ShouldReturnFalse() {
        // When
        boolean hasFactory = registry.hasFactory("NonExistentFactory");

        // Then
        assertThat(hasFactory).isFalse();
    }

    @Test
    void testHasFactoryFor_WithExistingProductType_ShouldReturnTrue() {
        // When
        boolean hasFactory = registry.hasFactoryFor(CreditoException.class);

        // Then
        assertThat(hasFactory).isTrue();
    }

    @Test
    void testHasFactoryFor_WithNonExistingProductType_ShouldReturnFalse() {
        // When
        boolean hasFactory = registry.hasFactoryFor(String.class);

        // Then
        assertThat(hasFactory).isFalse();
    }

    @Test
    void testGetFactoryCount_ShouldReturnCorrectCount() {
        // When
        int count = registry.getFactoryCount();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void testGetUniqueProductTypeCount_ShouldReturnCorrectCount() {
        // When
        long count = registry.getUniqueProductTypeCount();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void testGetFactoryNames_ShouldReturnSortedNames() {
        // When
        List<String> names = registry.getFactoryNames();

        // Then
        assertThat(names).hasSize(2);
        assertThat(names).containsExactly("CreditoExceptionFactory", "CreditoFactory");
    }

    @Test
    void testGetSupportedProductTypes_ShouldReturnSortedTypes() {
        // When
        List<Class<?>> types = registry.getSupportedProductTypes();

        // Then
        assertThat(types).hasSize(2);
        assertThat(types).containsExactly(Credito.class, CreditoException.class);
    }

    @Test
    void testGetFactory_WithNameAndProductType_ShouldReturnFactory() {
        // When
        Optional<AbstractFactory<CreditoException>> factory = registry.getFactory("CreditoExceptionFactory", CreditoException.class);

        // Then
        assertThat(factory).isPresent();
        assertThat(factory.get()).isEqualTo(creditoExceptionFactory);
    }

    @Test
    void testGetFactory_WithNameAndWrongProductType_ShouldReturnEmpty() {
        // When
        Optional<AbstractFactory<CreditoException>> factory = registry.getFactory("CreditoFactory", CreditoException.class);

        // Then
        assertThat(factory).isEmpty();
    }

    @Test
    void testCreate_WithValidProductType_ShouldCreateObject() {
        // Given
        Map<String, Object> parameters = Map.of("message", "Test message");
        CreditoException mockException = new br.com.guilhermedealmeidafreitas.creditos.exception.SimpleCreditoException("Test", "TEST", 400);
        when(creditoExceptionFactory.create(parameters)).thenReturn(mockException);

        // When
        CreditoException result = registry.create(CreditoException.class, parameters);

        // Then
        assertThat(result).isEqualTo(mockException);
    }

    @Test
    void testCreate_WithNonExistingProductType_ShouldThrowException() {
        // Given
        Map<String, Object> parameters = Map.of("value", "test");

        // When & Then
        assertThatThrownBy(() -> registry.create(String.class, parameters))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Nenhuma factory encontrada para o tipo: String");
    }

    @Test
    void testCreate_WithFactoryName_ShouldCreateObject() {
        // Given
        Map<String, Object> parameters = Map.of("message", "Test message");
        CreditoException mockException = new br.com.guilhermedealmeidafreitas.creditos.exception.SimpleCreditoException("Test", "TEST", 400);
        when(creditoExceptionFactory.create(parameters)).thenReturn(mockException);

        // When
        Object result = registry.create("CreditoExceptionFactory", parameters);

        // Then
        assertThat(result).isEqualTo(mockException);
    }

    @Test
    void testCreate_WithNonExistingFactoryName_ShouldThrowException() {
        // Given
        Map<String, Object> parameters = Map.of("value", "test");

        // When & Then
        assertThatThrownBy(() -> registry.create("NonExistentFactory", parameters))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Factory não encontrada: NonExistentFactory");
    }
}
