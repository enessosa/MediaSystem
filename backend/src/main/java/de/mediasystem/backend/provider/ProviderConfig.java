package de.mediasystem.backend.provider;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ProviderConfig {

    @Bean
    public RestClient aniListRestClient() {
        return RestClient.builder()
                .baseUrl("https://graphql.anilist.co")
                .build();
    }
}
