package br.com.guilhermedealmeidafreitas.creditos.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AuditEventTest {

    private AuditEvent auditEvent;

    @BeforeEach
    void setUp() {
        auditEvent = new AuditEvent();
    }

    @Test
    void testDefaultConstructor() {
        // When
        AuditEvent event = new AuditEvent();

        // Then
        assertThat(event).isNotNull();
        assertThat(event.getTimestamp()).isNotNull();
        assertThat(event.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void testParameterizedConstructor() {
        // Given
        String eventType = "CONSULTA_CREDITO";
        String endpoint = "/api/creditos/123";
        String httpMethod = "GET";

        // When
        AuditEvent event = new AuditEvent(eventType, endpoint, httpMethod);

        // Then
        assertThat(event).isNotNull();
        assertThat(event.getEventId()).isNotNull();
        assertThat(event.getEventId()).isNotBlank();
        assertThat(event.getEventType()).isEqualTo(eventType);
        assertThat(event.getEndpoint()).isEqualTo(endpoint);
        assertThat(event.getHttpMethod()).isEqualTo(httpMethod);
        assertThat(event.getSuccess()).isTrue();
        assertThat(event.getTimestamp()).isNotNull();
    }

    @Test
    void testGettersAndSetters() {
        // Given
        String eventId = "test-event-id";
        String eventType = "TEST_EVENT";
        LocalDateTime timestamp = LocalDateTime.now();
        String userId = "user123";
        String userIp = "192.168.1.1";
        String endpoint = "/api/test";
        String httpMethod = "POST";
        Map<String, Object> requestParameters = new HashMap<>();
        requestParameters.put("param1", "value1");
        Integer responseStatus = 200;
        Long executionTimeMs = 150L;
        Integer resultCount = 5;
        Boolean success = true;
        String errorMessage = "Test error";

        // When
        auditEvent.setEventId(eventId);
        auditEvent.setEventType(eventType);
        auditEvent.setTimestamp(timestamp);
        auditEvent.setUserId(userId);
        auditEvent.setUserIp(userIp);
        auditEvent.setEndpoint(endpoint);
        auditEvent.setHttpMethod(httpMethod);
        auditEvent.setRequestParameters(requestParameters);
        auditEvent.setResponseStatus(responseStatus);
        auditEvent.setExecutionTimeMs(executionTimeMs);
        auditEvent.setResultCount(resultCount);
        auditEvent.setSuccess(success);
        auditEvent.setErrorMessage(errorMessage);

        // Then
        assertThat(auditEvent.getEventId()).isEqualTo(eventId);
        assertThat(auditEvent.getEventType()).isEqualTo(eventType);
        assertThat(auditEvent.getTimestamp()).isEqualTo(timestamp);
        assertThat(auditEvent.getUserId()).isEqualTo(userId);
        assertThat(auditEvent.getUserIp()).isEqualTo(userIp);
        assertThat(auditEvent.getEndpoint()).isEqualTo(endpoint);
        assertThat(auditEvent.getHttpMethod()).isEqualTo(httpMethod);
        assertThat(auditEvent.getRequestParameters()).isEqualTo(requestParameters);
        assertThat(auditEvent.getResponseStatus()).isEqualTo(responseStatus);
        assertThat(auditEvent.getExecutionTimeMs()).isEqualTo(executionTimeMs);
        assertThat(auditEvent.getResultCount()).isEqualTo(resultCount);
        assertThat(auditEvent.getSuccess()).isEqualTo(success);
        assertThat(auditEvent.getErrorMessage()).isEqualTo(errorMessage);
    }

    @Test
    void testRequestParameters() {
        // Given
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("numeroNfse", "123456");
        parameters.put("page", 0);
        parameters.put("size", 10);

        // When
        auditEvent.setRequestParameters(parameters);

        // Then
        assertThat(auditEvent.getRequestParameters()).isEqualTo(parameters);
        assertThat(auditEvent.getRequestParameters()).containsKey("numeroNfse");
        assertThat(auditEvent.getRequestParameters()).containsKey("page");
        assertThat(auditEvent.getRequestParameters()).containsKey("size");
        assertThat(auditEvent.getRequestParameters().get("numeroNfse")).isEqualTo("123456");
        assertThat(auditEvent.getRequestParameters().get("page")).isEqualTo(0);
        assertThat(auditEvent.getRequestParameters().get("size")).isEqualTo(10);
    }

    @Test
    void testToString() {
        // Given
        auditEvent.setEventId("test-id");
        auditEvent.setEventType("TEST_EVENT");
        auditEvent.setUserId("user123");
        auditEvent.setUserIp("192.168.1.1");
        auditEvent.setEndpoint("/api/test");
        auditEvent.setHttpMethod("GET");
        auditEvent.setResponseStatus(200);
        auditEvent.setExecutionTimeMs(100L);
        auditEvent.setResultCount(3);
        auditEvent.setSuccess(true);

        // When
        String toString = auditEvent.toString();

        // Then
        assertThat(toString).isNotNull();
        assertThat(toString).contains("test-id");
        assertThat(toString).contains("TEST_EVENT");
        assertThat(toString).contains("user123");
        assertThat(toString).contains("192.168.1.1");
        assertThat(toString).contains("/api/test");
        assertThat(toString).contains("GET");
        assertThat(toString).contains("200");
        assertThat(toString).contains("100");
        assertThat(toString).contains("3");
        assertThat(toString).contains("true");
    }

    @Test
    void testEventIdGeneration() {
        // When
        AuditEvent event1 = new AuditEvent("EVENT1", "/api/test1", "GET");
        AuditEvent event2 = new AuditEvent("EVENT2", "/api/test2", "POST");

        // Then
        assertThat(event1.getEventId()).isNotNull();
        assertThat(event2.getEventId()).isNotNull();
        assertThat(event1.getEventId()).isNotEqualTo(event2.getEventId());
        assertThat(event1.getEventId()).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        assertThat(event2.getEventId()).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @Test
    void testTimestampInitialization() {
        // Given
        LocalDateTime beforeCreation = LocalDateTime.now();

        // When
        AuditEvent event = new AuditEvent("TEST_EVENT", "/api/test", "GET");
        LocalDateTime afterCreation = LocalDateTime.now();

        // Then
        assertThat(event.getTimestamp()).isNotNull();
        assertThat(event.getTimestamp()).isAfterOrEqualTo(beforeCreation);
        assertThat(event.getTimestamp()).isBeforeOrEqualTo(afterCreation);
    }

    @Test
    void testSuccessDefaultValue() {
        // When
        AuditEvent event = new AuditEvent("TEST_EVENT", "/api/test", "GET");

        // Then
        assertThat(event.getSuccess()).isTrue();
    }

    @Test
    void testNullValues() {
        // When
        auditEvent.setEventId(null);
        auditEvent.setEventType(null);
        auditEvent.setUserId(null);
        auditEvent.setUserIp(null);
        auditEvent.setEndpoint(null);
        auditEvent.setHttpMethod(null);
        auditEvent.setRequestParameters(null);
        auditEvent.setResponseStatus(null);
        auditEvent.setExecutionTimeMs(null);
        auditEvent.setResultCount(null);
        auditEvent.setSuccess(null);
        auditEvent.setErrorMessage(null);

        // Then
        assertThat(auditEvent.getEventId()).isNull();
        assertThat(auditEvent.getEventType()).isNull();
        assertThat(auditEvent.getUserId()).isNull();
        assertThat(auditEvent.getUserIp()).isNull();
        assertThat(auditEvent.getEndpoint()).isNull();
        assertThat(auditEvent.getHttpMethod()).isNull();
        assertThat(auditEvent.getRequestParameters()).isNull();
        assertThat(auditEvent.getResponseStatus()).isNull();
        assertThat(auditEvent.getExecutionTimeMs()).isNull();
        assertThat(auditEvent.getResultCount()).isNull();
        assertThat(auditEvent.getSuccess()).isNull();
        assertThat(auditEvent.getErrorMessage()).isNull();
    }
}
