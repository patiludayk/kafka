package com.learning.kafka.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ConsumerResponse<T> {
    private int partition;
    private long offset;
    private String key;
    private List<T> value;
    private String exception;
}
