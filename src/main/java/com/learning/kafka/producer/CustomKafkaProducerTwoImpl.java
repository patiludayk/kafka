package com.learning.kafka.producer;

import com.learning.kafka.dto.ProducerRequest;
import com.learning.kafka.dto.ProducerResponse;
import com.learning.kafka.service.CustomKafkaProducer;
import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

/**
 * This <>SpringBootProducer</> class is easy to understand and removes all boilerplate code for
 * kafka producer configuration.
 * If you want to take controller and understand KafkaProducer properties more in detail please refer <>ProducerConfiguration</>
 */
@Slf4j
@Service
public class CustomKafkaProducerTwoImpl<K, V> implements CustomKafkaProducer<K, V> {

    @Autowired
    private RecordMetadataHelper<K, V> recordMetadataHelper;

    @Autowired
    @Qualifier("producerTwo")
    private KafkaTemplate<K, V> kafkaTemplateTwo;

    /**
     * Send record to kafka broker using producer two which uses producerFactoryOne and KafkaTemplate-producerTwo
     * @param topic
     * @param key
     * @param value
     * @return ProducerResponse
     */
    @Override
    public ProducerResponse produce(@NotNull String topic, K key, V value) {

        ListenableFuture<SendResult<K, V>> future = kafkaTemplateTwo.send(topic, key, value);
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
    }

    @Override
    public List<ProducerResponse> sendMessageUsingProducerRequest(ProducerRequest request) {

        String topicName = request.getTopicName();
        Objects.requireNonNull(topicName, "Topic name cannot be null.");

        Stream<?> stream = request.getRecords().stream();
        final List<ProducerResponse> producerResponses = stream.map(record -> kafkaTemplateTwo.send(topicName, (K) request.getKey(), (V) record))
                .map(listenableFutureProducerResponseFunction)
                .collect(Collectors.toList());

        /*return request.getRecords().stream()
                .map(record -> kafkaTemplateTwo.send(topicName, key, record))
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

}
