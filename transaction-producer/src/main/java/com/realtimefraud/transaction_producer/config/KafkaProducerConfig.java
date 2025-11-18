package com.realtimefraud.producer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtimefraud.producer.model.Transaction;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, Transaction> producerFactory(KafkaProperties kafkaProperties, ObjectMapper objectMapper) {
        Map<String, Object> props = kafkaProperties.buildProducerProperties();
        // Ensure key/value serializer classes are set (value serializer instance will be provided)
        props.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Use the application ObjectMapper (Spring Boot configures it and will register JavaTimeModule
        // when jackson-datatype-jsr310 is on the classpath)
        // Disable adding type info headers so consumers deserialize into their own target class
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new JsonSerializer<>(objectMapper));
    }

    @Bean
    public KafkaTemplate<String, Transaction> kafkaTemplate(ProducerFactory<String, Transaction> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}