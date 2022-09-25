package com.learning.kafka.controller;

import com.learning.kafka.dto.ConsumerResponse;
import com.learning.kafka.dto.ProducerResponse;
import com.learning.kafka.producer.SpringBootProducer;
import com.learning.kafka.service.CustomKafkaConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This <>ActionController</> class responsible for user actions like consuming records from kafka.
 */
@RestController
@Slf4j
@RequestMapping("/kafka/consume")
public class ConsumerController {

    private CustomKafkaConsumer<ConsumerResponse> customKafkaConsumer;

    @Autowired
    public ConsumerController(CustomKafkaConsumer<ConsumerResponse> customKafkaConsumer) {
        this.customKafkaConsumer = customKafkaConsumer;
    }

    @GetMapping(value = "/default")
    public List<ConsumerResponse> consumeMessageFromKafkaTopic() {
        List<ConsumerResponse> messages = new ArrayList<>();
        customKafkaConsumer.consumeEvents(messages);

        return messages;
    }

}