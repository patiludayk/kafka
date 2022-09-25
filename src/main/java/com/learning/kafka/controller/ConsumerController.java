package com.learning.kafka.controller;

import com.learning.kafka.dto.ConsumerResponse;
import com.learning.kafka.dto.ProducerResponse;
import com.learning.kafka.producer.SpringBootProducer;
import com.learning.kafka.service.CustomKafkaConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This <>ActionController</> class responsible for user actions like consuming records from kafka.
 */
@RestController
@Slf4j
@RequestMapping("/kafka/consume")
public class ConsumerController {

    private CustomKafkaConsumer<ConsumerResponse> customKafkaConsumer;

    @Value("${TOPIC_NAME}")
    private String TOPIC;

    @Autowired
    public ConsumerController(CustomKafkaConsumer<ConsumerResponse> customKafkaConsumer) {
        this.customKafkaConsumer = customKafkaConsumer;
    }

    @GetMapping(value = "/default/{topicName}")
    public List<ConsumerResponse> consumeMessageFromKafkaTopic(@PathVariable("topicName") Optional<String> topicName) {
        List<ConsumerResponse> messages = new ArrayList<>();
        String topic = topicName.isPresent() ? topicName.get() : TOPIC;
        log.info("consuming records from topic: {}", topic);
        customKafkaConsumer.consumeEvents(topic, messages);

        return messages;
    }

}