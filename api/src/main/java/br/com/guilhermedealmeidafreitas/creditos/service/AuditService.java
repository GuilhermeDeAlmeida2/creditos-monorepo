package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.event.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Serviço responsável por publicar eventos de auditoria no Kafka.
 * Este serviço é usado para notificar consultas realizadas no sistema.
 */
@Service
public class AuditService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    
    @Autowired(required = false)
    private KafkaTemplate<String, AuditEvent> kafkaTemplate;
    
    @Value("${kafka.topic.audit-events:}")
    private String auditTopic;
    
    /**
     * Publica um evento de auditoria no Kafka de forma assíncrona.
     * 
     * @param event O evento de auditoria a ser publicado
     */
    public void publishAuditEvent(AuditEvent event) {
        try {
            // Verificar se o Kafka está disponível
            if (kafkaTemplate == null || auditTopic == null || auditTopic.isEmpty()) {
                logger.debug("Kafka não disponível, pulando publicação do evento: {}", event.getEventId());
                return;
            }
            
            logger.debug("Publicando evento de auditoria: {}", event.getEventId());
            
            // Publica o evento de forma assíncrona
            CompletableFuture<SendResult<String, AuditEvent>> future = 
                kafkaTemplate.send(auditTopic, event.getEventId(), event);
            
            // Adiciona callback para tratar o resultado
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Evento de auditoria publicado com sucesso. EventId: {}, Offset: {}", 
                        event.getEventId(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Erro ao publicar evento de auditoria. EventId: {}, Erro: {}", 
                        event.getEventId(), ex.getMessage(), ex);
                }
            });
            
        } catch (Exception e) {
            logger.error("Erro inesperado ao publicar evento de auditoria. EventId: {}, Erro: {}", 
                event.getEventId(), e.getMessage(), e);
        }
    }
    
    /**
     * Cria e publica um evento de auditoria para uma consulta realizada.
     * 
     * @param eventType Tipo do evento (ex: "CONSULTA_CREDITO", "CONSULTA_NFSE")
     * @param endpoint Endpoint acessado
     * @param httpMethod Método HTTP usado
     * @param requestParameters Parâmetros da requisição
     * @param responseStatus Status da resposta HTTP
     * @param executionTimeMs Tempo de execução em milissegundos
     * @param resultCount Quantidade de resultados retornados
     * @param success Se a operação foi bem-sucedida
     * @param errorMessage Mensagem de erro (se houver)
     */
    public void publishQueryAuditEvent(String eventType, String endpoint, String httpMethod,
                                     Map<String, Object> requestParameters, Integer responseStatus,
                                     Long executionTimeMs, Integer resultCount, Boolean success,
                                     String errorMessage) {
        
        AuditEvent event = new AuditEvent(eventType, endpoint, httpMethod);
        event.setRequestParameters(requestParameters);
        event.setResponseStatus(responseStatus);
        event.setExecutionTimeMs(executionTimeMs);
        event.setResultCount(resultCount);
        event.setSuccess(success);
        event.setErrorMessage(errorMessage);
        
        // TODO: Em um ambiente real, você obteria essas informações do contexto de segurança
        event.setUserId("system"); // Por enquanto, usando um valor padrão
        event.setUserIp("127.0.0.1"); // Por enquanto, usando um valor padrão
        
        publishAuditEvent(event);
    }
    
    /**
     * Cria e publica um evento de auditoria para uma consulta bem-sucedida.
     * 
     * @param eventType Tipo do evento
     * @param endpoint Endpoint acessado
     * @param httpMethod Método HTTP usado
     * @param requestParameters Parâmetros da requisição
     * @param executionTimeMs Tempo de execução em milissegundos
     * @param resultCount Quantidade de resultados retornados
     */
    public void publishSuccessfulQueryEvent(String eventType, String endpoint, String httpMethod,
                                          Map<String, Object> requestParameters, Long executionTimeMs,
                                          Integer resultCount) {
        publishQueryAuditEvent(eventType, endpoint, httpMethod, requestParameters, 
                             200, executionTimeMs, resultCount, true, null);
    }
    
    /**
     * Cria e publica um evento de auditoria para uma consulta que falhou.
     * 
     * @param eventType Tipo do evento
     * @param endpoint Endpoint acessado
     * @param httpMethod Método HTTP usado
     * @param requestParameters Parâmetros da requisição
     * @param responseStatus Status da resposta HTTP
     * @param executionTimeMs Tempo de execução em milissegundos
     * @param errorMessage Mensagem de erro
     */
    public void publishFailedQueryEvent(String eventType, String endpoint, String httpMethod,
                                      Map<String, Object> requestParameters, Integer responseStatus,
                                      Long executionTimeMs, String errorMessage) {
        publishQueryAuditEvent(eventType, endpoint, httpMethod, requestParameters, 
                             responseStatus, executionTimeMs, 0, false, errorMessage);
    }
}



