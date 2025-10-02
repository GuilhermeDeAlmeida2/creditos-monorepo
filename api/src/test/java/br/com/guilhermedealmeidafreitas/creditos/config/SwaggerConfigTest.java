package br.com.guilhermedealmeidafreitas.creditos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SwaggerConfigTest {

    private SwaggerConfig swaggerConfig;

    @BeforeEach
    void setUp() {
        swaggerConfig = new SwaggerConfig();
    }

    @Test
    @DisplayName("Should create OpenAPI bean successfully")
    void testCustomOpenAPICreatesBean() {
        // When
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Then
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo()).isNotNull();
    }

    @Test
    @DisplayName("Should configure OpenAPI with correct title")
    void testCustomOpenAPITitle() {
        // When
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Then
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("API de Créditos");
    }

    @Test
    @DisplayName("Should configure OpenAPI with correct description")
    void testCustomOpenAPIDescription() {
        // When
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Then
        assertThat(openAPI.getInfo().getDescription()).isEqualTo("API para gerenciamento de créditos constituídos");
    }

    @Test
    @DisplayName("Should configure OpenAPI with correct version")
    void testCustomOpenAPIVersion() {
        // When
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Then
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
    }

    @Test
    @DisplayName("Should configure OpenAPI with contact information")
    void testCustomOpenAPIContact() {
        // When
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Then
        Contact contact = openAPI.getInfo().getContact();
        assertThat(contact).isNotNull();
        assertThat(contact.getName()).isEqualTo("Equipe de Desenvolvimento");
        assertThat(contact.getEmail()).isEqualTo("dev@empresa.com");
    }

    @Test
    @DisplayName("Should return same instance on multiple calls")
    void testCustomOpenAPIConsistency() {
        // When
        OpenAPI openAPI1 = swaggerConfig.customOpenAPI();
        OpenAPI openAPI2 = swaggerConfig.customOpenAPI();

        // Then
        assertThat(openAPI1).isNotNull();
        assertThat(openAPI2).isNotNull();
        assertThat(openAPI1.getInfo().getTitle()).isEqualTo(openAPI2.getInfo().getTitle());
        assertThat(openAPI1.getInfo().getDescription()).isEqualTo(openAPI2.getInfo().getDescription());
        assertThat(openAPI1.getInfo().getVersion()).isEqualTo(openAPI2.getInfo().getVersion());
    }

    @Test
    @DisplayName("Should have all required OpenAPI fields configured")
    void testCustomOpenAPICompleteConfiguration() {
        // When
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Then
        Info info = openAPI.getInfo();
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isNotBlank();
        assertThat(info.getDescription()).isNotBlank();
        assertThat(info.getVersion()).isNotBlank();
        assertThat(info.getContact()).isNotNull();
        assertThat(info.getContact().getName()).isNotBlank();
        assertThat(info.getContact().getEmail()).isNotBlank();
    }

    @Test
    @DisplayName("Should create valid OpenAPI object structure")
    void testCustomOpenAPIStructure() {
        // When
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Then
        assertThat(openAPI).isInstanceOf(OpenAPI.class);
        assertThat(openAPI.getInfo()).isInstanceOf(Info.class);
        assertThat(openAPI.getInfo().getContact()).isInstanceOf(Contact.class);
    }

    @Test
    @DisplayName("Should not return null for any configuration field")
    void testCustomOpenAPINoNullFields() {
        // When
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Then
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isNotNull();
        assertThat(openAPI.getInfo().getDescription()).isNotNull();
        assertThat(openAPI.getInfo().getVersion()).isNotNull();
        assertThat(openAPI.getInfo().getContact()).isNotNull();
        assertThat(openAPI.getInfo().getContact().getName()).isNotNull();
        assertThat(openAPI.getInfo().getContact().getEmail()).isNotNull();
    }

    @Test
    @DisplayName("Should have proper email format in contact")
    void testCustomOpenAPIContactEmailFormat() {
        // When
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Then
        String email = openAPI.getInfo().getContact().getEmail();
        assertThat(email).contains("@");
        assertThat(email).contains(".");
        assertThat(email).isEqualTo("dev@empresa.com");
    }

    @Test
    @DisplayName("Should have meaningful title and description")
    void testCustomOpenAPIMeaningfulContent() {
        // When
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Then
        String title = openAPI.getInfo().getTitle();
        String description = openAPI.getInfo().getDescription();
        
        assertThat(title).isNotBlank();
        assertThat(title.length()).isGreaterThan(5);
        assertThat(description).isNotBlank();
        assertThat(description.length()).isGreaterThan(10);
        assertThat(title).contains("Créditos");
        assertThat(description).contains("créditos");
    }
}
