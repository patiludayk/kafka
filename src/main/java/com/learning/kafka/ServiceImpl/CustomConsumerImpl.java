package com.learning.kafka.ServiceImpl;

import com.learning.kafka.config.consumer.ConsumerConfiguration;
import com.learning.kafka.dto.ConsumerResponse;
import com.learning.kafka.service.CustomKafkaConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;

@Slf4j
@Service
public class CustomConsumerImpl implements CustomKafkaConsumer {

    @Value("${TOPIC_NAME:my-topic}")
    private String topicName;

    private ConsumerConfiguration consumerConfiguration;
    private KafkaConsumer<String, String> kafkaConsumer;

    @Autowired
    public CustomConsumerImpl(ConsumerConfiguration consumerConfiguration) {
        this.consumerConfiguration = consumerConfiguration;
    }

    @Override
    public void consumeEvents(List messages) {

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
                messages.add(ConsumerResponse.builder().partition(record.partition()).offset(record.offset()).key(record.key()).value(record.value()).build());
            }
        }
        log.info("Done reading from kafka topic. Closing kafka consumer.");
        kafkaConsumer.close();
    }

    @PostConstruct
    public void shutdown() {
    }

}
