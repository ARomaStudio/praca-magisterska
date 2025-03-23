package pm.s32237.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Configuration
public class BashCommandRunner {

    @Bean
    public CommandLineRunner runBashCommands() {
        return args -> {
            String[] commands = {
                "aws --endpoint-url=http://localhost:4566 s3 mb s3://lambda-jars",
                "aws --endpoint-url=http://localhost:4566 s3 mb s3://proto-files"
            };

            for (String command : commands) {
                System.out.println("Executing command: " + command);
                ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    System.out.println("Command failed with exit code " + exitCode + ", but continuing...");
                }
            }
        };
    }
} 