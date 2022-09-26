package com.learning.kafka.controller;

import com.learning.kafka.dto.ProducerRequest;
import com.learning.kafka.dto.ProducerResponse;
import com.learning.kafka.facade.CustomProducerFacade;
import com.learning.kafka.producer.CustomProducerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class ProducerController<T> {


    @Value("${TOPIC_NAME}")
    private String TOPIC;

    private CustomProducerImpl producer;
    private CustomProducerFacade customProducerFacade;

    @Autowired
    public ProducerController(CustomProducerImpl producer) {
        this.producer = producer;
    }

    @PostMapping(value = "/default/")
    public List<ProducerResponse> sendRecordToKafkaDefault(@RequestBody ProducerRequest request) {

        List<ProducerResponse> responseList = new ArrayList<>();
        String topic = request.getTopicName() != null && !request.getTopicName().isEmpty() ? request.getTopicName() : TOPIC;
        for (String record : request.getRecords()) {
            String key = UUID.randomUUID().toString();
            responseList.add(producer.sendMessage(topic, key, record));
        }

        return responseList;
    }

    @PostMapping(value = "/producerOne")
    public List<ProducerResponse> sendRecordsToKafkaViaProducerOne(@RequestBody ProducerRequest request) {
        return producer.sendMessageUsingProducerOne(request);
    }

    @PostMapping(value = "/producerOne1")
    public List<ProducerResponse> sendRecordsToKafkaViaProducerOne1(@RequestBody ProducerRequest request) {
        return customProducerFacade.sendMessageUsingProducerOne(request);
    }

    @PostMapping(value = "/producerTwo")
    public List<ProducerResponse> sendRecordsToKafkaViaProducerTwo(@RequestBody ProducerRequest request) {
        return customProducerFacade.sendMessageUsingProducerTwo(request);
    }

    @PostMapping(value = "/producerTwo1")
    public ResponseEntity sendRecordsToKafkaViaProducerTwo1(@RequestBody ProducerRequest request) {
        if (request.getRecords().size() > 1) {
            return new ResponseEntity<>("too many records, kindly use /kafka/produce/producerTwo", HttpStatus.PAYLOAD_TOO_LARGE);
        }
        return (ResponseEntity) customProducerFacade.sendMessageUsingProducerTwo(request);
    }

}
