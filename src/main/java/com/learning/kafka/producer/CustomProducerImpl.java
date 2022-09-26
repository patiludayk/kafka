package com.learning.kafka.producer;

import com.learning.kafka.dto.ProducerRequest;
import com.learning.kafka.dto.ProducerResponse;
import com.learning.kafka.service.CustomKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * This <>SpringBootProducer</> class is easy to understand and removes all boiler plate code for
 * kafka producer configuration.
 * If you want to take controller and understand KafkaProducer properties more in detail please refer <>ProducerConfiguration</>
 */
@Service
@Slf4j
public class CustomProducerImpl implements CustomKafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    @Qualifier("producerOne")
    private KafkaTemplate<String, String> kafkaTemplateOne;

    @Autowired
    @Qualifier("producerTwo")
    private KafkaTemplate<String, String> kafkaTemplateTwo;

    //TODO: now each producer will have its own impl class with respective kafkaTemplate - add that implementation and move code there from here!!!
    @Override
    public ProducerResponse produce(String topic, String key, Object value) {
        return null;
    }

    /**
     * send record to kafka using default kafkatemplate provided by springboot kafkaadmin bean
     * we can choose to go with default topic as well.
     * also topic name can be used in producer will be created by kafka only if auto.create.topics.enable=true.
     *
     * @param key
     * @param message
     * @return ProducerResponse
     */
    public ProducerResponse sendMessage(String topicName, String key, String message) {
        log.info("#### -> Producing message -> {}", message);
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, key, message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("Message [{}] delivered with offset {}", message, result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                log.warn("Unable to deliver message [{}]. {}", message, ex.getMessage());
            }
        });

        try {
            /**
             * Do not implement this in production until and unless special use case
             * This will have huge performance impact.
             * Let's consider, each message might take 500ms, then 100*500 becomes 50000 in terms of seconds its 50seconds.
             * Considering 50seconds is very high which will cause hell lot of problems in case of the performance factors.
             */
            RecordMetadata result = future.get().getRecordMetadata();
            return ProducerResponse.builder().partition(result.partition()).offset(result.offset()).msg("msg delivered.").build();
        } catch (InterruptedException e) {
            log.error("execution interrupted.");
            return ProducerResponse.builder().msg("msg failed to deliver. due to interruption").error(e).build();
        } catch (ExecutionException e) {
            log.error("error while sending msg to kafka");
            return ProducerResponse.builder().msg("msg failed to deliver.").error(e).build();
        }
    }

    /**
     * send record to kafka broker using producer one which uses producerfactoryone and kafkatemplate-producerOne
     * @param topicName
     * @param key
     * @param value
     * @return ProducerResponse
     */
    public ProducerResponse sendMessageUsingProducerOne(String topicName, String key, String value) {

        ListenableFuture<SendResult<String, String>> future = kafkaTemplateOne.send(topicName, key, value);
        try {
            //retry 3 times in case of timeout
            return retryGetProducerRecordMetadata(future, 3);
        } catch (InterruptedException e) {
            log.error("execution interrupted.");
            return ProducerResponse.builder().msg("msg failed to deliver. due to interruption").error(e).build();
        } catch (ExecutionException e) {
            log.error("error while sending msg to kafka");
            return ProducerResponse.builder().msg("msg failed to deliver.").error(e).build();
        }
    }

    public List<ProducerResponse> sendMessageUsingProducerOne(ProducerRequest request) {

        String topicName = request.getTopicName();
        String key = request.getKey();

        return request.getRecords().stream()
                .map(record -> kafkaTemplateOne.send(topicName, key, record))
                .map(this::apply)
                .collect(Collectors.toList());
    }

    /**
     * send record to kafka broker using producer two which uses producerfactorytwo and kafkatemplate-producerTwo
     *
     * @param topicName
     * @param key
     * @param value
     * @return ProducerResponse
     */
    public ProducerResponse sendMessageUsingProducerTwo(String topicName, String key, String value) {

        ListenableFuture<SendResult<String, String>> future = kafkaTemplateTwo.send(topicName, key, value);
        try {
            //retry 3 times in case of timeout
            return retryGetProducerRecordMetadata(future, 3);
        } catch (InterruptedException e) {
            log.error("execution interrupted.");
            return ProducerResponse.builder().msg("msg failed to deliver. due to interruption").error(e).build();
        } catch (ExecutionException e) {
            log.error("error while sending msg to kafka");
            return ProducerResponse.builder().msg("msg failed to deliver.").error(e).build();
        }
    }

    /**
     * get record metadata from future and retry in case of timeout
     * @param future
     * @param retry
     * @return ProducerResponse
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private ProducerResponse retryGetProducerRecordMetadata(ListenableFuture<SendResult<String, String>> future, int retry) throws InterruptedException, ExecutionException {
        while (retry > 0) {
            try {
                /**
                 * Do not implement this in production until and unless special use case
                 * This will have huge performance impact.
                 * Let's consider, each message might take 500ms, then 100*500 becomes 50000 in terms of seconds its 50seconds.
                 * Considering 50seconds is very high which will cause hell lot of problems in case of the performance factors.
                 */
                RecordMetadata result = future.get(1000, TimeUnit.MILLISECONDS).getRecordMetadata();
                return ProducerResponse.builder().partition(result.partition()).offset(result.offset()).msg("msg delivered.").build();
            } catch (TimeoutException e) {
                log.error("timeout waiting from broker response for producer. Retrying again");
                return retryGetProducerRecordMetadata(future, retry--);
            }
        }
        return ProducerResponse.builder().msg("msg failed to deliver.").error(new TimeoutException("timeout waiting from broker response for producer.")).build();
    }

    /**
     * function for stream map. One of the way to handle exception in stream
     * @param future
     * @return ProducerResponse
     */
    private ProducerResponse apply(ListenableFuture<SendResult<String, String>> future) {
        try {
            //retry 3 times in case of timeout
            return retryGetProducerRecordMetadata(future, 3);
        } catch (InterruptedException e) {
            log.error("execution interrupted.");
            return ProducerResponse.builder().msg("msg failed to deliver. due to interruption").error(e).build();
        } catch (ExecutionException e) {
            log.error("error while sending msg to kafka");
            return ProducerResponse.builder().msg("msg failed to deliver.").error(e).build();
        }
    }

}
