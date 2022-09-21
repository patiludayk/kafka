package com.learning.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * This <>SpringBootConsumer</> class is easy to understand and removes all boiler plate code for
 * kafka consumer configuration and creation.
 * If you want to take controller and understand KafkaConsumer properties more in detail please refer <>ConsumerConfiguration</>
 */
@Service
@Slf4j
public class SpringBootConsumer {

    @KafkaListener(topics = "${TOPIC_NAME}", groupId = "${GROUP_ID:kafka-learning}")
    public void consume(String message) throws IOException {
        log.info("#### -> Consumed message -> {}", message);
    }
}
