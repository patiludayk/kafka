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
 * This <>ActionController</> class responsible for user actions like sending message to kafka via producer
 * and consuming using consumer.
 */
@RestController
@Slf4j
@RequestMapping("/kafka")
public class ActionController {

    private final SpringBootProducer producer;

    @Autowired
    private CustomKafkaConsumer<ConsumerResponse> customKafkaConsumer;

    @Autowired
    public ActionController(SpringBootProducer producer) {
        this.producer = producer;
    }

    @PostMapping(value = "/produce")
    public List<ProducerResponse> sendMessageToKafkaTopic(@RequestBody List<String> events) {

        List<ProducerResponse> responseList = new ArrayList<>();
        for (String event : events) {
            String key = UUID.randomUUID().toString();
            responseList.add(producer.sendMessage(key, event));
        }

        return responseList;
    }

    @GetMapping(value = "/consume")
    public List<ConsumerResponse> consumeMessageFromKafkaTopic() {
        List<ConsumerResponse> messages = new ArrayList<>();
        customKafkaConsumer.consumeEvents(messages);

        return messages;
    }
}
