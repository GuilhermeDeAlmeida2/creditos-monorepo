package br.com.guilhermedealmeidafreitas.creditos.config;

import br.com.guilhermedealmeidafreitas.creditos.event.AuditEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuração do Kafka Producer para envio de eventos de auditoria.
 */
@Configuration
public class KafkaConfig {
    
    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Value("${kafka.producer.key-serializer}")
    private String keySerializer;
    
    @Value("${kafka.producer.value-serializer}")
    private String valueSerializer;
    
    @Value("${kafka.producer.acks}")
    private String acks;
    
    @Value("${kafka.producer.retries}")
    private int retries;
    
    @Value("${kafka.producer.batch-size}")
    private int batchSize;
    
    @Value("${kafka.producer.linger-ms}")
    private int lingerMs;
    
    @Value("${kafka.producer.buffer-memory}")
    private long bufferMemory;
    
    /**
     * Configuração do ProducerFactory para o Kafka.
     * Define as propriedades necessárias para o producer.
     */
    @Bean
    public ProducerFactory<String, AuditEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        // Configurações básicas
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // Configurações de confiabilidade
        configProps.put(ProducerConfig.ACKS_CONFIG, acks);
        configProps.put(ProducerConfig.RETRIES_CONFIG, retries);
        
        // Configurações de performance
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        
        // Configurações adicionais para garantir entrega
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    /**
     * Template do Kafka para envio de mensagens.
     * Este bean será usado pelo serviço de auditoria para publicar eventos.
     */
    @Bean
    public KafkaTemplate<String, AuditEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}



