package com.learning.kafka.config.producer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class CustomProducerConfiguration {

    @Value("${BOOTSTRAP_SERVER:localhost:9092}")
    private String bootstrapServer;

    @Bean(name = "producerOne")     //just for simplicity assigning name here otherwise spring by default provides method’s name as the name of the resulting bean.
    @Primary    //in case of 2 same kafkaTemplate of type <String, String> 1 needs to mark primary
    public KafkaTemplate<String, String> kafkaTemplateOne(@Qualifier("getProducerFactoryOne") ProducerFactory<String, String> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean(name = "producerTwo")
    public KafkaTemplate<String, String> kafkaTemplateTwo(@Qualifier("getProducerFactoryTwo") ProducerFactory<String, String> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }

}
