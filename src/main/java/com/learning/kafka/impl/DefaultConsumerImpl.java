package com.learning.kafka.impl;

import com.learning.kafka.config.consumer.ConsumerConfiguration;
import com.learning.kafka.dto.ConsumerRequest;
import com.learning.kafka.dto.ConsumerResponse;
import com.learning.kafka.service.CustomKafkaConsumer;
import com.learning.kafka.util.ConsumerType;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.BlockingQueue;

@Slf4j
@Service
public class DefaultConsumerImpl implements CustomKafkaConsumer<ConsumerResponse> {

    @Value("${TOPIC_NAME:my-topic}")
    private String topicName;

    private ConsumerConfiguration consumerConfiguration;
    private KafkaConsumer<String, String> kafkaConsumer;

    @Autowired
    public DefaultConsumerImpl(ConsumerConfiguration consumerConfiguration) {
        this.consumerConfiguration = consumerConfiguration;
    }

    /**
     * This returns the consumer name.
     *
     * @return String consumer name
     */
    @Override
    public String consumerName() {
        return ConsumerType.DEFAULT_CONSUMER.getConsumerName();
    }

    @Override
    public void consumeRecords(ConsumerRequest consumerRequest, BlockingQueue<ConsumerResponse> messages) {

        topicName = consumerRequest.getTopic().isEmpty() ? topicName : consumerRequest.getTopic();

        Properties consumerProperties = consumerConfiguration.getConsumerProperties();
        consumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        kafkaConsumer = new KafkaConsumer<>(consumerProperties);
        kafkaConsumer.subscribe(Arrays.asList(topicName));

        ConsumerRecords<String, String> consumerRecords = null;
        while (true) {
            consumerRecords = kafkaConsumer.poll(Duration.ofMillis(1000));

            if (consumerRecords == null || consumerRecords.isEmpty()) {
                log.info("No record found in poll.");
                break;
            }

            Iterator<ConsumerRecord<String, String>> recordIterator = consumerRecords.iterator();
            while (true) {
                if (!recordIterator.hasNext()) break;

                ConsumerRecord<String, String> record = recordIterator.next();
                messages.add(ConsumerResponse.builder().partition(record.partition()).offset(record.offset()).key(record.key()).value(Collections.singletonList(record.value())).build());
            }
        }
        log.info("Done reading from kafka topic. Closing kafka consumer.");
        kafkaConsumer.close();
    }
}
