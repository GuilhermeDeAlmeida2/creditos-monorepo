package br.com.guilhermedealmeidafreitas.creditos.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestFeaturesConfigTest {

    private TestFeaturesConfig testFeaturesConfig;

    @BeforeEach
    void setUp() {
        testFeaturesConfig = new TestFeaturesConfig();
    }

    @Test
    void testIsEnabled_DefaultValue() {
        // Given & When
        boolean enabled = testFeaturesConfig.isEnabled();

        // Then
        assertThat(enabled).isFalse();
    }

    @Test
    void testSetEnabled_True() {
        // Given
        testFeaturesConfig.setEnabled(true);

        // When
        boolean enabled = testFeaturesConfig.isEnabled();

        // Then
        assertThat(enabled).isTrue();
    }

    @Test
    void testSetEnabled_False() {
        // Given
        testFeaturesConfig.setEnabled(false);

        // When
        boolean enabled = testFeaturesConfig.isEnabled();

        // Then
        assertThat(enabled).isFalse();
    }

    @Test
    void testSetEnabled_MultipleChanges() {
        // Given
        testFeaturesConfig.setEnabled(true);
        assertThat(testFeaturesConfig.isEnabled()).isTrue();

        // When
        testFeaturesConfig.setEnabled(false);

        // Then
        assertThat(testFeaturesConfig.isEnabled()).isFalse();
    }
}
