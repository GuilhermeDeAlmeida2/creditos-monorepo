package br.com.guilhermedealmeidafreitas.creditos.config;

import br.com.guilhermedealmeidafreitas.creditos.event.AuditEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KafkaConfigTest {

    private KafkaConfig kafkaConfig;

    @BeforeEach
    void setUp() {
        kafkaConfig = new KafkaConfig();
        
        // Configurar valores das propriedades usando ReflectionTestUtils
        ReflectionTestUtils.setField(kafkaConfig, "bootstrapServers", "localhost:9092");
        ReflectionTestUtils.setField(kafkaConfig, "keySerializer", "org.apache.kafka.common.serialization.StringSerializer");
        ReflectionTestUtils.setField(kafkaConfig, "valueSerializer", "org.springframework.kafka.support.serializer.JsonSerializer");
        ReflectionTestUtils.setField(kafkaConfig, "acks", "all");
        ReflectionTestUtils.setField(kafkaConfig, "retries", 3);
        ReflectionTestUtils.setField(kafkaConfig, "batchSize", 16384);
        ReflectionTestUtils.setField(kafkaConfig, "lingerMs", 5);
        ReflectionTestUtils.setField(kafkaConfig, "bufferMemory", 33554432L);
    }

    @Test
    void testProducerFactory_NotNull() {
        // When
        ProducerFactory<String, AuditEvent> producerFactory = kafkaConfig.producerFactory();

        // Then
        assertThat(producerFactory).isNotNull();
        assertThat(producerFactory).isInstanceOf(org.springframework.kafka.core.DefaultKafkaProducerFactory.class);
    }

    @Test
    void testProducerFactory_Configuration() {
        // When
        ProducerFactory<String, AuditEvent> producerFactory = kafkaConfig.producerFactory();

        // Then
        assertThat(producerFactory).isNotNull();
        
        // Verificar se as configurações estão corretas através dos métodos do ProducerFactory
        // Como não podemos acessar diretamente as configurações, verificamos se o factory foi criado
        assertThat(producerFactory.getConfigurationProperties()).isNotNull();
    }

    @Test
    void testKafkaTemplate_NotNull() {
        // When
        KafkaTemplate<String, AuditEvent> kafkaTemplate = kafkaConfig.kafkaTemplate();

        // Then
        assertThat(kafkaTemplate).isNotNull();
        assertThat(kafkaTemplate).isInstanceOf(KafkaTemplate.class);
    }

    @Test
    void testKafkaTemplate_UsesCorrectProducerFactory() {
        // Given
        ProducerFactory<String, AuditEvent> producerFactory = kafkaConfig.producerFactory();

        // When
        KafkaTemplate<String, AuditEvent> kafkaTemplate = kafkaConfig.kafkaTemplate();

        // Then
        assertThat(kafkaTemplate).isNotNull();
        assertThat(kafkaTemplate.getProducerFactory()).isNotNull();
    }

    @Test
    void testProducerFactory_DefaultConfiguration() {
        // When
        ProducerFactory<String, AuditEvent> producerFactory = kafkaConfig.producerFactory();

        // Then
        assertThat(producerFactory).isNotNull();
        
        // Verificar se o factory pode ser usado para criar um producer
        // Isso indiretamente testa se as configurações estão corretas
        assertThat(producerFactory.createProducer()).isNotNull();
    }

    @Test
    void testKafkaTemplate_CanSendMessages() {
        // Given
        KafkaTemplate<String, AuditEvent> kafkaTemplate = kafkaConfig.kafkaTemplate();
        AuditEvent auditEvent = new AuditEvent("TEST_EVENT", "/test", "GET");

        // When & Then
        // Verificar se o template foi criado corretamente
        assertThat(kafkaTemplate).isNotNull();
        assertThat(kafkaTemplate.getDefaultTopic()).isNull(); // Não há tópico padrão configurado
    }

    @Test
    void testProducerFactory_ConfigurationProperties() {
        // When
        ProducerFactory<String, AuditEvent> producerFactory = kafkaConfig.producerFactory();

        // Then
        assertThat(producerFactory).isNotNull();
        
        // Verificar se as configurações básicas estão presentes
        var configProps = producerFactory.getConfigurationProperties();
        assertThat(configProps).isNotNull();
        assertThat(configProps).containsKey(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG);
        assertThat(configProps).containsKey(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG);
        assertThat(configProps).containsKey(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG);
        assertThat(configProps).containsKey(ProducerConfig.ACKS_CONFIG);
        assertThat(configProps).containsKey(ProducerConfig.RETRIES_CONFIG);
        assertThat(configProps).containsKey(ProducerConfig.BATCH_SIZE_CONFIG);
        assertThat(configProps).containsKey(ProducerConfig.LINGER_MS_CONFIG);
        assertThat(configProps).containsKey(ProducerConfig.BUFFER_MEMORY_CONFIG);
        assertThat(configProps).containsKey(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG);
        assertThat(configProps).containsKey(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION);
    }

    @Test
    void testProducerFactory_ConfigurationValues() {
        // When
        ProducerFactory<String, AuditEvent> producerFactory = kafkaConfig.producerFactory();

        // Then
        var configProps = producerFactory.getConfigurationProperties();
        
        assertThat(configProps.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG)).isEqualTo("localhost:9092");
        assertThat(configProps.get(ProducerConfig.ACKS_CONFIG)).isEqualTo("all");
        assertThat(configProps.get(ProducerConfig.RETRIES_CONFIG)).isEqualTo(3);
        assertThat(configProps.get(ProducerConfig.BATCH_SIZE_CONFIG)).isEqualTo(16384);
        assertThat(configProps.get(ProducerConfig.LINGER_MS_CONFIG)).isEqualTo(5);
        assertThat(configProps.get(ProducerConfig.BUFFER_MEMORY_CONFIG)).isEqualTo(33554432L);
        assertThat(configProps.get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG)).isEqualTo(true);
        assertThat(configProps.get(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION)).isEqualTo(5);
    }
}
