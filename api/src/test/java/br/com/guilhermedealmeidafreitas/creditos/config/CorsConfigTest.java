package br.com.guilhermedealmeidafreitas.creditos.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CorsConfigTest {

    private CorsConfig corsConfig;

    @BeforeEach
    void setUp() {
        corsConfig = new CorsConfig();
        // Configurar o valor da propriedade usando ReflectionTestUtils
        ReflectionTestUtils.setField(corsConfig, "allowedOrigins", 
            "http://localhost:4200,http://localhost:3000,https://example.com");
    }

    @Test
    void testCorsConfigurationSource_NotNull() {
        // When
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();

        // Then
        assertThat(source).isNotNull();
        assertThat(source).isInstanceOf(org.springframework.web.cors.UrlBasedCorsConfigurationSource.class);
    }

    @Test
    void testCorsConfigurationSource_ReturnsValidSource() {
        // When
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();

        // Then
        assertThat(source).isNotNull();
        // Verificar se é uma instância válida do tipo esperado
        assertThat(source.getClass().getName()).contains("UrlBasedCorsConfigurationSource");
    }

    @Test
    void testCorsConfigurationSource_CanBeCreatedMultipleTimes() {
        // When
        CorsConfigurationSource source1 = corsConfig.corsConfigurationSource();
        CorsConfigurationSource source2 = corsConfig.corsConfigurationSource();

        // Then
        assertThat(source1).isNotNull();
        assertThat(source2).isNotNull();
        // Ambos devem ser instâncias válidas
        assertThat(source1).isInstanceOf(org.springframework.web.cors.UrlBasedCorsConfigurationSource.class);
        assertThat(source2).isInstanceOf(org.springframework.web.cors.UrlBasedCorsConfigurationSource.class);
    }

    @Test
    void testCorsConfigurationSource_IsConsistent() {
        // When
        CorsConfigurationSource source1 = corsConfig.corsConfigurationSource();
        CorsConfigurationSource source2 = corsConfig.corsConfigurationSource();

        // Then
        assertThat(source1).isNotNull();
        assertThat(source2).isNotNull();
        // Ambos devem ter o mesmo tipo
        assertThat(source1.getClass()).isEqualTo(source2.getClass());
    }
}