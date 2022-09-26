package com.learning.kafka.service;

import com.learning.kafka.dto.ProducerRequest;
import com.learning.kafka.dto.ProducerResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CustomKafkaProducer<K, V> {

    ProducerResponse produce(String topic, K key, V value);

    List<ProducerResponse> sendMessageUsingProducerRequest(ProducerRequest request);
}
