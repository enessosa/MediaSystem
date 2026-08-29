package de.mediasystem.backend.provider;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TmdbSearchResponse(List<Result> results) {

    public record Result(Long id,
                         @JsonProperty("media_type") String mediaType,
                         String title,
                         String overview,
                         @JsonProperty("release_date") String releaseDate,
                         @JsonProperty("poster_path") String posterPath,
                         // ab hier Serie
                         String name,
                         @JsonProperty("first_air_date") String firstAirDate
    ) { }
}