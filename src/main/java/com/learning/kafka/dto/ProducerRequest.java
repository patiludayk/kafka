package com.learning.kafka.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ProducerRequest {
    private String topicName;
    private String key;
    private List<String> records;
}
