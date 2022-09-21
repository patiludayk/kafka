package com.learning.kafka.producer;

import com.learning.kafka.dto.ProducerResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;

/**
 * This <>SpringBootProducer</> class is easy to understand and removes all boiler plate code for
 * kafka producer configuration.
 * If you want to take controller and understand KafkaProducer properties more in detail please refer <>ProducerConfiguration</>
 */
@Service
@Slf4j
public class SpringBootProducer {

    @Value("${TOPIC_NAME}")
    private String TOPIC;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public ProducerResponse sendMessage(String key, String message) {
        log.info("#### -> Producing message -> {}", message);
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, key, message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("Message [{}] delivered with offset {}",
                        message,
                        result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                log.warn("Unable to deliver message [{}]. {}",
                        message,
                        ex.getMessage());
            }
        });
        try {
            RecordMetadata result = future.get().getRecordMetadata();
            return ProducerResponse.builder().partition(result.partition()).offset(result.offset()).msg("msg delivered.").build();
        } catch (InterruptedException e) {
            log.error("execution interrupted.");
            return ProducerResponse.builder().msg("msg failed to deliver. due to interruption").exception(String.valueOf(e)).build();
        } catch (ExecutionException e) {
            log.error("error while sending msg to kafka");
            return ProducerResponse.builder().msg("msg failed to deliver.").exception(String.valueOf(e)).build();
        }
    }
}
