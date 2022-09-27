package com.learning.kafka.facade;

import com.learning.kafka.dto.ProducerRequest;
import com.learning.kafka.dto.ProducerResponse;
import com.learning.kafka.impl.CustomKafkaProducerImpl;
import com.learning.kafka.service.CustomKafkaProducer;
import com.learning.kafka.util.ProducerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomProducerFacade<K, V> {

    private ApplicationContext applicationContext;
    private CustomKafkaProducer<K, V> customKafkaProducer;

    @Autowired
    public CustomProducerFacade(ApplicationContext applicationContext, CustomKafkaProducerImpl<K, V> customKafkaProducerNew) {
        this.applicationContext = applicationContext;
        this.customKafkaProducer = customKafkaProducerNew;
    }

    /**
     * This method send single record to kafka without doing any processing on key, value before sending it to kafka.
     *
     * @param topic        String
     * @param key          K
     * @param value        V
     * @param producerType ProducerType
     * @return ProducerResponse
     */
    public ProducerResponse sendRecord(String topic, K key, V value, ProducerType producerType) {

        KafkaTemplate<K, V> kafkaTemplate = getKafkaTemplateForProducer(producerType);
        return customKafkaProducer.produce(kafkaTemplate, topic, key, (V) value);
    }

    /**
     * This method send single record to kafka with doing some processing on key, value before sending it to kafka.
     *
     * @param topic        String
     * @param key          K
     * @param value        V
     * @param producerType ProducerType
     * @return ProducerResponse
     */
    public ProducerResponse sendProcessedRecord(String topic, K key, V value, ProducerType producerType) {

        KafkaTemplate<K, V> kafkaTemplate = getKafkaTemplateForProducer(producerType);

        /*Do some processing here on key and value as per requirement.*/
        value = (V) (key + ":" + value);
        key = (K) (key + UUID.randomUUID().toString());
        return customKafkaProducer.produce(kafkaTemplate, topic, key, (V) value);
    }

    /**
     * This sends record/s to kafka in bulk behaviour, records will be sent to kafka as it is from producer request no processing done
     * kafka producer properties used batch.size and linger.ms to manage this behaviour.
     *
     * @param request ProducerRequest
     * @return List<ProducerResponse>
     */
    public List<ProducerResponse> sendBulkRecordsUsingProducerRequest(ProducerRequest<K, V> request, ProducerType producerType) {

        KafkaTemplate<K, V> kafkaTemplate = getKafkaTemplateForProducer(producerType);

        return customKafkaProducer.sendBulkMessageUsingProducerRequest(kafkaTemplate, request);
    }

    /**
     * This method do processing on key, value before sending it to kafka.
     *
     * @param request ProducerRequest
     * @return List<ProducerResponse>
     */
    public List<ProducerResponse> sendBulkProcessedRecordsUsingProducerRequest(ProducerRequest<K, V> request, ProducerType producerType) {

        KafkaTemplate<K, V> kafkaTemplate = getKafkaTemplateForProducer(producerType);
        String topic = request.getTopicName();
        K key = (K) (Objects.isNull(request.getKey()) ? UUID.randomUUID().toString() : request.getKey());

        final Stream<?> recordStream = request.getRecords().stream();
        /*Add .map for massaging of key(above), value pushing to kafka for producer 1*/
        final List<ProducerResponse> producerResponses = recordStream
                .map(record -> customKafkaProducer.produce(kafkaTemplate, topic, key, (V) record))
                .collect(Collectors.toList());
        return producerResponses;
    }


    private KafkaTemplate<K, V> getKafkaTemplateForProducer(ProducerType producerType) {
        KafkaTemplate<K, V> kafkaTemplate = null;
        switch (producerType) {
            case BROKER2_PRODUCER_1:
                kafkaTemplate = (KafkaTemplate<K, V>) applicationContext.getBean("producerTwo");
                break;
            case BROKER1_PRODUCER_2:
                kafkaTemplate = (KafkaTemplate<K, V>) applicationContext.getBean("producerOne");
                break;
            case BROKER1_PRODUCER_3:
                kafkaTemplate = (KafkaTemplate<K, V>) applicationContext.getBean("producerThree");
                break;
            default:
                //this is BROKER1_PRODUCER_DEFAULT >> producer
                kafkaTemplate = (KafkaTemplate<K, V>) applicationContext.getBean("kafkaTemplate");
        }
        return kafkaTemplate;
    }

}
