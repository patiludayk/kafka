package com.learning.kafka.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ConsumerResponse {
    private int partition;
    private long offset;
    private String key;
    private String value;
    private String exception;
}
