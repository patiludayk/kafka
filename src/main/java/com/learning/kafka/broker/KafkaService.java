package com.learning.kafka.broker;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class KafkaService {

    @Value("${BOOTSTRAP_SERVERS}")
    private String bootstrapServers; //"localhost:9092, localhost:9093"

    public Set<String> getAllKafkaTopics() throws ExecutionException, InterruptedException {
        AdminClient adminClient = getAdminClient();

        ListTopicsOptions listTopicsOptions = new ListTopicsOptions();
        listTopicsOptions.listInternal(false);  //won't list internal topic such as __consumer_offsets

        return adminClient.listTopics(listTopicsOptions).names().get();
    }

    private AdminClient getAdminClient() {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        AdminClient adminClient = AdminClient.create(properties);
        return adminClient;
    }
}
