package br.com.guilhermedealmeidafreitas.creditos.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;

/**
 * Configuração de teste para desabilitar Kafka nos testes integrados.
 * Usado principalmente nos testes integrados onde não queremos depender do Kafka.
 */
@TestConfiguration
@Profile("integration")
public class TestConfig {

    /**
     * Mock do KafkaTemplate para testes integrados.
     * Evita problemas com Kafka que está desabilitado nos testes.
     */
    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return mock(KafkaTemplate.class);
    }
}
