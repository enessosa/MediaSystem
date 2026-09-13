package de.mediasystem.backend.provider;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class ProviderConfig {

    /**
     * Rest Client for AniList.
     * @return the client
     */
    @Bean
    public RestClient aniListRestClient() {
        return RestClient.builder()
                .baseUrl("https://graphql.anilist.co")
                .build();
    }

    /**
     * Rest Client for tmdb.
     * @param tmdbApiKey the api key from the env variables
     * @return the client
     */
    @Bean
    public RestClient tmdbRestClient(@Value("${tmdb.api-key}") String tmdbApiKey) {
        return RestClient.builder()
                .baseUrl("https://api.themoviedb.org/3")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tmdbApiKey)
                .build();
    }

    /**
     * Rest Client for rawg.
     * @return the client
     */
    @Bean
    public RestClient rawgRestClient() {
        return RestClient.builder()
                .baseUrl("https://api.rawg.io/api")
                .build();
    }

    /**
     * Rest Client for OpenLibrary. Keine Auth nötig, die API ist komplett offen.
     * @return the client
     */
    @Bean
    public RestClient openLibraryRestClient() {
        return RestClient.builder()
                .baseUrl("https://openlibrary.org")
                .build();
    }
}
