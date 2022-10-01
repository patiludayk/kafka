package com.learning.kafka.dto;

import com.learning.kafka.service.CustomKafkaConsumer;
import com.learning.kafka.util.ConsumerType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ConsumerRequest{
    private ConsumerType consumerType;
    private String topic;
}
