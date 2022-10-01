package com.learning.kafka.service;

import com.learning.kafka.dto.ConsumerRequest;

import java.util.concurrent.BlockingQueue;

public interface CustomKafkaConsumer<T> {

    /**
     * This returns the consumer name.
     *
     * @return String consumer name
     */
    String consumerName();

    /**
     * Consumes records from kafka topic in the messages.
     *
     * @param consumerRequest ConsumerRequest
     * @param messages        BlockingQueue
     */
    void consumeRecords(ConsumerRequest consumerRequest, BlockingQueue<T> messages);
}
