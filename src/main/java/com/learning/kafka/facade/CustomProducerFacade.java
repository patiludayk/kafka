package com.learning.kafka.facade;

import com.learning.kafka.dto.ProducerRequest;
import com.learning.kafka.dto.ProducerResponse;
import com.learning.kafka.producer.CustomProducerImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomProducerFacade {

    private CustomProducerImpl producer;

    public CustomProducerFacade(CustomProducerImpl customProducerImpl){
        this.producer = customProducerImpl;
    }
    public List<ProducerResponse> sendMessageUsingProducerOne(ProducerRequest request) {
        List<ProducerResponse> producerResponses = new ArrayList<>();

        String topicName = request.getTopicName();
        String key = request.getKey();
        for (String record : request.getRecords()) {
            producerResponses.add(producer.sendMessageUsingProducerOne(topicName, key, record));
        }
        return producerResponses;
    }

    public List<ProducerResponse> sendMessageUsingProducerTwo(ProducerRequest request) {
        List<ProducerResponse> producerResponses = new ArrayList<>();

        String topicName = request.getTopicName();
        String key = request.getKey();
        for (String record : request.getRecords()) {
            producerResponses.add(producer.sendMessageUsingProducerTwo(topicName, key, record));
        }
        return producerResponses;
    }

}
