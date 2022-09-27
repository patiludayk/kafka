package com.learning.kafka.impl;

import com.learning.kafka.dto.ProducerRequest;
import com.learning.kafka.dto.ProducerResponse;
import com.learning.kafka.helper.RecordMetadataHelper;
import com.learning.kafka.service.CustomKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class CustomKafkaProducerImpl<K, V> implements CustomKafkaProducer<K, V> {

    private RecordMetadataHelper<K, V> recordMetadataHelper;

    @Autowired
    public CustomKafkaProducerImpl(RecordMetadataHelper recordMetadataHelper){
        this.recordMetadataHelper = recordMetadataHelper;
    }

    /**
     * Send single record to kafka using kafka template provided.
     * @param kafkaTemplate
     * @param topic
     * @param key
     * @param value
     * @return ProducerResponse
     */
    @Override
    public ProducerResponse produce(KafkaTemplate<K, V> kafkaTemplate, String topic, K key, V value) {
        ListenableFuture<SendResult<K, V>> future = kafkaTemplate.send(topic, key, value);
        try {
            //retry 3 times in case of timeout
            return recordMetadataHelper.retryGetProducerRecordMetadataNew(future, 3);
        } catch (InterruptedException e) {
            log.error("execution interrupted.");
            return ProducerResponse.builder().msg("msg failed to deliver. due to interruption").error(e).build();
        } catch (ExecutionException e) {
            log.error("error while sending msg to kafka");
            return ProducerResponse.builder().msg("msg failed to deliver.").error(e).build();
        }
    }

    /**
     * Sends record/s in bulk using provided kafkatemplate - bulk means producer configuration provided in producer factory
     * kafka producer properties used batch.size and linger.ms to manage this behaviour.
     * @param kafkaTemplate KafkaTemplate<K, V>
     * @param request ProducerRequest
     * @return List<ProducerResponse>
     */
    @Override
    public List<ProducerResponse> sendBulkMessageUsingProducerRequest(KafkaTemplate<K, V> kafkaTemplate, ProducerRequest request) {
        String topicName = request.getTopicName();
        Objects.requireNonNull(topicName, "Topic name cannot be null.");

        Stream<?> stream = request.getRecords().stream();
        final List<ProducerResponse> producerResponses = stream.map(record -> kafkaTemplate.send(topicName, (K) request.getKey(), (V) record))
                .map(listenableFutureProducerResponseFunction)
                .collect(Collectors.toList());

        return producerResponses;
    }

    final Function<ListenableFuture<SendResult<K, V>>, ProducerResponse> listenableFutureProducerResponseFunction = future -> {
        try {
            //retry 3 times in case of timeout
            return recordMetadataHelper.retryGetProducerRecordMetadataNew(future, 3);

        } catch (InterruptedException e) {
            log.error("execution interrupted.");
            return ProducerResponse.builder().msg("msg failed to deliver. due to interruption").error(e).build();
        } catch (ExecutionException e) {
            log.error("error while sending msg to kafka");
            return ProducerResponse.builder().msg("msg failed to deliver.").error(e).build();
        }
    };
}
