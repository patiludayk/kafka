package com.learning.kafka.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ProducerRequest<K, V> {
    private String topicName;
    private K key;
    private List<V> records;
}
