package com.learning.kafka.config.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommandDetail {
    private int sequence;
    private String command;
    private String Type;
    private String kafkaBinDirectory;
    private String kafkaConfigDirectory;
    private String startScript;
    private String stopScript;
    private String propertiesFile;
}
