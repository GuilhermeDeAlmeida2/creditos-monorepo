package br.com.guilhermedealmeidafreitas.creditos.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Evento de auditoria para notificar consultas realizadas no sistema de créditos.
 * Este evento é publicado no Kafka sempre que uma consulta é realizada.
 */
public class AuditEvent {
    
    @JsonProperty("eventId")
    private String eventId;
    
    @JsonProperty("eventType")
    private String eventType;
    
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;
    
    @JsonProperty("userId")
    private String userId;
    
    @JsonProperty("userIp")
    private String userIp;
    
    @JsonProperty("endpoint")
    private String endpoint;
    
    @JsonProperty("httpMethod")
    private String httpMethod;
    
    @JsonProperty("requestParameters")
    private Map<String, Object> requestParameters;
    
    @JsonProperty("responseStatus")
    private Integer responseStatus;
    
    @JsonProperty("executionTimeMs")
    private Long executionTimeMs;
    
    @JsonProperty("resultCount")
    private Integer resultCount;
    
    @JsonProperty("success")
    private Boolean success;
    
    @JsonProperty("errorMessage")
    private String errorMessage;
    
    // Construtores
    public AuditEvent() {
        this.timestamp = LocalDateTime.now();
    }
    
    public AuditEvent(String eventType, String endpoint, String httpMethod) {
        this();
        this.eventId = java.util.UUID.randomUUID().toString();
        this.eventType = eventType;
        this.endpoint = endpoint;
        this.httpMethod = httpMethod;
        this.success = true;
    }
    
    // Getters e Setters
    public String getEventId() {
        return eventId;
    }
    
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserIp() {
        return userIp;
    }
    
    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    
    public String getHttpMethod() {
        return httpMethod;
    }
    
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
    
    public Map<String, Object> getRequestParameters() {
        return requestParameters;
    }
    
    public void setRequestParameters(Map<String, Object> requestParameters) {
        this.requestParameters = requestParameters;
    }
    
    public Integer getResponseStatus() {
        return responseStatus;
    }
    
    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }
    
    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    public Integer getResultCount() {
        return resultCount;
    }
    
    public void setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }
    
    public Boolean getSuccess() {
        return success;
    }
    
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    @Override
    public String toString() {
        return "AuditEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                ", userId='" + userId + '\'' +
                ", userIp='" + userIp + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", responseStatus=" + responseStatus +
                ", executionTimeMs=" + executionTimeMs +
                ", resultCount=" + resultCount +
                ", success=" + success +
                '}';
    }
}



