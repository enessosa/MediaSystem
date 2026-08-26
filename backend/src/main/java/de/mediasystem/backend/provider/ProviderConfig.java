package de.mediasystem.backend.provider;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class ProviderConfig {

    @Bean
    public RestClient aniListRestClient() {
        return RestClient.builder()
                .baseUrl("https://graphql.anilist.co")
                .build();
    }

    @Bean
    public RestClient tmdbRestClient(@Value("${tmdb.api-key}") String tmdbApiKey) {
        return RestClient.builder()
                .baseUrl("https://api.themoviedb.org/3")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tmdbApiKey)
                .build();
    }
}
