package com.learning.kafka.config.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.UUID;

@Component
public class ConsumerConfiguration {

    @Value("${BOOTSTRAP_SERVER:localhost:9092}")
    private String bootstrapServer;

    @Value("${GROUP_ID:kafka-learning}")
    private String groupId;

    public Properties getConsumerProperties() {
        // create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); 	//[latest, earliest, none]
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true"); //false
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "500");

        return properties;
    }

    public KafkaConsumer<String, String> getDefaultKafkaConsumer(){
        return new KafkaConsumer<String, String>(getConsumerProperties());
    }

}
