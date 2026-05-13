package com.mediqueue.notification.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.HashMap;
import java.util.Map;

@Configuration // Marks this as a Spring configuration class
public class KafkaConfig {

    // Inject Kafka broker address from application.yml
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {

        // Configuration map for Kafka producer
        Map<String, Object> config = new HashMap<>();

        // Kafka broker address (e.g., localhost:9092)
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Serializer for message key (String)
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Serializer for message value (JSON format)
        //config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Ensure strongest delivery guarantee (leader + replicas must acknowledge)
        config.put(ProducerConfig.ACKS_CONFIG, "all");

        // Retry sending message up to 3 times if failure occurs
        config.put(ProducerConfig.RETRIES_CONFIG, 3);

        // Enable idempotence to avoid duplicate messages
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        // Create and return ProducerFactory
       // return new DefaultKafkaProducerFactory<>(config);
        return new DefaultKafkaProducerFactory<>(
                config,
                new StringSerializer(),
                new JacksonJsonSerializer<Object>()
        );
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {

        // KafkaTemplate is used to send messages to Kafka topics
        // Example: kafkaTemplate.send("topic-name", message);
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2); // Small pool is usually enough for retries
        scheduler.setThreadNamePrefix("kafka-retry-scheduler-");
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JacksonJsonDeserializer.class);
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // This allows the retry logic to send messages back to Kafka
        factory.setReplyTemplate(kafkaTemplate());
        return factory;
    }
}
