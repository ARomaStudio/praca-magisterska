package pm.s32237.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;

@Configuration
public class BashCommandRunner {

    @Bean
    public CommandLineRunner runBashCommands() {
        return args -> {
            String[] commands = {
                "aws --endpoint-url=http://localhost:4566 s3 mb s3://lambda-jars",
                "aws --endpoint-url=http://localhost:4566 s3 mb s3://proto-files",
                "aws --endpoint-url=http://localhost:4566 lambda list-functions"
            };

            // Determine if we're running on Windows
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            String shell = isWindows ? "cmd.exe" : "sh";
            String shellArg = isWindows ? "/c" : "-c";

            for (String command : commands) {
                System.out.println("Executing command: " + command);
                ProcessBuilder processBuilder = new ProcessBuilder(shell, shellArg, command);
                
                // Set working directory to user's home directory where AWS CLI is typically installed
                if (isWindows) {
                    processBuilder.directory(new File(System.getProperty("user.home")));
                }
                
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();

                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                        System.out.println(line);
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    // Check if the error is due to bucket already existing
                    if (output.toString().contains("BucketAlreadyExists") || 
                        output.toString().contains("BucketAlreadyOwnedByYou")) {
                        System.out.println("Bucket already exists, continuing...");
                    } else {
                        System.out.println("Warning: Command failed with exit code " + exitCode + 
                                         ", but continuing application startup...");
                        System.out.println("Error details: " + output.toString().trim());
                    }
                } else {
                    if (command.contains("list-functions")) {
                        System.out.println("Successfully listed Lambda functions");
                    } else {
                        System.out.println("Successfully created bucket");
                    }
                }
            }
        };
    }
} 