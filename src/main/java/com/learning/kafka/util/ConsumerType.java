package com.learning.kafka.util;

public enum ConsumerType {

    DEFAULT_CONSUMER("default", "default consumer with raw consumer implementation"),
    CUSTOM_CONSUMER("custom", "consumer with KafkaListner implementation");

    private String consumerName;
    private String consumerDescription;

    ConsumerType(String consumerName, String consumerDescription){
        this.consumerName = consumerName;
        this.consumerDescription = consumerDescription;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public String getConsumerDescription() {
        return consumerDescription;
    }
}
