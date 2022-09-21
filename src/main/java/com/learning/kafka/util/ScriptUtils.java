package com.learning.kafka.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Component
@Slf4j
public class ScriptUtils {

    public void runScriptWithCommand(List<String> commands) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(commands);
        log.debug("commands: {}", pb.command());
        BufferedReader reader = null;
        BufferedReader errorReader = null;
        pb.redirectErrorStream(true);
        pb.inheritIO();
        try {
            Process p = pb.start();
            reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                log.debug(">> {}", line);
            }

            int exitVal = p.waitFor();
            if (exitVal == 0) {
                log.info("command executed successfully!");
            } else {
                //abnormal...
                String error;
                while ((error = errorReader.readLine()) != null) {
                    log.error("<ERROR> {}", error);
                }
                log.error("############### exit value: {}", exitVal);
            }
        } catch (IOException e) {
            log.error("error reading command logs. {}", e);
        } catch (InterruptedException e) {
            log.error("process interrupted. {}", e);
        } finally {
            reader.close();
            errorReader.close();
        }
    }
}
