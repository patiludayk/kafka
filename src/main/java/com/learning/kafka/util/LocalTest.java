package com.learning.kafka.util;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.io.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Slf4j
public class LocalTest {

    private static String kafkaBinDirectory = "/Users/udaypatil/kafka_2.13-3.2.1/bin/";
    private static String kafkaConfigDirectory = "/Users/udaypatil/kafka_2.13-3.2.1/config/";

    static int c = 1;

    public LocalTest(){
        log.info("in constructor");
        c = 2;
    }

    {
        log.info("in block");
        c = 3;
    }

    static {
        log.info("in static block");
        c = 4;
    }

    public static void main(String[] args) {
        LocalTest localTest = new LocalTest();
        log.info("c: {}", localTest.c);

        //localTest.executeShellCommand("/Users/udaypatil/test", "sh", "test.sh");

        //localTest.getMeProducerType("producer30");

        log.info("done.");
    }

    private void getMeProducerType(String producer1) {
        switch (producer1){
            case "producer1" :
                log.info("producer1");
                break;
            case "producer2" :
                log.info("producer2");
                break;
            case "producer3" :
                log.info("producer3");
                break;
            default:
                log.info("default");
        }
    }

    @PreDestroy
    private void shutdown(){
        log.info("exit called.");
    }


    public void executeShellCommand(String directory, String... command) {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows) {
            builder.command("cmd.exe", "/c", "dir");
        } else {
            builder.command(command);
        }
//        builder.directory(new File(System.getProperty("user.home")));
        builder.directory(new File(directory));
        Process process = null;
        try {
            process = builder.start();
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("command executed successfully!");
            } else {
                //abnormal...
                log.error("abnormal...");
            }
            Object o = future.get(5, TimeUnit.SECONDS);
            log.info("{}", o);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
        }
    }
}

