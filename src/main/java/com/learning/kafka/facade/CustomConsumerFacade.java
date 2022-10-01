package com.learning.kafka.facade;

import com.learning.kafka.dto.ConsumerRequest;
import com.learning.kafka.dto.ConsumerResponse;
import com.learning.kafka.service.CustomKafkaConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomConsumerFacade {

    private final Map<String, CustomKafkaConsumer> consumerMap;
    private CustomKafkaConsumer kafkaConsumer;

    @Autowired
    public CustomConsumerFacade(List<CustomKafkaConsumer> consumerList) {
        consumerMap = consumerList.stream().collect(Collectors.toMap(CustomKafkaConsumer::consumerName, Function.identity()));
    }

    public void consumeRecords(ConsumerRequest consumerRequest, BlockingQueue<ConsumerResponse> consumerResponses) {

        kafkaConsumer = consumerMap.get(consumerRequest.getConsumerType().getConsumerName());
        kafkaConsumer.consumeRecords(consumerRequest, consumerResponses);
    }

}
