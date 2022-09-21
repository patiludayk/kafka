package com.learning.kafka.config.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class ProducerConfiguration {

    @Value("${BOOTSTRAP_SERVER:localhost:9092}")
    private String bootstrapServer;

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
        return new KafkaProducer<String, String>(producerProps);
    }
}
