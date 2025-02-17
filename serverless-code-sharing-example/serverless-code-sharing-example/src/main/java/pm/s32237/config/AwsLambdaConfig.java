package pm.s32237.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

import java.net.URI;

@Configuration
public class AwsLambdaConfig {

    @Bean
    public LambdaClient lambdaClient() {
        return LambdaClient.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .region(Region.US_EAST_1) // change the region as needed
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

}