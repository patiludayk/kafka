package com.learning.kafka.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProducerResponse {
    private int partition;
    private long offset;
    private String msg;
    private String exception;
}
