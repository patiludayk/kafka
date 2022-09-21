package com.learning.kafka.config.broker;

import com.learning.kafka.util.ScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Arrays;

@Configuration
@Slf4j
public class StreamServerConfig {

    private ScriptUtils scriptUtils;

    @Autowired
    public StreamServerConfig(ScriptUtils scriptUtils){
        this.scriptUtils = scriptUtils;
    }

    @Bean
    public void startZookeeperAndKafka() throws IOException {
        log.info("starting zookeeper and kafka.");
        scriptUtils.runScriptWithCommand(Arrays.asList("sh", "src/main/resources/scripts/kafka-zookeeper-server-start.sh"));
        log.info("zookeeper and kafka started.");
    }

    @PreDestroy
    private void shutdown() throws IOException {
        log.info("shutting down stream servers.");
        scriptUtils.runScriptWithCommand(Arrays.asList("sh", "src/main/resources/scripts/kafka-zookeeper-server-stop.sh"));
        log.info("stream servers down.");
    }

}
