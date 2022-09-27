package com.learning.kafka.controller;

import com.learning.kafka.dto.ProducerRequest;
import com.learning.kafka.facade.CustomProducerFacade;
import com.learning.kafka.util.ProducerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/kafka/produce")
public class ProducerController<K, V> {

    private CustomProducerFacade<K, V> customProducerFacade;

    @Autowired
    public ProducerController(CustomProducerFacade customProducerFacade) {
        this.customProducerFacade = customProducerFacade;
    }

    /**
     * sends single original record to kafka using producer
     * @param request ProducerRequest
     * @return ResponseEntity
     */
    @PostMapping(value = "/{producer}/single")
    public ResponseEntity sendRecordToKafkaViaProducerTwo(@PathVariable String producer, @RequestBody ProducerRequest request) {

        ProducerType producerType = getProducerType(producer);
        if (request.getRecords().size() > 1) {
            return new ResponseEntity<>("too many records in records list, kindly use /kafka/produce/producerOne/bulk for single record push", HttpStatus.PAYLOAD_TOO_LARGE);
        }
        return new ResponseEntity<>(customProducerFacade.sendRecord(request.getTopicName(), (K) request.getKey(), (V) request.getRecords().get(0), producerType), HttpStatus.OK);
    }

    /**
     * sends single record with processed value
     * @param request ProducerRequest
     * @return ResponseEntity
     */
    @PostMapping(value = "{producer}/singlep")
    public ResponseEntity sendProcessedRecordToKafkaViaProducerTwo(@PathVariable String producer, @RequestBody ProducerRequest request) {
        ProducerType producerType = getProducerType(producer);
        if (request.getRecords().size() > 1) {
            return new ResponseEntity<>("too many records in records list, kindly use /kafka/produce/producerOne/bulk for single record push", HttpStatus.PAYLOAD_TOO_LARGE);
        }
        return new ResponseEntity<>(customProducerFacade.sendProcessedRecord(request.getTopicName(), (K) request.getKey(), (V) request.getRecords().get(0), producerType), HttpStatus.OK);
    }

    /**
     * sends records in bulk using producer
     *
     * @param request ProducerRequest
     * @return List<ProducerResponse>
     */
    @PostMapping(value = "{producer}/bulk")
    public List sendRecordsToKafkaViaProducerTwo(@PathVariable String producer, @RequestBody ProducerRequest request) {
        ProducerType producerType = getProducerType(producer);
        return customProducerFacade.sendBulkRecordsUsingProducerRequest(request, producerType);
    }

    /**
     * sends records in bulk with processed value
     *
     * @param request ProducerRequest
     * @return List<ProducerResponse>
     */
    @PostMapping(value = "{producer}/bulkp")
    public List sendProcessedRecordsToKafkaViaProducerTwo(@PathVariable String producer, @RequestBody ProducerRequest request) {
        ProducerType producerType = getProducerType(producer);
        return customProducerFacade.sendBulkProcessedRecordsUsingProducerRequest(request, producerType);
    }

    private ProducerType getProducerType(String producer) {
        for (ProducerType p : ProducerType.values()) {
            if(p.getProducerName().equals(producer)){
                return p;
            }
        }
        log.warn("setting to default producer.");
        return ProducerType.BROKER1_PRODUCER_DEFAULT;
    }


}
