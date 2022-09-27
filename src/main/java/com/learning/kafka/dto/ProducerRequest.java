package com.learning.kafka.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProducerRequest<K, V> {
    private String topicName;
    private K key;
    private List<V> records;
}
