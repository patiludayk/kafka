package com.learning.kafka.controller;

import com.learning.kafka.broker.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
@RequestMapping(value = "/kafka")
public class KafkaController {

    private KafkaService kafkaService;

    @Autowired
    public KafkaController(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @GetMapping(value = "topics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<String> getAllKafkaTopics(String brokerId) throws ExecutionException, InterruptedException {
        return kafkaService.getAllKafkaTopics();
    }
}
