package com.learning.kafka.controller;

import com.learning.kafka.dto.ProducerRequest;
import com.learning.kafka.dto.ProducerResponse;
import com.learning.kafka.facade.ProducerFacade;
import com.learning.kafka.producer.SpringBootProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This <>ActionController</> class responsible for user actions like sending message to kafka via producer
 */
@RestController
@Slf4j
@RequestMapping("/kafka/produce")
public class ProducerController {

    private SpringBootProducer producer;
    private ProducerFacade producerFacade;

    @Autowired
    public ProducerController(SpringBootProducer producer) {
        this.producer = producer;
    }

    @PostMapping(value = "/default")
    public List<ProducerResponse> sendRecordToKafkaDefault(@RequestBody List<String> events) {

        List<ProducerResponse> responseList = new ArrayList<>();
        for (String event : events) {
            String key = UUID.randomUUID().toString();
            responseList.add(producer.sendMessage(key, event));
        }

        return responseList;
    }

    @PostMapping(value = "/producerOne")
    public List<ProducerResponse> sendRecordsToKafkaViaProducerOne(@RequestBody ProducerRequest request) {
        return producer.sendMessageUsingProducerOne(request);
    }

    @PostMapping(value = "/producerOne1")
    public List<ProducerResponse> sendRecordsToKafkaViaProducerOne1(@RequestBody ProducerRequest request) {
        return producerFacade.sendMessageUsingProducerOne(request);
    }
}
