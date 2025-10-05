package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.event.AuditEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private KafkaTemplate<String, AuditEvent> kafkaTemplate;

    @InjectMocks
    private AuditService auditService;

    private AuditEvent auditEvent;
    private Map<String, Object> requestParameters;

    @BeforeEach
    void setUp() {
        // Configurar o valor do tópico usando ReflectionTestUtils
        ReflectionTestUtils.setField(auditService, "auditTopic", "test-audit-topic");
        
        auditEvent = new AuditEvent("TEST_EVENT", "/test/endpoint", "GET");
        auditEvent.setEventId("test-event-id");
        
        requestParameters = new HashMap<>();
        requestParameters.put("param1", "value1");
        requestParameters.put("param2", "value2");
    }

    @Test
    void testPublishAuditEvent_Sucesso() {
        // Given
        CompletableFuture<SendResult<String, AuditEvent>> future = new CompletableFuture<>();
        SendResult<String, AuditEvent> sendResult = createMockSendResult();
        future.complete(sendResult);
        
        when(kafkaTemplate.send(anyString(), anyString(), any(AuditEvent.class)))
            .thenReturn(future);

        // When
        auditService.publishAuditEvent(auditEvent);

        // Then
        verify(kafkaTemplate, times(1)).send(eq("test-audit-topic"), eq("test-event-id"), eq(auditEvent));
    }

    @Test
    void testPublishAuditEvent_ErroKafka() {
        // Given
        CompletableFuture<SendResult<String, AuditEvent>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka error"));
        
        when(kafkaTemplate.send(anyString(), anyString(), any(AuditEvent.class)))
            .thenReturn(future);

        // When
        auditService.publishAuditEvent(auditEvent);

        // Then
        verify(kafkaTemplate, times(1)).send(eq("test-audit-topic"), eq("test-event-id"), eq(auditEvent));
    }

    @Test
    void testPublishAuditEvent_ExcecaoInesperada() {
        // Given
        when(kafkaTemplate.send(anyString(), anyString(), any(AuditEvent.class)))
            .thenThrow(new RuntimeException("Unexpected error"));

        // When
        auditService.publishAuditEvent(auditEvent);

        // Then
        verify(kafkaTemplate, times(1)).send(eq("test-audit-topic"), eq("test-event-id"), eq(auditEvent));
    }

    @Test
    void testPublishQueryAuditEvent_Sucesso() {
        // Given
        CompletableFuture<SendResult<String, AuditEvent>> future = new CompletableFuture<>();
        SendResult<String, AuditEvent> sendResult = createMockSendResult();
        future.complete(sendResult);
        
        when(kafkaTemplate.send(anyString(), anyString(), any(AuditEvent.class)))
            .thenReturn(future);

        // When
        auditService.publishQueryAuditEvent(
            "CONSULTA_CREDITO", 
            "/api/creditos/123", 
            "GET", 
            requestParameters, 
            200, 
            100L, 
            5, 
            true, 
            null
        );

        // Then
        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), any(AuditEvent.class));
    }

    @Test
    void testPublishSuccessfulQueryEvent() {
        // Given
        CompletableFuture<SendResult<String, AuditEvent>> future = new CompletableFuture<>();
        SendResult<String, AuditEvent> sendResult = createMockSendResult();
        future.complete(sendResult);
        
        when(kafkaTemplate.send(anyString(), anyString(), any(AuditEvent.class)))
            .thenReturn(future);

        // When
        auditService.publishSuccessfulQueryEvent(
            "CONSULTA_CREDITO", 
            "/api/creditos/123", 
            "GET", 
            requestParameters, 
            100L, 
            5
        );

        // Then
        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), any(AuditEvent.class));
    }

    @Test
    void testPublishFailedQueryEvent() {
        // Given
        CompletableFuture<SendResult<String, AuditEvent>> future = new CompletableFuture<>();
        SendResult<String, AuditEvent> sendResult = createMockSendResult();
        future.complete(sendResult);
        
        when(kafkaTemplate.send(anyString(), anyString(), any(AuditEvent.class)))
            .thenReturn(future);

        // When
        auditService.publishFailedQueryEvent(
            "CONSULTA_CREDITO", 
            "/api/creditos/123", 
            "GET", 
            requestParameters, 
            404, 
            50L, 
            "Not found"
        );

        // Then
        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), any(AuditEvent.class));
    }

    @Test
    void testPublishQueryAuditEvent_ComErro() {
        // Given
        CompletableFuture<SendResult<String, AuditEvent>> future = new CompletableFuture<>();
        SendResult<String, AuditEvent> sendResult = createMockSendResult();
        future.complete(sendResult);
        
        when(kafkaTemplate.send(anyString(), anyString(), any(AuditEvent.class)))
            .thenReturn(future);

        // When
        auditService.publishQueryAuditEvent(
            "CONSULTA_CREDITO", 
            "/api/creditos/123", 
            "GET", 
            requestParameters, 
            500, 
            200L, 
            0, 
            false, 
            "Internal server error"
        );

        // Then
        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), any(AuditEvent.class));
    }

    private SendResult<String, AuditEvent> createMockSendResult() {
        // Criar um mock do SendResult
        SendResult<String, AuditEvent> sendResult = org.mockito.Mockito.mock(SendResult.class);
        org.apache.kafka.clients.producer.RecordMetadata recordMetadata = 
            org.mockito.Mockito.mock(org.apache.kafka.clients.producer.RecordMetadata.class);
        
        when(sendResult.getRecordMetadata()).thenReturn(recordMetadata);
        when(recordMetadata.offset()).thenReturn(123L);
        
        return sendResult;
    }
}
