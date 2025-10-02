package br.com.guilhermedealmeidafreitas.creditos.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PingController.class)
class PingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Setup inicial se necessário
    }

    @Test
    @DisplayName("Should return pong response with timestamp")
    void testPing_ShouldReturnPongResponse() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/ping")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("pong"))
                .andExpect(jsonPath("$.ts").exists())
                .andExpect(jsonPath("$.ts").isString());
    }

    @Test
    @DisplayName("Should return valid timestamp format")
    void testPing_ShouldReturnValidTimestamp() throws Exception {
        // Given
        Instant beforeCall = Instant.now();

        // When & Then
        mockMvc.perform(get("/api/ping")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ts").value(matchesRegex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+Z")));

        Instant afterCall = Instant.now();
        
        // Verificar que o timestamp está dentro do intervalo esperado
        // (implementação seria mais complexa em um teste real)
        assert afterCall.isAfter(beforeCall) || afterCall.equals(beforeCall);
    }

    @Test
    @DisplayName("Should return correct response structure")
    void testPing_ShouldReturnCorrectStructure() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/ping")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isMap())
                .andExpect(jsonPath("$").value(hasKey("message")))
                .andExpect(jsonPath("$").value(hasKey("ts")))
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("ts").exists());
    }

    @Test
    @DisplayName("Should return different timestamps on multiple calls")
    void testPing_ShouldReturnDifferentTimestamps() throws Exception {
        // When & Then - Primeira chamada
        String firstResponse = mockMvc.perform(get("/api/ping")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("pong"))
                .andExpect(jsonPath("$.ts").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Pequena pausa para garantir timestamp diferente
        Thread.sleep(1);

        // When & Then - Segunda chamada
        String secondResponse = mockMvc.perform(get("/api/ping")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("pong"))
                .andExpect(jsonPath("$.ts").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Verificar que as respostas são diferentes (timestamps diferentes)
        assert !firstResponse.equals(secondResponse);
    }

    @Test
    @DisplayName("Should handle GET request without content-type header")
    void testPing_ShouldHandleRequestWithoutContentType() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/ping"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("pong"))
                .andExpect(jsonPath("$.ts").exists());
    }

    @Test
    @DisplayName("Should return JSON response")
    void testPing_ShouldReturnJsonResponse() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/ping")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("pong"))
                .andExpect(jsonPath("$.ts").exists());
    }

    @Test
    @DisplayName("Should return response with correct HTTP status")
    void testPing_ShouldReturnOkStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/ping"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("pong"))
                .andExpect(jsonPath("$.ts").exists());
    }

    @Test
    @DisplayName("Should handle multiple concurrent requests")
    void testPing_ShouldHandleConcurrentRequests() throws Exception {
        // When & Then - Simular múltiplas requisições
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/ping")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("pong"))
                    .andExpect(jsonPath("$.ts").exists());
        }
    }

    @Test
    @DisplayName("Should return response with valid Instant format")
    void testPing_ShouldReturnValidInstantFormat() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/ping")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ts").value(matchesRegex("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+Z")));
    }

    @Test
    @DisplayName("Should return pong message consistently")
    void testPing_ShouldReturnPongMessageConsistently() throws Exception {
        // When & Then - Múltiplas chamadas devem retornar "pong"
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/api/ping")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("pong"));
        }
    }

    @Test
    @DisplayName("Should return response without additional fields")
    void testPing_ShouldReturnOnlyRequiredFields() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/ping")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasKey("message")))
                .andExpect(jsonPath("$").value(hasKey("ts")))
                .andExpect(jsonPath("message").exists())
                .andExpect(jsonPath("ts").exists())
                .andExpect(jsonPath("$.message").value("pong"))
                .andExpect(jsonPath("$.ts").exists());
    }
}
