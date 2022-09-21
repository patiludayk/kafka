/*
package com.learning.kafka.config.broker;

import com.learning.kafka.config.dto.CommandDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class KafkaServerStartConfig {

    @Value("${kafka.bin.directory}")
    private String kafkaBinDirectory;

    @Value("${kafka.config.directory}")
    private String kafkaConfigDirectory;

    public void executeShellCommand(String command){
        //Runtime.getRuntime().exec("cd", kafkaBinDirectory + "kafka-server-stop.sh");
    }

    //@Bean(name = "zookeeper")
    public void startZookeeper() throws InterruptedException {
        CommandDetail commandDetail = CommandDetail.builder()
                .sequence(1)
                .Type("zookeeper")
                .command("sh")
                .kafkaConfigDirectory(kafkaBinDirectory)
                .startScript("zookeeper-server-start.sh")
                .kafkaConfigDirectory(kafkaConfigDirectory)
                .propertiesFile("zookeeper.properties")
                .build();
        executeCommand(commandDetail, "sh", kafkaBinDirectory + "zookeeper-server-start.sh", kafkaConfigDirectory +"zookeeper.properties");
        TimeUnit.SECONDS.sleep(5);
        log.info("zookeeper started.");
    }

    //@Bean(name = "kafka")
    @DependsOn({"zookeeper"})
    public void startKafka() throws InterruptedException {
        log.info("kafka starting");
        CommandDetail commandDetail = CommandDetail.builder()
                .sequence(2)
                .Type("kafka")
                .command("sh")
                .kafkaConfigDirectory(kafkaBinDirectory)
                .startScript("kafka-server-start.sh")
                .kafkaConfigDirectory(kafkaConfigDirectory)
                .propertiesFile("server.properties")
                .build();
        executeCommand(commandDetail, "sh", kafkaBinDirectory + "kafka-server-start.sh", kafkaConfigDirectory +"server.properties");
        TimeUnit.SECONDS.sleep(3);
        log.info("kafka started.");
    }

    private boolean executeCommand(CommandDetail commandDetail, String... command) {

        log.info("Initing separate thread to execute command. This will execute command in separate process. Type: {}, command: {}", commandDetail.getType(), commandDetail.getCommand());

        CompletableFuture<Boolean> completableFuture = CompletableFuture.supplyAsync(() -> {
            boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
            ProcessBuilder processBuilder = new ProcessBuilder();

            if (isWindows) {
                // -- Windows --
                // Run a command
                processBuilder.command(command);
                // Run a bat file
                //processBuilder.command("C:\\Users\\hello.bat");
            } else {
                // -- Linux / Mac OS
                // Run a shell command
                processBuilder.command(command);
                log.info("*******{}********", commandDetail.getSequence());
                processBuilder.command().forEach(s -> log.info(s));
                log.info("***************");
            }
            processBuilder.redirectErrorStream(true);
            try {
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                    //TODO: add condition to check if server starts in case of zookeeper and kafka which are continuous running commands till end of application unlike other commands which terminates
                }

                int exitVal = process.waitFor();
                if (exitVal == 0) {
                    log.info("command executed successfully!");
                    return true;
                } else {
                    //abnormal...
                    log.info("#######{}########", commandDetail.getSequence());
                    processBuilder.command().forEach(s -> log.info(s));
                    log.info("###############");
                    log.error("command terminated abnormally, server: {}, command: {}", commandDetail.getType(), commandDetail.getCommand());
                    return false;
                }
            } catch (IOException e) {
                log.error("IOException executing command, error: {}", e);
                return false;
            } catch (InterruptedException e) {
                log.error("InterruptedException executing command, error: {}", e);
                return false;
            }
        });
        return completableFuture.getNow(true);
    }

    @PreDestroy
    public void shutdown() {
        log.info("stop running server.");
        try {
            //1. stop kafka
            log.info("shutting down kafka running server.");
            executeCommand(CommandDetail.builder().sequence(3).Type("kafka").command("sh").build(), "sh", kafkaBinDirectory + "kafka-server-stop.sh");
            TimeUnit.SECONDS.sleep(5);
            //2. stop zookeeper.
            log.info("shutting down zookeeper running server.");
            executeCommand(CommandDetail.builder().sequence(4).Type("zookeeper").command("sh").build(), "sh", kafkaBinDirectory + "zookeeper-server-stop.sh");
            TimeUnit.SECONDS.sleep(5);
        } catch (Exception e) {
            log.error("exception while stopping server.{}", e);;
        }
        log.info("###STOPPED FROM THE LIFECYCLE###");
    }

}
*/
