package com.learning.kafka.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CustomKafkaConsumer<T> {
    void consumeRecords(String topicName, List<T> messages);
}
