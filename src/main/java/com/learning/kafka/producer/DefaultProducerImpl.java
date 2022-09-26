package com.learning.kafka.producer;

import com.learning.kafka.dto.ProducerRequest;
import com.learning.kafka.dto.ProducerResponse;
import com.learning.kafka.service.CustomKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This <>SpringBootProducer</> class is easy to understand and removes all boilerplate code for
 * kafka producer configuration.
 * If you want to take controller and understand KafkaProducer properties more in detail please refer <>ProducerConfiguration</>
 */
@Service
@Slf4j
public class DefaultProducerImpl<K, V> implements CustomKafkaProducer<K, V> {

    @Autowired
    private KafkaTemplate<K, V> kafkaTemplate;

    @Autowired
    private RecordMetadataHelper<K, V> recordMetadataHelper;

   /* @Autowired
    @Qualifier("producerOne")
    private KafkaTemplate<String, String> kafkaTemplateOne;

    @Autowired
    @Qualifier("producerTwo")
    private KafkaTemplate<String, String> kafkaTemplateTwo;*/

    /**
     * send record to kafka using default kafkatemplate provided by springboot kafkaadmin bean
     * we can choose to go with default topic as well.
     * also topic name can be used in producer will be created by kafka only if auto.create.topics.enable=true.
     *
     * @param topic
     * @param key
     * @param value
     * @return ProducerResponse
     */
    @Override
    public ProducerResponse produce(String topic, K key, V value) {
        log.info("#### -> Producing message -> {}", value);
        ListenableFuture<SendResult<K, V>> future = kafkaTemplate.send(topic, key, value);

        future.addCallback(new ListenableFutureCallback<SendResult<K, V>>() {
            @Override
            public void onSuccess(SendResult<K, V> result) {
                log.info("Message [{}] delivered with offset {}", value, result.getRecordMetadata().offset());
            }

            @Override
            public void onFailure(Throwable ex) {
                log.warn("Unable to deliver message [{}]. {}", value, ex.getMessage());
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

    @Override
    public List<ProducerResponse> sendMessageUsingProducerRequest(ProducerRequest request) {

        String topicName = request.getTopicName();
        Objects.requireNonNull(topicName, "Topic name cannot be null.");

        Stream<?> stream = request.getRecords().stream();
        final List<ProducerResponse> producerResponses = stream.map(record -> kafkaTemplate.send(topicName, (K) request.getKey(), (V) record))
                .map(listenableFutureProducerResponseFunction)
                .collect(Collectors.toList());

        /*return request.getRecords().stream()
                .map(record -> kafkaTemplate.send(topicName, key, record))
                .map(recordMetadataHelper::apply)
                .collect(Collectors.toList());*/
        return producerResponses;
    }

    final Function<ListenableFuture<SendResult<K, V>>, ProducerResponse> listenableFutureProducerResponseFunction = future -> {
        try {
            //retry 3 times in case of timeout
            return recordMetadataHelper.retryGetProducerRecordMetadata(future, 3);

        } catch (InterruptedException e) {
            log.error("execution interrupted.");
            return ProducerResponse.builder().msg("msg failed to deliver. due to interruption").error(e).build();
        } catch (ExecutionException e) {
            log.error("error while sending msg to kafka");
            return ProducerResponse.builder().msg("msg failed to deliver.").error(e).build();
        }
    };

    /*
     *//**
     * send record to kafka broker using producer one which uses producerfactoryone and kafkatemplate-producerOne
     * @param topicName
     * @param key
     * @param value
     * @return ProducerResponse
     *//*
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
        Objects.requireNonNull(topicName, "Topic name cannot be null.");

        return request.getRecords().stream()
                .map(record -> kafkaTemplateOne.send(topicName, key, record))
                .map(this::apply)
                .collect(Collectors.toList());
    }

    *//**
     * send record to kafka broker using producer two which uses producerfactorytwo and kafkatemplate-producerTwo
     *
     * @param topicName
     * @param key
     * @param value
     * @return ProducerResponse
     *//*
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

    *//**
     * get record metadata from future and retry in case of timeout
     * @param future
     * @param retry
     * @return ProducerResponse
     * @throws InterruptedException
     * @throws ExecutionException
     *//*
    private ProducerResponse retryGetProducerRecordMetadata(ListenableFuture<SendResult<String, String>> future, int retry) throws InterruptedException, ExecutionException {
        while (retry > 0) {
            try {
                *//**
     * Do not implement this in production until and unless special use case
     * This will have huge performance impact.
     * Let's consider, each message might take 500ms, then 100*500 becomes 50000 in terms of seconds its 50seconds.
     * Considering 50seconds is very high which will cause hell lot of problems in case of the performance factors.
     *//*
                RecordMetadata result = future.get(1000, TimeUnit.MILLISECONDS).getRecordMetadata();
                return ProducerResponse.builder().partition(result.partition()).offset(result.offset()).msg("msg delivered.").build();
            } catch (TimeoutException e) {
                log.error("timeout waiting from broker response for producer. Retrying again");
                return retryGetProducerRecordMetadata(future, retry--);
            }
        }
        return ProducerResponse.builder().msg("msg failed to deliver.").error(new TimeoutException("timeout waiting from broker response for producer.")).build();
    }

    *//**
     * function for stream map. One of the way to handle exception in stream
     * @param future
     * @return ProducerResponse
     *//*
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
*/
}
