package com.learning.kafka.util;

public enum ProducerType {
    BROKER1_PRODUCER_DEFAULT("default", "broker1-string-string--default-producer"),
    BROKER2_PRODUCER_1("producer2", "broker2-string-string-producer-1"),
    BROKER1_PRODUCER_2("producer1", "broker1-string-string-producer-2"),
    BROKER1_PRODUCER_3("producer3", "broker1-string-User-producer-3");

    private String producerName;
    private String producerDescription;

    public String getProducerName() {
        return producerName;
    }

    public String getProducerDescription() {
        return producerDescription;
    }

    ProducerType(String producerName, String description){
        this.producerName = producerName;
        this.producerDescription = description;
    }
}
