package com.learning.kafka.config.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@Slf4j
public class CustomProducerFactory {

    @Value("${BOOTSTRAP_SERVER:localhost:9092}")
    private String bootstrapServer;

    @Value("${SECOND_BOOTSTRAP_SERVER:localhost:9092}")
    private String someOtherBootstrapServer;

    private KafkaProducer<String, String> defaultProducer;

    @Bean
    public KafkaProducer<String, String> getProducer(){

        Properties producerProps = new Properties();
        // producer properties
        producerProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        producerProps.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create safe producer
        producerProps.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        producerProps.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        producerProps.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
        //producerProps.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

        // high throughput producer
        //producerProps.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        producerProps.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
        producerProps.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024));// 32KB

        // create producer
        this. defaultProducer=  new KafkaProducer<String, String>(producerProps);

        return defaultProducer;
    }

    @Bean
    public ProducerFactory<String, String> getProducerFactoryOne(){
        Map<String, Object> producerOneConfig = new HashMap<>();
        // producer properties
        producerOneConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        producerOneConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerOneConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        /* leaving this config on user if they want's to set this or not above props are enough to run producer
        // create safe producer
        producerOneConfig.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        producerOneConfig.put(ProducerConfig.ACKS_CONFIG, "all");
        producerOneConfig.put(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
        //producerOneConfig.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

        // high throughput producer
        //producerOneConfig.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        producerOneConfig.put(ProducerConfig.LINGER_MS_CONFIG, "20");
        producerOneConfig.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024));// 32KB
        */

        return new DefaultKafkaProducerFactory<>(producerOneConfig);
    }

    @Bean
    public ProducerFactory<String, String> getProducerFactoryTwo(){
        Map<String, Object> producerTwoConfig = new HashMap<>();
        // producer properties
        producerTwoConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, someOtherBootstrapServer);   //different broker to send!
        producerTwoConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerTwoConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        /* leaving this config on user if they want's to set this or not above props are enough to run producer
        // create safe producer
        producerTwoConfig.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        producerTwoConfig.put(ProducerConfig.ACKS_CONFIG, "all");
        producerTwoConfig.put(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
        //producerTwoConfig.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

        // high throughput producer
        //producerTwoConfig.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        producerTwoConfig.put(ProducerConfig.LINGER_MS_CONFIG, "20");
        producerTwoConfig.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024));// 32KB
        */

        return new DefaultKafkaProducerFactory<>(producerTwoConfig);
    }

    @PreDestroy
    public void close(){
        this.defaultProducer.close();
    }

}
