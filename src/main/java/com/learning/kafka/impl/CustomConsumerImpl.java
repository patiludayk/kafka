package com.learning.kafka.impl;

import com.learning.kafka.dto.ConsumerRequest;
import com.learning.kafka.dto.ConsumerResponse;
import com.learning.kafka.service.CustomKafkaConsumer;
import com.learning.kafka.util.ConsumerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This <>SpringBootConsumer</> class is easy to understand and removes all boiler plate code for
 * kafka consumer configuration and creation.
 * If you want to take controller and understand KafkaConsumer properties more in detail please refer <>ConsumerConfiguration</>
 */
@Service
@Slf4j
public class CustomConsumerImpl implements CustomKafkaConsumer<ConsumerResponse> {

    private BlockingQueue<ConsumerResponse> recordsQueue = new LinkedBlockingQueue<>(5);

    /**
     * This returns the consumer name.
     *
     * @return String consumer name
     */
    @Override
    public String consumerName() {
        return ConsumerType.CUSTOM_CONSUMER.getConsumerName();
    }

    /**
     * Consumes records from kafka topic in the messages.
     *
     * @param consumerRequest ConsumerRequest
     * @param messages        BlockingQueue
     */
    @Override
    public void consumeRecords(ConsumerRequest consumerRequest, BlockingQueue<ConsumerResponse> messages) {
        recordsQueue.drainTo(messages);
    }

    @KafkaListener(topics = "${TOPIC_NAME}", groupId = "${GROUP_ID:kafka-learning}"/*, containerFactory = "customKafkaListenerContainerFactory"*/)
    public void consume(String message) throws IOException, InterruptedException {
        log.debug("#### -> Consumed message -> {}", message);
        log.info("queue capacity: {}", recordsQueue.remainingCapacity());
        consumeRecord(message);
        log.info("queue remaining capacity: {}", recordsQueue.remainingCapacity());
    }

    private void consumeRecord(String record) throws InterruptedException {
        boolean isWaiting = false;
        while (recordsQueue.remainingCapacity() == 0) {
            if (!isWaiting) {
                log.info("waiting for queue capacity.");
            }
            log.debug("waiting for queue capacity.");
            //TODO: rather sleeping pause KafkaListner here and once capacity available resume polling.
            TimeUnit.SECONDS.sleep(2);
            isWaiting = true;
        }
        recordsQueue.add(ConsumerResponse.builder().value(Collections.singletonList(record)).build());
    }

}
