package com.learning.kafka.service;

import java.util.List;

public interface CustomKafkaConsumer<T> {
    void consumeEvents(String topicName, List<T> messages);
}
