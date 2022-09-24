package com.learning.kafka.service;

import java.util.List;

public interface CustomKafkaConsumer<T> {
    void consumeEvents(List<T> messages);
}
