package com.learning.kafka.controller;

import com.learning.kafka.dto.ProducerRequest;
import com.learning.kafka.dto.ProducerResponse;
import com.learning.kafka.facade.CustomProducerFacade;
import com.learning.kafka.producer.CustomKafkaProducerOneImpl;
import com.learning.kafka.service.CustomKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/kafka/produce/producerOne")
public class CustomProducerOneController {

    private CustomKafkaProducer producer;
    private CustomProducerFacade customProducerFacade;

    @Autowired
    public CustomProducerOneController(CustomKafkaProducerOneImpl producer, CustomProducerFacade customProducerFacade) {
        this.producer = producer;
        this.customProducerFacade = customProducerFacade;
    }

    /**
     * Bulk send
     *
     * @param request
     * @return List<ProducerResponse>
     */
    @PostMapping(value = "/bulk")
    public List<ProducerResponse> sendRecordsToKafkaViaProducerOne(@RequestBody ProducerRequest request) {
        return producer.sendMessageUsingProducerRequest(request);
    }

    /**
     * Single record send
     *
     * @param request
     * @return ResponseEntity
     */
    @PostMapping(value = "/single")
    public ResponseEntity sendRecordToKafkaViaProducerOne(@RequestBody ProducerRequest request) {
        if (request.getRecords().size() > 1) {
            return new ResponseEntity<>("too many records in records list, kindly use /kafka/produce/producerOne/bulk for single record push", HttpStatus.PAYLOAD_TOO_LARGE);
        }
        return new ResponseEntity<>(producer.produce(request.getTopicName(), request.getKey(), request.getRecords().get(0)), HttpStatus.OK);
    }

    /**
     * use this api to do some processing on key, value before pushing to kafka.
     *
     * @param request
     * @return List<ProducerResponse>
     */
    @PostMapping(value = "/process")
    public List<ProducerResponse> sendProcessedRecordsToKafkaViaProducerOne(@RequestBody ProducerRequest request) {
        return customProducerFacade.sendMessageUsingProducerOne(request);
    }

}
