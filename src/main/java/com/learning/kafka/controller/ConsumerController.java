package com.learning.kafka.controller;

import com.learning.kafka.dto.ConsumerRequest;
import com.learning.kafka.dto.ConsumerResponse;
import com.learning.kafka.facade.CustomConsumerFacade;
import com.learning.kafka.util.ConsumerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.learning.kafka.util.ConsumerType.CUSTOM_CONSUMER;
import static com.learning.kafka.util.ConsumerType.DEFAULT_CONSUMER;

/**
 * This <>ActionController</> class responsible for user actions like consuming records from kafka.
 */
@RestController
@Slf4j
@RequestMapping("/kafka/consume")
public class ConsumerController {

    private CustomConsumerFacade consumerFacade;

    @Value("${TOPIC_NAME}")
    private String TOPIC;

    @Autowired
    public ConsumerController(CustomConsumerFacade consumerFacade) {
        this.consumerFacade = consumerFacade;
    }

    @GetMapping(value = "/default/{topicName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BlockingQueue<ConsumerResponse> consumeMessageFromKafkaTopic(@PathVariable("topicName") Optional<String> topicName) {
        BlockingQueue<ConsumerResponse> messages = new LinkedBlockingQueue<>();
        String topic = topicName.isPresent() ? topicName.get() : TOPIC;
        log.info("consuming records from topic: {}", topic);
        consumerFacade.consumeRecords(ConsumerRequest.builder().consumerType(DEFAULT_CONSUMER).topic(topic).build(), messages);

        return messages;
    }

    @GetMapping(value = "/custom/{topicName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BlockingQueue<ConsumerResponse> consumeRecordsFromKafkaTopic(@PathVariable("topicName") Optional<String> topicName) {
        BlockingQueue<ConsumerResponse> consumerResponses = new ArrayBlockingQueue<>(10);
        String topic = topicName.isPresent() ? topicName.get() : TOPIC;
        log.info("consuming records from topic: {}", topic);
        consumerFacade.consumeRecords(ConsumerRequest.builder().consumerType(CUSTOM_CONSUMER).topic(topic).build(), consumerResponses);

        return consumerResponses;
    }

}