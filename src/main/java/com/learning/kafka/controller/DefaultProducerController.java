package com.learning.kafka.controller;

import com.learning.kafka.dto.ProducerRequest;
import com.learning.kafka.dto.ProducerResponse;
import com.learning.kafka.facade.CustomProducerFacade;
import com.learning.kafka.producer.DefaultProducerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This <>ActionController</> class responsible for user actions like sending message to kafka via producer
 */
@RestController
@Slf4j
@RequestMapping("/kafka/produce")
public class DefaultProducerController<K, V> {


    @Value("${TOPIC_NAME}")
    private String TOPIC;

    @Autowired
    private DefaultProducerImpl<K, V> producer;

    @Autowired
    private CustomProducerFacade customProducerFacade;

    @PostMapping(value = "/default")
    public List<ProducerResponse> sendRecordToKafkaDefault(@RequestBody ProducerRequest request) {

        String topic = request.getTopicName() != null && !request.getTopicName().isEmpty() ? request.getTopicName() : TOPIC;
        K key = (K) request.getKey();

        final Stream<?> recordStream = request.getRecords().stream();
        final List<ProducerResponse> producerResponses = recordStream
                .map(record -> producer.produce(topic, key, (V) record))
                .collect(Collectors.toList());

        return producerResponses;
    }
/*

    @PostMapping(value = "/producerOne")
    public List<ProducerResponse> sendRecordsToKafkaViaProducerOne(@RequestBody ProducerRequest request) {
        return producer.sendMessageUsingProducerRequest(request);
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
        return new ResponseEntity<>(customProducerFacade.sendMessageUsingProducerTwo(request), HttpStatus.OK);
    }
*/
}
