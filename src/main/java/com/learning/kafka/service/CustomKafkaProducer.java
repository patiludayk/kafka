package com.learning.kafka.service;

import com.learning.kafka.dto.ProducerResponse;
import org.springframework.stereotype.Service;

@Service
public interface CustomKafkaProducer<T> {

    ProducerResponse produce(String topic, String key, T value);
}
