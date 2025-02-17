package pm.s32237.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;

import java.net.URI;

@Configuration
public class IamClientConfig {
    @Bean
    public IamClient iamClient() {
        return IamClient.builder()
                .region(Region.EU_CENTRAL_1) // Set your AWS region
                .endpointOverride(URI.create("http://localhost:4566"))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

}
