package com.learning.kafka.facade;

import com.learning.kafka.dto.ProducerRequest;
import com.learning.kafka.dto.ProducerResponse;
import com.learning.kafka.producer.CustomKafkaProducerOneImpl;
import com.learning.kafka.producer.CustomKafkaProducerTwoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomProducerFacade<K, V> {

    @Autowired
    private CustomKafkaProducerOneImpl<K, V> customKafkaProducerOneImpl;

    @Autowired
    private CustomKafkaProducerTwoImpl<K, V> customKafkaProducerTwoImpl;

    public List<ProducerResponse> sendMessageUsingProducerOne(ProducerRequest request) {

        String topic = request.getTopicName();
        K key = (K) (Objects.isNull(request.getKey()) ? UUID.randomUUID().toString() : request.getKey());

        final Stream<?> recordStream = request.getRecords().stream();
        /*Add .map for massaging of key(above), value pushing to kafka for producer 1*/
        final List<ProducerResponse> producerResponses = recordStream
                .map(record -> customKafkaProducerOneImpl.produce(topic, key, (V) record))
                .collect(Collectors.toList());
        return producerResponses;
    }

    public List<ProducerResponse> sendMessageUsingProducerTwo(ProducerRequest request) {

        String topic = request.getTopicName();
        K key = (K) (Objects.isNull(request.getKey()) ? UUID.randomUUID().toString() : request.getKey());

        final Stream<?> recordStream = request.getRecords().stream();
        /*Add .map for massaging of key(above), value pushing to kafka for producer 2*/
        final List<ProducerResponse> producerResponses = recordStream
                .map(record -> customKafkaProducerTwoImpl.produce(topic, key, (V) record))
                .collect(Collectors.toList());
        return producerResponses;
    }

    /*public List<ProducerResponse> sendMessageUsingProducerOne(ProducerRequest request) {
        List<ProducerResponse> producerResponses = new ArrayList<>();

        String topicName = request.getTopicName();
        String key = request.getKey();
        for (String record : request.getRecords()) {
            producerResponses.add(customProducerOneImpl.produce(topicName, key, record));
        }
        return producerResponses;
    }

    public List<ProducerResponse> sendMessageUsingProducerTwo(ProducerRequest request) {
        List<ProducerResponse> producerResponses = new ArrayList<>();

        String topicName = request.getTopicName();
        String key = request.getKey();
        for (String record : request.getRecords()) {
            producerResponses.add(customProducerTwoImpl.produce(topicName, key, record));
        }
        return producerResponses;
    }*/

}
