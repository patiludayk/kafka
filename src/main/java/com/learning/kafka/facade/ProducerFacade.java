package com.learning.kafka.facade;

import com.learning.kafka.dto.ProducerRequest;
import com.learning.kafka.dto.ProducerResponse;
import com.learning.kafka.producer.SpringBootProducer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProducerFacade {

    private SpringBootProducer producer;

    public ProducerFacade(SpringBootProducer springBootProducer){
        this.producer = springBootProducer;
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
}
