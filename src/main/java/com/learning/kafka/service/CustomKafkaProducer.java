package com.learning.kafka.service;

import com.learning.kafka.dto.ProducerRequest;
import com.learning.kafka.dto.ProducerResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CustomKafkaProducer<K, V> {

    ProducerResponse produce(KafkaTemplate<K, V> kafkaTemplate, String topic, K key, V value);

    List<ProducerResponse> sendBulkMessageUsingProducerRequest(KafkaTemplate<K, V> kafkaTemplate, ProducerRequest request);
}
